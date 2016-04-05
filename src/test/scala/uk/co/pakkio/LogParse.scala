package uk.co.pakkio

import org.scalatest.FunSuite

import scala.util.parsing.combinator.RegexParsers

/**
  * This code can be freely reused
  */
case class Rec(date:String, time:String, thread: String, level: String, rest: String)

trait Parser extends RegexParsers {
  val date = "\\d{4}-\\d{2}-\\d{2}".r
  val time = "\\d{2}:\\d{2}:\\d{2},\\d{3}".r
  val thread = "\\[.*\\]".r
  val level = "\\w+".r
  val rest = ".*".r
  def log: Parser[Rec]    = date  ~ time ~ thread ~ level ~ rest ^^ {
    case d ~ t ~ thr ~ l ~ r =>

      Rec(d,t,thr,l,r)
  }
}



class LogParser extends FunSuite with Parser {
  def generate(i: Int): Stream[String] = {
    s"2016-04-04 21:42:10,000 [main thread] INFO Description $i" #::
      generate(i + 1)

  }
  test("mytest"){
    //val b=generate(1).take(3)
    val parsed = generate(0) map (s =>
      parse(log, s))

    println(parsed.head)
    println(parsed(1))
    println(parsed(2))


  }

}