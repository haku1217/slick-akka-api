lazy val akkaVersion = "2.5.9"
lazy val akkaHttpVersion = "10.1.0"
lazy val slickVersion = "3.2.3"
lazy val scalazVersion = "7.2.19"

name := "slick-akka-api"

version := "0.1"

scalaVersion := "2.12.5"

resolvers += Resolver.sbtPluginRepo("releases") // ←追加部分

organization := "todo.api"

name := "todo-api"

version := "0.1-SNAPSHOT"

libraryDependencies ++= Seq(
  "com.typesafe.akka" %% "akka-actor" % akkaVersion,
  "com.typesafe.akka" %% "akka-slf4j" % akkaVersion,
  "ch.qos.logback" % "logback-classic" % "1.2.3",
  "com.typesafe.akka" %% "akka-testkit" % akkaVersion % Test,
  "com.typesafe.akka" %% "akka-stream" % akkaVersion,
  "com.typesafe.akka" %% "akka-http" % akkaHttpVersion,
  "com.typesafe.akka" %% "akka-http-testkit" % akkaHttpVersion % Test,
  "com.typesafe.akka" %% "akka-http-spray-json" % akkaHttpVersion,
  "io.spray" %% "spray-json" % "1.3.4",
  "com.typesafe.slick" %% "slick" % slickVersion,
  "com.typesafe.slick" %% "slick-hikaricp" % slickVersion,
  "com.typesafe.slick" %% "slick-codegen"% slickVersion,
  "org.scalaz" %% "scalaz-core" % scalazVersion,
  "org.scalaz" %% "scalaz-concurrent" % scalazVersion,
  "org.scalaz" %% "scalaz-effect" % scalazVersion,
  "org.scalaz" %% "scalaz-iteratee" % scalazVersion,
  "org.scalatest" %% "scalatest" % "3.0.5" % Test,
  "com.h2database" % "h2" % "1.4.196"
)

mainClass in Compile := Some("todo.api.Main")

dockerBaseImage := "java:8-jdk-alpine"

dockerExposedPorts := Seq(8000)

enablePlugins(JavaAppPackaging, AshScriptPlugin, DockerPlugin)

scalacOptions ++= Seq(
  "-deprecation",
  "-feature",
  "-unchecked",
  "-language:_",
  "-Xlint",
  "-Xfatal-warnings",
  "-Ywarn-dead-code",
  "-Ywarn-numeric-widen",
  "-Ywarn-unused",
  "-Ywarn-unused-import",
  "-Ywarn-value-discard"
)