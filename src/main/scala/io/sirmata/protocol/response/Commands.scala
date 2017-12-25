package io.sirmata.protocol.response

import io.sirmata.protocol.Modes._

trait Commands {
  sealed trait CommandResponse

  case object NoResponse extends CommandResponse

  case class ProtocolVersion(majorVersion: Byte, minorVersion: Byte) extends CommandResponse

  case class ReportFirmware(majorVersion: Byte, minorVersion: Byte, firmwareName: String) extends CommandResponse

  case class PinCapability(mode: PinMode, resolution: Int)
  case class PinCapabilities(capabilitys: Set[PinCapability])
  object PinCapabilities {
    def apply(capabilitys: PinCapability*): PinCapabilities = PinCapabilities(capabilitys.toSet)
  }
  case class CapabilityResponse(pins: List[PinCapabilities]) extends CommandResponse
  object CapabilityResponse {
    def apply(pins: PinCapabilities*): CapabilityResponse = CapabilityResponse(pins.toList)
  }

  case class AnalogMappingResponse(support: List[AnalogSupport]) extends CommandResponse

  case class PinStateResponse(pin: Short, mode: PinMode, state: List[Byte]) extends CommandResponse

  sealed class AnalogSupport
  object AnalogSupport {

    case object NoSupported extends AnalogSupport
    case class Supported(channel: Short) extends AnalogSupport
  }

}