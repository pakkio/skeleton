package uk.co.pakkio.experiments

import org.scalatest.FunSuite

/**
  * This code can be freely reused
  */
class DuckTypingSpec extends FunSuite {
  def genericAction(obj: {def method(x:String):String }): Unit = {
    println(obj.method("hello"))
  }
  case class C1(name:String="unnamed") {
    def method(x:String):String = s"C1: $x"
  }
  case class C2(name:String="unnamed") {
    def method(x:String):String = s"C2: $x"
    def othermethod = "other"
  }
  test("about ducks") {
    //assert(1==0)
    genericAction(new C1)
    genericAction(new C2)
  }

}
