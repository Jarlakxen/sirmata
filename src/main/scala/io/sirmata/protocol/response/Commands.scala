package io.sirmata.protocol.response

import io.sirmata.protocol.Modes._

trait Commands {
  sealed trait CommandResponse

  case class ProtocolVersion(majorVersion: Byte, minorVersion: Byte) extends CommandResponse

  case class ReportFirmware(majorVersion: Byte, minorVersion: Byte, firmwareName: String) extends CommandResponse

  case class CapabilityResponse(support: List[List[(PinMode, Int)]]) extends CommandResponse

  case class AnalogMappingResponse(support: List[AnalogSupport]) extends CommandResponse

  case class PinStateResponse(state: List[Byte]) extends CommandResponse

  sealed class AnalogSupport
  object AnalogSupport {

    case object NoSupported extends AnalogSupport
    case class Supported(channel: Short) extends AnalogSupport
  }

}