package io.sirmata.protocol.request

import io.sirmata.Pin
import io.sirmata.protocol.Modes._

trait Commands {

  // Commands

  sealed trait CommandRequest

  /**
   * This message requests firmware version of a Firmata device.
   */
  case object RequestFirmware extends CommandRequest
  /**
   * This message requests capability repot of a Firmata device.
   */
  case object RequestCapability extends CommandRequest
  /**
   * The analog mapping query provides the information about which pins (as
   * used with Firmata's pin mode message) correspond to the analog channels.
   */
  case object RequestAnalogMapping extends CommandRequest

  case class RequestPinState(pin: Short) extends CommandRequest

  /**
   * The sampling interval sets how often analog data and i2c data is reported
   * to the client. The default value is 19 milliseconds.
   */
  case class SetSamplingInterval(interval: Short) extends CommandRequest {
    require(interval >= 10, "interval must equal or higher than 10")
    require(interval < 65535, "interval must lower than 65535")
  }

  case class GetSamplingInterval()

  case class SetPinMode(pin: Short, mode: PinMode) extends CommandRequest

  case class DigitalWrite(pin: Short, value: PinValue) extends CommandRequest

  case class SetPinValue(pin: Short, value: PinValue) extends CommandRequest

  case object ResetFirmware extends CommandRequest

}
