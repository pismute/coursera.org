package ex.step1

object weekshit {
  val problem = new Pouring(Vector(4, 7, 9))      //> problem  : ex.step1.Pouring = ex.step1.Pouring@f0756d
  problem.moves                                   //> res0: scala.collection.immutable.IndexedSeq[Product with Serializable with ex
                                                  //| .step1.weekshit.problem.Move] = Vector(Empty(0), Empty(1), Empty(2), Fill(0),
                                                  //|  Fill(1), Fill(2), Pour(0,1), Pour(0,2), Pour(1,0), Pour(1,2), Pour(2,0), Pou
                                                  //| r(2,1))
  problem.pathSets.take(3).toList                 //> res1: List[Set[ex.step1.weekshit.problem.Path]] = List(Set(--> Vector(0, 0, 
                                                  //| 0)), Set(Pour(0,1)--> Vector(4, 0, 0), Pour(2,0)--> Vector(0, 0, 9), Pour(0,
                                                  //| 2)--> Vector(4, 0, 0), Fill(1)--> Vector(0, 7, 0), Fill(2)--> Vector(0, 0, 9
                                                  //| ), Pour(2,1)--> Vector(0, 0, 9), Empty(2)--> Vector(0, 0, 0), Pour(1,2)--> V
                                                  //| ector(0, 7, 0), Fill(0)--> Vector(4, 0, 0), Empty(1)--> Vector(0, 0, 0), Pou
                                                  //| r(1,0)--> Vector(0, 7, 0), Empty(0)--> Vector(0, 0, 0)), Set(Pour(0,2) Empty
                                                  //| (1)--> Vector(4, 0, 0), Empty(0) Pour(0,1)--> Vector(4, 0, 0), Empty(1) Pour
                                                  //| (0,2)--> Vector(4, 0, 0), Fill(1) Pour(0,1)--> Vector(4, 7, 0), Pour(2,1) Po
                                                  //| ur(1,0)--> Vector(0, 7, 9), Empty(1) Pour(2,0)--> Vector(0, 0, 9), Empty(1) 
                                                  //| Pour(2,1)--> Vector(0, 0, 9), Empty(1) Fill(0)--> Vector(4, 0, 0), Empty(0) 
                                                  //| Empty(2)--> Vector(0, 0, 0), Pour(1,0) Pour(1,2)--> Vector(0, 0, 7), Pour(2,
                                                  //| 0) Pour(1,2)--> Vector(0, 7, 9), Pour(1,2) Pour(0,1)--> Vector(4, 7, 0), Fil
                                                  //| l(0) Fill(1)--> Vector(4
                                                  //| Output exceeds cutoff limit.
  problem.solutions(2)                            //> res2: Stream[ex.step1.weekshit.problem.Path] = Stream(Fill(2) Pour(2,1)--> V
                                                  //| ector(0, 7, 2), ?)
}