package mera.com.torre.recommender.engine

import cats.data.EitherT
import cats.instances.future._
import cats.instances.list._
import cats.syntax.traverse._
import com.typesafe.scalalogging.LazyLogging
import mera.com.torre.recommender.http.client._

import scala.concurrent.{ExecutionContext, Future}

trait Recommender { this: LazyLogging =>

  import CosineSimilarity._

  def client: TorreClient
  implicit def ec: ExecutionContext

  def recommendUsers(username: String): Future[Either[ErrorMessage, List[(User, Double)]]] = {
    logger.info(s"Getting recommendations for $username")

    val getUserBio: EitherT[Future, ErrorMessage, User] = EitherT(client.getUserBio(username))
    val getConnections: EitherT[Future, ErrorMessage, List[ConnectionResponse]] = EitherT(client.getConnections(username, "", 200))
    val getPeople: EitherT[Future, ErrorMessage, List[Person]] = EitherT(client.getPeople("", 2000))

    val peopleToConnect: EitherT[Future, ErrorMessage, List[String]] = for {
      connections <- getConnections
      people <- getPeople
    } yield filterNotConnected(connections, people, username)

    val recommendedPeople: EitherT[Future, ErrorMessage, List[(User, Double)]] = for {
      user <- getUserBio
      people <- peopleToConnect
      x <- people.par.map(p => getRecommendation(user, p)).toList.sequenceU
    } yield x.map(s => (s.anotherUser, s.similarity)).sortBy(_._2).reverse
    recommendedPeople.value
  }

  def getRecommendation(mainUser: User, anotherUserName: String): EitherT[Future, ErrorMessage, UserSimilarity] = {
    for{
      another <- EitherT(client.getUserBio(anotherUserName))
    } yield calculateUserSimilarity(mainUser, another)
  }

  def calculateUserSimilarity(mainUser: User, anotherUser: User): UserSimilarity = {
    val mainVector = VectorSimilarity(1,1,1)
    val vSimilarity = VectorSimilarity(mainUser, anotherUser)
    val cosSimilarity = calculateSimilarity(mainVector, vSimilarity)
    UserSimilarity(cosSimilarity, mainUser, anotherUser)
  }

  def filterNotConnected(connections: List[ConnectionResponse], allUsers: List[Person], mainUser: String): List[String] = {
    val connectedUsers: List[String] = mainUser :: connections.map(_.person.id)
    allUsers.filter(p => !connectedUsers.contains(p.id)).map(_.publicId)
  }
}
