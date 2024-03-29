package com.lamerSoft


import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import play.api.libs.json.JsValue

object Tokenizer {

  case class ProcessPan(messageId: String, json: JsValue)

  case class ProcessToken(json: JsValue)

  def props: Props = Props(new Tokenizer())
}

class Tokenizer extends Actor with ActorLogging {

  println("Tokenizer actor started")

  import Tokenizer.{ProcessPan, ProcessToken}



  protected val jsonParser: ActorRef = createJsonParser

  protected def createJsonParser: ActorRef = {
    context.actorOf(JsonParser.props(self))
  }

  override def receive: Receive = {
    case ProcessPan(messageId: String, json: JsValue) =>
      jsonParser ! JsonParser.ParsPan(messageId, json)
    case ProcessToken(json: JsValue) =>
      jsonParser ! JsonParser.ParsToken(json)
    case _ => println("lol")
  }
}
