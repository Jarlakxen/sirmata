package io.sirmata.protocol

import scala.concurrent.duration._
import scala.concurrent.ExecutionContext._

import akka.util.ByteString

import io.sirmata._
import io.sirmata.utils._
import Modes._

class CommandsSpecs extends Spec {

  implicit val context = Firmata.Context(ProtocolVersion(2, 5), ReportFirmware(2, 5, ""), ArduinoUno.board)

  "Commands specification" should "serializer RequestFirmware" in {

    RequestFirmware.toBytes shouldBe List(
      Tokens.StartSysSex,
      Tokens.ReportFirmware,
      Tokens.EndSysSex)
  }

  it should "serializer RequestCapability" in {
    RequestCapability.toBytes shouldBe List(
      Tokens.StartSysSex,
      Tokens.CapabilityQuery,
      Tokens.EndSysSex)
  }

  it should "serializer  RequestAnalogMapping" in {
    RequestAnalogMapping.toBytes shouldBe List(
      Tokens.StartSysSex,
      Tokens.AnalogMappingQuery,
      Tokens.EndSysSex)
  }

  it should "serializer  SetSamplingInterval" in {
    SetSamplingInterval(1026).toBytes shouldBe List(
      Tokens.StartSysSex,
      Tokens.SamplingInterval,
      2.toByte,
      8.toByte,
      Tokens.EndSysSex)
  }

  it should "serializer SetPinMode" in {
    SetPinMode(10, PinMode.DigitalInput).toBytes shouldBe List(
      Tokens.SetPinMode,
      10.toByte,
      PinMode.DigitalInput.toByte)
  }

  it should "deserializer ProtocolVersion" in {
    val inputCmd = ByteString(0xF9, 0x02, 0x03)
    inputCmd.toCommandResponse shouldBe Some(ProtocolVersion(2, 3))
  }

  it should "deserializer ReportFirmware" in {
    val inputCmd = ByteString(
      0xF0, 0x79, 0x02, 0x03, 0x53, 0x00, 0x74, 0x00, 0x61, 0x00,
      0x6E, 0x00, 0x64, 0x00, 0x61, 0x00, 0x72, 0x00, 0x64, 0x00,
      0x46, 0x00, 0x69, 0x00, 0x72, 0x00, 0x6D, 0x00, 0x61, 0x00,
      0x74, 0x00, 0x61, 0x00, 0x2E, 0x00, 0x69, 0x00, 0x6E, 0x00,
      0x6F, 0x00, 0xF7)

    inputCmd.toCommandResponse shouldBe Some(ReportFirmware(2, 3, "StandardFirmata.ino"))
  }

