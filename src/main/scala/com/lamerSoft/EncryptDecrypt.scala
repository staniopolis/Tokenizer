package com.lamerSoft

import akka.actor.{Actor, ActorLogging, ActorRef, Props}

import java.security.MessageDigest
import java.util

import javax.crypto.Cipher
import javax.crypto.spec.SecretKeySpec
import org.apache.commons.codec.binary.Base64

object EncryptDecrypt {

  case class EncryptPan(messageId: String, pan: String)

  case class DecryptToken(token: String)

  case class DecryptAndSendToPars(messageId: String, token: String)

  def props(jsonParser: ActorRef): Props = Props(new EncryptDecrypt(jsonParser))
}


class EncryptDecrypt(jsonParser: ActorRef) extends Actor with ActorLogging {

  import EncryptDecrypt.{DecryptAndSendToPars, DecryptToken, EncryptPan}

  println("Encrypt/Decrypt actor started")


  def encrypt(key: String, value: String): String = {
    val cipher: Cipher = Cipher.getInstance("AES/ECB/PKCS5Padding")
    cipher.init(Cipher.ENCRYPT_MODE, keyToSpec(key))
    Base64.encodeBase64String(cipher.doFinal(value.getBytes("UTF-8")))
  }

  def decrypt(key: String, encryptedValue: String): String = {
    val cipher: Cipher = Cipher.getInstance("AES/ECB/PKCS5PADDING")
    cipher.init(Cipher.DECRYPT_MODE, keyToSpec(key))
    new String(cipher.doFinal(Base64.decodeBase64(encryptedValue)))
  }

  def keyToSpec(key: String): SecretKeySpec = {
    var keyBytes: Array[Byte] = (SALT + key).getBytes("UTF-8")
    val sha: MessageDigest = MessageDigest.getInstance("SHA-1")
    keyBytes = sha.digest(keyBytes)
    keyBytes = util.Arrays.copyOf(keyBytes, 16)
    new SecretKeySpec(keyBytes, "AES")
  }

  private val SALT: String =
    "0w84fjsdnvwejfpaJUGojoiuho^[we*$ijnFLns-0i(E" // Изменяемое по желанию

  def createDBKeyRep: ActorRef = {
    context.actorOf(DBKeyRep.props(jsonParser))
  }

  override def receive: Receive = {
    case EncryptPan(messageId: String, pan: String) =>
      println("encrypting.....")
      val token = encrypt(messageId, pan)
      createDBKeyRep ! DBKeyRep.Put(messageId, token)
      println(token)
    case DecryptToken(token: String) =>
      createDBKeyRep ! DBKeyRep.Get(token)
    case DecryptAndSendToPars(messageId: String, token: String) =>
      val pan = decrypt(messageId, token)
      jsonParser ! JsonParser.PanParseToJson(messageId, pan)
      println(pan)
  }



}
