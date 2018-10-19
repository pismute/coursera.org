package timeusage

import org.apache.spark.sql.{ColumnName, DataFrame, Row}
import org.apache.spark.sql.types.{
  DoubleType,
  StringType,
  StructField,
  StructType
}
import org.junit.runner.RunWith
import org.scalatest.junit.JUnitRunner
import org.scalatest.{BeforeAndAfterAll, WordSpec, Matchers}

import scala.util.Random

@RunWith(classOf[JUnitRunner])
class TimeUsageSpec extends WordSpec with BeforeAndAfterAll with Matchers {
  import TimeUsage._
  import TimeUsageSpec._
  import org.apache.spark.sql.execution.debug._

  "TimeUsage" can {
    "timeUsageSummary" ignore {
      val df = timeUsageSummary(primaryNeedsColumns, workColumns, otherColumns, initDf)

      "timeUsageGrouped should be the same with timeUsageGroupedSql" ignore {
        val r1 = timeUsageGrouped(df).collect().map(_.toSeq)
        val r2 = timeUsageGroupedSql(df).collect().map(_.toSeq)

        r1 should be (r2)
      }


      "timeUsageGroupedTyped" in {
        val typed = timeUsageSummaryTyped(df)
        timeUsageGroupedTyped(typed).show
      }
    }
  }
}

object TimeUsageSpec {
  import TimeUsage._

  val (columns, initDf) = read("/timeusage/smalldata.csv")
  val (primaryNeedsColumns, workColumns, otherColumns) = classifiedColumns(columns)
}
