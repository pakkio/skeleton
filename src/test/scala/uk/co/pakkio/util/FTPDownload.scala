package uk.co.pakkio.util

/**
  * Created by Claudio Pacchiega on 10/04/2016.
  * This software is freely usable by anyone
  */
object FTPDownload {
  def downloadFile(from: String, folder: String, file: String): Unit = {
    println(s"Downloading $file...")
    val client: MyFTP = MyFTPClient()

    client.connect(from)
    client.login("anonymous", "")
    client.cd(folder)
    client.setbinary

    val downloaded = client.downloadFile(file)
    if (!downloaded) {
      println(s"can't download $file")
      System.exit(-1)
    }
    client.disconnect()
    println(s"Downloaded $file...")
  }
}
