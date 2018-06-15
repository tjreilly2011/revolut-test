name := "revolut-test"

lazy val quillVersion = "2.5.4"
lazy val logbackVersion = "1.2.3"
lazy val twitterServerLogBackClassic = "18.5.0"
lazy val slfjVersion = "1.6.4"
lazy val h2dbVersion = "1.4.197"
lazy val finchVersion = "0.20.0"
lazy val circeVersion = "0.9.0"
lazy val catsVersion = "1.1.0"
lazy val twitterServerVersion = "18.5.0"
lazy val finagleVersion = "18.5.0"

lazy val scalaTestVersion = "3.0.5"
//lazy val mockitoTestVersion = "1.10.19"

lazy val commonSettings = Seq(
  version := "1.0",
  scalaVersion := "2.11.12",
  scalacOptions ++= Seq(
    "-target:jvm-1.8"
    , "-feature"
    , "-deprecation"
    , "-Xfatal-warnings"
    , "-Xmax-classfile-name", "100"
    , "-unchecked"
    , "-language:implicitConversions"
    , "-language:reflectiveCalls"
    , "-language:postfixOps"
    , "-language:higherKinds"
    , "-encoding", "UTF-8"
    , "-Yno-adapted-args"
    , "-Xlint"
    , "-Ywarn-numeric-widen"
    , "-Ywarn-value-discard"
    , "-Xfuture"
    //    , "-Xlog-implicits"
  ),
  javacOptions ++= Seq("-source", "1.8", "-target", "1.8", "-Xlint"),
  javaOptions ++= Seq("-Xmx1G","-Dquill.binds.log=true"),
  resolvers += Resolver.url("typesafe", url("http://repo.typesafe.com/typesafe/ivy-releases/"))(Resolver.ivyStylePatterns)
)

lazy val root = (project in file("."))
  .settings(commonSettings: _*)
  .settings(
    libraryDependencies ++= Seq(
      "com.h2database" % "h2" % h2dbVersion,
      "org.slf4j" % "slf4j-api" % slfjVersion,
      "ch.qos.logback" % "logback-classic" % logbackVersion,
      "com.twitter" %% "twitter-server-logback-classic" % twitterServerLogBackClassic,
//      "org.apache.logging.log4j" % "log4j-api" % log4j2Version,
//      "org.apache.logging.log4j" % "log4j-core" % log4j2Version,
      "io.getquill" %% "quill-jdbc" % quillVersion,
      "com.github.finagle" %% "finch-core" % finchVersion,
      "com.github.finagle" %% "finch-circe" % finchVersion,
      "io.circe" %% "circe-generic" % circeVersion,
      "io.circe" %% "circe-parser" % circeVersion,
      "org.typelevel" % "cats-core_2.11" % catsVersion,
      "com.twitter" %% "twitter-server" % twitterServerVersion,
      "com.twitter" % "finagle-stats_2.11" % finagleVersion,
      "org.scalactic" %% "scalactic" % scalaTestVersion,
      "org.scalatest" %% "scalatest" % scalaTestVersion % "test"
    ),
      //.map(_.exclude("org.slf4j", "*")),
//      .map(_.exclude("org.slf4j", "slfj-nop-1.6.4")),
//    excludeDependencies ++= Seq(
//      "org.apache.logging.log4j" % "log4j-slf4j-impl"
//    ),
    // set the main class for 'sbt run'
    mainClass in (Compile, run) := Some("com.tomre.revolut.RevolutTestAppServer")
  )
fork := true