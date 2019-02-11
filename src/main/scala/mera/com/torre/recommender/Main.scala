package mera.com.torre.recommender

import akka.actor.ActorSystem
import akka.http.caching.LfuCache
import akka.http.caching.scaladsl.{Cache, CachingSettings, LfuCacheSettings}
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.Uri
import akka.http.scaladsl.server.directives.CachingDirectives._
import akka.http.scaladsl.server.{RequestContext, RouteResult}
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import mera.com.torre.recommender.http.Api
import mera.com.torre.recommender.http.client.TorreClientImpl

import scala.concurrent.ExecutionContext
import scala.concurrent.duration._
import scala.util.Properties

object Main extends App with Api with LazyLogging {

  implicit val system: ActorSystem      = ActorSystem("test")
  implicit val mat: ActorMaterializer   = ActorMaterializer()
  implicit val ec: ExecutionContext     =  system.dispatcher

  val client = new TorreClientImpl()
  val keyerFunction: PartialFunction[RequestContext, Uri] = {
    case r: RequestContext => r.request.uri
  }

  val defaultCachingSettings: CachingSettings = CachingSettings(system)

  val lfuCacheSettings: LfuCacheSettings = defaultCachingSettings.lfuCacheSettings
    .withInitialCapacity(25)
    .withMaxCapacity(50)
    .withTimeToLive(120.seconds)
    .withTimeToIdle(40.seconds)

  val cachingSettings: CachingSettings =
    defaultCachingSettings.withLfuCacheSettings(lfuCacheSettings)

  val lfuCache: Cache[Uri, RouteResult] = LfuCache(cachingSettings)

  val api = alwaysCache(lfuCache, keyerFunction)(corsHandler(route))

  val port = Properties.envOrElse("PORT", "8080").toInt

  logger.info(s"Port: $port")

  Http().bindAndHandle(handler = api, interface = "0.0.0.0", port = port) map { binding =>
    logger.info("REST interface bound to {}", binding.localAddress)
  } recover {
    case ex => logger.error(s"REST interface could not bind to", ex.getMessage)
  }

}
