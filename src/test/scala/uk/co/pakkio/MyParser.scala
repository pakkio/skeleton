package uk.co.pakkio

import org.joda.time.DateTime
import org.joda.time.format.DateTimeFormat
import util.StringUtils

import scala.util.parsing.combinator.RegexParsers

/**
  * This code can be freely reused
  */
case class Rec(caller: String, date: DateTime, request: String, code: Int, bytes: Option[Int])




// \\w+
class MyParser extends Serializable with RegexParsers with StringUtils {
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

      Rec(name, d, request1, code1.toInt, bytes1.toIntOpt)

  }
}
