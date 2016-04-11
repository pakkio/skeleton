package uk.co.pakkio.bigfile

package com.meetu.akka.bigdata

import java.io.RandomAccessFile

import akka.actor.{Actor, ActorRef, Props, ActorSystem}
import akka.routing.BalancingPool

object BigDataProcessor extends App {
  val system = ActorSystem("BigDataProcessor")

  val bigdataFilePath = "src/main/resources/bigdata.txt"
  val defaultBlockSize = 1 * 1024 * 1024
  val diagnostics: ActorRef = system.actorOf(Props[Diagnostics])
  distributeMessages

  private def distributeMessages = {
    val workerCount = Runtime.getRuntime.availableProcessors
    //val workers = Vector.fill(workerCount * 2)(system.actorOf(Props[FileWorker]))
    val router = system.actorOf(BalancingPool(workerCount * 2).props(Props[FileWorker])
    val totalChunks = totalMessages(bigdataFilePath)
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
}

class FileWorker extends Actor {
  var byteBuffer = new Array[Byte](BigDataProcessor.defaultBlockSize)
  def receive = {
    case BigDataMessage(bigDataFilePath, chunkIndex, totalChunks) =>
      val lines = readLines(bigDataFilePath, chunkIndex)
      getWordCount(lines)
      BigDataProcessor.diagnostics ! BigDataMessage(bigDataFilePath, chunkIndex, totalChunks)
  }

  def getWordCount(lines: Array[String]) = {
    lines foreach {
      line => line.split(" +").length
    }
  }

  private def readLines(bigDataFilePath: String, chunkIndex: Int): Array[String] = {
    val randomAccessFile = new RandomAccessFile(bigDataFilePath, "r")
    try {
      val seek = (chunkIndex - 1) * BigDataProcessor.defaultBlockSize
      randomAccessFile.seek(seek)
      randomAccessFile.read(byteBuffer)
      val rawString = new String(byteBuffer)
      rawString.split(System.getProperty("line.separator"))
    } finally {
      randomAccessFile.close
    }
  }
}

class Diagnostics extends Actor {
  var startTime = 0.0
  def receive = {
    case BigDataMessage(bigDataFilePath, chunkIndex, totalChunks) =>
      if (chunkIndex == 1) startTime = System.currentTimeMillis
      if (chunkIndex == totalChunks) println("Total Time: " + (System.currentTimeMillis - startTime))
  }
}

case class BigDataMessage(bigdataFilePath: String, chunkIndex: Int, totalChunks: Int)