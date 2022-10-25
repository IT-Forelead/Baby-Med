package babymed.util

import java.security.MessageDigest

object BabyMedStringUtil{


  def createHash(text: String): String = {
    val digest = MessageDigest.getInstance("SHA1")
    digest.digest(text.getBytes("UTF-8")).map(0xff & _).map("%02x".format(_)).mkString
  }


}
