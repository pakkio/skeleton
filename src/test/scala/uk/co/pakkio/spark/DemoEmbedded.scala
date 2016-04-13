package uk.co.pakkio.spark

/* SimpleApp.scala */
import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.scalatest.FunSuite

class DemoEmbedded extends FunSuite {

  val logFile = "NASA_access_log_Aug95" // Should be some file on your system
  lazy val conf = new SparkConf()
      .setAppName("Your Application Name")
      .setMaster("local");
  lazy val sc = new SparkContext(conf)
  lazy val logData = sc.textFile(logFile, 2).cache()
  test("setup spark") {
    //import org.apache.log4j.PropertyConfigurator

    //PropertyConfigurator.configure("./log4j.properties")
    conf
    sc
    sc.setLogLevel("WARN")

  }
  test("load file as rrd") {
    logData
  }
  test("simple") {

    val counts = logData.flatMap(line => line.split(" "))
        .count()
      /*
      .map(word => (word, 1))
      .reduceByKey( (a, b) => a + b)*/

    println(s"num of words: $counts")
    assert(counts === 15700035)

    //val numAs = logData.filter(line => line.contains("a")).count()
    //val numBs = logData.filter(line => line.contains("b")).count()
    //println("Lines with a: %s, Lines with b: %s".format(numAs, numBs))
  }
}
