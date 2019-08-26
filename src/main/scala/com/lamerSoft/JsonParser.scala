package com.lamerSoft

import akka.actor.{Actor, ActorLogging, ActorRef, Props}
import play.api.libs.json._


object JsonParser {

  case class ParsPan(messageId: String, json: JsValue)

  case class ParsToken(json: JsValue)

  case class TokenParseToJson(token: String)

  case class PanParseToJson(messageId: String, pan: String)

  def props(tokenizer: ActorRef): Props = Props(new JsonParser(tokenizer))
}

class JsonParser(tokenizer: ActorRef) extends Actor with ActorLogging {


  println ("Jason parse actor started")
  import JsonParser.{PanParseToJson, ParsPan, ParsToken, TokenParseToJson}
  private val encryptDecrypt = createEncryptDecrypt()

  def createEncryptDecrypt(): ActorRef = {
    context.actorOf(EncryptDecrypt.props(self))
  }

  override def receive: Receive = {
    case ParsPan(messageId: String, json: JsValue) =>
      println("parsing...")
      val pan = (json \ "pan").as[String]
      println("parsed")
      encryptDecrypt ! EncryptDecrypt.EncryptPan(messageId, pan)
    case ParsToken(json: JsValue) =>
      val token = (json \ "token").as[String]
      encryptDecrypt ! EncryptDecrypt.DecryptToken(token)
    case TokenParseToJson(token: String) =>
      val json: JsValue = JsObject(Seq("token" -> JsString(token)))
      println(json) // Что-то чделать дальше
    case PanParseToJson(messageId: String, pan: String) =>
      val json: JsValue = JsObject(Map("messageId" -> JsString(messageId), "token" -> JsString(pan)))
      println(json) // Что-то чделать дальше
  }

}
