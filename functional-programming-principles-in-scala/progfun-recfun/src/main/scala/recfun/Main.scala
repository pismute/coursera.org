package recfun
import common._

object Main {
  def main(args: Array[String]) {
    println("Pascal's Triangle")
    for (row <- 0 to 10) {
      for (col <- 0 to row)
        print(pascal(col, row) + " ")
      println()
    }
  }

  /**
   * Exercise 1
   * 
   * reference
   *   http://stackoverflow.com/questions/1763954/c-pascals-triangle
   *   http://www.mathsisfun.com/pascals-triangle.html
   */
  def pascal(c: Int, r: Int): Int = 
    if( c == 0 || c == r ) 1 
    else pascal(c-1, r-1) + pascal(c, r -1)

  /**
   * Exercise 2
   * 
   * reference
   *   http://stackoverflow.com/questions/2711032/basic-recursion-check-balanced-parenthesis
   */
  def balance(chars: List[Char]): Boolean = {
    def bal(input: List[Char], stack: Int ): Boolean = {
      if( input.isEmpty )
        stack == 0
      else if(input.head == '(')
        bal(input.slice(1, input.length), stack + 1)
      else if(input.head == ')') {
        if(stack < 1)
          false
        else
          bal(input.slice(1, input.length), stack - 1)
      } else

      bal(input.slice(1, input.length), stack)
    }  

    bal(chars, 0)
  }

  /**
   * Exercise 3
   * 
   * reference
   *   http://slideguitarist.blogspot.kr/2006/08/change-counting-algorithm-tdd.html
   */
  def countChange(money: Int, coins: List[Int]): Int = {
    def sum(xs: IndexedSeq[Int]): Int = (0 /: xs)(_ + _)
    def count(money: Int, coins: List[Int]): Int = {
      if( money == 0 )
        1
      else if( coins.isEmpty )
        0
      else if( coins.size == 1 && ( money % coins(0) ) == 0 )
        1
      else {
      val currentCoin = coins(0)
      val remainingCoins = coins.slice(1, coins.size )
      val currentPossibilities = 
        for(i <- 0 until money / currentCoin + 1)
            yield currentCoin * i

        sum( for(p <- currentPossibilities) yield count( money - p, remainingCoins ) )
      }
    }

    count(money, coins)
  }
}
