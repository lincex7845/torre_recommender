package mera.com.torre.recommender.engine

import mera.com.torre.recommender.http._

case class VectorSimilarity(strengths: Double, aspirations: Double, opportunities: Double){
  def toArray = Array(strengths, aspirations, opportunities)
}

object VectorSimilarity{
  import TFIDFCalculator._

  def apply(mainUser: User, user: User): VectorSimilarity = {
    val s1 = mainUser.strengths.map(_.name)
    val s2 = user.strengths.map(_.name)
    val strengthsSimilarity = calculateStrengthsTFIDF(s1, s2)

    val a1 = mainUser.aspirations.map(_.name)
    val a2 = user.aspirations.map(_.name)
    val aspirationsSimilarity = calculateStrengthsTFIDF(a1, a2)

    val o1 = mainUser.opportunities.filter(_.active).map(_.name)
    val o2 = user.opportunities.filter(_.active).map(_.name)
    val opportunitiesSimilarity = calculateStrengthsTFIDF(o1,o2)


    new VectorSimilarity(strengthsSimilarity, aspirationsSimilarity, opportunitiesSimilarity)
  }

}

case class UserSimilarity(similarity: Double, mainUser:User, anotherUser: User)
