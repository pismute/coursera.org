package ex

import streams.Bloxorz._
import streams.Bloxorz.Level1._

object workshit {
  Stream((Block(Pos(2,1),Pos(3,1)),List(Down))).tail.minBy{case (block, moves) => moves.length }._2
                                                  //> java.lang.UnsupportedOperationException: empty.minBy
                                                  //| 	at scala.collection.TraversableOnce$class.minBy(TraversableOnce.scala:21
                                                  //| 4)
                                                  //| 	at scala.collection.immutable.Stream.minBy(Stream.scala:186)
                                                  //| 	at ex.workshit$$anonfun$main$1.apply$mcV$sp(ex.workshit.scala:7)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$$anonfun$$exe
                                                  //| cute$1.apply$mcV$sp(WorksheetSupport.scala:76)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$.redirected(W
                                                  //| orksheetSupport.scala:65)
                                                  //| 	at org.scalaide.worksheet.runtime.library.WorksheetSupport$.$execute(Wor
                                                  //| ksheetSupport.scala:75)
                                                  //| 	at ex.workshit$.main(ex.workshit.scala:6)
                                                  //| 	at ex.workshit.main(ex.workshit.scala)
  List(1, 2, 3).head
  
  level
    startBlock
  goal
  //neighborsWithHistory(Block(Pos(2,3),Pos(3,3)), List(Right, Right, Down)) mkString "\n"
  //newNeighborsOnly(neighborsWithHistory(startBlock, Nil), Set(startBlock)) mkString "\n"
  Block(Pos(2,1),Pos(3,1)).left
  terrain(Pos(3,0))
  
  pathsToGoal mkString "\n"
  //solution



/*
  def terrainFunction(levelVector: Vector[Vector[Char]]): Pos => Boolean = {
    def func(pos:Pos):Boolean =
      levelVector.length - pos.x > 0 && levelVector(pos.x).length - pos.y > 0
    
    func
  }

  val tfunc = terrainFunction(Vector(Vector('S', 'T'), Vector('o', 'o'), Vector('o', 'o')))
  tfunc(Pos(0,0))
  tfunc(Pos(3,0))

  def findChar(c: Char, levelVector: Vector[Vector[Char]]): Pos = {
      val ret = for{
        level <- levelVector
        tile <- level
        if tile == c
      } yield Pos(level.indexOf(tile), levelVector.indexOf(level))

      if ( ret.length > 0 ) {
        ret(0)
      }else{
        null
      }
    }



  findChar('T', Vector(Vector('S', 'T'), Vector('o', 'o'), Vector('o', 'o')))
  val b = Block(Pos(1,1),Pos(1,1))
  b.isStanding
  b.legalNeighbors
  //.foldLeft(Stream[(Block, List[Move])]())((accu, x)=> (x._1, x._2 :: history) #:: accu )
  neighborsWithHistory(Block(Pos(1,1),Pos(1,1)), List(Left,Up)).toSet ==
	  Set(
		  (Block(Pos(1,2),Pos(1,3)), List(Right,Left,Up)),
		  (Block(Pos(2,1),Pos(3,1)), List(Down,Left,Up))
		)
 
*/
}