package kvstore

import akka.actor.Props
import akka.actor.Actor
import akka.actor.ActorRef
import scala.concurrent.duration._
import akka.event.LoggingReceive
import akka.actor.ActorLogging
import akka.util.Timeout

object Replicator {
  case class Replicate(key: String, valueOption: Option[String], id: Long)
  case class Replicated(key: String, id: Long)

  case class Snapshot(key: String, valueOption: Option[String], seq: Long)
  case class SnapshotAck(key: String, seq: Long)

  def props(replica: ActorRef): Props = Props(new Replicator(replica))
}

class Replicator(val replica: ActorRef) extends Actor with ActorLogging{
  import Replicator._
  import Replica._
  import context.dispatcher

  /*
   * The contents of this actor is just a suggestion, you can implement it in any way you like.
   */

  // map from sequence number to pair of sender and request
  var acks = Map.empty[Long, (ActorRef, Replicate)]
  // a sequence of not-yet-sent snapshots (you can disregard this if not implementing batching)
  var pending = Vector.empty[Snapshot]

  var _seqCounter = 0L
  def nextSeq = {
    val ret = _seqCounter
    _seqCounter += 1
    ret
  }

  override def preStart() = {
    //To allow for batching (see above) we will assume that a lost Snapshot message will lead to a resend at most 200 milliseconds after the Replicate request was received
    context.system.scheduler.schedule(100.millis, 100.millis, self, Timeout)
  }

  /* TODO Behavior for the Replicator. */
  def receive: Receive = {
    case op @ Replicate(key, valueOption, id) =>
      val seq = nextSeq

      this.acks += seq -> (sender, op)

      replica ! Snapshot(key, valueOption, seq)

    case SnapshotAck(key, seq) =>

      this.acks.get(seq).foreach{
        case (actor, op) =>
          actor ! Replicated(op.key, op.id)
          this.acks -= seq
      }

    case Timeout =>

      this.acks.foreach {
        case (seq, (actor, Replicate(key, valueOption, id))) =>
          replica ! Snapshot(key, valueOption, seq)
      }

    case _ =>
  }

}
