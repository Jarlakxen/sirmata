package io.sirmata.stream

import akka.actor._
import akka.stream._
import akka.stream.scaladsl._
import akka.stream.stage._
import akka.event.slf4j.SLF4JLogging
import akka.util.ByteString
import io.sirmata.protocol._

class ResponseDeserializerStage extends GraphStage[FlowShape[ByteString, CommandResponse]] with SLF4JLogging {

  private var buffer = ByteString.empty

  val in = Inlet[ByteString]("ResponseDeserializerStage.in")
  val out = Outlet[CommandResponse]("ResponseDeserializerStage.out")

  override val shape = FlowShape.of(in, out)

  override def createLogic(attr: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) {
      setHandler(in, new InHandler {
        override def onPush(): Unit = {
          process(grab(in)) match {
            case Some(cmd) => push(out, cmd)
            case None => pull(in)
          }
        }
      })
      setHandler(out, new OutHandler {
        override def onPull(): Unit = {
          pull(in)
        }
      })
    }

  private def process(elem: ByteString): Option[CommandResponse] = {
    buffer ++= elem

    log.debug("Receiving bytes: " + elem.map(b => "0x" + b.toHexString.toUpperCase).mkString(", "))

    extractor(buffer.take(MinCommandSize), buffer.drop(MinCommandSize)) match {
      case (Some(cmd), remaining) =>
        buffer = remaining
        Some(cmd)

      case (None, _) =>
        None
    }
  }

  private def extractor(current: ByteString, remaining: ByteString): (Option[CommandResponse], ByteString) =
    (current: Option[CommandResponse]) match {
      case value @ Some(cmd) => (value, remaining)
      case None if remaining.nonEmpty => extractor(current :+ remaining.head, remaining.tail)
      case None => (None, ByteString.empty)
    }
}

object ResponseDeserializerStage {

  def apply(): ResponseDeserializerStage = new ResponseDeserializerStage()

}