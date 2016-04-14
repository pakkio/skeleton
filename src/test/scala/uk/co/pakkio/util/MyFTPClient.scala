package uk.co.pakkio.util

import org.apache.commons.net.ftp.{FTPClient => ApacheFTPClient}

object MyFTPClient {
  def apply (): MyFTP =
    new MyFTP(new ApacheFTPClient)
}
