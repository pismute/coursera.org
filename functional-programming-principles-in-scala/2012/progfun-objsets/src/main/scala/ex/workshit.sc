import ex._
import objsets._

object workshit {
  val x = new Rational(1, 3)                      //> x  : ex.Rational = 1/3
  val y = new Rational(5, 7)                      //> y  : ex.Rational = 5/7
  val z = new Rational(3, 2)                      //> z  : ex.Rational = 3/2
  x + y * z                                       //> res0: ex.Rational = 59/42
  x + y * z                                       //> res1: ex.Rational = 59/42
                                                  
  x.neg()                                         //> res2: ex.Rational = 1/-3
  x - y                                           //> res3: ex.Rational = 8/-21
  x - y * z                                       //> res4: ex.Rational = 31/-42
  x - y * z                                       //> res5: ex.Rational = 31/-42
  y * z                                           //> res6: ex.Rational = 15/14
	class A(x: Int, y: Int) {
		require(y > 0, "denominator must be positive")
	}

	def a(x:Int, y:Int) =
	  require(y > 0, "denominator must be positive")
                                                  //> a: (x: Int, y: Int)Unit
  true                                            //> res7: Boolean(true) = true
  false                                           //> res8: Boolean(false) = false
  1                                               //> res9: Int(1) = 1
  if (true) 1 else false                          //> res10: AnyVal = 1

    
	var ts = new TestSets {}                  //> ts  : java.lang.Object with TestSets = workshit$$anonfun$main$1$$anon$1@64cd
                                                  //| 4b

  ts.set5.filter(tw => tw.user == "a").size()     //> res11: Int = 1
}

trait TestSets {
	val set1 = new Empty
	val set2 = set1.incl(new Tweet("a", "a body", 20))
	val set3 = set2.incl(new Tweet("b", "b body", 20))
	val c = new Tweet("c", "c body", 7)
	val d = new Tweet("d", "d body", 9)
	val set4c = set3.incl(c)
	val set4d = set3.incl(d)
	val set5 = set4c.incl(d)
}