package com.nekopiano.scala.excel.password

import org.apache.poi.poifs.crypt.{Decryptor, EncryptionInfo}
import org.apache.poi.poifs.filesystem.POIFSFileSystem

/**
  * Created on 3/30/16.
  */
object PasswordFinderTest extends App {

  // 24 characters
  //val minuscules = ('a' to 'z').toSeq
  val minuscules = ('a' to 'z').toSeq
  val majuscules = ('A' to 'Z').toSeq
  val numbers = ('0' to '9').toSeq

  val jaAlphabets = Seq('a','b','d','e','f','g','h','i','j','k','m','n','o','r','s','t','u')

  val series = numbers


  val fisEnc = getClass.getResourceAsStream("/encrypted.xlsx")
  val fileSystem = new POIFSFileSystem(fisEnc)
  val encInfo = new EncryptionInfo(fileSystem)
  implicit val decryptor = Decryptor.getInstance(encInfo)

  //val combos = generatePasswordCombos(6)
  val combos = generatePasswordCombos(4)

  val comboNumbers = combos.size
  println("All the comobs are " + comboNumbers)

  import collection.parallel.ForkJoinTasks.defaultForkJoinPool._
  println("parallelism: " + getParallelism)
  println("All the threads are " + Thread.getAllStackTraces().keySet())

  combos.zipWithIndex.par.foreach{case(characterCombo, i) => {

    val permutations = permutate(characterCombo)
    println("permutations.size=" + permutations.size)

    permutations foreach (sample => {
      //val password = "2016" + characters.mkString
      val password = sample.mkString + "naitei"

      val threadName = Thread.currentThread.getName
      println((i.toFloat/comboNumbers * 100) + " % done. password: " + password + " threadName: " + threadName)


      checkPassword(password)

    })

    if (i % 100 == 0) {
      val threadName = Thread.currentThread.getName
      println((i.toFloat/comboNumbers * 100) + " % done. the first password of the combo: " + characterCombo.head + " threadName: " + threadName)
      //println("All the threads are " + Thread.getAllStackTraces().keySet())
    }

  }}

  println("The password not found.")


  def generatePasswordCombos(size:Int) = {
    val set = 1 to size map {i=> series}
    val combos = set.flatten.combinations(size).toSeq
    println("combos.size=" + combos.size)
    // This can throw java.lang.OutOfMemoryError.
    //val patterns = combos.map(_.permutations)
    //patterns.flatten.toSeq
    combos
  }
  def permutate(combo: Seq[Char]) = {
    println("combo.size=" + combo.size)
    val permutations = combos.map(_.permutations)
    println("permutations.size=" + permutations.size)
    permutations.flatten.toSeq
  }

  def checkPassword(password:String)(implicit decryptor: Decryptor) = {
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
