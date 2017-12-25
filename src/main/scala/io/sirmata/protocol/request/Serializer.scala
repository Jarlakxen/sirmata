package io.sirmata.protocol.request

import akka.util.ByteString

import io.sirmata._
import io.sirmata.protocol.Modes._
import io.sirmata.protocol.Tokens

trait Serializer {
  self: Commands =>
    
  type CommandSerializer[CMD <: CommandRequest] = (Firmata.Context, CMD) => ByteString

  implicit val CommandRequestToBytes: CommandSerializer[CommandRequest] =
    (context, cmd) => cmd match {
      case RequestFirmware => RequestFirmwareToBytes(context, RequestFirmware)
      case RequestCapability => RequestCapabilityToBytes(context, RequestCapability)
      case RequestAnalogMapping => RequestAnalogMappingToBytes(context, RequestAnalogMapping)
      case req: RequestPinState => RequestPinStateToBytes(context, req)
      case req: SetSamplingInterval => SetSamplingIntervalToBytes(context, req)
      case req: SetPinMode => SetPinModeToBytes(context, req)
      case req: DigitalWrite => DigitalWriteToBytes(context, req)
      case req: SetPinValue => SetPinValueToBytes(context, req)
      case ResetFirmware => ResetFirmwareToBytes(context, ResetFirmware)
    }

  implicit val RequestFirmwareToBytes: CommandSerializer[RequestFirmware.type] =
    (_, _) => ByteString(
      Tokens.StartSysSex,
      Tokens.ReportFirmware,
      Tokens.EndSysSex)

  implicit val RequestCapabilityToBytes: CommandSerializer[RequestCapability.type] =
    (_, _) => ByteString(
      Tokens.StartSysSex,
      Tokens.CapabilityQuery,
      Tokens.EndSysSex)

  implicit val RequestAnalogMappingToBytes: CommandSerializer[RequestAnalogMapping.type] =
    (_, _) => ByteString(
      Tokens.StartSysSex,
      Tokens.AnalogMappingQuery,
      Tokens.EndSysSex)

  implicit val RequestPinStateToBytes: CommandSerializer[RequestPinState] =
    (_, cmd) => ByteString(
      Tokens.StartSysSex,
      Tokens.PinStateQuery,
      cmd.pin.byteValue,
      Tokens.EndSysSex)

  implicit val SetSamplingIntervalToBytes: CommandSerializer[SetSamplingInterval] =
    (_, cmd) => ByteString(
      Tokens.StartSysSex,
      Tokens.SamplingInterval,
      (cmd.interval & 0x7F).byteValue,
      ((cmd.interval >> 7) & 0x7F).byteValue,
      Tokens.EndSysSex)

  implicit val SetPinModeToBytes: CommandSerializer[SetPinMode] =
    (_, cmd) => ByteString(
      Tokens.SetPinMode,
      cmd.pin.byteValue,
      cmd.mode.byteValue)

  implicit val DigitalWriteToBytes: CommandSerializer[DigitalWrite] =
    (_, cmd) => ByteString(
      Tokens.SetPinValue,
      cmd.pin.byteValue,
      cmd.value.byteValue)

  implicit val SetPinValueToBytes: CommandSerializer[SetPinValue] =
    (_, cmd) => ByteString(
      Tokens.SetPinValue,
      cmd.pin.byteValue,
      cmd.value.byteValue)

  implicit val ResetFirmwareToBytes: CommandSerializer[ResetFirmware.type] =
    (_, _) => ByteString(Tokens.SystemReset)

  implicit class GenericSerializableCommandRequest[T <: CommandRequest](response: T) {
    def toByteString(implicit context: Firmata.Context, ser: CommandSerializer[T]): ByteString =
      ser(context, response)

    def toBytes(implicit context: Firmata.Context, ser: CommandSerializer[T]): List[Byte] =
      toByteString(context, ser).toList
  }
}