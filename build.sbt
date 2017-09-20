import sbt.Keys._

// ··· Project Info ···

name := "sirmata-core"

organization := "io.sirmata"

crossScalaVersions := Seq("2.12.3")

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

val akkaV             = "2.5.4"
val akkaSerialStreamV = "4.1.1"
val enumeratumV       = "1.5.12"
val configsV          = "0.4.4"
val slf4JV            = "1.7.25"
val logbackV          = "1.2.3"
val scalatestV        = "3.0.4"
val scalacticV        = "3.0.4"

libraryDependencies ++= Seq(
  // --- Akka --
  "com.typesafe.akka"             %% "akka-actor"                         % akkaV             %  "provided",
  "com.typesafe.akka"             %% "akka-slf4j"                         % akkaV             %  "provided",
  "com.typesafe.akka"             %% "akka-stream"                        % akkaV             %  "provided",
  // --- Serial ---
  "ch.jodersky"                   %  "akka-serial-native"                 % akkaSerialStreamV % "runtime",
  "ch.jodersky"                   %% "akka-serial-stream"                 % akkaSerialStreamV,
  // --- Utils ---
  "com.beachape"                  %% "enumeratum"                         % enumeratumV,
  "com.github.kxbmap"             %% "configs"                            % configsV,  
  // --- Logger ---
  "org.slf4j"                     %  "slf4j-api"                          % slf4JV,
  "ch.qos.logback"                %  "logback-classic"                    % logbackV          %  "test",
  // --- Testing ---
  "com.typesafe.akka"             %% "akka-stream-testkit"                % akkaV             %  "test",
  "org.scalatest"                 %% "scalatest"                          % scalatestV        %  "test",
  "org.scalactic"                 %% "scalactic"                          % scalacticV        %  "test"
)
