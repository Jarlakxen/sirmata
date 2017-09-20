package io.sirmata

import java.util.{ Timer, TimerTask }
import java.util.concurrent.TimeUnit

import scala.collection.JavaConverters._
import scala.concurrent.duration._

import org.scalactic.TypeCheckedTripleEquals
import org.scalatest._
import org.scalatest.time._
import org.slf4j.LoggerFactory

import akka.actor.ActorSystem
import akka.stream.ActorMaterializer
import akka.testkit.TestKitBase

/**
 * Boilerplate remover and preferred testing style.
 */
trait Spec extends FlatSpec
    with Matchers
    with OptionValues
    with Inside
    with Retries
    with TryValues
    with Inspectors
    with TypeCheckedTripleEquals
    with BeforeAndAfterAll { self =>

  val log = LoggerFactory.getLogger(this.getClass)

}

trait AkkaSpec extends Spec with TestKitBase {
  implicit lazy val system = ActorSystem()
  implicit lazy val ec = system.dispatcher
  implicit lazy val materializer = ActorMaterializer()

  override def afterAll(): Unit = shutdown(system)
}