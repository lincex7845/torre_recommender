package mera.com.torre.recommender.client

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.{Accept, `Content-Type`}
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.stream.ActorMaterializer
import cats.syntax.either.catsSyntaxEitherId
import monix.eval.Task
import monix.execution.Scheduler

trait TorreClient {

  implicit def as: ActorSystem

  implicit def mat: ActorMaterializer

  def getUserBio(username: String): Task[Either[ErrorMessage, User]]

  def getConnections(username: String, criterion: String, limit: Int): Task[Either[ErrorMessage, List[User]]]

  def getPeople(criterion: String, limit: Int): Task[Either[ErrorMessage, List[User]]]

}

class TorreClientImpl(implicit override val as: ActorSystem, override val mat: ActorMaterializer, val s: Scheduler) extends TorreClient {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  private val getUserBioPath = "api/bios/%"
  private val getConnectionsPath = "api/people/%s/connections"
  private val getPeoplePath = "api/people"

  private val defaultRequest = HttpRequest(HttpMethods.GET)
    .withHeaders(Accept(MediaTypes.`application/json`), `Content-Type`(ContentTypes.`application/json`))

  override def getUserBio(username: String): Task[Either[ErrorMessage, User]] = {

    val rq = defaultRequest.withUri(uriFrom(getUserBioPath, username))

    for {
      res <- Task.deferFuture(sendRequest(rq))
      u <- handleResponse[User](res)
    } yield u
  }

  private def handleResponse[A](response: HttpResponse)(implicit uma: Unmarshaller[HttpResponse, A], ums: Unmarshaller[HttpResponse, ErrorMessage]): Task[Either[ErrorMessage, A]] = {
    if (response.status.isSuccess()) {
      Task.deferFuture(Unmarshal(response).to[A]).map(_.asRight[ErrorMessage])
    }
    else {
      Task.deferFuture(Unmarshal(response).to[ErrorMessage]).map(_.asLeft[A])
    }
  }

  private def uriFrom(path: String, pathParameters: String*) =
    Uri.Empty
      .withScheme(Uri.httpScheme(securedConnection = true))
      .withHost("torre.bio")
      .withPath(Uri.Path(String.format(path, pathParameters)))

  override def getConnections(username: String, criterion: String, limit: Int): Task[Either[ErrorMessage, List[User]]] = {
    val parameters = Query(Map("q" -> criterion, "limit" -> limit.toString))
    val uri = uriFrom(getConnectionsPath, username).withQuery(parameters)
    val rq = defaultRequest.withUri(uri)

    for {
      res <- Task.deferFuture(sendRequest(rq))
      users <- handleResponse[List[User]](res)
    } yield users
  }

  override def getPeople(criterion: String, limit: Int): Task[Either[ErrorMessage, List[User]]] = {
    val parameters = Query(Map("q" -> criterion, "limit" -> limit.toString))
    val uri = uriFrom(getPeoplePath).withQuery(parameters)
    val rq = defaultRequest.withUri(uri)

    for {
      res <- Task.deferFuture(sendRequest(rq))
      users <- handleResponse[List[User]](res)
    } yield users
  }
}