package quickcheck

import common._
import org.scalacheck.{Arbitrary, _}
import Arbitrary._
import Gen._
import Prop._

abstract class QuickCheckHeap extends Properties("Heap") with IntHeap {

  lazy val genHeap: Gen[H] = for {
    x <- arbitrary[Int]
    node <- oneOf(const(empty), genHeap)
  } yield insert(x, node)


  implicit lazy val arbHeap: Arbitrary[H] = Arbitrary(genHeap)

  property("gen1") = forAll { (h: H) =>
    val m = if (isEmpty(h)) 0 else findMin(h)
    findMin(insert(m, h)) == m
  }

  property("hint1") = forAll { (a: Int, b: Int) =>
    val max = a.max(b)
    val h = insert(a, insert(b, empty))

    findMin(deleteMin(h)) == max
  }

  property("hint2") = forAll { (a: Int, b:Int) =>
    deleteMin(deleteMin(insert(b, insert(a, empty)))) == empty
  }

  property("hint3") = forAll { h: H =>
    def sorted(h: H, acc: Int = Int.MinValue): Boolean = {
      if( isEmpty(h) ) true
      else {
        val m = findMin(h)
        if ( m >= acc ) sorted(deleteMin(h), m) else false
      }
    }

    sorted(h)
  }

  property("hint4") = forAll { (h1: H, h2:H) =>
    val min = findMin(h1).min(findMin(h2))

    findMin(meld(h1, h2)) == min
  }

  property("gen4") = forAll { (a: Int, b: Int, c: Int) =>
    val l = List(a, b, c).sorted

    val h = insert(a, insert(b, insert(c, empty)))

    findMin(deleteMin(h)) == l(1)
  }

}
