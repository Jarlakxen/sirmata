package io.sirmata

import io.sirmata.protocol.Modes._

case class Board(pins: List[Pin]) {
  def port(id: Int): List[Pin] = pins.slice(id * 8, 8);
}

case class Pin(index: Int, name: String, supportedModes: List[PinMode], currentMode: PinMode = PinMode.Unkown, currentValue: Long = 0l) {
  val port = index / 8
}

object DigitalPin {
  def apply(index: Int): Pin = Pin(index, index.toString, List(PinMode.Input, PinMode.Output))
}

object PwmPin {
  def apply(index: Int): Pin = Pin(index, index.toString, List(PinMode.Input, PinMode.Output, PinMode.PWM))
}

object SerialPin {
  def apply(index: Int): Pin = Pin(index, index.toString, List(PinMode.Input, PinMode.Output, PinMode.Serial))
}

object AnalogPin {
  def apply(index: Int, name: String): Pin = Pin(index, name, List(PinMode.Analog))
}