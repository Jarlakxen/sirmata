package io.sirmata

import scala.concurrent._

import akka.actor.ActorSystem
import akka.NotUsed
import akka.serial.SerialSettings
import akka.serial.stream.Serial, Serial._
import akka.stream._
import akka.stream.scaladsl._
import akka.stream.stage._
import akka.util.ByteString

import io.sirmata.protocol._
import io.sirmata.stream._

class Firmata(src: SourceQueueWithComplete[CommandRequest], snk: SinkQueueWithCancel[CommandResponse]) {
  import scala.reflect._
  def <~[Req <: CommandRequest, Res <: CommandResponse: ClassTag](req: Req)(implicit ec: ExecutionContext, flow: CommandsFlow[Req, Res]): Future[Res] =
    src.offer(req).flatMap {
      case QueueOfferResult.Enqueued if classTag[Res].runtimeClass == NoResponse.getClass =>
        Future.successful(NoResponse.asInstanceOf[Res])
      case QueueOfferResult.Enqueued =>
        snk.pull().flatMap {
          case Some(req) => Future.successful(req.asInstanceOf[Res])
          case None => Future.failed(new RuntimeException("Unexpected stream end"))
        }
      case QueueOfferResult.Dropped =>
        Future.failed(new RuntimeException("QueueOfferResult.Dropped"))
      case QueueOfferResult.QueueClosed =>
        Future.failed(new RuntimeException("QueueOfferResult.QueueClosed"))
      case QueueOfferResult.Failure(ex) =>
        Future.failed(ex)
    }

}

object Firmata {
  val DefaultBaudRate = 57600
  val MinSupportedVersion = 2.5

  case class UnsupportedFirmataVersion(version: ProtocolVersion) extends Exception(s"Unsupported firmata version ${version.majorVersion}.${version.majorVersion}. Min version $MinSupportedVersion")
  case class UnexpectedMessage(msg: Any, expected: Class[_]) extends Exception(s"Unexpected message ${msg}. Expected message of type ${expected.getSimpleName}")

  case class Context(protocol: ProtocolVersion, firmware: ReportFirmware, boardLayout: BoardLayout, samplingInterval: Int = 19)
  /*
  def apply(descriptor: String = "/dev/ttyACM0", baudRate: Int = DefaultBaudRate)(implicit system: ActorSystem): Flow[CommandRequest, CommandResponse, Future[Connection]] = {
    val settings = SerialSettings(baudRate)
    apply(descriptor, settings)
  }

  def apply(descriptor: String, settings: SerialSettings)(implicit system: ActorSystem): Flow[CommandRequest, CommandResponse, Future[Connection]] = {
    val serial: Flow[ByteString, ByteString, Future[Connection]] = Serial().open(descriptor, settings)
    SerializerFlow.viaMat(serial.viaMat(DeserializerFlow)(Keep.left))(Keep.right)
  }
  */

  def apply(descriptor: String = "/dev/ttyACM0", baudRate: Int = DefaultBaudRate)(implicit system: ActorSystem): Firmata = {
    val settings = SerialSettings(baudRate)
    apply(descriptor, settings)
  }

  def apply(descriptor: String, settings: SerialSettings)(implicit system: ActorSystem): Firmata = {
    implicit val mat = ActorMaterializer()

    val source = Source.queue(64, OverflowStrategy.backpressure).via(flow(descriptor, settings))

    val (src, snk) = source.toMat(Sink.queue[CommandResponse])(Keep.both).run

    new Firmata(src, snk)
  }

  def flow(descriptor: String = "/dev/ttyACM0", baudRate: Int = DefaultBaudRate)(implicit system: ActorSystem): Flow[CommandRequest, CommandResponse, NotUsed] = { // Flow[CommandRequest, Promise[CommandResponse], NotUsed] = {
    val settings = SerialSettings(baudRate)
    flow(descriptor, settings)
  }

  def flow(descriptor: String, settings: SerialSettings)(implicit system: ActorSystem): Flow[CommandRequest, CommandResponse, NotUsed] = { // Flow[CommandRequest, Promise[CommandResponse], NotUsed] = {
    import GraphDSL.Implicits._
    /*
    val toPromise = BidiFlow.fromGraph(GraphDSL.create() { implicit b =>
      // construct and add the top flow, going outbound
      val outbound = b.add(Flow[CommandRequest].map(req => (req, Promise[CommandResponse]())))

      // construct and add the bottom flow, going inbound
      // val inbound = b.add(Flow[ByteString].map(fromBytes))

      val unzip = b.add(Unzip[CommandRequest, Promise[CommandResponse]])
      val zip = b.add(ZipWith[CommandResponse, Promise[CommandResponse], Promise[CommandResponse]]((res, promise) => promise.success(res)))

      outbound.out ~> unzip.in
      unzip.out1 ~> zip.in1

      // fuse them together into a BidiShape
      BidiShape.of(outbound.in, unzip.out0, zip.in0, zip.out)
    })
*/
    val serial: Flow[ByteString, ByteString, Future[Connection]] = Serial().open(descriptor, settings)
    val reqResFlow: Flow[CommandRequest, CommandResponse, Future[Serial.Connection]] = SerializerFlow.viaMat(serial.viaMat(DeserializerFlow)(Keep.left))(Keep.right)

    //toPromise.atop(new FirmataInitializer).join(reqResFlow)
    BidiFlow.fromGraph(new FirmataInitializer).join(reqResFlow)
  }

}

