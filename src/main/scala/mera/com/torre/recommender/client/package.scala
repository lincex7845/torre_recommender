package mera.com.torre.recommender

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}

import scala.concurrent.Future

package object client {

  def sendRequest(r: HttpRequest)(implicit as: ActorSystem): Future[HttpResponse] =
    Http().singleRequest(r)

}
