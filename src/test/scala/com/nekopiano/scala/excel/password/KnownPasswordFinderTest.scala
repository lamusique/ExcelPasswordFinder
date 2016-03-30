package com.nekopiano.scala.excel.password

import java.security.GeneralSecurityException

import org.apache.poi.poifs.crypt.{Decryptor, EncryptionInfo}
import org.apache.poi.poifs.filesystem.POIFSFileSystem
import org.apache.poi.xssf.usermodel.XSSFWorkbook

/**
  * Created by neko on 3/30/16.
  */
object KnownPasswordFinderTest extends App {

  // 24 characters
  //val minuscules = ('a' to 'z').toSeq
  //val minuscules = ('a' to 'r').toSeq
  val minuscules = Seq('d', 'b','a','r','c','h')


  // An encrypted file

  val fisEnc = getClass.getResourceAsStream("/encrypted.xlsx")
  val fileSystem = new POIFSFileSystem(fisEnc)
  val encInfo = new EncryptionInfo(fileSystem)
  implicit val decryptor = Decryptor.getInstance(encInfo)

  val set = generatePassword(4)
  val patterns = set.size
  println("All the patterns are " + patterns)

  import collection.parallel.ForkJoinTasks.defaultForkJoinPool._
  println("parallelism: " + getParallelism)
  println("All the threads are " + Thread.getAllStackTraces().keySet())

  set.zipWithIndex.par.foreach{case(characters, i) => {
    val password = "2016M" + characters.mkString
    checkPassword(password)
    if (i % 200 == 0) {
      val threadName = Thread.currentThread.getName
      println((i.toFloat/patterns * 100) + " % done. password: " + password + " threadName: " + threadName)
      //println("All the threads are " + Thread.getAllStackTraces().keySet())
    }
  }}

  println("The password not found.")


  def generatePassword(size:Int) = {
    val set = 1 to size map {i=> minuscules}
    val combos = set.flatten.combinations(size)
    val patterns = combos map(_.permutations)
    patterns.flatten.toSeq
  }

  def checkPassword(password:String)(implicit decryptor: Decryptor) = {
    if (password == "2016March") {
      println("HERE!!!!!!!!!!!!!!!!!!!!!!!!!!!! verify: " + decryptor.verifyPassword(password))
    }
    if (decryptor.verifyPassword(password)) {
      val threadName = Thread.currentThread.getName
      val message = "Found the password: " + password + ", threadName: " + threadName
      println(message)
      // Can't stop the process??
      throw new RuntimeException(message)
    }
    false
  }


}
