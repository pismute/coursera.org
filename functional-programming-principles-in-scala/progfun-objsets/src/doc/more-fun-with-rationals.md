# more fun with rationals

## Data Abstraction

데이터를 사용하는 클라이언트 모르게 데이터의 구현을 변경하는 것을 Data Abstraction라고 부른다.

gcd를 이용해 Rational 클래스에 *약분*하는 기능을 추가했다:

```scala
class Rational(x: Int, y: Int) {
  private def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
  private val g = gcd(x, y)

  def numer = x/g
  def denom = y/g
}
```

*Data Abstraction 1*. 다음과 같이 수정할 수 있다:

```scala
class Rational(x: Int, y: Int) {
  private def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
  def numer = x / gcd(x, y)
  def denom = y / gcd(x, y)
}
```

number와 denom에 접근할 때마다 gcd를 호출하기 때문에 자주 접근하면 효율이 떨어진다.

*Data Abstraction 2*. `def`를 `val`로 변경해서 땅 한번만 계산되도록 할 수도 있다:

```scala
class Rational(x: Int, y: Int) {
  private def gcd(a: Int, b: Int): Int = if (b == 0) a else gcd(b, a % b)
  val numer = x / gcd(x, y)
  val denom = y / gcd(x, y)
}
```

## Self Reference 

this --;Java랑 같다.

```scala
  def max(that: Rational) =
    if (this.less(that)) that else this
```

## assert, require, assume

assert, require, assume는 모두 Predef 객체에 들어 있다. [Predef](http://www.scala-lang.org/api/current/scala/Predef$.html)에는 명시적으로 import하지 않아도 사용할 수 있는 type아니 type alias들이 정의돼 있다.

컴파일할 때 제거 할 수 있다(@elidable). 다음은 Predef 소스에서 발최했다:


```scala
object Predef {
  ...
/** Tests an expression, throwing an `AssertionError` if false.
   *  Calls to this method will not be generated if `-Xelide-below`
   *  is at least `ASSERTION`.
   *
   *  @see elidable
   *  @param assertion   the expression to test
   */
  @elidable(ASSERTION)
  def assert(assertion: Boolean) {
    if (!assertion)
      throw new java.lang.AssertionError("assertion failed")
  }

  /** Tests an expression, throwing an `AssertionError` if false.
   *  Calls to this method will not be generated if `-Xelide-below`
   *  is at least `ASSERTION`.
   *
   *  @see elidable
   *  @param assertion   the expression to test
   *  @param message     a String to include in the failure message
   */
  @elidable(ASSERTION) @inline
  final def assert(assertion: Boolean, message: => Any) {
    if (!assertion)
      throw new java.lang.AssertionError("assertion failed: "+ message)
  }

  /** Tests an expression, throwing an `AssertionError` if false.
   *  This method differs from assert only in the intent expressed:
   *  assert contains a predicate which needs to be proven, while
   *  assume contains an axiom for a static checker.  Calls to this method
   *  will not be generated if `-Xelide-below` is at least `ASSERTION`.
   *
   *  @see elidable
   *  @param assumption   the expression to test
   */
  @elidable(ASSERTION)
  def assume(assumption: Boolean) {
    if (!assumption)
      throw new java.lang.AssertionError("assumption failed")
  }

  /** Tests an expression, throwing an `AssertionError` if false.
   *  This method differs from assert only in the intent expressed:
   *  assert contains a predicate which needs to be proven, while
   *  assume contains an axiom for a static checker.  Calls to this method
   *  will not be generated if `-Xelide-below` is at least `ASSERTION`.
   *
   *  @see elidable
   *  @param assumption   the expression to test
   *  @param message      a String to include in the failure message
   */
  @elidable(ASSERTION) @inline
  final def assume(assumption: Boolean, message: => Any) {
    if (!assumption)
      throw new java.lang.AssertionError("assumption failed: "+ message)
  }

  /** Tests an expression, throwing an `IllegalArgumentException` if false.
   *  This method is similar to `assert`, but blames the caller of the method
   *  for violating the condition.
   *
   *  @param requirement   the expression to test
   */
  def require(requirement: Boolean) {
    if (!requirement)
      throw new IllegalArgumentException("requirement failed")
  }

  /** Tests an expression, throwing an `IllegalArgumentException` if false.
   *  This method is similar to `assert`, but blames the caller of the method
   *  for violating the condition.
   *
   *  @param requirement   the expression to test
   *  @param message       a String to include in the failure message
   */
  @inline final def require(requirement: Boolean, message: => Any) {
    if (!requirement)
      throw new IllegalArgumentException("requirement failed: "+ message)
  }

  ...
}

```

* [Design by Contract](http://blog.m1key.me/2010/02/programming-scala-design-by-contract.html)에 따라 precondition에는 require를, postcondition에는 assume을 사용하라는 의견이 있다.
* recondition에는 require, postcondition에느 assume, 그외 assert 아닐까?
* 강의에서 assume을 언급하지 않는 이유가 뭘까?

## constructor

primary constructor:

```scala
class Rational(x: Int, y: Int) {
  ...
}
```

auxiliary constructors:

```scala
class Rational(x: Int, y: Int) {
  def this(x: Int) = this(x, 1)
  ...
}
```

auxiliary constructors에서 꼭 primary constructors를 먼저 호출해야 컴파일된다:

```scala
class Hello(msg:String){
  //def this() = {println("World");this("")} 은 컴파일되지 않는다.
  def this() = {this("");println("World")}
  println( "hello " + msg)
}
```