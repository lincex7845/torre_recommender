package mera.com.torre.recommender

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import mera.com.torre.recommender.http.{Api, CorsSupport}
import mera.com.torre.recommender.http.client.TorreClientImpl

import scala.concurrent.ExecutionContext
import scala.util.Properties

object Main extends App with Api with CorsSupport with LazyLogging {

  implicit val system: ActorSystem                               = ActorSystem("test")
  implicit val mat: ActorMaterializer                   = ActorMaterializer()
  implicit val ec: ExecutionContext =  system.dispatcher

  val client = new TorreClientImpl()

  val api = route

  val port = Properties.envOrElse("PORT", "8080").toInt

  logger.info(s"Port: $port")

  Http().bindAndHandle(handler = corsHandler(api), interface = "0.0.0.0", port = port) map { binding =>
    logger.info("REST interface bound to {}", binding.localAddress)
  } recover {
    case ex => logger.error(s"REST interface could not bind to", ex.getMessage)
  }


}
