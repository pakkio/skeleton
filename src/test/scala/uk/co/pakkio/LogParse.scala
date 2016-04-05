package uk.co.pakkio

import org.scalatest.FunSuite

import scala.util.parsing.combinator.RegexParsers

/**
  * This code can be freely reused
  */
case class Rec(_a:String, _b:String)

trait Parser extends RegexParsers {
  val date = "\\d{4}-\\d{2}-\\d{2}"
  val time = "\\d{2}:\\d{2}:\\d{2},\\d{3}"
  def log: Parser[Rec]    = date.r  ~ time.r ^^ {
    case d ~ t =>

      Rec(d,t)
  }
}


class LogParser extends FunSuite with Parser {
  test("mytest"){
    val a=parse(log,"2016-04-04 21:42:10,000 ")
    println(a)
  }

}