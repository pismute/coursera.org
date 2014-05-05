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
   */
  def pascal(c: Int, r: Int): Int = (c, r) match {
    case (0, _) => 1
    case (cc, rr) if cc < 0 || rr < 0 => 0
    case (cc, rr) if cc == rr => 1
    case n => pascal(c-1, r-1) + pascal(c, r-1)
  }

  /**
   * Exercise 2
   */
  def balance(chars: List[Char]): Boolean = {
    def bal(open:Long, c:List[Char]):Boolean = (open,c) match {
      case (0, Nil ) => true
      case (_, Nil ) => false
      case (0, cc :: rest) if cc == ')' => false
      case (n, cc :: rest) if cc == '(' => bal(n+1, rest)
      case (n, cc :: rest) if cc == ')' => bal(n-1, rest)
      case (_, cc :: rest) => bal(open, rest)
    }

    bal(0, chars)
  }

  /**
   * Exercise 3
   */
  def countChange(money: Int, coins: List[Int]): Int = {
    def count(money:Int, cs:List[Int]):Int = (money, cs) match {
      case (0, Nil) => 1
      case (_, Nil) => 0
      case (m, c :: rest) =>
        (0 to money/c)
        .map(_*c)
        .map( (possible)=> count(m - possible, rest) )
        .sum
    }

    count(money, coins)
  }
}
