package simulations

import org.scalatest.FunSuite

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner

@RunWith(classOf[JUnitRunner])
class CircuitSuite extends CircuitSimulator with FunSuite {
  val InverterDelay = 1
  val AndGateDelay = 3
  val OrGateDelay = 5

  test("andGate example") {
    val in1, in2, o = new Wire
    andGate(in1, in2, o)
    in1.setSignal(false)
    in2.setSignal(false)
    run

    assert(o.getSignal === false, "and 1")

    in1.setSignal(true)
    run

    assert(o.getSignal === false, "and 2")

    in2.setSignal(true)
    run

    assert(o.getSignal === true, "and 3")
  }

  //
  // to complete with tests for orGate, demux, ...
  //
  test("orGate example") {
    val in1, in2, o = new Wire
    orGate(in1, in2, o)
    in1.setSignal(false)
    in2.setSignal(false)
    run

    assert(o.getSignal === false, "and 1")

    in1.setSignal(true)
    run

    assert(o.getSignal === true, "and 2")

    in2.setSignal(true)
    run

    assert(o.getSignal === true, "and 3")
  }

  test("orGate2 example") {
    val in1, in2, o = new Wire
    orGate2(in1, in2, o)
    in1.setSignal(false)
    in2.setSignal(false)
    run

    assert(o.getSignal === false, "and 1")

    in1.setSignal(true)
    run

    assert(o.getSignal === true, "and 2")

    in2.setSignal(true)
    run

    assert(o.getSignal === true, "and 3")
  }

  def newWires(name:String, n:Int):List[Wire] = n match {
    case 0 => Nil
    case n =>
      val w = new Wire
      //probe(s"${name}(${n-1})", w)
      w :: newWires(name, n - 1 )
  }

  test("demux has no control") {
    val in = new Wire
    val o = newWires("o", 1)

    demux(in, Nil, o)
    in.setSignal(true)
    run
    assert(o.head.getSignal === true, "and 1")

    in.setSignal(false)
    run
    assert(o.head.getSignal === false, "and 2")
  }

  test("demux has a control") {
    val in = new Wire
    val c = newWires("c", 1)
    val o = newWires("o", 2)

    demux(in, c, o)
    in.setSignal(true)
    c(0).setSignal(false)
    run
    println(o.map(_.getSignal).mkString(","))
    assert(o(1).getSignal === true, "and 1")

    c(0).setSignal(true)
    run
    println(o.map(_.getSignal).mkString(","))
    assert(o(0).getSignal === true, "and 2")
   }

  /*
    In C1 C0 | O0 O1 O2 O3
    -------------------------
    0  0  0  | 0  0  0  0
    0  0  1  | 0  0  0  0
    0  1  0  | 0  0  0  0
    0  1  1  | 0  0  0  0
    1  0  0  | 0  0  0  1
    1  0  1  | 0  0  1  0
    1  1  0  | 0  1  0  0
    1  1  1  | 1  0  0  0
   */
  test("demux has two control") {
    val in = new Wire
    val c = newWires("c", 2)
    val o = newWires("o", 4)

    demux(in, c, o)
    in.setSignal(true)
    c(0).setSignal(false)
    c(1).setSignal(false)
    run
    assert(o(3).getSignal === true, "and 1")

    c(0).setSignal(false)
    c(1).setSignal(true)
    run
    println(o.map(_.getSignal).mkString(","))
    assert(o(2).getSignal === true, "and 2")

    c(0).setSignal(true)
    c(1).setSignal(false)
    run
    println(o.map(_.getSignal).mkString(","))
    assert(o(1).getSignal === true, "and 3")

    c(1).setSignal(true)
    c(0).setSignal(true)
    run
    println(o.map(_.getSignal).mkString(","))
    assert(o(0).getSignal === true, "and 4")
   }


}
