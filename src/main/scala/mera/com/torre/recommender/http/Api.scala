package mera.com.torre.recommender.http

import akka.actor.ActorSystem
import akka.http.scaladsl.model.StatusCodes
import akka.http.scaladsl.server.{Directives, Route}
import akka.stream.ActorMaterializer
import com.typesafe.scalalogging.LazyLogging
import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
import io.circe.generic.auto._
import mera.com.torre.recommender.engine.Recommender
import mera.com.torre.recommender.http.client.TorreClient

import scala.concurrent.ExecutionContext
import scala.util.{Failure, Success}

trait Api extends Directives with Recommender { this: LazyLogging =>

  implicit def system: ActorSystem

  implicit def mat: ActorMaterializer

  implicit def ec: ExecutionContext

  def client : TorreClient

  val route: Route =
    pathPrefix("user"){
      get{
        path(Segment){ username =>
          onComplete(client.getUserBio(username)){
            case Success(value) => value match{
              case Right(user) => complete(StatusCodes.OK -> user)
              case Left(error) => complete(StatusCodes.BadRequest -> error)
            }
            case Failure(exception) =>
              logger.error("Error consuming bio service", exception)
              complete(StatusCodes.InternalServerError)
          }
        } ~
        path(Segment / "connections"){ username =>
          onComplete(client.getConnections(username, "", 100)){
            case Success(value) => value match {
              case Right(list) => complete(StatusCodes.OK -> list)
              case Left(error) => complete(StatusCodes.BadRequest -> error)
            }
            case Failure(ex) =>
              logger.error("Error consuming connections service", ex)
              complete(StatusCodes.InternalServerError)
          }
        } ~
        pathSingleSlash{
          onComplete(client.getPeople("", 15)){
            case Success(value) => value match {
              case Right(list) => complete(StatusCodes.OK -> list)
              case Left(error) => complete(StatusCodes.BadRequest -> error)
            }
            case Failure(ex) =>
              logger.error("Error consuming people service", ex)
              complete(StatusCodes.InternalServerError)
          }
        } ~
        path( Segment / "recommend"){ username =>
          onComplete(recommendUsers(username)){
            case Success(value) =>
              value match {
                case Right(list) =>
                  logger.info(s"Recommended people: ${list.size}")
                  complete(StatusCodes.OK -> list.take(20))
                case Left(error) => complete(StatusCodes.BadRequest -> error)
              }
            case Failure(ex) =>
              logger.error("Error Recommendation System", ex)
              complete(StatusCodes.InternalServerError)
          }
        } ~
        path("ping"){
          get{
            complete(StatusCodes.OK)
          }
        }
      }
    }
}
