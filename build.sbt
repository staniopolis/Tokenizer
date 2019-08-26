name := "com.lamerSoft.Tokenizer"

version := "0.1"

scalaVersion := "2.12.0"

val akkaVer = "2.6.0-M6"
val dbdVersion = "4.1.0"
val logbackVer = "1.2.3"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVer,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVer,
  "com.typesafe.akka" %% "akka-persistence" % akkaVer,
  "com.datastax.oss" % "java-driver-core" % dbdVersion,
  "com.datastax.oss" % "java-driver-query-builder" % dbdVersion,
  "ch.qos.logback" % "logback-classic" % logbackVer,

"org.scala-lang.modules" %% "scala-parser-combinators" % "1.1.2",
"commons-codec" % "commons-codec" % "1.13",
"com.typesafe.play" %% "play-json" % "2.8.0-M5"
)