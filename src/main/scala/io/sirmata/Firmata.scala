package io.sirmata

import scala.concurrent._

import akka.actor.ActorSystem
import akka.serial.SerialSettings
import akka.serial.stream.Serial, Serial._
import akka.stream.scaladsl._
import akka.util.ByteString

import io.sirmata.protocol._
import io.sirmata.stream._

object Firmata {
  val DefaultBaudRate = 57600
  val MinSupportedVersion = 2.5

  case class Context(protocol: ProtocolVersion, firmware: ReportFirmware, board: Board, samplingInterval: Int = 19)

  def apply(descriptor: String = "/dev/ttyUSB0", baudRate: Int = DefaultBaudRate)(implicit system: ActorSystem): Flow[CommandRequest, CommandResponse, Future[Connection]] = {
    val settings = SerialSettings(baudRate)
    apply(descriptor, settings)
  }
  def apply(descriptor: String, settings: SerialSettings)(implicit system: ActorSystem): Flow[CommandRequest, CommandResponse, Future[Connection]] = {
    val serial: Flow[ByteString, ByteString, Future[Connection]] = Serial().open(descriptor, settings)
    SerializerFlow.viaMat(serial.viaMat(DeserializerFlow)(Keep.left))(Keep.right)
  }

}