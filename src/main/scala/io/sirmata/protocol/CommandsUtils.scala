package io.sirmata.protocol

import akka.util.ByteString

import io.sirmata._
import io.sirmata.protocol.Modes._

trait CommandsUtils {

  type CommandSerializer[CMD <: CommandRequest] = (Firmata.Context, CMD) => ByteString

  implicit val CommandRequestToBytes: CommandSerializer[CommandRequest] =
    (context, cmd) => cmd match {
      case req: RequestFirmware => RequestFirmwareToBytes(context, req)
      case req: RequestCapability => RequestCapabilityToBytes(context, req)
      case req: RequestAnalogMapping => RequestAnalogMappingToBytes(context, req)
      case req: RequestPinState => RequestPinStateToBytes(context, req)
      case req: SetSamplingInterval => SetSamplingIntervalToBytes(context, req)
      case req: SetPinMode => SetPinModeToBytes(context, req)
      case req: DigitalWrite => DigitalWriteToBytes(context, req)
      case req: SetPinValue => SetPinValueToBytes(context, req)
      case req: ResetFirmware => ResetFirmwareToBytes(context, req)
    }

  implicit val RequestFirmwareToBytes: CommandSerializer[RequestFirmware] =
    (_, _) => ByteString(
      Tokens.StartSysSex,
      Tokens.ReportFirmware,
      Tokens.EndSysSex)

  implicit val RequestCapabilityToBytes: CommandSerializer[RequestCapability] =
    (_, _) => ByteString(
      Tokens.StartSysSex,
      Tokens.CapabilityQuery,
      Tokens.EndSysSex)

  implicit val RequestAnalogMappingToBytes: CommandSerializer[RequestAnalogMapping] =
    (_, _) => ByteString(
      Tokens.StartSysSex,
      Tokens.AnalogMappingQuery,
      Tokens.EndSysSex)

  implicit val RequestPinStateToBytes: CommandSerializer[RequestPinState] =
    (_, cmd) => ByteString(
      Tokens.SetPinMode,
      Tokens.PinStateQuery,
      Tokens.EndSysSex)

  implicit val SetSamplingIntervalToBytes: CommandSerializer[SetSamplingInterval] =
    (_, cmd) => ByteString(
      Tokens.StartSysSex,
      Tokens.SamplingInterval,
      (cmd.interval & 0x7F).toByte,
      ((cmd.interval >> 7) & 0x7F).toByte,
      Tokens.EndSysSex)

  implicit val SetPinModeToBytes: CommandSerializer[SetPinMode] =
    (_, cmd) => ByteString(
      Tokens.SetPinMode,
      cmd.pin.toByte,
      cmd.mode.toByte)

  implicit val DigitalWriteToBytes: CommandSerializer[DigitalWrite] =
    (_, cmd) => ByteString(
      Tokens.SetPinValue,
      cmd.pin.toByte,
      cmd.value.toByte)

  implicit val SetPinValueToBytes: CommandSerializer[SetPinValue] =
    (_, cmd) => ByteString(
      Tokens.SetPinValue,
      cmd.pin.toByte,
      cmd.value.toByte)

  implicit val ResetFirmwareToBytes: CommandSerializer[ResetFirmware] =
    (_, cmd) => ByteString(Tokens.SystemReset)

  implicit class GenericSerializableCommandRequest[T <: CommandRequest](response: T) {
    def toByteString(implicit context: Firmata.Context, ser: CommandSerializer[T]): ByteString =
      ser(context, response)

    def toBytes(implicit context: Firmata.Context, ser: CommandSerializer[T]): List[Byte] =
      toByteString(context, ser).toList
  }
}