package calculator

object Polynomial {
  def computeDelta(a: Signal[Double], b: Signal[Double],
      c: Signal[Double]): Signal[Double] = {
    Signal(Math.pow(b(), 2) - 4*a()*c())
  }

  def computeSolutions(a: Signal[Double], b: Signal[Double],
      c: Signal[Double], delta: Signal[Double]): Signal[Set[Double]] = {
    Signal{
      if(delta() < 0) Set()
      else {
        val d = Math.pow(delta(), 0.5)
        Set((-d - b()) / (2*a()), (d - b()) / (2 * a()))
      }
    }
  }

  def compute(a: Signal[Double], b: Signal[Double], c: Signal[Double]): Signal[Set[Double]] = {
    computeSolutions(a, b, c, computeDelta(a, b, c))
  }
}
