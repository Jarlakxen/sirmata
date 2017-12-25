package io.sirmata.protocol

import scodec.bits._
import scodec.codecs.literals._

object Modes extends Modes

trait Modes {

  sealed class PinMode(val byteValue: ByteVector)

  object PinMode {

    val values = List(
      DigitalInput,
      DigitalOutput,
      AnalogInput,
      PWM,
      Servo,
      Shift,
      I2C,
      Onewire,
      Stepper,
      Encoder,
      Serial,
      InputPullUp,
      Ignore,
      Unkown)

    def of(value: Byte) = values.find { _.byteValue.toByte(false) == value } getOrElse (throw new NoSuchElementException(s"Invalid PinMode 0x${value.toHexString.toUpperCase}"))

    /**
     * Digital pin in input mode
     */
    case object DigitalInput extends PinMode(hex"00")
    /**
     * Digital pin in output mode
     */
    case object DigitalOutput extends PinMode(hex"01")
    /**
     * Analog pin in analog input mode
     */
    case object AnalogInput extends PinMode(hex"02")
    /**
     * Digital pin in PWM output mode
     */
    case object PWM extends PinMode(hex"03")
    /**
     * Digital pin in Servo output mode
     */
    case object Servo extends PinMode(hex"04")
    /**
     * shiftIn/shiftOut mode
     */
    case object Shift extends PinMode(hex"05")
    /**
     * Pin included in I2C setup
     */
    case object I2C extends PinMode(hex"06")
    /**
     * Onewire setup
     */
    case object Onewire extends PinMode(hex"07")
    /**
     * Stepper setup
     */
    case object Stepper extends PinMode(hex"08")
    /**
     * Stepper setup
     */
    case object Encoder extends PinMode(hex"09")
    /**
     * Serial setup
     */
    case object Serial extends PinMode(hex"0A")
    /**
     * Ignore setup
     */
    case object InputPullUp extends PinMode(hex"0B")
    /**
     * Ignore setup
     */
    case object Ignore extends PinMode(hex"7F")
    /**
     * Unkown setup
     */
    case object Unkown extends PinMode(hex"10")

  }

  sealed class PinValue(val byteValue: ByteVector)

  object PinValue {

    case object High extends PinValue(hex"01")
    case object Low extends PinValue(hex"00")

    val values = List(High, Low)
  }

  sealed class I2CMode(val byteValue: ByteVector)

  object I2CMode {
    case object Write extends I2CMode(hex"00")
    case object Read extends I2CMode(hex"01")
    case object ContinuousRead extends I2CMode(hex"02")
    case object StopReading extends I2CMode(hex"03")

    val values = List(Write, Read, ContinuousRead, StopReading)
  }

  sealed class SerialMode(val byteValue: ByteVector)

  object SerialMode {

    case object ContinuousRead extends SerialMode(hex"00")
    case object StopReading extends SerialMode(hex"01")

    val values = List(ContinuousRead, StopReading)
  }
}