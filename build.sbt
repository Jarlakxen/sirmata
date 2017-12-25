import sbt.Keys._

// ··· Project Info ···

name := "sirmata-core"

organization := "io.sirmata"

crossScalaVersions := Seq("2.12.4")

scalaVersion := { crossScalaVersions.value.head }

fork in run  := true

publishMavenStyle := true

licenses += ("Apache-2.0", url("https://www.apache.org/licenses/LICENSE-2.0.html"))

// ··· Project Enviroment ···


// ··· Project Options ···

scalacOptions ++= Seq(
    "-encoding",
    "utf8",
    "-feature",
    "-language:postfixOps",
    "-language:implicitConversions",
    "-language:higherKinds",
    "-unchecked",
    "-deprecation"
)

scalacOptions in Test ++= Seq("-Yrangepos")

parallelExecution := false

// ··· Project Repositories ···

resolvers ++= Seq(Resolver.jcenterRepo)

// ··· Project Dependancies ···

val akkaV             = "2.5.8"
val akkaSerialStreamV = "4.1.2"
val configsV          = "0.4.4"
val scodecV           = "1.10.3"
val slf4JV            = "1.7.25"
val logbackV          = "1.2.3"
val catsV             = "1.0.0-RC2"
val scalatestV        = "3.0.4"
val scalacticV        = "3.0.4"

libraryDependencies ++= Seq(
  // --- Akka --
  "com.typesafe.akka"             %% "akka-actor"                         % akkaV             %  Provided,
  "com.typesafe.akka"             %% "akka-slf4j"                         % akkaV             %  Provided,
  "com.typesafe.akka"             %% "akka-stream"                        % akkaV             %  Provided,
  // --- Serial ---
  "ch.jodersky"                   %  "akka-serial-native"                 % akkaSerialStreamV % "runtime",
  "ch.jodersky"                   %% "akka-serial-stream"                 % akkaSerialStreamV,
  // --- Utils ---
  "org.scodec"                    %% "scodec-core"                        % scodecV,
  "com.github.kxbmap"             %% "configs"                            % configsV,  
  // --- Logger ---
  "org.slf4j"                     %  "slf4j-api"                          % slf4JV,
  "ch.qos.logback"                %  "logback-classic"                    % logbackV          %  Test,
  // --- Testing ---
  "org.typelevel"                 %% "cats-core"                          % catsV             %  Test,
  "com.typesafe.akka"             %% "akka-stream-testkit"                % akkaV             %  Test,
  "org.scalatest"                 %% "scalatest"                          % scalatestV        %  Test,
  "org.scalactic"                 %% "scalactic"                          % scalacticV        %  Test
)
