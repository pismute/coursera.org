package reductions

import org.scalameter._
import common._

object ParallelCountChangeRunner {

  @volatile var seqResult = 0

  @volatile var parResult = 0

  val standardConfig = config(
    Key.exec.minWarmupRuns -> 20,
    Key.exec.maxWarmupRuns -> 40,
    Key.exec.benchRuns -> 80,
    Key.verbose -> true
  ) withWarmer(new Warmer.Default)

  def main(args: Array[String]): Unit = {
    val amount = 250
    val coins = List(1, 2, 5, 10, 20, 50)
    val seqtime = standardConfig measure {
      seqResult = ParallelCountChange.countChange(amount, coins)
    }
    println(s"sequential result = $seqResult")
    println(s"sequential count time: $seqtime ms")

    def measureParallelCountChange(threshold: ParallelCountChange.Threshold): Unit = {
      val fjtime = standardConfig measure {
        parResult = ParallelCountChange.parCountChange(amount, coins, threshold)
      }
      println(s"parallel result = $parResult")
      println(s"parallel count time: $fjtime ms")
      println(s"speedup: ${seqtime / fjtime}")
    }

    measureParallelCountChange(ParallelCountChange.moneyThreshold(amount))
    measureParallelCountChange(ParallelCountChange.totalCoinsThreshold(coins.length))
    measureParallelCountChange(ParallelCountChange.combinedThreshold(amount, coins))
  }
}

object ParallelCountChange {

  /** Returns the number of ways change can be made from the specified list of
   *  coins for the specified amount of money.
   */
  def countChange(money: Int, coins: List[Int]): Int = {
    def countChangeAcc(pairs: List[(Int, List[Int], List[Int])], acc:List[List[Int]] = Nil): List[List[Int]] = {
      pairs match {
        case Nil => acc
        case p :: ps =>
          p match {
            case (0, cs, footprint) => countChangeAcc(ps, footprint :: acc)
            case (_, Nil, _) => countChangeAcc(ps, acc)
            case (m, _, _) if m < 0 => countChangeAcc(ps, acc)
            case (m, c :: cs, fp) =>
              val nps = (0 to m/c)
                .map(m - _*c)
                .map((_, cs, c :: fp))
                .toList ++ ps

              countChangeAcc(nps, acc)
          }
      }
    }

    countChangeAcc(List((money, coins, Nil))).size
  }

  type Threshold = (Int, List[Int]) => Boolean

  /** In parallel, counts the number of ways change can be made from the
   *  specified list of coins for the specified amount of money.
   */
  def parCountChange(money: Int, coins: List[Int], threshold: Threshold): Int = {
    if( money == 0 ) 1
    else if (money < 0) 0
    else if( threshold(money, coins) ) countChange(money, coins)
    else coins match {
      case Nil => 0
      //case c :: Nil => if(money-c == 0) 1 else 0
      case c :: cs if money-c == 0 => 1
      case c :: cs if money-c < 0 => 0
      case c :: cs =>
        val (a, b) = parallel(parCountChange(money - c, coins, threshold), parCountChange(money, cs, threshold))

        a + b
    }
  }

  /** Threshold heuristic based on the starting money. */
  def moneyThreshold(startingMoney: Int): Threshold = {
    val t = startingMoney * 2 / 3

    (m, cs) => m <= t
  }

  /** Threshold heuristic based on the total number of initial coins. */
  def totalCoinsThreshold(totalCoins: Int): Threshold = {
    val t = totalCoins * 2/3

    (m, cs) => cs.size <= t
  }

  /** Threshold heuristic based on the starting money and the initial list of coins. */
  def combinedThreshold(startingMoney: Int, allCoins: List[Int]): Threshold = {
    val t = startingMoney * allCoins.size / 2

    (m, cs) => m*cs.size <= t
  }
}
