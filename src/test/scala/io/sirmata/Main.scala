package io.sirmata

import scala.concurrent._
import scala.concurrent.duration._
import scala.util._

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._

import io.sirmata.protocol._
import io.sirmata.protocol.Modes._

object Main extends App {

  implicit val system = ActorSystem("Firmata")
  implicit val ec = system.dispatcher
  implicit val mat = ActorMaterializer()

  val firmata = Firmata()

  Await.result({
    for {
      reportFirmware <- (firmata <~ RequestFirmware)
      capabilityResponse <- (firmata <~ RequestCapability)
      analogMappings <- (firmata <~ RequestAnalogMapping)
      _ <- (firmata <~ SetPinMode(2, PinMode.DigitalOutput))
      _ <- (firmata <~ SetPinValue(2, PinValue.High))
      pin2State <- (firmata <~ RequestPinState(2))
      _ <- (firmata <~ ResetFirmware)
    } yield {
      println(s"ReportFirmware -> Name: ${reportFirmware.firmwareName}, Version: ${reportFirmware.majorVersion}.${reportFirmware.minorVersion}")
      println(s"CapabilityResponse:\n${capabilityResponse.pins.zipWithIndex.map { case (cap, pin) => s"\tPin[$pin]: ${cap.capabilitys.map(_.mode).mkString(", ")}" }.mkString("\n")}")
      println(s"AnalogMappings:\n${analogMappings.support.zipWithIndex.map{ case (s, pin) => s"\tPin[$pin]: $s"}.mkString("\n")}")
      println(s"PinState: ${pin2State}")
    }
  }, 10.seconds)

  Await.ready(system.terminate(), 5.seconds)
}