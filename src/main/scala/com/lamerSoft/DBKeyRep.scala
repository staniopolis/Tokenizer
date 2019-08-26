package com.lamerSoft

import java.util.UUID

import akka.actor.{Actor, ActorLogging, ActorRef, PoisonPill, Props}
import com.datastax.oss.driver.api.core.CqlSession
import com.datastax.oss.driver.api.querybuilder.QueryBuilder._
import com.lamerSoft.EncryptDecrypt.DecryptAndSendToPars

object DBKeyRep {

  case class Put(messageId: String, encryptedPan: String)

  case class Get(token: String)

  def props(jsonParser: ActorRef): Props = Props(new DBKeyRep(jsonParser))

}


class DBKeyRep(jsonParser: ActorRef) extends Actor with ActorLogging {

  import DBKeyRep.{Get, Put}
  import com.lamerSoft.EncryptDecrypt.DecryptAndSendToPars

  private val session = CqlSession.builder.build

  override def receive: Receive = {
    case Put(messageId: String, encryptedPan: String) => {
      val uuid = now()
      jsonParser ! JsonParser.TokenParseToJson(encryptedPan)
      val insert = insertInto("tokenizer", "somerepo")
        .value("id", uuid)
        .value("messageId", literal(messageId))
        .value("value", literal(encryptedPan))
      val statement = insert.build
      session.execute(statement)
      session.close()
    }
    case Get(token: String) =>
      val query = selectFrom("tokenizer", "somerepo")
        .column("messageId")
        .whereColumn("value").isEqualTo(literal(token))
      val statement = query.build
      val rs = session.execute(statement)
      if (rs.one() == null) sender() ! PoisonPill // но надобно отправить Токенайзеру на рассмотрение проблемы и принятие решения
      else sender() ! DecryptAndSendToPars(rs.one().getString("messageId"), token)
      session.close()
  }
}
