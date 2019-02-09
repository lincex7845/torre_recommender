package mera.com.torre.recommender.engine

import language.postfixOps


object CosineSimilarity{

  private [this] def dot(x: Array[Double], y: Array[Double]) = {
    val products: Array[Double] = for {
      (a, b) <- x zip y
    } yield a * b
    products.sum
  }

  private [this] def magnitude(x: Array[Double]): Double = {
    math.sqrt(x map (i => math.pow(i, 2)) sum )
  }

  def calculateSimilarity(vector1: VectorSimilarity, vector2: VectorSimilarity): Double ={
    val v1 = vector1.toArray
    val v2 = vector2.toArray
    val crossMagnitude = magnitude(v1) * magnitude(v2)
    if(crossMagnitude.equals(0d)) 0
    else dot(v1, v2) / crossMagnitude
  }

}
