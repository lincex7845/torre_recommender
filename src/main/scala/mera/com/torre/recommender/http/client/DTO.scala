package mera.com.torre.recommender.http.client

trait DTO

case class ErrorMessage(code: String, message: String) extends DTO

case class User(
                 person: Person,
                 strengths: List[Strength],
                 aspirations: List[Aspiration],
                 opportunities: List[Opportunity]
               ) extends DTO

case class Person(
                   publicId: String,
                  name: String,
                  email: String,
                  professionalHeadline: String,
                  location: String,
                  picture: String,
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