package com.nekopiano.scala.excel.password

import java.io.FileInputStream
import java.security.GeneralSecurityException

import org.apache.poi.hssf.usermodel.HSSFWorkbook
import org.apache.poi.poifs.crypt.{Decryptor, EncryptionInfo}
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.xssf.usermodel.XSSFWorkbook

import scala.io.Source

/**
  * Created by neko on 3/30/16.
  */
object ExcelOpeningTest extends App {

  // A bare file

  val fis = getClass.getResourceAsStream("/unencrypted.xlsx")
  //val unencryptedSource = Source.fromURL(getClass.getResource("/unencrypted.xlsx"))
  val wb = new XSSFWorkbook(fis)
  val numberOfSheets = wb.getNumberOfSheets
  println(numberOfSheets)


  // An encrypted file

  val fisEnc = getClass.getResourceAsStream("/encrypted.xlsx")
  val fileSystem = new POIFSFileSystem(fisEnc)
  val encInfo = new EncryptionInfo(fileSystem)
  val decryptor = Decryptor.getInstance(encInfo)

  try {
    if (!decryptor.verifyPassword("2016March")) {
      throw new RuntimeException("Unable to process: document is encrypted")
    }

    val dataStream = decryptor.getDataStream(fileSystem)
    val wbEnc = new XSSFWorkbook(dataStream)
    // parse dataStream
    val numberOfSheetsEnc = wb.getNumberOfSheets
    println(numberOfSheetsEnc)

  } catch {
    case gse: GeneralSecurityException => throw new RuntimeException("Unable to process encrypted document", gse)
  }

  // Concurrent opening


}
