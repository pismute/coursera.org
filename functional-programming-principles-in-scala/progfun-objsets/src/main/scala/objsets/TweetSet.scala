package objsets

import common._
import TweetReader._
import scala.annotation.tailrec

class Tweet(val user: String, val text: String, val retweets: Int) {

  override def toString: String =
    "User: " + user + "\n" +
    "Text: " + text + " [" + retweets + "]"

}

abstract class TweetSet {

  /** This method takes a predicate and returns a subset of all the elements
   *  in the original set for which the predicate is true.
   */
  def filter(p: Tweet => Boolean): TweetSet = this.filter0(p, new Empty())
  def filter0(p: Tweet => Boolean, accu: TweetSet): TweetSet
  def union(that: TweetSet): TweetSet = union0(this, that)
  @tailrec
  private def union0(left: TweetSet, right:TweetSet): TweetSet =
    if( left.isEmpty ) right
    else union0( left.tail, right.incl(left.head) )
    
  def size() = size0(this)
  def size0(set: TweetSet): Int = {
    if (set.isEmpty) 0
    else 1 + size0(set.tail)
  }
  
  // Hint: the method "remove" on TweetSet will be very useful.
  def ascendingByRetweet: Trending = ascendingByRetweet0( new EmptyTrending() )

  @tailrec
  private def ascendingByRetweet0(accu:Trending, curr:TweetSet = this): Trending =
    if( curr.isEmpty ) accu
    else {
      val tweet = curr.findMin
      ascendingByRetweet0( accu + tweet, curr.remove(tweet) )
    }
  
  def findMax0(curr: Tweet): Tweet =
    if (this.isEmpty) curr
    else if (this.head.retweets > curr.retweets) this.tail.findMax0(this.head)
    else this.tail.findMax0(curr)

  def findMax: Tweet =
    this.tail.findMax0(this.head)
  
  // The following methods are provided for you, and do not have to be changed
  // -------------------------------------------------------------------------
  def incl(x: Tweet): TweetSet
  def contains(x: Tweet): Boolean
  def isEmpty: Boolean
  def head: Tweet
  def tail: TweetSet

  /** This method takes a function and applies it to every element in the set.
   */
  def foreach(f: Tweet => Unit): Unit = {
    if (!this.isEmpty) {
      f(this.head)
      this.tail.foreach(f)
    }
  }

  def remove(tw: Tweet): TweetSet

  def findMin0(curr: Tweet): Tweet =
    if (this.isEmpty) curr
    else if (this.head.retweets < curr.retweets) this.tail.findMin0(this.head)
    else this.tail.findMin0(curr)

  def findMin: Tweet =
    this.tail.findMin0(this.head)
  // -------------------------------------------------------------------------
}

class Empty extends TweetSet {

  def filter0(p: Tweet => Boolean, accu: TweetSet): TweetSet = this

  // The following methods are provided for you, and do not have to be changed
  // -------------------------------------------------------------------------
  def contains(x: Tweet): Boolean = false
  def incl(x: Tweet): TweetSet = new NonEmpty(x, new Empty, new Empty)
  def isEmpty = true
  def head = throw new Exception("Empty.head")
  def tail = throw new Exception("Empty.tail")
  def remove(tw: Tweet): TweetSet = this
  // -------------------------------------------------------------------------
}

class NonEmpty(elem: Tweet, left: TweetSet, right: TweetSet) extends TweetSet {

  def filter0(p: Tweet => Boolean, accu: TweetSet): TweetSet = {
    val next = if( p( this.head ) ) accu.incl(this.head) else accu
    
    if (this.tail.isEmpty) next
    else this.tail.filter0(p, next)
  }
  
  // The following methods are provided for you, and do not have to be changed
  // -------------------------------------------------------------------------
  def contains(x: Tweet): Boolean =
    if (x.text < elem.text) left.contains(x)
    else if (elem.text < x.text) right.contains(x)
    else true

  def incl(x: Tweet): TweetSet = {
    if (x.text < elem.text) new NonEmpty(elem, left.incl(x), right)
    else if (elem.text < x.text) new NonEmpty(elem, left, right.incl(x))
    else this
  }

  def isEmpty = false
  def head = if (left.isEmpty) elem else left.head
  def tail = if (left.isEmpty) right else new NonEmpty(elem, left.tail, right)

  def remove(tw: Tweet): TweetSet =
    if (tw.text < elem.text) new NonEmpty(elem, left.remove(tw), right)
    else if (elem.text < tw.text) new NonEmpty(elem, left, right.remove(tw))
    else left.union(right)
  // -------------------------------------------------------------------------
}


/** This class provides a linear sequence of tweets.
 */
abstract class Trending {
  def + (tw: Tweet): Trending
  def head: Tweet
  def tail: Trending
  def isEmpty: Boolean
  def foreach(f: Tweet => Unit): Unit = {
    if (!this.isEmpty) {
      f(this.head)
      this.tail.foreach(f)
    }
  }
  
  def size() = size0(this)
  def size0(set: Trending): Int = {
    if (set.isEmpty) 0
    else 1 + size0(set.tail)
  }
}

class EmptyTrending extends Trending {
  def + (tw: Tweet) = new NonEmptyTrending(tw, new EmptyTrending)
  def head: Tweet = throw new Exception
  def tail: Trending = throw new Exception
  def isEmpty: Boolean = true
  override def toString = "EmptyTrending"
}

class NonEmptyTrending(elem: Tweet, next: Trending) extends Trending {
  /** Appends tw to the end of this sequence.
   */
  def + (tw: Tweet): Trending =
    new NonEmptyTrending(elem, next + tw)
  def head: Tweet = elem
  def tail: Trending = next
  def isEmpty: Boolean = false
  override def toString =
    "NonEmptyTrending(" + elem.retweets + ", " + next + ")"
}

object GoogleVsApple {
  val google = List("android", "Android", "galaxy", "Galaxy", "nexus", "Nexus")
  
  val apple = List("ios", "iOS", "iphone", "iPhone", "ipad", "iPad")

  val googleTweets: TweetSet = 
    TweetReader.allTweets.filter{ tweet =>
      google.exists( tweet.text.contains(_) )
    }

  val appleTweets: TweetSet = 
    TweetReader.allTweets.filter{ tweet =>
      apple.exists( tweet.text.contains(_) )
    }
  
  // Q: from both sets, what is the tweet with highest #retweets?
  val trending: Trending = trending0(new EmptyTrending(), googleTweets.union(appleTweets))
  @tailrec
  private def trending0(accu:Trending, curr:TweetSet): Trending =
    if( curr.isEmpty ) accu
    else {
      val tweet = curr.findMin
      
      trending0( accu + tweet, curr.remove(tweet) )
    }
  
  def ascending() = trending0(new EmptyTrending(), TweetReader.allTweets)
}

object Main extends App {
  // Some help printing the results:
  println("RANKED:")
  println( GoogleVsApple.trending.size() )
  
  //GoogleVsApple.descending foreach println
  //GoogleVsApple.trending foreach println
}