  it should "deserializer CapabilityResponse" in {
    import PinMode._
    val inputCmd = ByteString(0xF0, 0x6C, 0x0, 0x1, 0x1, 0x1, 0x4,
      0xE, 0x7F, 0x0, 0x1, 0x1, 0x1, 0x3, 0x8, 0x4, 0xE, 0x7F, 0x0, 0x1, 0x1, 0x1, 0x4, 0xE, 0x7F, 0x0, 0x1, 0x1, 0x1, 0x3, 0x8,
      0x4, 0xE, 0x7F, 0x0, 0x1, 0x1, 0x1, 0x3, 0x8, 0x4, 0xE, 0x7F, 0x0, 0x1, 0x1, 0x1, 0x4, 0xE, 0x7F, 0x0, 0x1, 0x1, 0x1, 0x4,
      0xE, 0x7F, 0x0, 0x1, 0x1, 0x1, 0x3, 0x8, 0x4, 0xE, 0x7F, 0x0, 0x1, 0x1, 0x1, 0x3, 0x8, 0x4, 0xE, 0x7F, 0x0, 0x1, 0x1, 0x1,
      0x3, 0x8, 0x4, 0xE, 0x7F, 0x0, 0x1, 0x1, 0x1, 0x4, 0xE, 0x7F, 0x0, 0x1, 0x1, 0x1, 0x4, 0xE, 0x7F, 0x0, 0x1, 0x1, 0x1, 0x2,
      0xA, 0x7F, 0x0, 0x1, 0x1, 0x1, 0x2, 0xA, 0x7F, 0x0, 0x1, 0x1, 0x1, 0x2, 0xA, 0x7F, 0x0, 0x1, 0x1, 0x1, 0x2, 0xA, 0x7F, 0x0,
      0x1, 0x1, 0x1, 0x2, 0xA, 0x6, 0x1, 0x7F, 0x0, 0x1, 0x1, 0x1, 0x2, 0xA, 0x6, 0x1, 0xF7)

    inputCmd.toCommandResponse shouldBe Some(CapabilityResponse(
      PinCapabilities(Set(PinCapability(DigitalInput, 1), PinCapability(DigitalOutput, 1), PinCapability(Servo, 14))),
      PinCapabilities(Set(PinCapability(DigitalInput, 1), PinCapability(DigitalOutput, 1), PinCapability(PWM, 8), PinCapability(Servo, 14))),
      PinCapabilities(Set(PinCapability(DigitalInput, 1), PinCapability(DigitalOutput, 1), PinCapability(Servo, 14))),
      PinCapabilities(Set(PinCapability(DigitalInput, 1), PinCapability(DigitalOutput, 1), PinCapability(PWM, 8), PinCapability(Servo, 14))),
      PinCapabilities(Set(PinCapability(DigitalInput, 1), PinCapability(DigitalOutput, 1), PinCapability(PWM, 8), PinCapability(Servo, 14))),
      PinCapabilities(Set(PinCapability(DigitalInput, 1), PinCapability(DigitalOutput, 1), PinCapability(Servo, 14))),
      PinCapabilities(Set(PinCapability(DigitalInput, 1), PinCapability(DigitalOutput, 1), PinCapability(Servo, 14))),
      PinCapabilities(Set(PinCapability(DigitalInput, 1), PinCapability(DigitalOutput, 1), PinCapability(PWM, 8), PinCapability(Servo, 14))),
      PinCapabilities(Set(PinCapability(DigitalInput, 1), PinCapability(DigitalOutput, 1), PinCapability(PWM, 8), PinCapability(Servo, 14))),
      PinCapabilities(Set(PinCapability(DigitalInput, 1), PinCapability(DigitalOutput, 1), PinCapability(PWM, 8), PinCapability(Servo, 14))),
      PinCapabilities(Set(PinCapability(DigitalInput, 1), PinCapability(DigitalOutput, 1), PinCapability(Servo, 14))),
      PinCapabilities(Set(PinCapability(DigitalInput, 1), PinCapability(DigitalOutput, 1), PinCapability(Servo, 14))),
      PinCapabilities(Set(PinCapability(DigitalInput, 1), PinCapability(DigitalOutput, 1), PinCapability(AnalogInput, 10))),
      PinCapabilities(Set(PinCapability(DigitalInput, 1), PinCapability(DigitalOutput, 1), PinCapability(AnalogInput, 10))),
      PinCapabilities(Set(PinCapability(DigitalInput, 1), PinCapability(DigitalOutput, 1), PinCapability(AnalogInput, 10))),
      PinCapabilities(Set(PinCapability(DigitalInput, 1), PinCapability(DigitalOutput, 1), PinCapability(AnalogInput, 10))),
      PinCapabilities(Set(PinCapability(DigitalInput, 1), PinCapability(DigitalOutput, 1), PinCapability(AnalogInput, 10), PinCapability(I2C, 1))),
      PinCapabilities(Set(PinCapability(DigitalInput, 1), PinCapability(DigitalOutput, 1), PinCapability(AnalogInput, 10), PinCapability(I2C, 1)))))
  }

  it should "deserializer AnalogMappingResponse" in {
    val inputCmd = ByteString(0xF0, 0x6A, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0xF7)

    inputCmd.toCommandResponse shouldBe Some(AnalogMappingResponse(List(
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.Supported(0),
      AnalogSupport.Supported(1),
      AnalogSupport.Supported(2),
      AnalogSupport.Supported(3),
      AnalogSupport.Supported(4),
      AnalogSupport.Supported(5))))
  }

  it should "deserializer PinStateResponse" in {
    val inputCmd = ByteString(0xF0, 0x6A, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x7F, 0x0, 0x1, 0x2, 0x3, 0x4, 0x5, 0xF7)

    inputCmd.toCommandResponse shouldBe Some(AnalogMappingResponse(List(
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.NoSupported,
      AnalogSupport.Supported(0),
      AnalogSupport.Supported(1),
      AnalogSupport.Supported(2),
      AnalogSupport.Supported(3),
      AnalogSupport.Supported(4),
      AnalogSupport.Supported(5))))
  }

}