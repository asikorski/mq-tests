lazy val main = project.in(file("."))

name := "mq-tests"

version := "0.1.4"


libraryDependencies ++= Seq(
  "junit" % "junit" % "4.11",
  "org.scalatest" % "scalatest_2.10" % "2.1.5",
  "com.typesafe" % "config" % "1.0.2",
  "com.github.scopt" %% "scopt" % "3.2.0",
  "org.apache.httpcomponents" % "httpclient" % "4.3.1",
  "org.json" % "json" % "20131018",
  "net.minidev" % "json-smart" % "1.2",
  "org.mockito" % "mockito-all" % "1.9.5",
  "org.mozilla" % "rhino" % "1.7R4",
  "org.mongodb" %% "casbah" % "2.7.2",
  "com.typesafe" % "config" % "1.0.2",
  "log4j" % "log4j" % "1.2.17",
  "com.typesafe" %% "scalalogging-slf4j" % "1.0.1",
  "org.slf4j" % "slf4j-api" % "1.7.1",
  "org.slf4j" % "log4j-over-slf4j" % "1.7.1",  // for any java classes looking for this
  "ch.qos.logback" % "logback-classic" % "1.0.3",
  "com.typesafe.akka" %% "akka-actor" % "2.3.0",
  "com.typesafe.akka" %% "akka-testkit" % "2.3.0",
  "com.rabbitmq" % "amqp-client" % "3.3.1",
  "com.github.sstone" % "amqp-client_2.11" % "1.4"
)


