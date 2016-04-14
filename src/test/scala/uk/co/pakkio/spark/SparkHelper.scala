package uk.co.pakkio.spark

import org.apache.spark.{SparkConf, SparkContext}

trait SparkHelper {
  val cores = 8
  val name = "AppName"

  lazy val conf = {
    println(s"Setting up Spark configuration")
    new SparkConf()
      .setAppName(name)
      .setMaster(s"local[$cores]")

  }
  lazy val sc = {
    new SparkContext(conf)
  }

}
