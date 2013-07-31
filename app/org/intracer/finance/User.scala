package org.intracer.finance


class User(val fullname: String) {



}


object User {
  def login(username: String, password: String): Option[User] = {
    if (username == "intracer@gmail.com" && password == "123") {
      Some(new User("Illia Korniiko"))
    } else {
      None
    }
  }
}