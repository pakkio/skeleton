package uk.co.pakkio


import java.io.FileInputStream
import java.nio.file.{Files, Paths}
import java.util.concurrent.atomic.AtomicInteger

import org.scalatest.FunSuite
import ftp.FTPDownload
import util.{CodecCleanerUtils, Gunzip}


class LogParser extends FunSuite
  with Parser
  with CodecCleanerUtils {

  test("downloading file") {
    nasaFile
  }

  test("parsing only 1 line") {

    val sampleLineTakenFromNASAAccessLog = """ix-esc-ca2-07.ix.netcom.com - - [01/Aug/1995:00:00:12 -0400] "GET /history/apollo/images/apollo-logo1.gif HTTP/1.0\" 200 1173"""

    parse(log, sampleLineTakenFromNASAAccessLog) match {
      case Success(matched, _) =>
        assert(matched.bytes === Some(1173))
        assert(matched.date.monthOfYear.get() === 8)
      case Failure(error, _) => fail(error)
      case Error(error, _) => fail(error)
    }

  }
  test("assessing length of records") {
    assert(recs.length === 1569880)
  }
  test("assessing number of callers") {
    assert(callers.size === 75059)
  }
  test("some statistics"){
    val maxNumOfCallsByASinglecaller = map.max
    val minNumOfCallsByASinglecaller = map.min
    val avgNumOfCallsByASinglecaller = map.sum / map.length
    assert(maxNumOfCallsByASinglecaller === 6530)
    assert(minNumOfCallsByASinglecaller === 1)
    assert(avgNumOfCallsByASinglecaller === 20)

  }

  test("filtering callers") {
    callers.map(p => {
      val recs1: Array[Rec] = p._2.toArray
      import org.saddle.time._
      val sortedevents = recs1.sortBy(_.date)

      sortedevents
    }
    )
  }

  lazy val nasaFile = {
    val fname = "NASA_access_log_Aug95"
    val fnamez = fname + ".gz"
    if (!Files.exists(Paths.get(fname))) {
      if (!Files.exists(Paths.get(fnamez))) {
        FTPDownload.downloadFile("ita.ee.lbl.gov","traces",fnamez)
      }
      Gunzip.gunzip(fname)
    }
    new FileInputStream(fname)
  }





  lazy val map = callers.map(s => s._2.length).toParArray

  lazy val callers = recs.par.groupBy(_.caller)

  lazy val recs = {
    val is = nasaFile // getClass.getResource("/access_log_Aug95").openStream()
    val chunkSize = 200000 //128 * 1024
    val iterator = toSource(is).getLines().grouped(chunkSize)//.toStream.par

    val cnt = new AtomicInteger

    val recs = for {
      lines <- iterator
      line <- lines
      //groups.incrementAndGet()
      parsed = parse(log, line)
      if parsed.successful
    } yield
      parsed.get
    val ret = recs.toArray
    ret
  }
}