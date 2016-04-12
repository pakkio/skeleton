package uk.co.pakkio.bigfile

//package com.meetu.akka.bigdata

import java.io.RandomAccessFile
import java.util.concurrent.atomic.AtomicInteger

import akka.actor.{Actor, ActorRef, ActorSystem, Props}
import akka.routing.BalancingPool

trait System {
  val system = ActorSystem("BigDataProcessor")

}

object BigDataProcessor extends App with System {

  val workerCount = Runtime.getRuntime.availableProcessors //* 2
  val bigdataFilePath = "NASA_access_log_Aug95"
  val defaultBlockSize = 1 * 1024 * 1024
  val diagnostics: ActorRef = system.actorOf(Props( new Diagnostics(system) ))
  distributeMessages



  private def distributeMessages = {

    //val workers = Vector.fill(workerCount * 2)(system.actorOf(Props[FileWorker]))
    val router = system.actorOf(BalancingPool(workerCount).props(Props[FileWorker]))
    val totalChunks = totalMessages(bigdataFilePath)
    println(s"TotalChunks: $totalChunks")
    for (i <- 1 to totalChunks) {
      router ! BigDataMessage(bigdataFilePath, i, totalChunks)
    }
  }

  private def totalMessages(bigDataFilePath: String): Int = {
    val randomAccessFile = new RandomAccessFile(bigDataFilePath, "r")
    try {
      (randomAccessFile.length / defaultBlockSize).toInt
    } finally {
      randomAccessFile.close
    }
  }

  def getWordCount(lines: List[String]): Int = {
    val wordsPerLine = lines map {
      line => line.split(" +").length
    }
    wordsPerLine.reduce(_ + _)
  }
}



class FileWorker extends Actor {
  var byteBuffer = new Array[Byte](BigDataProcessor.defaultBlockSize)
  def receive = {
    case BigDataMessage(bigDataFilePath, chunkIndex, totalChunks) =>
      val lines = readLines(bigDataFilePath, chunkIndex)
      val numwords = BigDataProcessor.getWordCount(lines)
      BigDataProcessor.diagnostics ! BigDataResult(chunkIndex, totalChunks, numwords)
  }



  private def readLines(bigDataFilePath: String, chunkIndex: Int): List[String] = {
    val randomAccessFile = new RandomAccessFile(bigDataFilePath, "r")
    try {
      val seek = (chunkIndex - 1) * BigDataProcessor.defaultBlockSize
      randomAccessFile.seek(seek)
      randomAccessFile.read(byteBuffer)
      val rawString = new String(byteBuffer)
      rawString.split(System.getProperty("line.separator")).toList
    } finally {
      randomAccessFile.close
    }
  }
}

class Diagnostics(system: ActorSystem) extends Actor {
  var startTime = 0.0
  var total = 0
  var received = 0

  //var maxlines = 0

  def receive = {


    case BigDataResult(chunkIndex, totalChunks, count) =>
      received += 1
      if (received == 1) startTime = System.currentTimeMillis
      //println(s"chunk $chunkIndex, wordcounted $count")
      total += count

      if (received == totalChunks) {
        println(s"Total Count: [14124822] $total, Total Time: ${(System.currentTimeMillis - startTime)}")
        system.terminate
      }

  }
}

case class BigDataMessage(bigdataFilePath: String, chunkIndex: Int, totalChunks: Int)
case class BigDataResult(chunkIndex: Int, totalChunks: Int, count: Int)