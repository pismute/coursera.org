import common._

object workshit {

  Map(1->2, 2->3).toList                          //> res0: List[(Int, Int)] = List((1,2), (2,3))
  
  var l:List[Int] = 1 to 5 toList                 //> l  : List[Int] = List(1, 2, 3, 4, 5)

  def last[T](xs: List[T]): T = xs match {
    case List() => throw new Error("last of empty list")
    case List(x) => x
    case y :: ys => last(ys)
  }                                               //> last: [T](xs: List[T])T
  
  def init[T](xs: List[T]): List[T] = xs match {
    case List() => throw new Error("init of empty list")
    case List(x) => Nil
    case y :: ys => List(y) ++ init(ys)
  }                                               //> init: [T](xs: List[T])List[T]
  
  def concat[T](xs: List[T], ys: List[T]): List[T] = xs match {
    case List() => ys
    case z :: zs => z :: concat(zs, ys)
  }                                               //> concat: [T](xs: List[T], ys: List[T])List[T]

  def removeAt[T](n: Int, xs: List[T]):List[T] = n match {
    case 0 => xs.tail
    case _ => xs.head :: removeAt(n-1, xs.tail)
  }                                               //> removeAt: [T](n: Int, xs: List[T])List[T]
  
  def flatten(xs: List[Any]): List[Any] = xs match{
    case List() => xs
    case (y:List[Any]) :: ys => flatten(y) ++ flatten(ys)
    case (y:Any) :: ys => y +: flatten(ys)
  }                                               //> flatten: (xs: List[Any])List[Any]

  last(l)                                         //> res1: Int = 5
  l.init                                          //> res2: List[Int] = List(1, 2, 3, 4)
  init(l)                                         //> res3: List[Int] = List(1, 2, 3, 4)
  removeAt(1, List("a", "b", "c", "d"))           //> res4: List[java.lang.String] = List(a, c, d)
  flatten(List(List(1, 1), 2, List(3, List(5, 8))))
                                                  //> res5: List[Any] = List(1, 1, 2, 3, 5, 8)
  (1,2) match{
    case (x:Int, y:Int) => println(x + ", " + y)
  }                                               //> 1, 2
  

  def msort(xs: List[Int]): List[Int] = {
    val n = xs.length/2
    if (n == 0) xs
    else {
      def merge(xs: List[Int], ys: List[Int]):List[Int] = (xs, ys) match {
        case (Nil, _) => ys
        case (_, Nil) => xs
        case (x :: xs1, y :: ys1) => {
            if (x < y) x :: merge(xs1, ys)
            else y :: merge(xs, ys1)
          }
      }
      val (fst, snd) = xs splitAt n
      merge(msort(fst), msort(snd))
    }
  }                                               //> msort: (xs: List[Int])List[Int]
  
  msort( List(4, 3, 2, 1) )                       //> res6: List[Int] = List(1, 2, 3, 4)
  
  def pack[T](xs: List[T]): List[List[T]] = xs match {
    case Nil => Nil
    case x :: xs1 =>
      val (first, rest) = xs span(y=> y==x)
      first :: pack(rest)
  }                                               //> pack: [T](xs: List[T])List[List[T]]

  def encode[T](xs: List[T]): List[(T, Int)] =
    pack(xs) map (ys => (ys.head, ys.length))     //> encode: [T](xs: List[T])List[(T, Int)]
    
  pack(List("a", "a", "a", "b", "c", "c", "a"))   //> res7: List[List[java.lang.String]] = List(List(a, a, a), List(b), List(c, c
                                                  //| ), List(a))
  encode(List("a", "a", "a", "b", "c", "c", "a")) //> res8: List[(java.lang.String, Int)] = List((a,3), (b,1), (c,2), (a,1))
                                                  
  def mapFun[T, U](xs: List[T], f: T => U): List[U] =
    (xs foldRight List[U]())( f(_) :: _ )         //> mapFun: [T, U](xs: List[T], f: T => U)List[U]

  def lengthFun[T](xs: List[T]): Int =
    (xs foldRight 0)( (x, y) => 1 + y )           //> lengthFun: [T](xs: List[T])Int

  mapFun(1 to 5 toList, (x:Int) => x + 1 )        //> res9: List[Int] = List(2, 3, 4, 5, 6)
  lengthFun(1 to 5 toList)                        //> res10: Int = 5

  (1 to 5 toList) match {
    case x :: y :: zs => println( x + ", " + y + ", " + zs )
  }                                               //> 1, 2, List(3, 4, 5)
                                                  //| Output exceeds cutoff limit. 
}