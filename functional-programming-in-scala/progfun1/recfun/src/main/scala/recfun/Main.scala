package recfun

import scala.annotation.tailrec

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
  def pascal(c: Int, r: Int): Int =
    if (c == 0 || c == r) 1
    else pascal(c - 1, r - 1) + pascal(c, r - 1);

  /**
    * Exercise 2
    */
  def balance(chars: List[Char]): Boolean = {
    @tailrec
    def b(cs: List[Char], acc: Int = 0): Boolean =
      if (acc < 0) false
      else cs match {
        case Nil => acc == 0
        case c :: rest if c == '(' => b(rest, acc + 1);
        case c :: rest if c == ')' => b(rest, acc - 1);
        case c :: rest => b(rest, acc);
      }

    b(chars);
  }

  /**
    * Exercise 3
    */
  def countChange(money: Int, coins: List[Int]): Int = {
    @tailrec
    def count(tries:List[(Int, List[Int])], acc:Int = 0):Int = tries match {
      case Nil => acc
      case t :: ts => t match {
        case (0, _) => count(ts, acc + 1);
        case (_, Nil) => count(ts, acc);
        case (m, c :: cs) =>
          val newTries = (0 to m/c).map(m - _*c).map((_, cs)).toList

          count(newTries ++ ts, acc)
      }
    }

    count(List((money, coins)))
  }
}
