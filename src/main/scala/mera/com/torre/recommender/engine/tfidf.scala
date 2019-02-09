package mera.com.torre.recommender.engine

object Util {
  def countOccurrences(list1: List[String], list2: List[String]): Double = {
    list2.map{ element =>
      list1.count(_.equals(element)).toDouble
    }.sum
  }
}

object TF{

  import Util._

  private [this] def calculateTF(list1: List[String], list2: List[String]): Double = {
    if(list2.isEmpty) 0d
    else countOccurrences(list1, list2) / list2.size.toDouble
  }

  def calculateStrengthsTF(s1: List[String], s2: List[String]): Double = {
    calculateTF(s1,s2)
  }

  def calculateAspirationsTF(a1: List[String], a2: List[String]) : Double = {
    calculateTF(a1,a2)
  }

  def calculateOpportunitiesTF(o1: List[String], o2: List[String]): Double = {
    calculateTF(o1,o2)
  }
}

object IDF{

  import Util._

  private [this] def calculateIDF(list1: List[String], list2: List[String]): Double = {
    val ocurrences = countOccurrences(list1, list2)
    if (ocurrences == 0d) 0d
    else Math.log10(list2.size.toDouble /  ocurrences)
  }

  def calculateStrengthIDF(s1: List[String], s2: List[String]): Double = {
    calculateIDF(s1,s2)
  }

  def calculateAspirationsIDF(a1: List[String], a2: List[String]): Double = {
    calculateIDF(a1,a2)
  }

  def calculateOpportunitiesIDF(o1: List[String], o2: List[String]): Double = {
    calculateIDF(o1,o2)
  }
}

object TFIDFCalculator {

  import IDF._
  import TF._

  def calculateStrengthsTFIDF(s1: List[String], s2: List[String]): Double = {
    calculateStrengthsTF(s1, s2) * calculateStrengthIDF(s1, s2)
  }

  def calculateAspirationsTFIDF(a1: List[String], a2: List[String]): Double = {
    calculateAspirationsTF(a1, a2) * calculateAspirationsIDF(a1, a2)
  }

  def calculateOpportunitiesTFIDF(o1: List[String], o2: List[String]): Double = {
    calculateOpportunitiesTF(o1, o2) * calculateOpportunitiesIDF(o1, o2)
  }

}
