import scala.annotation.tailrec
object workshit {
  //currying
	def sum(f: Int => Int)(a: Int, b: Int): Int = {
		def loop(a: Int, acc: Int): Int = {
			if (a > b) 0
			else f(a) + loop(a+1, b)
		}
		loop(a, b)
	}                                         //> sum: (f: Int => Int)(a: Int, b: Int)Int
	
	sum(x=>x)(1, 4)                           //> res0: Int = 10
	
  //higher-opder functions
  def product(x:Int, y:Int) = x * y               //> product: (x: Int, y: Int)Int
  def factorial(x:Int):Int = if( x == 1 ) 1 else product(x, factorial(x - 1))
                                                  //> factorial: (x: Int)Int
  def general(f: (Int, Int) => Int, a: Int, b: Int): Int = if (a > b) 0 else f(a, general(f, a+1, b))
                                                  //> general: (f: (Int, Int) => Int, a: Int, b: Int)Int
  
  factorial( 4 )                                  //> res1: Int = 24
  general( _ + _, 1, 4)                           //> res2: Int = 10
  general( _ * _, 1, 4)                           //> res3: Int = 0
  general( (x:Int, y:Int) => {println(":" + x + "," + y);x + y}, 1, 4)
                                                  //> :4,0
                                                  //| :3,4
                                                  //| :2,7
                                                  //| :1,9
                                                  //| res4: Int = 10
  1 * 2 * 3 * 4                                   //> res5: Int = 24
}