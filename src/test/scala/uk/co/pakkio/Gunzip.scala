package uk.co.pakkio

import java.io._
import java.nio.file.{Files, Paths}
import java.util.zip.GZIPInputStream
object Gunzip {
  private val buf = new Array[Byte](1024)

  // see https://gist.github.com/sasaki-shigeo/8431252
  def gunzip(path: String) {
    val src = new File(path ++ ".gz")
    val dst = new File(path)

    try {
      val in = new GZIPInputStream(new FileInputStream(src))
      try {
        val out = new BufferedOutputStream(new FileOutputStream(dst))
        try {
          var n = in.read(buf)
          while (n >= 0) {
            out.write(buf, 0, n)
            n = in.read(buf)
          }
        }
        finally {
          out.flush
        }
      } catch {
        case _: FileNotFoundException =>
          System.err.printf("Permission Denied: %s", path)
        case _: SecurityException =>
          System.err.printf("Permission Denied: %s", path)
      }
    } catch {
      case _: FileNotFoundException =>
        System.err.printf("File Not Found: %s", path ++ ".gz")
      case _: SecurityException =>
        System.err.printf("Permission Denied: %s", path ++ ".gz")
    }
  }
}
