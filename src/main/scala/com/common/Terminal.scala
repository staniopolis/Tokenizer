package com.common

import play.api.libs.json.JsValue

import scala.util.parsing.combinator.RegexParsers

trait Terminal {

  protected sealed trait Command

  protected object Command {

    case class Message(messageId: String, pan: JsValue) extends Command

    case object Status extends Command

    case object Quit extends Command

    case class Unknown(command: String) extends Command

    def apply(command: String): Command =
      CommandParser.parseAsCommand(command)
  }

  private object CommandParser extends RegexParsers {

    def parseAsCommand(s: String): Command =
      parseAll(parser, s) match {
        case Success(command, _) => command
        case _ => Command.Unknown(s)
      }

    def processPAN: Parser[Command.Message] =
      opt(uuid) ~ ("pan|p".r ~> opt(pan)) ^^ {
        case messageId ~ pan =>
          Command.Message(
            messageId getOrElse "fbbe2470-c316-11e9-a20e-e161346c6b1c",
            pan getOrElse JsonValue.Pan1.json
          )
      }

    def getStatus: Parser[Command.Status.type] =
      "status|s".r ^^ (_ => Command.Status)

    def quit: Parser[Command.Quit.type] =
      "quit|q".r ^^ (_ => Command.Quit)

    def pan: Parser[JsValue] =
      "1|2|3".r ^^ JsonValue.apply

    def uuid: Parser[String] =
          """\d+""".r ^^ (_.toString)

  }

  private val parser: CommandParser.Parser[Command] =
    CommandParser.processPAN | CommandParser.getStatus | CommandParser.quit
}
