package io.sirmata.protocol

import enumeratum._

object Modes extends Modes

trait Modes {
  
  sealed class PinMode(value: Byte) extends EnumEntry {
    def toByte = value
  }

  object PinMode extends Enum[PinMode] {

    val values = findValues

    def withByteValue(value: Byte) = values.find { _.toByte == value } getOrElse (throw new NoSuchElementException(s"Invalid PinMode 0x${value.toHexString.toUpperCase}"))

    /**
     * Digital pin in input mode
     */
    case object Input extends PinMode(0x00)
    /**
     * Digital pin in output mode
     */
    case object Output extends PinMode(0x01)
    /**
     * Analog pin in analog input mode
     */
    case object Analog extends PinMode(0x02)
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
     * Serial setup
     */
    case object Serial extends PinMode(0x0A)
    /**
     * Ignore setup
     */
    case object Ignore extends PinMode(0x7F)
    /**
     * Unkown setup
     */
    case object Unkown extends PinMode(0x10)

  }

  sealed class PinValue(value: Byte) extends EnumEntry {
    def toByte = value
  }

  object PinValue extends Enum[PinValue] {

    val values = findValues

    case object High extends PinValue(0x01)
    case object Low extends PinValue(0x00)
  }

  sealed class I2CMode(value: Byte) extends EnumEntry {
    def toByte = value
  }

  object I2CMode extends Enum[I2CMode] {

    val values = findValues

    case object Write extends I2CMode(0x00)
    case object Read extends I2CMode(0x01)
    case object ContinuousRead extends I2CMode(0x02)
    case object StopReading extends I2CMode(0x03)
  }

  sealed class SerialMode(value: Byte) extends EnumEntry {
    def toByte = value
  }

  object SerialMode extends Enum[SerialMode] {

    val values = findValues

    case object ContinuousRead extends SerialMode(0x00)
    case object StopReading extends SerialMode(0x01)
  }
}