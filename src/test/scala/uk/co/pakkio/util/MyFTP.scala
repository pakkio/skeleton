package uk.co.pakkio.util

import java.io.{File, FileOutputStream, InputStream}

import org.apache.commons.net.ftp.{FTP, FTPClient, FTPFile}

import scala.util.Try

final class MyFTP(client: FTPClient) {
  def login(username: String, password: String): Try[Boolean] = Try {
    client.login(username, password)
  }

  def connect(host: String): Try[Unit] = Try {
    client.connect(host)
    client.enterLocalPassiveMode()
  }

  def connected: Boolean = client.isConnected
  def disconnect(): Unit = client.disconnect()

  def canConnect(host: String): Boolean = {
    client.connect(host)
    val connectionWasEstablished = connected
    client.disconnect()
    connectionWasEstablished
  }
  def setbinary = client.setFileType(FTP.BINARY_FILE_TYPE)
  //def getbinary = client.get

  def listFiles(dir: Option[String]): Array[FTPFile] = dir match {
    case Some(d) => client.listFiles(d)
    case None    => client.listFiles
  }

  def connectWithAuth(host: String,
                      username: String = "anonymous",
                      password: String = "") : Try[Boolean] = {
    for {
      connection <- connect(host)
      login      <- login(username, password)
    } yield login
  }

  def extractNames(f: Option[String] => Array[FTPFile]) =
    f(None).map(_.getName).toSeq

  def cd(path: String): Boolean =
    client.changeWorkingDirectory(path)

  def filesInCurrentDirectory: Seq[String] =
    extractNames(listFiles)

  def downloadFileStream(remote: String): InputStream = {
    val stream = client.retrieveFileStream(remote)
    client.completePendingCommand() // make sure it actually completes!!
    stream
  }

  def downloadFile(remote: String): Boolean = {
    val os = new FileOutputStream(new File(remote))
    client.retrieveFile(remote, os)
  }

  def streamAsString(stream: InputStream): String =
    scala.io.Source.fromInputStream(stream).mkString
}
