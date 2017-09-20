package io.sirmata.stream

import akka.actor._
import akka.stream._
import akka.stream.stage._
import akka.stream.scaladsl._
import akka.event.slf4j.SLF4JLogging
import akka.util.ByteString
import io.sirmata.Firmata
import io.sirmata.protocol._

class RequestSerializerStage extends GraphStage[FlowShape[CommandRequest, ByteString]] with SLF4JLogging {

  val in = Inlet[CommandRequest]("RequestSerializerStage.in")
  val out = Outlet[ByteString]("RequestSerializerStage.out")

  override val shape = FlowShape.of(in, out)

  override def createLogic(attr: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {
      setHandler(in, new InHandler {
        override def onPush() {
          push(out, toBytes(grab(in)))
        }
      })
      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          pull(in)
        }
      })
    }

  def toBytes(cmd: CommandRequest): ByteString = {
    implicit val ctx: Firmata.Context = null
    val bytes = cmd.toByteString
    log.debug("Sending bytes: " + bytes.map(b => "0x" + b.toHexString.toUpperCase).mkString(", "))
    bytes
  }

}

object RequestSerializerStage {

  def apply(): GraphStage[FlowShape[CommandRequest, ByteString]] = new RequestSerializerStage()

}

class RequestSerializerStage2 extends GraphStage[FlowShape[(Firmata.Context, CommandRequest), (Firmata.Context, ByteString)]] with SLF4JLogging {

  val in = Inlet[(Firmata.Context, CommandRequest)]("RequestSerializerStage.in")
  val out = Outlet[(Firmata.Context, ByteString)]("RequestSerializerStage.out")

  override val shape = FlowShape.of(in, out)

  override def createLogic(attr: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {
      setHandler(in, new InHandler {
        override def onPush() {
          val (ctx, cmd) = grab(in)
          push(out, toBytes(ctx, cmd))
        }
      })
      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          pull(in)
        }
      })
    }

  def toBytes(implicit ctx: Firmata.Context, cmd: CommandRequest): (Firmata.Context, ByteString) = {
    val bytes = cmd.toByteString
    log.debug("Sending bytes: " + bytes.map(b => "0x" + b.toHexString.toUpperCase).mkString(", "))
    (ctx, bytes)
  }

}

object RequestSerializerStage2 {

  def apply(): RequestSerializerStage2 = new RequestSerializerStage2()

}