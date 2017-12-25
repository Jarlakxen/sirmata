package io.sirmata

object utils {

  object ArduinoUno {
    val board = BoardLayout {
      List(
        SerialPin(0),
        SerialPin(1),
        DigitalPin(2),
        PwmPin(3),
        DigitalPin(4),
        PwmPin(5),
        PwmPin(6),
        DigitalPin(7),
        DigitalPin(8),
        PwmPin(9),
        PwmPin(10),
        PwmPin(11),
        DigitalPin(12),
        DigitalPin(13),
        AnalogPin(14, "A0"),
        AnalogPin(15, "A1"),
        AnalogPin(16, "A2"),
        AnalogPin(17, "A3"),
        AnalogPin(18, "A4"),
        AnalogPin(19, "A5"))
    }
  }

}