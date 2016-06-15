import org.scalacheck.{Arbitrary, _}
import Arbitrary._
import Gen._
import Prop._

type H = List[Int]

val a: Gen[H]

oneOf(Ge, a)