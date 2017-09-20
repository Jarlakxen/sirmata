package io

import java.io.{ File, IOException }
import java.nio.file.Files

import scala.concurrent._
import scala.concurrent.duration._
import scala.sys.process._
import scala.util.control.NonFatal

import akka.actor._
import akka.serial.SerialSettings
import akka.stream.ActorMaterializer
import akka.stream.scaladsl._
import akka.testkit._
import akka.util.ByteString
import akka.NotUsed

package object sirmata {

  final val SetupTimeout = 500.milliseconds

  type InWriter = ByteString => Unit

  def writeIn(bytes: ByteString, sink: Sink[ByteString, Future[_]], wait: Long = 100)(implicit ec: ExecutionContext, mat: ActorMaterializer) {
    Future {
      Thread.sleep(wait)
      Source.single(bytes).toMat(sink)(Keep.right).run()
    }
  }

  def withEcho[A](action: (String, SerialSettings, Sink[ByteString, Future[_]]) => A): A = {
    val dir = Files.createTempDirectory("sirmata-pty").toFile
    val in = new File(dir, "in")
    val out = new File(dir, "out")

    val socat = try {
      val s = Seq(
        "socat",
        "-d -d",
        s"pty,raw,b115200,echo=0,link=${in.getAbsolutePath}",
        s"pty,raw,b115200,echo=0,link=${out.getAbsolutePath}").run(ProcessLogger(println(_)), false)
      Thread.sleep(SetupTimeout.toMillis) // allow ptys to set up
      s
    } catch {
      case NonFatal(ex) =>
        throw new IOException("Error running echo service, make sure the program 'socat' is installed", ex)
    }

    try {
      val sink: Sink[ByteString, Future[_]] = FileIO.toPath(in.toPath)
      val result = action(out.getAbsolutePath, SerialSettings(baud = 115200), sink)
      Thread.sleep(SetupTimeout.toMillis) // allow for async cleanup before destroying ptys
      result
    } finally {
      socat.destroy()
      dir.delete()
    }
  }

}