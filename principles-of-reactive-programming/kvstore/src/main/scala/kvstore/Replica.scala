package kvstore

import akka.actor.{ OneForOneStrategy, Props, ActorRef, Actor }
import kvstore.Arbiter._
import scala.collection.immutable.Queue
import akka.actor.SupervisorStrategy.Restart
import scala.annotation.tailrec
import akka.pattern.{ ask, pipe }
import akka.actor.Terminated
import scala.concurrent.duration._
import akka.actor.PoisonPill
import akka.actor.OneForOneStrategy
import akka.actor.SupervisorStrategy
import akka.util.Timeout
import akka.event.LoggingReceive
import akka.actor.ActorLogging

object Replica {
  sealed trait Operation {
    def key: String
    def id: Long
  }
  case class Insert(key: String, value: String, id: Long) extends Operation
  case class Remove(key: String, id: Long) extends Operation
  case class Get(key: String, id: Long) extends Operation

  sealed trait OperationReply
  case class OperationAck(id: Long) extends OperationReply
  case class OperationFailed(id: Long) extends OperationReply
  case class GetResult(key: String, valueOption: Option[String], id: Long) extends OperationReply

  def props(arbiter: ActorRef, persistenceProps: Props): Props = Props(new Replica(arbiter, persistenceProps))

  case object RepersistTimeout
  case class OneSecondAfterPersistTimeout(id:Long)
  case class OneSecondAfterReplicateTimeout(id:Long)
}

class Replica(val arbiter: ActorRef, persistenceProps: Props) extends Actor with ActorLogging{
  import Replica._
  import Replicator._
  import Persistence._
  import context.dispatcher

  override def preStart() = {
    arbiter ! Join

    context.system.scheduler.schedule(Duration.Zero, 100.millis, self, RepersistTimeout)
  }

  /*
   * The contents of this actor is just a suggestion, you can implement it in any way you like.
   */

  var kv = Map.empty[String, String]
  // a map from secondary replicas to replicators
  var secondaries = Map.empty[ActorRef, ActorRef]
  // the current set of replicators
  var replicators = Set.empty[ActorRef]

  def receive = {
    case JoinedPrimary   => context.become(leader)
    case JoinedSecondary => context.become(replica)
  }

  /* TODO Behavior for  the leader role. */
  val leader: Receive = {
    case Get(key, id) => sender ! GetResult(key, this.kv.get(key), id)
    case op @ Insert(key, value, id) =>
      this.kv += key -> value

      val persist = Persist(key, Some(value), id)
      this.persistAcks += id -> (sender, persist)

      context.system.scheduler.scheduleOnce(1.seconds, self, OneSecondAfterPersistTimeout(id))

      this.persistor ! persist

    case op @ Remove(key, id) =>
      this.kv -= key

      val persist = Persist(key, None, id)
      this.persistAcks += id -> (sender, persist)

      context.system.scheduler.scheduleOnce(1.seconds, self, OneSecondAfterPersistTimeout(id))
      this.persistor ! persist

    case Persisted(key, id) =>
      this.persistAcks.get(id).foreach {
        case (client, Persist(key, valueOption, _)) =>

          this.persistAcks -= id

          if( this.replicators.isEmpty ) {
            client ! OperationAck(id)
          }else {
            context.system.scheduler.scheduleOnce(1.seconds, self, OneSecondAfterReplicateTimeout(id))

            var replicatorAcks = Map.empty[ActorRef, ActorRef]

            //replicate
            this.replicators.foreach{ (replicator)=>

              replicatorAcks += replicator -> client

              replicator ! Replicate(key, valueOption, id)
            }

            this.replicateAcks += id -> replicatorAcks
          }
      }

    case Replicated(key, id) =>
      this.replicateAcks.get(id).foreach { (replicatorAcks)=>
        val client = replicatorAcks(sender)
        val acks = replicatorAcks - sender

        if(acks.isEmpty){
          this.replicateAcks -= id
          client ! OperationAck(id)
        }else{
          this.replicateAcks += id -> acks
        }
      }

    case OneSecondAfterReplicateTimeout(id) =>
      this.replicateAcks.get(id).foreach { (replicatorAcks)=>

        val client = replicatorAcks.head._2

        client ! OperationFailed(id)
        this.replicateAcks -= id
      }

    case OneSecondAfterPersistTimeout(id) =>
      this.persistAcks.get(id).foreach{
        case (client, persist) =>
          client ! OperationFailed(id)
          this.persistAcks -= id
      }

    case RepersistTimeout =>
      this.persistAcks.foreach {
        case (id, (client, persist)) => //retry
          this.persistor ! persist
      }

    case Replicas(replicas) =>
      val drop = this.secondaries.keySet -- replicas
      val nova = replicas.dropWhile( (replica) => replica == self || this.secondaries.contains(replica) )

      log.debug("replicas:{}", replicas)
      log.debug("drop:{}", drop)

      //stop
      drop.foreach{ (replica) =>
        //stop replicator
        val replicator = this.secondaries(replica)
        context.stop(replicator)

        //stop replica
        context.stop(replica)

        //remove
        this.replicators -= replicator
        this.secondaries -= replica

        this.replicateAcks.foreach{
          case (id, acks) =>
            if( acks.contains(replicator) ){
              self.tell(Replicated("None", id), replicator)
            }
        }
      }

      //new
      nova.foreach{ (replica) =>
        val replicator = context.actorOf( Replicator.props(replica) )

        this.replicators += replicator

        this.secondaries += replica -> replicator

        //replicate
        this.kv.foreach{
          case (k, v) => replicator ! Replicate(k, Some(v), 0)
        }

      }

    case _ =>
  }

  //id-> replicator -> sender
  var replicateAcks = Map.empty[Long, Map[ActorRef, ActorRef]]
  //id-> (sender, persist)
  var persistAcks = Map.empty[Long, (ActorRef, Persist)]
  var seq = 0L

  /* TODO Behavior for the replica role. */
  val replica: Receive = {
    case Get(key, id) => sender ! GetResult(key, this.kv.get(key), id)
    case op @ Snapshot(key, valueOption, seq) =>
      if( this.seq < seq ) {
      }else if (this.seq > seq) {
        sender ! SnapshotAck(key, seq)
      }else{
        valueOption match {
          case Some(v) => this.kv += key -> v
          case None => this.kv -= key
        }

        val persist = Persist(key, valueOption, seq)
        this.persistAcks += seq -> (sender, persist)

        this.persistor ! persist

        this.seq += 1L
      }
    case Persisted(key, id) =>
        this.persistAcks.get(id).foreach {
          case (actor, Persist(key, _, _)) =>
            actor ! SnapshotAck(key, id)
            this.persistAcks -= id
        }
    case RepersistTimeout =>
      this.persistAcks.foreach {
        case (id, (actor, persist)) => this.persistor ! persist
      }
    case _ =>
  }

  val persistor = context.actorOf(this.persistenceProps, "persister")

}
