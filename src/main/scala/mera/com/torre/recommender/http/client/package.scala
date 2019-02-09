package mera.com.torre.recommender.http

import akka.actor.ActorSystem
import akka.http.scaladsl.Http
import akka.http.scaladsl.model.{HttpRequest, HttpResponse}
import akka.stream.ActorMaterializer

import scala.concurrent.duration._
import scala.concurrent.{ExecutionContext, Future}

package object client {

  def sendRequest(r: HttpRequest)(implicit as: ActorSystem, mat: ActorMaterializer, ec: ExecutionContext): Future[HttpResponse] = {
    println(s"Sending request to: ${r.uri}")
   Http().singleRequest(r).flatMap(hres => hres.toStrict(5.seconds))
  }


}
