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
      override def preStart(): Unit = {
        setKeepGoing(true)
      }
      setHandler(in, new InHandler {
        override def onPush() {
          val cmd = grab(in)
          log.trace(s"Serializing $cmd")
          push(out, toBytes(cmd))
        }
        override def onUpstreamFinish(): Unit = {
          log.warn("RequestSerializerStage.in Finish")
          super.onUpstreamFinish()
        }
        override def onUpstreamFailure(ex: Throwable): Unit = {
          log.error("RequestSerializerStage.in failed", ex)
          super.onUpstreamFailure(ex)
        }
      })
      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          pull(in)
        }
        override def onDownstreamFinish(): Unit = {
          log.warn("RequestSerializerStage.out Finish")
          super.onDownstreamFinish()
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