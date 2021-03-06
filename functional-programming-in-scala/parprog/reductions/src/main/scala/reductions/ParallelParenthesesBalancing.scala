package reductions

import scala.annotation._
import org.scalameter._
import common._

object ParallelParenthesesBalancingRunner {

  @volatile var seqResult = false

  @volatile var parResult = false

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 40,
    Key.exec.maxWarmupRuns -> 80,
    Key.exec.benchRuns -> 120,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val length = 100000000
    val chars = new Array[Char](length)
    val threshold = 10000
    val seqtime = standardConfig measure {
      seqResult = ParallelParenthesesBalancing.balance(chars)
    }
    println(s"sequential result = $seqResult")
    println(s"sequential balancing time: $seqtime ms")

    val fjtime = standardConfig measure {
      parResult = ParallelParenthesesBalancing.parBalance(chars, threshold)
    }
    println(s"parallel result = $parResult")
    println(s"parallel balancing time: $fjtime ms")
    println(s"speedup: ${seqtime / fjtime}")
  }
}

object ParallelParenthesesBalancing {

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def balance(chars: Array[Char]): Boolean = {
    def balanceAcc(chars: List[Char], acc: Int = 0): Boolean =
      if (acc < 0) false
      else chars match {
        case Nil => acc == 0
        case c :: cs if c == '(' => balanceAcc(cs, acc + 1)
        case c :: cs if c == ')' => balanceAcc(cs, acc - 1)
        case c :: cs => balanceAcc(cs, acc)
      }

    balanceAcc(chars.toList)
  }

  /** Returns `true` iff the parentheses in the input `chars` are balanced.
   */
  def parBalance(chars: Array[Char], threshold: Int): Boolean = {

    def traverse(idx: Int, until: Int, arg1: Int = 0, arg2: Int = 0) : (Int, Int) = {
      if(idx == until) (arg1, arg2)
      else {
        chars(idx) match {
          case '(' => traverse(idx+1, until, arg1, arg2 + 1)
          case ')' => traverse(idx+1, until, if(arg2 == 0) -1 else arg1, arg2 - 1)
          case _ => traverse(idx+1, until, arg1, arg2)
        }
      }
    }

    def reduce(from: Int, until: Int) : (Int, Int) = {
      if(until - from <= threshold) traverse(from, until)
      else {
        val mid = from + (until-from)/2
        val (l, r) = parallel(reduce(from, mid), reduce(mid, until))

        (if(l._1 == -1) -1 else if(l._2 + r._2 < 0) -1 else 0, l._2 + r._2)
      }
    }

    reduce(0, chars.length) == (0, 0)
  }

  // For those who want more:
  // Prove that your reduction operator is associative!

}
