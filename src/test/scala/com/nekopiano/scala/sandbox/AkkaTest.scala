package com.nekopiano.scala.sandbox

import akka.actor.{Actor, ActorRef, ActorSystem, Props}

/**
  * Created on 3/30/16.
  */
object AkkaTest extends App {



  val system = ActorSystem("system")
  val actor = system.actorOf(Props[HelloActor])

  actor ! "Hello"
  actor ! "Hi"


  import akka.pattern.ask
  import akka.util.Timeout
  import scala.concurrent.duration._
  import scala.util.{Success,Failure}
  import scala.concurrent.ExecutionContext.Implicits.global

  implicit val timeout = Timeout(5 seconds)
  val reply = actor ? "How are you?"
  reply.onSuccess{
    case msg:String => println("reply from actor: " + msg)
  }

  val reply2 = actor ? "How's it going?"
  reply2.onComplete{
    case Success(msg: String) => println("reply from actor: " + msg)
    case Failure(e) => println("Message Failure e:" + e)
    case others => println("An unexpected result: " + others)
  }


  // Actor hierarchy

  val superActor = system.actorOf(Props[SupervisorActor], "supervisorActor")
  superActor ! "Hello"
  superActor ! MessageForChild("Hello")
  val reply3 = superActor ? ChildMessage
  reply3.onSuccess{
    case actor:ActorRef => println(actor)
  }


}

class HelloActor extends Actor{
  def receive = {
    case "Hello" => println("World")
    case "How are you?" => sender ! "I'm fine thank you!"
  }
}

class ChildActor extends Actor {
  def receive = {
    case message:String => println("A message in a child: " + message)
  }
}

class SupervisorActor extends Actor {
  override def preStart = context.actorOf(Props[ChildActor], "childActor")
  def receive = {
    case message:String => println("A message in a supervisor: " + message)
    case MessageForChild(message:String) => context.actorSelection("childActor") ! message
    case ChildMessage => sender ! context.actorSelection("childActor")
  }
}

case class MessageForChild(message:String)
case class ChildMessage()
