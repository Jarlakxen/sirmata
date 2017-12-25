package io.sirmata.protocol.response

import akka.util.ByteString

import io.sirmata.protocol.Modes._
import io.sirmata.protocol.Tokens

trait Deserializer {
  self: Commands =>

  // Deserializer
  private def CapabilityResponseDeserializer(payload: List[Byte]) = {
    def support(bytes: List[Byte]): Set[PinCapability] = bytes.grouped(2).map { g => PinCapability(PinMode.of(g(0)), g(1).toInt) }.toSet

    def splitPayload(payload: List[Byte]): List[PinCapabilities] = {
      (payload.takeWhile { _ != 0X7F.toByte }, payload.dropWhile { _ != 0X7F.toByte }) match {
        case (rest, Nil) => List(PinCapabilities(support(rest)))
        case (head, tail) => List(PinCapabilities(support(head))) ++ splitPayload(tail.drop(1))
      }
    }

    CapabilityResponse(splitPayload(payload))
  }

  private def AnalogMappingResponseDeserializer(payload: List[Byte]) =
    AnalogMappingResponse(payload.map {
      case 0X7F => AnalogSupport.NoSupported
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

    case Tokens.StartSysSex :: Tokens.PinStateResponse :: pin :: mode :: (payload ::> Tokens.EndSysSex) =>
      Some(PinStateResponse(pin.toShort, PinMode.of(mode), payload))
    case _ => None
  }

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