package io.sirmata

import akka.util.ByteString
import akka.stream.scaladsl._

import io.sirmata.protocol._

package object stream {

  val SerializerFlow: Flow[CommandRequest, ByteString, _] = Flow[CommandRequest].via(RequestSerializerStage())

  val DeserializerFlow: Flow[ByteString, CommandResponse, _] = Flow[ByteString].via(ResponseDeserializerStage())

}