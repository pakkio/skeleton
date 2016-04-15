package uk.co.pakkio

import java.io.FileInputStream
import java.nio.file.{Files, Paths}

import uk.co.pakkio.util.Gunzip

class LoadZipped(
                  val fname: String,
                  val webSite: String,
                  val folder: String
                ) {
  val fnamez = fname + ".gz"
  if (!Files.exists(Paths.get(fname))) {
    if (!Files.exists(Paths.get(fnamez))) {
      util.FTPDownload.downloadFile(webSite, folder, fnamez)
    }
    Gunzip.gunzip(fname)
  }

  def is = new FileInputStream(fname)
}

object Nasa extends LoadZipped(fname = "NASA_access_log_Aug95",
  webSite = "ita.ee.lbl.gov",
  folder = "traces")
