package uk.co.pakkio.util

import java.io.InputStream
import scala.util.control.Exception._

trait StringUtils {

  implicit class StringImprovements(val s: String) {

    def toIntOpt = catching(classOf[NumberFormatException]) opt s.toInt
  }

}
trait CodecCleanerUtils {
  def toSource(inputStream: InputStream): scala.io.BufferedSource = {
    import java.nio.charset.{Charset, CodingErrorAction}
    val decoder = Charset.forName("UTF-8").newDecoder()
    decoder.onMalformedInput(CodingErrorAction.IGNORE)
    scala.io.Source.fromInputStream(inputStream)(decoder)
  }
}

