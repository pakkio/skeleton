package uk.co.pakkio

import java.io.FileInputStream
import java.nio.file.{Files, Paths}

import uk.co.pakkio.util.Gunzip

trait NasaFile {

  val fname = "NASA_access_log_Aug95"
  private val webSite = "ita.ee.lbl.gov"
  private val folder = "traces"

  lazy val loadNasaFile = {

    val fnamez = fname + ".gz"
    if (!Files.exists(Paths.get(fname))) {
      if (!Files.exists(Paths.get(fnamez))) {
        util.FTPDownload.downloadFile(webSite,folder,fnamez)
      }
      Gunzip.gunzip(fname)
    }
    new FileInputStream(fname)
  }


}
