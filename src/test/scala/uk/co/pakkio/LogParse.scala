package uk.co.pakkio


import java.io.InputStream
import java.net.{HttpURLConnection, URL}
import java.util.concurrent.atomic.AtomicInteger

import org.joda.time.{DateTime, DateTimeZone}
import org.joda.time.format.{DateTimeFormat, DateTimeFormatter, ISODateTimeFormat}
import org.scalatest.FunSuite

import scala.io.{Codec, Source}
import scala.reflect.io.File
import scala.util.parsing.combinator.RegexParsers

/**
  * This code can be freely reused
  */
case class Rec(caller: String, date: DateTime, request: String, code: Int, bytes: Option[Int])

object StringUtils {
  implicit class StringImprovements(val s: String) {
    import scala.util.control.Exception._
    def toIntOpt = catching(classOf[NumberFormatException]) opt s.toInt
  }
}

// \\w+
trait Parser extends RegexParsers {
  val caller = "\\S+".r
  val dash = "-".r
  val date = """\[(.*)\]""".r
  val quotes = "\""
  val request = """[^"]*""".r
  val code = "\\d+".r
  val bytes = "[\\d-]+".r

  //val date = "\\d{4}-\\d{2}-\\d{2}"
  //val time = "\\d{2}:\\d{2}:\\d{2},\\d{3} "
  val parser1 =
    DateTimeFormat.forPattern("dd/MMM/yyyy:HH:mm:ss Z")
  def log: Parser[Rec] = caller ~ dash ~ dash ~ date ~ quotes ~ request ~ quotes ~ code ~ bytes ^^ {
    case name ~ d1 ~ d2 ~ date1 ~ q ~ request1 ~ q2 ~ code1 ~ bytes1 =>

      val removeBrackets = date1.substring(1, date1.length() - 1)
      val d: DateTime = parser1.parseDateTime(removeBrackets)
      import StringUtils._
      Rec(name, d, request1, code1.toInt, bytes1.toIntOpt)

  }
}


class LogParser extends FunSuite with Parser {
  val sample = """ix-esc-ca2-07.ix.netcom.com - - [01/Aug/1995:00:00:12 -0400] "GET /history/apollo/images/apollo-logo1.gif HTTP/1.0\" 200 1173"""

  def toSource(inputStream: InputStream): scala.io.BufferedSource = {
    import java.nio.charset.Charset
    import java.nio.charset.CodingErrorAction
    val decoder = Charset.forName("UTF-8").newDecoder()
    decoder.onMalformedInput(CodingErrorAction.IGNORE)
    scala.io.Source.fromInputStream(inputStream)(decoder)
  }


  test("parsing only 1 line") {
    parse(log, sample) match {
      case Success(matched, _) =>
        assert(matched.bytes === Some(1173))
        assert(matched.date.monthOfYear.get() === 8)
      case Failure(error, _) => fail(error)
      case Error(error, _) => fail(error)
    }

  }
  test("parsing 1.5M lines of logs in about 4 seconds (instead of 11 because of par and grouped)") {
    val is = getClass.getResource("/access_log_Aug95").openStream()
    val chunkSize = 128 * 1024
    val iterator = toSource(is).getLines().grouped(chunkSize)
    val groups: AtomicInteger = new AtomicInteger()
    var counter: AtomicInteger = new AtomicInteger()
    var errors = new AtomicInteger
    iterator.foreach {

      lines => lines.par.foreach {
        groups.incrementAndGet
        line => {
          val parsed = parse(log, line)
          if (parsed.successful)
            counter.incrementAndGet()
          else {
            errors.incrementAndGet()
            println(line)
          }
        }
      }
    }
    println(s"Total groups: ${groups.get} Errors ${errors.get}")
    assert(counter.get() === 1569880)
  }

}