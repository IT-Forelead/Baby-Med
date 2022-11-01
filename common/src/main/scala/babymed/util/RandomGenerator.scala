package babymed.util

import scala.collection.mutable

import babymed.refinements.Password
import babymed.syntax.refined.commonSyntaxAutoRefineV

object RandomGenerator {
  def randomPassword(len: Int): Password = {
    val rand = new scala.util.Random(System.nanoTime)
    val sb = new mutable.StringBuilder(len)
    val ab = "123456789ABCDEFGHJKLMNPQRSTUVWXYZabcdefghjkmnpqrstuvwxyz"
    for (i <- 0 until len)
      sb.append(ab(rand.nextInt(ab.length)))
    sb.toString
  }
}
