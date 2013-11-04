import patmat.Huffman._

object workshit {
  makeOrderedLeafList(List(('t', 2), ('e', 1), ('x', 3)))
                                                  //> res0: List[patmat.Huffman.Leaf] = List(Leaf(e,1), Leaf(t,2), Leaf(x,3))
  List(Leaf('e',1), Leaf('t',2), Leaf('x',3))     //> res1: List[patmat.Huffman.Leaf] = List(Leaf(e,1), Leaf(t,2), Leaf(x,3))

  val leaflist = List(Leaf('e', 1), Leaf('t', 2), Leaf('x', 4))
                                                  //> leaflist  : List[patmat.Huffman.Leaf] = List(Leaf(e,1), Leaf(t,2), Leaf(x,4)
                                                  //| )
  combine(leaflist)                               //> res2: List[patmat.Huffman.CodeTree] = List(Fork(Leaf(e,1),Leaf(t,2),List(e, 
                                                  //| t),3), Leaf(x,4))

  until(singleton, combine)(leaflist)             //> res3: patmat.Huffman.CodeTree = Fork(Fork(Leaf(e,1),Leaf(t,2),List(e, t),3),
                                                  //| Leaf(x,4),List(e, t, x),7)

  val trees = createCodeTree( string2Chars("etxtxxx") )
                                                  //> trees  : patmat.Huffman.CodeTree = Fork(Fork(Leaf(e,1),Leaf(t,2),List(e, t),
                                                  //| 3),Leaf(x,4),List(e, t, x),7)


  decode(trees, List(0, 0,1,0,1,0,1,0,1,1,1,1,0,0,0))
                                                  //> res4: List[Char] = List(e, x, t, t, t, x, x, x, e)
  decodedSecret                                   //> res5: List[Char] = List(h, u, f, f, m, a, n, e, s, t, c, o, o, l)
  
  val t1 = Fork(Leaf('a',2), Leaf('b',3), List('a','b'), 5)
                                                  //> t1  : patmat.Huffman.Fork = Fork(Leaf(a,2),Leaf(b,3),List(a, b),5)


  val table = convert( trees )                    //> table  : patmat.Huffman.CodeTable = List((x,List(1)), (t,List(0, 1)), (e,Lis
                                                  //| t(0, 0)))
	"xxte".toList.foldLeft(List[Bit]()){
	  _ ++ codeBits(table)(_)
	}                                         //> res6: List[patmat.Huffman.Bit] = List(1, 1, 0, 1, 0, 0)

  quickEncode(trees)("xxte".toList)               //> res7: List[patmat.Huffman.Bit] = List(1, 1, 0, 1, 0, 0)

}