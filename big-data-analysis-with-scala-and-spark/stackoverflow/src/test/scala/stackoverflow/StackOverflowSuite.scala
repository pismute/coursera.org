package stackoverflow

import org.scalatest.{FunSuite, BeforeAndAfterAll}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.apache.spark.SparkConf
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.rdd.RDD
import java.io.File

@RunWith(classOf[JUnitRunner])
class StackOverflowSuite extends FunSuite with BeforeAndAfterAll {


  lazy val testObject = new StackOverflow {
    override val langs =
      List(
        "JavaScript", "Java", "PHP", "Python", "C#", "C++", "Ruby", "CSS",
        "Objective-C", "Perl", "Scala", "Haskell", "MATLAB", "Clojure", "Groovy")
    override def langSpread = 50000
    override def kmeansKernels = 45
    override def kmeansEta: Double = 20.0D
    override def kmeansMaxIterations = 120
  }

  test("testObject can be instantiated") {
    val instantiatable = try {
      testObject
      true
    } catch {
      case _: Throwable => false
    }
    assert(instantiatable, "Can't instantiate a StackOverflow object")
  }

  import StackOverflowSuite._
  import StackOverflow._

  test("???") {
    val raw     = rawPostings(sc.parallelize(Sample))
    val grouped = groupedPostings(raw)
    val scored  = scoredPostings(grouped)
    val vectors = vectorPostings(scored)

    println(s"=======${vectors.collect().toList}")
  }

}

object StackOverflowSuite {

  val Sample =
"""1,27233496,,,0,C#
1,23698767,,,9,C#
1,5484340,,,0,C#
2,5494879,,5484340,1,
1,9419744,,,2,Objective-C
1,26875732,,,1,C#
1,9002525,,,2,C++
2,9003401,,9002525,4,
2,9003942,,9002525,1,
2,9005311,,9002525,0,
1,5257894,,,1,Java
1,21984912,,,0,Java
2,21985273,,21984912,0,
1,27398936,,,0,PHP
1,28903923,,,0,PHP
2,28904080,,28903923,0,
1,20990204,,,6,PHP
1,5077978,,,-2,Python
2,5078493,,5077978,4,
2,5078743,,5077978,3,
1,8031082,,,8,Objective-C
2,8031100,,8031082,6,
2,8031125,,8031082,1,
1,16551759,,,9,C++
2,16551822,,16551759,7,
2,16551829,,16551759,1,
2,16551831,,16551759,0,
2,16551845,,16551759,2,
2,16552071,,16551759,1,
1,17826681,,,0,PHP
""".split("\n")

}
