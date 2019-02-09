package mera.com.torre.recommender.http

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait Api extends Directives { this: LazyLogging =>

  implicit def system: ActorSystem

  implicit def mat: ActorMaterializer

  implicit def ec: ExecutionContext

  def client : TorreClient

  val route: Route = pathPrefix("user"){
    get{
      pathSingleSlash{
        onComplete(client.getUserBio("torrenegra")){
          case Success(value) => value match{
            case Right(user) => complete(StatusCodes.OK -> user)
            case Left(error) => complete(StatusCodes.BadRequest -> error)
          }
          case Failure(exception) =>
            logger.error("Error WebServer", exception)
            complete(StatusCodes.InternalServerError)
        }
      }
    }
  }~ pathPrefix("ping"){
    get{
      complete(StatusCodes.OK)
    }
  }

}
