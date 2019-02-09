package mera.com.torre.recommender.http.client

import akka.actor.ActorSystem
import akka.http.scaladsl.model.Uri.Query
import akka.http.scaladsl.model._
import akka.http.scaladsl.model.headers.Accept
import akka.http.scaladsl.unmarshalling.{Unmarshal, Unmarshaller}
import akka.stream.ActorMaterializer

import scala.concurrent.{ExecutionContext, Future}

trait TorreClient {

  implicit def as: ActorSystem

  implicit def mat: ActorMaterializer

  implicit def ec: ExecutionContext

  def getUserBio(username: String): Future[Either[ErrorMessage, User]]

  def getConnections(username: String, criterion: String, limit: Int): Future[Either[ErrorMessage, List[User]]]

  def getPeople(criterion: String, limit: Int): Future[Either[ErrorMessage, List[User]]]

}

class TorreClientImpl(implicit val as: ActorSystem, val mat: ActorMaterializer, val ec: ExecutionContext) extends TorreClient {

  import de.heikoseeberger.akkahttpcirce.FailFastCirceSupport._
  import io.circe.generic.auto._

  private val getUserBioPath = "/api/bios/%s"
  private val getConnectionsPath = "/api/people/%s/connections"
  private val getPeoplePath = "/api/people"

  private val defaultRequest = HttpRequest(HttpMethods.GET)
    .withHeaders(Accept(MediaTypes.`application/json`))

  private def handleResponse[A](response: HttpResponse)(implicit uma: Unmarshaller[ResponseEntity, A]): Future[Either[ErrorMessage, A]] = {
    println("Handling response")
    if (response.status.isSuccess()) {
     Unmarshal(response.entity).to[A].map(Right(_))
    }
    else {
      Unmarshal(response.entity).to[ErrorMessage].map(Left(_))
    }
  }

  private def uriFrom(path: String, userName: Option[String]) = {
    val baseUri = Uri.Empty
      .withScheme(Uri.httpScheme(securedConnection = true))
      .withHost("torre.bio")

      baseUri.withPath(userName.fold(Uri.Path(path))(u => Uri.Path(String.format(path,u))))
  }



  override def getUserBio(username: String): Future[Either[ErrorMessage, User]] = {

    val rq = defaultRequest.withUri(uriFrom(getUserBioPath, Some(username)))

    for {
      res <- sendRequest(rq)
      user <- handleResponse[User](res)
    } yield user
  }

  override def getConnections(username: String, criterion: String, limit: Int): Future[Either[ErrorMessage, List[User]]] = {
    val parameters = Query(Map("q" -> criterion, "limit" -> limit.toString))
    val uri = uriFrom(getConnectionsPath, Some(username)).withQuery(parameters)
    val rq = defaultRequest.withUri(uri)

    for {
      res <- sendRequest(rq)
      users <- handleResponse[List[User]](res)
    } yield users
  }

  override def getPeople(criterion: String, limit: Int): Future[Either[ErrorMessage, List[User]]] = {
    val parameters = Query(Map("q" -> criterion, "limit" -> limit.toString))
    val uri = uriFrom(getPeoplePath, None).withQuery(parameters)
    val rq = defaultRequest.withUri(uri)

    for {
      res <- sendRequest(rq)
      users <- handleResponse[List[User]](res)
    } yield users
  }
}