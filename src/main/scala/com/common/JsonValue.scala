package com.common

import play.api.libs.json.{JsValue, Json}

import scala.util.Random

sealed trait JsonValue {
  val json: JsValue
}

object JsonValue {

  case object Pan1 extends JsonValue {
    val json: JsValue = Json.parse(
      """
      {
        "pan" : "1234567890123456"
         }
      """)
  }

  case object Pan2 extends JsonValue {
    val json: JsValue = Json.parse(
      """
      {
        "pan" : "4638495746384958"
         }
      """)
  }

  case object Pan3 extends JsonValue {
    val json: JsValue = Json.parse(
      """
      {
        "pan" : "1029384756102938"
         }
      """)
  }

  val pans: Set[JsValue] =
    Set(Pan1.json, Pan2.json, Pan3.json)

  def apply(code: String): JsValue =
    code.toLowerCase match {
      case "1" => Pan1.json
      case "2" => Pan2.json
      case "3" => Pan3.json
      case _ => throw new IllegalArgumentException(s"""Unknown pan code "$code"!""")
    }

  def anyOther(pan: JsValue): JsValue = {
    val others = pans - pan
    others.toVector(Random.nextInt(others.size))
  }
}

