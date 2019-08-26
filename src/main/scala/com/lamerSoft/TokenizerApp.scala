package com.lamerSoft


import scala.collection._
import akka.actor.{ActorRef, ActorSystem}
import akka.event.Logging
import com.common.Terminal
import play.api.libs.json.JsValue

import scala.collection.breakOut
import scala.annotation.tailrec
import scala.concurrent.Await
import scala.concurrent.duration.Duration
import scala.io.StdIn

object TokenizerApp {
  private val opt = """(\S+)=(\S+)""".r

  def main(args: Array[String]): Unit = {
    val opts = argsToOpts(args.toList)
    applySystemProperties(opts)
    val name = opts.getOrElse("name", "tokenizer")
    val system = ActorSystem(s"$name-system")
    val tokenizerApp = new TokenizerApp(system)
    tokenizerApp.run()

  }


  private[lamerSoft] def argsToOpts(args: Seq[String]): Map[String, String] =
    args.collect { case opt(key, value) => key -> value }(breakOut)

  private[lamerSoft] def applySystemProperties(opts: Map[String, String]): Unit =
    for ((key, value) <- opts if key startsWith "-D")
      System.setProperty(key substring 2, value)
}

class TokenizerApp(system: ActorSystem) extends Terminal {

  println("Tokenizer system started")

  private val log = Logging(system, getClass.getName)
  private val tokenizer = createTokenizer()

  def run(): Unit = {
    log.warning(f"{} running%nEnter "
      + Console.BLUE + "commands" + Console.RESET
      + " into the terminal: "
      + Console.BLUE + "[e.g. `q` or `quit`]" + Console.RESET, getClass.getSimpleName)
    commandLoop()
    Await.ready(system.whenTerminated, Duration.Inf)
  }

  @tailrec
  private def commandLoop(): Unit =
    Command(StdIn.readLine()) match {
      case Command.Message(messageId, jsonValue) =>
        processPAN(messageId, jsonValue)
        commandLoop()
      case Command.Quit =>
        system.terminate()
      case Command.Unknown(command) =>
        log.warning("Unknown command {}!", command)
        commandLoop()
    }


  protected def createTokenizer(): ActorRef =
    system.actorOf(Tokenizer.props, "tokenizer")

  protected def processPAN(messageId: String, json: JsValue): Unit = {
    println(messageId, json, json.getClass.getName)
    tokenizer ! Tokenizer.ProcessPan(messageId, json)
  }

  protected def processToken(json: JsValue): Unit = {
    tokenizer ! Tokenizer.ProcessToken(json)
  }
}

