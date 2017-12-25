package io.sirmata.protocol

object Modes extends Modes

trait Modes {

  sealed class PinMode(val byteValue: Byte)

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

    def of(value: Byte) = values.find { _.byteValue == value } getOrElse (throw new NoSuchElementException(s"Invalid PinMode 0x${value.toHexString.toUpperCase}"))

    /**
     * Digital pin in input mode
     */
    case object DigitalInput extends PinMode(0x00)
    /**
     * Digital pin in output mode
     */
    case object DigitalOutput extends PinMode(0x01)
    /**
     * Analog pin in analog input mode
     */
    case object AnalogInput extends PinMode(0x02)
    /**
     * Digital pin in PWM output mode
     */
    case object PWM extends PinMode(0x03)
    /**
     * Digital pin in Servo output mode
     */
    case object Servo extends PinMode(0x04)
    /**
     * shiftIn/shiftOut mode
     */
    case object Shift extends PinMode(0x05)
    /**
     * Pin included in I2C setup
     */
    case object I2C extends PinMode(0x06)
    /**
     * Onewire setup
     */
    case object Onewire extends PinMode(0x07)
    /**
     * Stepper setup
     */
    case object Stepper extends PinMode(0x08)
    /**
     * Stepper setup
     */
    case object Encoder extends PinMode(0x09)
    /**
     * Serial setup
     */
    case object Serial extends PinMode(0x0A)
    /**
     * Ignore setup
     */
    case object InputPullUp extends PinMode(0x0B)
    /**
     * Ignore setup
     */
    case object Ignore extends PinMode(0x7F)
    /**
     * Unkown setup
     */
    case object Unkown extends PinMode(0x10)

  }

  sealed class PinValue(val byteValue: Byte)

  object PinValue {

    case object High extends PinValue(0x01)
    case object Low extends PinValue(0x00)

    val values = List(High, Low)
  }

  sealed class I2CMode(val byteValue: Byte)

  object I2CMode {
    case object Write extends I2CMode(0x00)
    case object Read extends I2CMode(0x01)
    case object ContinuousRead extends I2CMode(0x02)
    case object StopReading extends I2CMode(0x03)

    val values = List(Write, Read, ContinuousRead, StopReading)
  }

  sealed class SerialMode(val byteValue: Byte)

  object SerialMode {

    case object ContinuousRead extends SerialMode(0x00)
    case object StopReading extends SerialMode(0x01)

    val values = List(ContinuousRead, StopReading)
  }
}