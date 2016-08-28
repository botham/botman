name := """telegram-bot"""

version := "1.0-SNAPSHOT"

herokuAppName in Compile := "fierce-retreat-47404"

lazy val root = (project in file(".")).enablePlugins(PlayJava)

scalaVersion := "2.11.7"

resolvers += "jitpack" at "https://jitpack.io"

libraryDependencies ++= Seq(
  javaJdbc,
  cache,
  evolutions,
  javaWs,
  "mysql" % "mysql-connector-java" % "5.1.36",
  "com.github.rubenlagus" % "TelegramBots" % "v2.3.3.6",
  "biz.paluch.redis" % "lettuce" % "4.2.1.Final"
)
