package patmat

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

import patmat.Huffman._

@RunWith(classOf[JUnitRunner])
class HuffmanSuite extends FunSuite {
	trait TestTrees {
		val t1 = Fork(Leaf('a',2), Leaf('b',3), List('a','b'), 5)
		val t2 = Fork(Fork(Leaf('a',2), Leaf('b',3), List('a','b'), 5), Leaf('d',4), List('a','b','d'), 9)
	}


  test("weight of a larger tree") {
    new TestTrees {
      assert(weight(t1) === 5)
    }
  }


  test("chars of a larger tree") {
    new TestTrees {
      assert(chars(t2) === List('a','b','d'))
    }
  }


  test("string2chars(\"hello, world\")") {
    assert(string2Chars("hello, world") === List('h', 'e', 'l', 'l', 'o', ',', ' ', 'w', 'o', 'r', 'l', 'd'))
  }


  test("makeOrderedLeafList for some frequency table") {
    assert(makeOrderedLeafList(List(('t', 2), ('e', 1), ('x', 3))) === List(Leaf('e',1), Leaf('t',2), Leaf('x',3)))
  }


  test("combine of some leaf list") {
    val leaflist = List(Leaf('e', 1), Leaf('t', 2), Leaf('x', 4))
    assert(combine(leaflist) === List(Fork(Leaf('e',1),Leaf('t',2),List('e', 't'),3), Leaf('x',4)))
  }

//  test("combine of some forks") {
//    new TestTrees {
//      println(combine(List(t1, t2)))
//    }
//  }


  test("decode and encode a very short text should be identity") {
    new TestTrees {
      assert(encode(t1)("ab".toList) === List(0, 1))
      assert(decode(t1, List(0, 1)) === List('a', 'b'))
      assert(decode(t1, encode(t1)("ab".toList)) === "ab".toList)

      assert(decode(t2, encode(t2)("abd".toList)) === "abd".toList)
    }
  }

  test("times of some word"){
    assert(times("abcdeabcdabcaba".toList) == List(('e',1), ('a',5), ('b',4), ('c',3), ('d',2)))
  }

//  test("'createCodeTree(someText)' gives an optimal encoding, the number of bits when encoding 'someText' is minimal") {
//    new TestTrees {
//      assert(createCodeTree("aaabbdddd".toList) === t2)
//    }
//  }

  test("singleton of nil"){
    assert(singleton(Nil) == false)
  }

  test("decode secret") {
    assert(decodedSecret.mkString === "huffmanestcool")
  }

  test("build code tables") {
    new TestTrees {
      assert(convert(t1) === List(('a',List(0)), ('b',List(1))))
      assert(convert(t2) === List(('a',List(0, 0)), ('b',List(0, 1)), ('d',List(1))))
    }
  }

  test("encode and quickEncode should be the same") {
    new TestTrees {
      assert(encode(t1)("ab".toList) === quickEncode(t1)("ab".toList))
    }
  }
}
