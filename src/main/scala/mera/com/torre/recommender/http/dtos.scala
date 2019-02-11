package mera.com.torre.recommender.http

case class ErrorMessage(code: String, message: String)

case class ConnectionResponse(
                person: Person,
                degrees: Long
                             )

case class User(
                 person: Person,
                 strengths: List[Strength],
                 aspirations: List[Aspiration],
                 opportunities: List[Opportunity]
               )

case class Person(
                  id: String,
                  publicId: String,
                  name: String,
                  email: Option[String],
                  professionalHeadline: Option[String],
                  location: Option[String],
                  picture: Option[String],
                  weight: Double,
                  stats: Stats
                 )

case class Strength(
                     code: Long,
                     name: String,
                     weight: Double
                   )

case class Aspiration(
                       name: String
                     )

case class Opportunity(
                        name: String,
                        active: Boolean
                      )

case class Stats(recommendations: Long, signalers: Long)

case class RecommendationsResponse(user: User, recommendations: List[Recommendation])

case class Recommendation(user: User, similarity:Double)