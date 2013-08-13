package org.intracer.finance

import java.security.MessageDigest
import java.math.BigInteger


class User(val fullname: String) {


}


object User {
  def login(username: String, password: String): Option[User] = {
    if (sha1(username + "/" + password) == "bc3c8804c34b060f9f7379aedf5c7a940edba989") {
      Some(new User(***REMOVED***))
    } else {
      None
    }
  }

  def sha1(input: String) = {

    val digest = MessageDigest.getInstance("SHA-1");

    digest.update(input.getBytes(), 0, input.length());

    new BigInteger(1, digest.digest()).toString(16);
  }

}