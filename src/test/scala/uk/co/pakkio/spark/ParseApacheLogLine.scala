package uk.co.pakkio.spark

import uk.co.pakkio.MyParser

case object ParseApacheLogLine extends Serializable
  with MyParser
{
  def apply(x:String) = {
    parse(log,x)
  }
}
