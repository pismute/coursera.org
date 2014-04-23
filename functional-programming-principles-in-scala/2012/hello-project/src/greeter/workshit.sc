import ex._

object shit {
	val x = new Rational(1, 3)                //> x  : ex.Rational = 1/3
	val y = new Rational(5, 7)                //> y  : ex.Rational = 5/7
	val z = new Rational(3, 2)                //> z  : ex.Rational = 3/2
	x.add(y).mul(z)                           //> res0: ex.Rational = 66/42
  x.add(y).mul(z).compact()                       //> res1: ex.Rational = 11/7
                                                  
  x.neg()                                         //> res2: ex.Rational = -1/3
  x.sub(y).mul(z)                                 //> res3: ex.Rational = -24/42
  x.sub(y).sub(z)                                 //> res4: ex.Rational = -79/42
}