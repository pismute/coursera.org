package quickcheck

import common._
import scala.annotation.tailrec
import org.scalacheck._
import Arbitrary._
import Gen._
import Prop._

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap{

  property("min1") = forAll { a: Int =>
    val h = insert(a, empty)
    findMin(h) == a
  }

  property("gen1") = forAll { (h: H) =>
    val m = if (isEmpty(h)) 0 else findMin(h)
    findMin(insert(m, h))==m
  }

  lazy val genHeap: Gen[H] = for {
    a <- arbitrary[A]
    h <- oneOf(value(empty), genHeap)
  } yield meld(insert(a, empty), h)

  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)


  val genIntPair = for {
    v1 <- arbitrary[Int]
    v2 <- arbitrary[Int]
  } yield (v1, v2)

  property("hint1") = forAll(genIntPair) { case (a, b) =>
    val h = insert(a, insert(b, empty) )
    findMin(h) == scala.math.min(a, b)
  }

  property("hint2") = forAll { a: Int =>
    deleteMin( insert(a, empty) ) == empty
  }

  property("hint3") = forAll(genHeap) { h =>
    def rebuild(o:H, n:H = empty):H = o match {
        val oo = deleteMin(o)
        rebuild(oo, meld(n, insert(findMin(o), oo)))
    }

    findMin( h ) == findMin( rebuild(h) )
  }

  val genHeapPair = for {
    v1 <- genHeap
    v2 <- genHeap
  } yield (v1, v2)

  property("hint4") = forAll(genHeapPair) { case (h1, h2) =>
    val m1 = findMin(h1)
    val m2 = findMin(h2)

    findMin( meld(h1, h2) ) == scala.math.min(m1, m2)
  }

}
