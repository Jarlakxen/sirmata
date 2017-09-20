package io.sirmata.protocol

import akka.util.ByteString
import io.sirmata.protocol.Modes._

trait CommandsResponse {

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

  // Deserializer

  private def CapabilityResponseDeserializer(payload: List[Byte]) = {
    def support(bytes: List[Byte]): List[(PinMode, Int)] = bytes.grouped(2).map { g => (PinMode.withByteValue(g(0)), g(1).toInt) }.toList

    def splitPayload(payload: List[Byte]): List[List[(PinMode, Int)]] = {
      (payload.takeWhile { _ != 127.toByte }, payload.dropWhile { _ != 127.toByte }) match {
        case (rest, Nil) => List(support(rest))
        case (head, tail) => List(support(head)) ++ splitPayload(tail.drop(1))
      }
    }

    CapabilityResponse(splitPayload(payload))
  }

  private def AnalogMappingResponseDeserializer(payload: List[Byte]) =
    AnalogMappingResponse(payload.map {
      case 127 => AnalogSupport.NoSupported
      case value => AnalogSupport.Supported(value)
    })

  object ::> { def unapply[A](l: List[A]) = if (l.nonEmpty) Some((l.init, l.last)) else None }

  implicit def ByteString2CommandResponse(bytes: ByteString): Option[CommandResponse] = ByteList2CommandResponse(bytes.toList)

  implicit def ByteList2CommandResponse(bytes: List[Byte]): Option[CommandResponse] = bytes match {
    case Tokens.ReportVersion :: majorVersion :: minorVersion :: Nil =>
      Some(ProtocolVersion(majorVersion, minorVersion))

    case Tokens.StartSysSex :: Tokens.ReportFirmware :: majorVersion :: minorVersion :: (payload ::> Tokens.EndSysSex) =>
      Some(ReportFirmware(majorVersion, minorVersion, new String(payload.grouped(2).map(_.head).toArray)))

    case Tokens.StartSysSex :: Tokens.CapabilityResponse :: (payload ::> Tokens.EndSysSex) =>
      Some(CapabilityResponseDeserializer(payload))

    case Tokens.StartSysSex :: Tokens.AnalogMappingResponse :: (payload ::> Tokens.EndSysSex) =>
      Some(AnalogMappingResponseDeserializer(payload))

    case Tokens.StartSysSex :: Tokens.PinStateResponse :: (payload ::> Tokens.EndSysSex) =>
      Some(PinStateResponse(payload))
    case _ => None
  }

  // Stringify
  /*
  private type CommandStringify[CMD] = CMD => String

  implicit val ProtocolVersionToString: CommandStringify[ProtocolVersion] = {
    case ProtocolVersion(majorVersion, minorVersion) => s"v$majorVersion.$minorVersion"
  }

  implicit val ReportFirmwareToString: CommandStringify[ReportFirmware] = {
    case ReportFirmware(majorVersion, minorVersion, firmwareName) => s"v$majorVersion.$minorVersion - $firmwareName"
  }
*/
  // Utils

  implicit class SerializableCommandResponse[T <: CommandResponse](cmd: T) {
    def toByteString(implicit serializer: T => ByteString): ByteString = serializer(cmd)
    def toBytes(implicit serializer: T => ByteString): List[Byte] = serializer(cmd).toList
    def asString(implicit stringify: T => String): String = stringify(cmd)
  }

  implicit class DeserializableCommandResponse(bytes: ByteString) {
    def toCommandResponse: Option[CommandResponse] = bytes
  }
}