/**
 * Copyright (C) 2009-2013 Typesafe Inc. <http://www.typesafe.com>
 */
package actorbintree

import akka.actor._
import scala.collection.immutable.Queue
import akka.event.LoggingReceive

object BinaryTreeSet {

  trait Operation {
    def requester: ActorRef
    def id: Int
    def elem: Int
  }

  trait OperationReply {
    def id: Int
  }

  /** Request with identifier `id` to insert an element `elem` into the tree.
    * The actor at reference `requester` should be notified when this operation
    * is completed.
    */
  case class Insert(requester: ActorRef, id: Int, elem: Int) extends Operation

  /** Request with identifier `id` to check whether an element `elem` is present
    * in the tree. The actor at reference `requester` should be notified when
    * this operation is completed.
    */
  case class Contains(requester: ActorRef, id: Int, elem: Int) extends Operation

  /** Request with identifier `id` to remove the element `elem` from the tree.
    * The actor at reference `requester` should be notified when this operation
    * is completed.
    */
  case class Remove(requester: ActorRef, id: Int, elem: Int) extends Operation

  /** Request to perform garbage collection*/
  case object GC

  /** Holds the answer to the Contains request with identifier `id`.
    * `result` is true if and only if the element is present in the tree.
    */
  case class ContainsResult(id: Int, result: Boolean) extends OperationReply

  /** Message to signal successful completion of an insert or remove operation. */
  case class OperationFinished(id: Int) extends OperationReply

}


class BinaryTreeSet extends Actor {
  import BinaryTreeSet._
  import BinaryTreeNode._

  def createRoot: ActorRef = context.actorOf(BinaryTreeNode.props(0, initiallyRemoved = true))

  var root = createRoot

  // optional
  var pendingQueue = Queue.empty[Operation]

  // optional
  def receive = normal

  // optional
  /** Accepts `Operation` and `GC` messages. */
  val normal: Receive = {
    case op:Operation => root ! op
    case GC =>
      val oldRoot = root
      root = createRoot
      context.become( this.garbageCollecting(root) )

      oldRoot ! CopyTo(root)
  }

  // optional
  /** Handles messages while garbage collection is performed.
    * `newRoot` is the root of the new binary tree where we want to copy
    * all non-removed elements into.
    */
  def garbageCollecting(newRoot: ActorRef): Receive = {
    case op:Operation => this.pendingQueue :+= op
    case CopyFinished =>
      this.pendingQueue.foreach( root ! _ )
      this.pendingQueue = Queue.empty[Operation]

      context.become(normal)
  }

}

object BinaryTreeNode {
  trait Position

  case object Left extends Position
  case object Right extends Position

  case class CopyTo(treeNode: ActorRef)
  case object CopyFinished

  def props(elem: Int, initiallyRemoved: Boolean = false) = Props(classOf[BinaryTreeNode],  elem, initiallyRemoved)
}

class BinaryTreeNode(val elem: Int, initiallyRemoved: Boolean) extends Actor with ActorLogging{
  import BinaryTreeNode._
  import BinaryTreeSet._

  var subtrees = Map[Position, ActorRef]()
  var removed = initiallyRemoved

  // optional
  def receive = normal

  def move(e:Int) =
    if( e < elem ) Some(Left)
    else if( e > elem) Some(Right)
    else if ( removed ) Some(Left)
    else None

  // optional
  /** Handles `Operation` messages and `CopyTo` requests. */
  val normal: Receive = {
    case op @ Insert(requester, id, e) =>
      move(e) match {
        case Some(pos) =>
          subtrees.get(pos) match {
            case Some(actor) => actor ! op
            case None =>
              subtrees += pos -> context.actorOf(BinaryTreeNode.props(e))
              requester ! OperationFinished(id: Int)
          }
        case None => requester ! OperationFinished(id: Int)
      }
    case op @ Remove(requester, id, e) =>
      move(e) match {
        case Some(pos) =>
          subtrees.get(pos) match {
            case Some(actor) => actor ! op
            case None => None
          }
        case None =>
          removed = true
          requester ! OperationFinished(id: Int)
      }
    case op @ Contains(requester, id, e) =>
      move(e) match {
        case Some(pos) =>
          subtrees.get(pos) match {
            case Some(actor) => actor ! op
            case None => requester ! ContainsResult(id, false)
          }
        case None => requester ! ContainsResult(id, true)
      }
    case op @ CopyTo(root) =>
      if( !this.removed ) root ! Insert(self, 1, this.elem)

      if( this.subtrees.isEmpty ){
        sender ! CopyFinished
      }else {
        val children = this.subtrees.values.toSet

        children.foreach( _ ! op )

        context.become( this.copying( sender, children, this.removed == false ) )
      }
  }

  // optional
  /** `expected` is the set of ActorRefs whose replies we are waiting for,
    * `insertConfirmed` tracks whether the copy of this node to the new tree has been confirmed.
    */
  def copying(parent:ActorRef, expected: Set[ActorRef], insertConfirmed: Boolean): Receive = {
    case CopyFinished =>
      val newExpected = expected - sender

      if( newExpected.isEmpty ) {
        context.become( normal )

        parent ! CopyFinished
      } else {
        context.become( this.copying(parent, newExpected, insertConfirmed) )
      }
  }

}
