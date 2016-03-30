package com.nekopiano.scala.sandbox

import java.util.concurrent.BrokenBarrierException

/**
  * Created by neko on 3/30/16.
  */
object ExceptionCatchTest extends App {

  try {
    throw new BrokenBarrierException("bbe")
  } catch {
    case re:IllegalArgumentException => println("caught re:" + re)
    //case e:Exception => throw e
  }

  println("end.")

}
