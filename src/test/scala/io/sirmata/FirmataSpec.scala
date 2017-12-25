package io.sirmata

import scala.concurrent._
import scala.concurrent.duration._
import scala.concurrent.ExecutionContext._

import akka.stream.scaladsl._
import akka.util.ByteString

import io.sirmata._
import io.sirmata.protocol._
import io.sirmata.stream._
import io.sirmata.utils._

class FirmataSpec extends AkkaSpec {
/*
  "Firmata" should "RequestFirmware" in {
    withEcho {
      case (port, settings, sink) =>
        val graph = Source.single(RequestFirmware)
          .via(Firmata(port, settings)) // send to echo pty
          .toMat(Sink.head)(Keep.right)

        writeIn(ByteString(0xF9, 0x02, 0x03), sink)

        Await.result(graph.run(), 2000.seconds)

    }
  }
*/
}