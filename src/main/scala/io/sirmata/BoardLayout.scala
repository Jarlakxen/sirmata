package io.sirmata

import io.sirmata.protocol.Modes._

case class BoardLayout(pins: List[Pin[_]]) {
  def port(id: Int): List[Pin[_]] = pins.slice(id * 8, 8);
}

sealed trait Pin[T <: Pin[T]] {
  val index: Int
  val name: String = index.toString
  val supportedModes: Set[PinMode]
  val currentMode: PinMode
  val currentValue: Long

  val port = index / 8

  def withMode(newMode: PinMode): T
  def withValue(newValue: Long): T
}

object Pin {
  val DefaultPinMode: PinMode = PinMode.Unkown
  val DefaultPinValue: Long = 0l
}

final case class DigitalPin(
  index: Int,
  currentMode: PinMode = Pin.DefaultPinMode,
  currentValue: Long = Pin.DefaultPinValue) extends Pin[DigitalPin] {
  override val supportedModes = Set(PinMode.DigitalInput, PinMode.DigitalOutput)

  def withMode(newMode: PinMode): DigitalPin = this.copy(currentMode = newMode)
  def withValue(newValue: Long): DigitalPin = this.copy(currentValue = newValue)
}

final case class PwmPin(
  index: Int,
  currentMode: PinMode = Pin.DefaultPinMode,
  currentValue: Long = Pin.DefaultPinValue) extends Pin[PwmPin] {
  override val supportedModes = Set(PinMode.DigitalInput, PinMode.DigitalOutput, PinMode.PWM)

  def withMode(newMode: PinMode): PwmPin = this.copy(currentMode = newMode)
  def withValue(newValue: Long): PwmPin = this.copy(currentValue = newValue)
}

final case class SerialPin(
  index: Int,
  currentMode: PinMode = Pin.DefaultPinMode,
  currentValue: Long = Pin.DefaultPinValue) extends Pin[SerialPin] {
  override val supportedModes = Set(PinMode.DigitalInput, PinMode.DigitalOutput, PinMode.Serial)

  def withMode(newMode: PinMode): SerialPin = this.copy(currentMode = newMode)
  def withValue(newValue: Long): SerialPin = this.copy(currentValue = newValue)
}

final case class AnalogPin(
  index: Int,
  override val name: String,
  currentMode: PinMode = Pin.DefaultPinMode,
  currentValue: Long = Pin.DefaultPinValue) extends Pin[AnalogPin] {
  override val supportedModes = Set(PinMode.AnalogInput)

  def withMode(newMode: PinMode): AnalogPin = this.copy(currentMode = newMode)
  def withValue(newValue: Long): AnalogPin = this.copy(currentValue = newValue)
}
