package io.sirmata.protocol

import io.sirmata.Pin
import Modes._

trait CommandsRequest {

  // Commands

  sealed trait CommandRequest

  sealed trait CommandReplayableRequest extends CommandRequest

  /**
   * This message requests firmware version of a Firmata device.
   */
  case class RequestFirmware() extends CommandReplayableRequest
  /**
   * This message requests capability repot of a Firmata device.
   */
  case class RequestCapability() extends CommandReplayableRequest
  /**
   * The analog mapping query provides the information about which pins (as
   * used with Firmata's pin mode message) correspond to the analog channels.
   */
  case class RequestAnalogMapping() extends CommandReplayableRequest

  case class RequestPinState() extends CommandReplayableRequest

  /**
   * The sampling interval sets how often analog data and i2c data is reported
   * to the client. The default value is 19 milliseconds.
   */
  case class SetSamplingInterval(interval: Short) extends CommandRequest {
    require(interval >= 10, "interval must equal or higher than 10")
    require(interval < 65535, "interval must lower than 65535")
  }

  case class GetSamplingInterval()

  case class SetPinMode(pin: Short, mode: PinMode) extends CommandRequest {
    import SetPinMode._
    private[this] lazy val findPin: List[Pin] => List[Pin] = { pins =>
      val index = pins.indexWhere(_.index == pin)
      pins.updated(index, pins(index).copy(currentMode = mode))
    }
  }

  case class DigitalWrite(pin: Short, value: PinValue) extends CommandRequest

  case class SetPinValue(pin: Short, value: PinValue) extends CommandRequest

  case class ResetFirmware() extends CommandRequest

}
