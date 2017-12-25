package io.sirmata.protocol

import scodec.Codec
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

  val DigitalMessage2: Codec[Unit] = hex"90"
  val DigitalMessage = 0x90.toByte

  /**
   * send data for an analog pin (or PWM)
   */
  val AnalogMessage = 0xE0.toByte

  /**
   * enable analog input by pin #
   */
  val ReportAnalog = 0xC0.toByte

  /**
   * enable digital input by port pair
   */
  val ReportDigital = 0xD0.toByte

  /**
   * set a pin to INPUT/OUTPUT/PWM/etc
   */
  val SetPinMode = 0xF4.toByte

  /**
   * set the pin value
   */
  val SetPinValue = 0xF5.toByte

  /**
   * report protocol version
   */
  val ReportVersion = 0xF9.toByte

  /**
   * reset from MIDI
   */
  val SystemReset = 0xFF.toByte

  /**
   * start a MIDI Sysex message
   */
  val StartSysSex = 0xF0.toByte

  /**
   * start a MIDI Sysex message
   */
  val EndSysSex = 0xF7.toByte

}

// Extended command set using sysex (0-127/0x00-0x7F)
/* 0x00-0x0F reserved for user-defined commands */

private[protocol] trait SysExCommandsTokens {

  val ReservedCommand = 0x00.toByte // 2nd SysEx data byte is a chip-specific command (AVR, PIC, TI, etc).
  val ServoConfig = 0x70.toByte // set max angle, minPulse, maxPulse, freq
  val StringData = 0x71.toByte // a string message with 14-bits per byte
  val ShiftData = 0x75.toByte // a bitstream to/from a shift register
  val I2cRequest = 0x76.toByte // send an I2C read/write request
  val I2cREPLY = 0x77.toByte // a reply to an I2C read request
  val I2cConfig = 0x78.toByte // config I2C settings such as delay times and power pins
  val ExtendedAnalog = 0x6F.toByte // analog write (PWM, Servo, etc) to any pin
  val PinStateQuery = 0x6D.toByte // ask for a pin's current mode and value
  val PinStateResponse = 0x6E.toByte // reply with pin's current mode and value
  val CapabilityQuery = 0x6B.toByte // ask for supported modes and resolution of all pins
  val CapabilityResponse = 0x6C.toByte // reply with supported modes and resolution
  val AnalogMappingQuery = 0x69.toByte // ask for mapping of analog to pin numbers
  val AnalogMappingResponse = 0x6A.toByte // reply with mapping info
  val ReportFirmware = 0x79.toByte // report name and version of the firmware
  val SamplingInterval = 0x7A.toByte // set the poll rate of the main loop
  val SysExNonRealtime = 0x7E.toByte // MIDI Reserved for non-realtime messages
  val SysExRealtime = 0x7F.toByte // MIDI Reserved for realtime messages
}
