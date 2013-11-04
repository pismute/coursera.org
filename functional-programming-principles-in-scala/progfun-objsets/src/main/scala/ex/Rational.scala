package ex

class Rational(x: Int, y: Int) {
  private def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
  private val g = gcd(x, y)

  def numer = x/g
  def denom = y/g
  
      
  def +(r: Rational) = 
    new Rational(numer * r.denom + r.numer * denom,
      denom * r.denom)

  def *(r: Rational) =
    new Rational(numer * r.numer,
      denom * r.denom)
  
  override def toString = numer + "/" + denom
  
  def -(r: Rational) = 
    new Rational(numer * r.denom - r.numer * denom,
      denom * r.denom)
  
  def neg() = new Rational(-numer, denom)
  
  def less(that: Rational) =
    this.numer * that.denom < that.numer * this.denom
}