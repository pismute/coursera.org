package suggestions



import language.postfixOps
import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext.Implicits.global
import scala.util.{Try, Success, Failure}
import rx.lang.scala._
import org.scalatest._
import gui._
import rx.lang.scala.Notification.{ OnNext, OnError, OnCompleted }
import rx.lang.scala.subjects._

import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner


@RunWith(classOf[JUnitRunner])
class WikipediaApiTest extends FunSuite {

  object mockApi extends WikipediaApi {
    def wikipediaSuggestion(term: String) = Future {
      if (term.head.isLetter) {
        for (suffix <- List(" (Computer Scientist)", " (Footballer)")) yield term + suffix
      } else {
        List(term)
      }
    }
    def wikipediaPage(term: String) = Future {
      "Title: " + term
    }
  }

  import mockApi._

  test("WikipediaApi should make the stream valid using sanitized") {
    val notvalid = Observable("erik", "erik meijer", "martin")
    val valid = notvalid.sanitized

    var count = 0
    var completed = false

    val sub = valid.subscribe(
      term => {
        assert(term.forall(_ != ' '))
        count += 1
      },
      t => assert(false, s"stream error $t"),
      () => completed = true
    )
    assert(completed && count == 3, "completed: " + completed + ", event count: " + count)
  }

  test("WikipediaApi should correctly use concatRecovered") {
    val requests = Observable(1, 2, 3)
    val remoteComputation = (n: Int) => Observable(0 to n)
    val responses = requests concatRecovered remoteComputation
    val sum = responses.foldLeft(0) { (acc, tn) =>
      tn match {
        case Success(n) => acc + n
        case Failure(t) => throw t
      }
    }
    var total = -1
    val sub = sum.subscribe {
      s => total = s
    }

    assert(total == (1 + 1 + 2 + 1 + 2 + 3), s"Sum: $total")
  }

  test("about complete"){
    def echo[T](head:String, obs:Observable[T]) =
      obs.materialize.subscribe( x => x match {
        case OnNext(t) => println(s"$head:$t")
        case OnError(e) => println(s"$head:$e")
        case OnCompleted() => println(s"$head:complete")
      })

    echo( "Observable(1,2,3)", Observable(1,2,3) )
    echo( ".recovered", Observable(1,2,3).recovered )

    echo( "concatRecovered", Observable(1,2,3,4,5).concatRecovered (t=> t match {
      case 4 => Observable(new Exception)
      case n => Observable(n)
    }))
  }

  test("cancatRecovered1"){
    val exception = new Exception;
    val result = Observable(1,2,3,4,5).concatRecovered (t=> t match {
      case 4 => Observable(exception)
      case n => Observable(n)
    }).toBlockingObservable.toList

    assert( result == List(Success(1), Success(2), Success(3), Failure(exception), Success(5)), result)
  }

  test("cancatRecovered2"){
    val exception = new Exception;
    val result = Observable(1,2,3).concatRecovered (t=> Observable(t, t, t)).toBlockingObservable.toList
    val oracle = List(Success(1), Success(1), Success(1), Success(2), Success(2), Success(2), Success(3), Success(3), Success(3))

    assert( result == oracle, result)
  }

  test("""|Observable(1, 2, 3).zip(Observable.interval(700 millis)).timedOut(1L)
          |should return the first value, and complete without errors
       |""".stripMargin.replace("\n","")){
    Observable(1, 2, 3).zip(Observable.interval(700 millis))
      .timedOut(1L)
      .subscribe{ t =>
        assert(t._1 == 1, t._1)
      }
  }
}
