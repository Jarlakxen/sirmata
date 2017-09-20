package io.sirmata.stream

import scala.concurrent.ExecutionContext._

import akka.actor.ActorSystem
import akka.stream._
import akka.stream.scaladsl._

import akka.stream.scaladsl._
import akka.stream.testkit.scaladsl._
import akka.util.ByteString

import io.sirmata._
import io.sirmata.protocol._

class CommandsDeserializerStageSpec extends AkkaSpec {

  "DeviceCommandEmitter" should "We get 1 command from 1 byte sequence" in {

    val inputCmd = ByteString(0xF9, 0x02, 0x03)

    Source.single(inputCmd)
      .via(DeserializerFlow)
      .runWith(TestSink.probe[CommandResponse])
      .request(1)
      .expectNext(ProtocolVersion(2, 3))
      .expectComplete()
      .cancel()
  }

  it should "where we get 2 command from 3 byte sequence" in {

    val inputSeq1 = ByteString(0xF9, 0x02, 0x03, 0xF0)
    val inputSeq2 = ByteString(0x79, 0x02, 0x03, 0x53, 0x00, 0x74, 0x00, 0x61, 0x00, 0x6E, 0x00, 0x64, 0x00, 0x61, 0x00, 0x72, 0x00, 0x64, 0x00)
    val inputSeq3 = ByteString(0x46, 0x00, 0x69, 0x00, 0x72, 0x00, 0x6D, 0x00, 0x61, 0x00, 0x74, 0x00, 0x61, 0x00, 0x2E, 0x00, 0x69, 0x00, 0x6E, 0x00, 0x6F, 0x00, 0xF7)

    Source(List(inputSeq1, inputSeq2, inputSeq3))
      .via(DeserializerFlow)
      .runWith(TestSink.probe[CommandResponse])
      .request(3)
      .expectNext(ProtocolVersion(2, 3), ReportFirmware(2, 3, "StandardFirmata.ino"))
      .expectComplete()
      .cancel()
  }

}