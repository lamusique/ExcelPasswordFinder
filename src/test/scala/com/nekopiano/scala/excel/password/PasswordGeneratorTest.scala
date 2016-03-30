package com.nekopiano.scala.excel.password

/**
  * Created on 3/30/16.
  */
object PasswordGeneratorTest extends App {

  // 24 characters
  val minuscules = ('a' to 'z').toSeq
  val majuscules = ('A' to 'Z').toSeq
  val numbers = ('0' to '9').toSeq

  val list = Set(1,2,3).subsets.map(_.toList).toList
  println(list)

  generate(3)

  def generate(size:Int) = {
    val set = 1 to size map {i=> minuscules}
    val combos = set.flatten.combinations(size)
    val patterns = combos map (combo => {
      combo.permutations
    })
    println(patterns.flatten.mkString(","))

  }

}
