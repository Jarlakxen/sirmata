package io.sirmata.protocol

import scodec.bits._
import scodec.codecs.literals._

/**
 * Set of constants that represent tokens of Firmata protocol.<br/>
 */

object Tokens extends MessageTypesTokens with SysExCommandsTokens

private[protocol] trait MessageTypesTokens {

  /**
   * send data for a digital pin
   */

  val DigitalMessage = hex"90"

  /**
   * send data for an analog pin (or PWM)
   */
  val AnalogMessage = hex"E0"

  /**
   * enable analog input by pin #
   */
  val ReportAnalog = hex"C0"

  /**
   * enable digital input by port pair
   */
  val ReportDigital = hex"D0"

  /**
   * set a pin to INPUT/OUTPUT/PWM/etc
   */
  val SetPinMode = hex"F4"

  /**
   * set the pin value
   */
  val SetPinValue = hex"F5"

  /**
   * report protocol version
   */
  val ReportVersion = hex"F9"

  /**
   * reset from MIDI
   */
  val SystemReset = hex"FF"

  /**
   * start a MIDI Sysex message
   */
  val StartSysSex = hex"F0"

  /**
   * start a MIDI Sysex message
   */
  val EndSysSex = hex"F7"

}

// Extended command set using sysex (0-127/"00-0x7F)
/* 0x00-0x0F reserved for user-defined commands */

private[protocol] trait SysExCommandsTokens {

  val ReservedCommand = hex"00" // 2nd SysEx data byte is a chip-specific command (AVR, PIC, TI, etc).

  val ServoConfig = hex"70" // set max angle, minPulse, maxPulse, freq

  val StringData = hex"71" // a string message with 14-bits per byte

  val ShiftData = hex"75" // a bitstream to/from a shift register

  val I2cRequest = hex"76" // send an I2C read/write request

  val I2cREPLY = hex"77" // a reply to an I2C read request

  val I2cConfig = hex"78" // config I2C settings such as delay times and power pins

  val ExtendedAnalog = hex"6F" // analog write (PWM, Servo, etc) to any pin

  val PinStateQuery = hex"6D" // ask for a pin's current mode and value

  val PinStateResponse = hex"6E" // reply with pin's current mode and value

  val CapabilityQuery = hex"6B" // ask for supported modes and resolution of all pins

  val CapabilityResponse = hex"6C" // reply with supported modes and resolution

  val AnalogMappingQuery = hex"69" // ask for mapping of analog to pin numbers

  val AnalogMappingResponse = hex"6A" // reply with mapping info

  val ReportFirmware = hex"79" // report name and version of the firmware

  val SamplingInterval = hex"7A" // set the poll rate of the main loop

  val SysExNonRealtime = hex"7E" // MIDI Reserved for non-realtime messages

  val SysExRealtime = hex"7F" // MIDI Reserved for realtime messages
}

