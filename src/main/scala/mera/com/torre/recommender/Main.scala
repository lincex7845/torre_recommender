package mera.com.torre.recommender

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import mera.com.torre.recommender.client.TorreClientImpl
import mera.com.torre.recommender.http.Api

import scala.concurrent.ExecutionContext

object Main extends App with Api with LazyLogging {

  implicit val system: ActorSystem                               = ActorSystem("test")
  implicit val mat: ActorMaterializer                   = ActorMaterializer()
  implicit val ec: ExecutionContext =  system.dispatcher

  val client = new TorreClientImpl()

  val api = route

  Http().bindAndHandle(handler = api, interface = "0.0.0.0", port = 8080) map { binding =>
    logger.info("REST interface bound to {}", binding.localAddress)
  } recover {
    case ex => logger.error(s"REST interface could not bind to", ex.getMessage)
  }


}
