package mera.com.torre.recommender.client

case class User(
                 person: Person,
                 strengths: List[Strength],
                 aspirations: List[Aspiration],
                 opportunities: List[Opportunity]
               )

case class Person(publicId: String,
                  name: String,
                  email: String,
                  professionalHeadline: String,
                  location: String,
                  picture: String,
                  weight: String)

case class Strength(
                     code: Long,
                     name: String,
                     weight: String
                   )

case class Aspiration(
                       name: String
                     )

case class Opportunity(
                        name: String,
                        active: Boolean
                      )