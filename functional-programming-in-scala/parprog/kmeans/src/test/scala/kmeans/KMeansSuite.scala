package kmeans

import java.util.concurrent._
import scala.collection._
import org.scalatest.FunSuite
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import common._
import scala.math._

object KM extends KMeans
import KM._

@RunWith(classOf[JUnitRunner])
class KMeansSuite extends FunSuite {

  def checkClassify(points: GenSeq[Point], means: GenSeq[Point], expected: GenMap[Point, GenSeq[Point]]) {
    assert(classify(points.par, means.par) == expected,
      s"classify($points, $means) should equal to $expected")
  }

  test("'classify should work for empty 'points' and empty 'means'") {
    val points: GenSeq[Point] = IndexedSeq()
    val means: GenSeq[Point] = IndexedSeq()
    val expected = GenMap[Point, GenSeq[Point]]()
    checkClassify(points, means, expected)
  }

  test("'classify' should work for empty 'points' and 'means' == GenSeq(Point(1,1,1))") {
    val points: GenSeq[Point] = IndexedSeq()
    val mean = new Point(1, 1, 1)
    val means: GenSeq[Point] = IndexedSeq(mean)
    val expected = GenMap[Point, GenSeq[Point]]((mean, GenSeq()))
    checkClassify(points, means, expected)
  }

  test("'classify' should work for 'points' == GenSeq((1, 1, 0), (1, -1, 0), (-1, 1, 0), (-1, -1, 0)) and 'means' == GenSeq((0, 0, 0))") {
    val p1 = new Point(1, 1, 0)
    val p2 = new Point(1, -1, 0)
    val p3 = new Point(-1, 1, 0)
    val p4 = new Point(-1, -1, 0)
    val points: GenSeq[Point] = IndexedSeq(p1, p2, p3, p4)
    val mean = new Point(0, 0, 0)
    val means: GenSeq[Point] = IndexedSeq(mean)
    val expected = GenMap((mean, GenSeq(p1, p2, p3, p4)))
    checkClassify(points, means, expected)
  }

  test("'classify' should work for 'points' == GenSeq((1, 1, 0), (1, -1, 0), (-1, 1, 0), (-1, -1, 0)) and 'means' == GenSeq((1, 0, 0), (-1, 0, 0))") {
    val p1 = new Point(1, 1, 0)
    val p2 = new Point(1, -1, 0)
    val p3 = new Point(-1, 1, 0)
    val p4 = new Point(-1, -1, 0)
    val points: GenSeq[Point] = IndexedSeq(p1, p2, p3, p4)
    val mean1 = new Point(1, 0, 0)
    val mean2 = new Point(-1, 0, 0)
    val means: GenSeq[Point] = IndexedSeq(mean1, mean2)
    val expected = GenMap((mean1, GenSeq(p1, p2)), (mean2, GenSeq(p3, p4)))
    checkClassify(points, means, expected)
  }

  def checkParClassify(points: GenSeq[Point], means: GenSeq[Point], expected: GenMap[Point, GenSeq[Point]]) {
    assert(classify(points.par, means.par) == expected,
      s"classify($points par, $means par) should equal to $expected")
  }

  test("'classify with data parallelism should work for empty 'points' and empty 'means'") {
    val points: GenSeq[Point] = IndexedSeq()
    val means: GenSeq[Point] = IndexedSeq()
    val expected = GenMap[Point,GenSeq[Point]]()
    checkParClassify(points, means, expected)
  }

/*
  def checkConverged(eta: Double, os: GenSeq[Point], ns: GenSeq[Point], expected: Boolean) {
    assert(converged(eta)(os, ns) == expected,
      s"converged($eta)($os, $ns) should equal to $expected")
  }

  test("convergence must true") {
    val os: GenSeq[Point] = GenSeq(new Point(0.0, 0.33, 0.0), new Point(0.0, 10.0, 0.0))
    val ns: GenSeq[Point] = GenSeq(new Point(0.0, 10.0, 0.0), new Point(0.0, 0.33, 0.0))
    val eta: Double = 12.25
    checkConverged(eta, os, ns, true)
  }
*/
/*
  def checkKmean(points: GenSeq[Point], means: GenSeq[Point], eta: Double, expected: GenSeq[Point]) {
    assert(kMeans(points, means, eta) === expected,
      s"kMeans($points, $means, $eta) should equal to $expected")
  }

  test("kMean should work for ...") {
    //val points: GenSeq[Point] = for(i <- 0 until 800000) yield new Point(i, i*2, i*3)
    val points: GenSeq[Point] = GenSeq(new Point(0, 0, 1), new Point(0,0, -1), new Point(0,1,0), new Point(0,10,0))
    val means: GenSeq[Point] = GenSeq(new Point(0, -1, 0), new Point(0, 2, 0))
    //val eta: Double = 12.25
    val eta: Double = 12.25
    val expected: GenSeq[Point] = GenSeq(new Point(0.0, 0.0, 0.0), new Point(0.0, 5.5, 0.0))
    checkKmean(points, means, eta, expected)
  }
  */
}


  
