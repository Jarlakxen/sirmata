package io.sirmata

package object protocol
    extends protocol.request.Commands
    with protocol.request.Serializer
    with protocol.response.Commands
    with protocol.response.Deserializer {

  val MinCommandSize = 3

  sealed trait CommandsFlow[Req <: CommandRequest, Res <: CommandResponse] {
    type Response = Res
  }

  implicit case object RunRequestFirmwareCommand extends CommandsFlow[RequestFirmware.type, ReportFirmware]

  implicit case object RunRequestCapabilityCommand extends CommandsFlow[RequestCapability.type, CapabilityResponse]
  
  implicit case object RunRequestAnalogMappingCommand extends CommandsFlow[RequestAnalogMapping.type, AnalogMappingResponse]
  
  implicit case object RunSetPinModeCommand extends CommandsFlow[SetPinMode, NoResponse.type]
  
  implicit case object RunSetPinValueCommand extends CommandsFlow[SetPinValue, NoResponse.type]
  
  implicit case object RunRequestPinStateCommand extends CommandsFlow[RequestPinState, PinStateResponse]
  
  implicit case object RunResetFirmwareCommand extends CommandsFlow[ResetFirmware.type, NoResponse.type]

}
  