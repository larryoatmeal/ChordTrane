package models.website

import anorm._
import anorm.RowParser
import anorm.SqlParser._
import play.api.Play.current
import play.api.db.DB
import play.api.Logger

case class User(id: Int, email: String, password: String, firstName: String, lastName: String)


object User extends DatabaseObject{
  val dummyUser = User(1, "bossanova@mailinator.com", "password", "Ash", "Ketchum")

	val userParser: RowParser[User] = {
    import anorm.~
    get[Int]("id") ~
    get[String]("email") ~
    get[String]("password") ~
    get[String]("firstName") ~
    get[String]("lastName") map {
      case id ~ email ~ password ~ firstName ~ lastName =>
        User(id, email, password, firstName, lastName)
    } 
  }

  def login(email: String, password: String): Option[User] = DB.withConnection{//returns User if successful, otherwise, return None
  	implicit connection =>
		val potentialUser = SQL(s"""SELECT * FROM users WHERE email = {email} AND password = {password}""").on(
			"email" -> email,
			"password" -> password
		).as(userParser *)

		if(potentialUser.isEmpty) None
		else{
			Some(potentialUser.head)
		}
  }

  def updateUser(user: User) = DB.withConnection{
    implicit connection=>
    SQL("""
      UPDATE users
      SET
      email = {email},
      password = {password},
      firstName = {firstName},
      lastName = {lastName}
      WHERE id = {id}
      """).on(
    "id" -> user.id,
    "email" -> user.email,
    "password" -> user.password,
    "firstName" -> user.firstName,
    "lastName" -> user.lastName
   ).executeUpdate()
  }


  
  




}















