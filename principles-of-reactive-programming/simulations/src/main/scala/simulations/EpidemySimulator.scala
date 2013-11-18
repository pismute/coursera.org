package simulations

import math.random

class EpidemySimulator extends Simulator {

  def randomBelow(i: Int) = (random * i).toInt

  protected[simulations] object SimConfig {
    //val population: Int = 300
    //val roomRows: Int = 8
    //val roomColumns: Int = 8
    val population: Int = 300
    val roomRows: Int = 8
    val roomColumns: Int = 8

    // to complete: additional parameters of simulation
    val airTrafficRate = 0.01
    val byChosenFewAct = 0.05
    val prevalenceRate = 0.01
    val deadRate = 0.25
    val transmissibilityRete = 0.40
  }

  import SimConfig._

  //val persons: List[Person] = List() // to complete: construct list of persons
  val persons: List[Person] = List.tabulate(population)(new Person(_))

  class Person (val id: Int) {
    var infected = false
    var sick = false
    var immune = false
    var dead = false

    //by Chosen Few Act
    var chosen = false

    // demonstrates random number generation
    var row: Int = randomBelow(roomRows)
    var col: Int = randomBelow(roomColumns)

    def visiblyInfectious:Boolean = sick || dead

    //
    // to complete with simulation logic
    //

    def neighbors = {
      val up = if( row == (roomRows - 1) ) 0 else row + 1
      val down = if( row == 0 ) 7 else row - 1
      val right = if( col == (roomColumns - 1) ) 0 else col + 1
      val left = if( col == 0 ) 7 else col - 1

      (up, col) :: (row, right) :: (down, col) :: (row, left) :: Nil
    }

    def becomesInfected() = {
      //The Chosen Few Act(!chosen)
      //transmissibility rate(transmissibility rate)
      if(!chosen && random < transmissibilityRete) {
        infected = true

        if( infected ) {
          afterDelay(6)(sick = true)

          afterDelay(14){
            dead = random < deadRate

            if(!dead) {
              afterDelay(2){
                immune = true
                sick = false
              }
              afterDelay(4){
                infected = false
                immune = false
              }
            }
          }
        }
      }
    }

    /*
    def withinDays():Int = {
      val days = randomBelow(5) + 1

      //Reduce Mobility Act.
      //The mobility of a visibly infected person is further reduced by half.
      if( this.visiblyInfectious ) days*2
      else days
    }
    */

    def move(row:Int, col:Int) = {
      this.row = row
      this.col = col

      //faq4, may become infected
      persons
      .find(p=> p.row == this.row && p.col == this.col && p.infected)
        .foreach(p=> this.becomesInfected())
    }

    def nextRoomOf(neighbors:List[(Int,Int)]):Option[(Int,Int)] = {
      neighbors.map{ case (row, col) =>
        persons
        .find( p => p.row == row && p.col == col && p.visiblyInfectious == false)
          .map(p=> (row, col) )
      }.flatten match {
        case Nil => None
        case rooms => Some( rooms( randomBelow(rooms.size) ) )
      }
    }

    def tryToMove():Unit = {
      if(!dead) {
        if( random < airTrafficRate ) {

          move( randomBelow(roomRows), randomBelow(roomColumns) )

        }else {

          this.nextRoomOf(this.neighbors)
          .foreach{ case (row, col) => move(row, col) }
        }

        //set next move
        afterDelay(randomBelow(5)+1){tryToMove()}
      }
    }
  }

  def init() {
    persons
    .take( (population * prevalenceRate).toInt )
    .foreach(_.infected = true)

    persons
    .takeRight( (population * byChosenFewAct).toInt )
    .foreach(_.chosen = true)

    persons.foreach{ p=>
      //println(p)
      afterDelay(randomBelow(5)+1){p.tryToMove()}
    }
  }

  init()
}
