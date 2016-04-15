package uk.co.pakkio.spark

/* SimpleApp.scala */

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.scalatest.{FunSuite, FunSuiteLike}
import uk.co.pakkio.{Nasa, Rec}



class DemoEmbeddedSpec
  extends FunSuite
  with SparkHelper
  {
  override val cores = 8

  lazy val nasa = {
    println("Loading nasa logs if needed..")
    Nasa.is
  }

  lazy val loadLogData = sc.textFile(Nasa.fname, cores)
  test("setup spark") {
    // hide all ugly loginfo from here on
    sc.setLogLevel("ERROR")
  }
  test("load file as rrd") {
    loadLogData
  }

  lazy val parsedLines = loadLogData.map(ParseApacheLogLine(_)).filter(_.successful).map(_.get).cache
  test("parseLines") {
    parsedLines
    val count = parsedLines.count
    println(s"num of parsed: ${count}")
    // parsed: 1569898
    // successful: 1569880 (diff is 18)
  }
  lazy val callers = parsedLines.groupBy(_.caller).cache
  test("aggregate by caller"){

    println(s"num of unique callers ${callers.count}")
      //(k:String,it:Iterable[Rec]) => (k, it.toList))

  }
  test("statistics") {
    val sizes = callers.map( group => group._2.size).cache
    println(s" minimal number of requests for caller ${sizes.min}")
    println(s" max     number of requests for caller ${sizes.max}")
    println(s" avg     number of requests for caller ${sizes.sum/sizes.count}")
  }


}