private[sirmata] class FirmataInitializer extends GraphStage[BidiShape[CommandRequest, CommandRequest, CommandResponse, CommandResponse]] {
  import Firmata._

  val in1 = Inlet[CommandRequest]("FirmataInitializer.in1")
  val out1 = Outlet[CommandRequest]("FirmataInitializer.out1")
  val in2 = Inlet[CommandResponse]("FirmataInitializer.in2")
  val out2 = Outlet[CommandResponse]("FirmataInitializer.out2")

  override def createLogic(inheritedAttributes: Attributes): GraphStageLogic =
    new GraphStageLogic(shape) with StageLogging {

      var protocolVersion: ProtocolVersion = _
      var reportFirmware: ReportFirmware = _

      val topFlowHandlers = new InHandler with OutHandler {
        override def onPush(): Unit =
          grab(in1) match {
            case RequestFirmware =>
              push(out2, reportFirmware)
              pull(in1)
            case req =>
              push(out1, req)
          }
        override def onPull(): Unit =
          if (!hasBeenPulled(in1)) pull(in1)

        override def onUpstreamFailure(ex: Throwable): Unit = {
          log.error("FirmataInitializer.in1 failed", ex)
          super.onUpstreamFailure(ex)
        }
        override def onUpstreamFinish(): Unit = {
          log.warning("FirmataInitializer.in1 Finish")
          //super.onUpstreamFinish()
        }
        override def onDownstreamFinish(): Unit = {
          log.warning("FirmataInitializer.out1 Finish")
          //super.onDownstreamFinish()
        }
      }

      val bottomFlowHandlers = new InHandler with OutHandler {
        override def onPush(): Unit =
          push(out2, grab(in2))

        override def onPull(): Unit =
          if (!hasBeenPulled(in2)) pull(in2)

        override def onUpstreamFailure(ex: Throwable): Unit = {
          log.error("FirmataInitializer.in2 failed", ex)
          super.onUpstreamFailure(ex)
        }
        override def onUpstreamFinish(): Unit = {
          log.warning("FirmataInitializer.in2 Finish")
          // super.onUpstreamFinish()
        }
        override def onDownstreamFinish(): Unit = {
          log.warning("FirmataInitializer.out2 Finish")
          // super.onDownstreamFinish()
        }
      }

      override def preStart(): Unit = {
        setKeepGoing(true)
        read(in2)({
          case pv @ ProtocolVersion(majorVersion, minorVersion) if (majorVersion.toDouble + (minorVersion.toDouble / 10)) >= MinSupportedVersion =>
            log.debug(s"Incoming ProtocolVersion($majorVersion, $minorVersion)")
            read(in2)({
              case rf @ ReportFirmware(majorVersion, minorVersion, firmwareName) =>
                log.debug(s"Incoming ReportFirmware($majorVersion, $minorVersion, $firmwareName)")
                protocolVersion = pv
                reportFirmware = rf
                setHandlers(in1, out1, topFlowHandlers)
                setHandlers(in2, out2, bottomFlowHandlers)
                pull(in1)
              case other =>
                failStage(UnexpectedMessage(other, classOf[ReportFirmware]))
            }, () => ())
          case version @ ProtocolVersion(majorVersion, minorVersion) =>
            val ex = UnsupportedFirmataVersion(version)
            log.error(s"Incoming ProtocolVersion($majorVersion, $minorVersion)", ex)
            failStage(ex)
          case other =>
            failStage(UnexpectedMessage(other, classOf[ProtocolVersion]))
        }, () => ())
      }

      setHandler(in1, totallyIgnorantInput)
      setHandler(out1, ignoreTerminateOutput)
      setHandler(in2, totallyIgnorantInput)
      setHandler(out2, ignoreTerminateOutput)
    }

  override def shape = BidiShape(in1, out1, in2, out2)
}

class FirmataCommander(val flow: Flow[CommandRequest, Promise[CommandResponse], NotUsed]) {

  def send[Req <: CommandRequest](req: Req)(implicit cmdsFlow: CommandsFlow[Req, _]): cmdsFlow.Response = null.asInstanceOf[cmdsFlow.Response]

}