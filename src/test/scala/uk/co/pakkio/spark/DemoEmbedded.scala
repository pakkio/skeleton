package uk.co.pakkio.spark

/* SimpleApp.scala */

import org.apache.spark.SparkContext
import org.apache.spark.SparkContext._
import org.apache.spark.SparkConf
import org.scalatest.{FunSuite, FunSuiteLike}
import uk.co.pakkio.{NasaFile, MyParser}

class DemoEmbedded
  extends MyParser
  with Serializable
  with FunSuiteLike
  with NasaFile {

  loadNasaFile

  lazy val conf = new SparkConf()
    .setAppName("Your Application Name")
    .setMaster("local");
  lazy val sc = new SparkContext(conf)
  lazy val logData = sc.textFile(fname, 8).cache()
  test("setup spark") {
    //import org.apache.log4j.PropertyConfigurator

    //PropertyConfigurator.configure("./log4j.properties")
    //LogParser
    conf
    sc
    sc.setLogLevel("WARN")

  }
  test("load file as rrd") {
    logData
  }
  test("parseLog") {



    val func = (x:String) => parse(log,x)
    val parsed = logData.map(line => line.length)


    /*
    .map(word => (word, 1))
    .reduceByKey( (a, b) => a + b)*/
    val count = parsed.count
    println(s"num of parsed: ${count}")
    assert(count === 15700035)

    //val numAs = logData.filter(line => line.contains("a")).count()
    //val numBs = logData.filter(line => line.contains("b")).count()
    //println("Lines with a: %s, Lines with b: %s".format(numAs, numBs))
  }
}
