package models.minecraft

import anorm._
import anorm.RowParser
import anorm.SqlParser._
import play.api.Play.current
import play.api.db.DB
import play.api.Logger


case class User(username: String, password: String, id: Int)

object User extends models.website.DatabaseObject{

	val TableName = "minecraftUser"

	val userParser: RowParser[User] = {
		import anorm.~
		get[String]("username") ~
		get[String]("password") ~
		get[Int]("id") map{
			case username ~ password ~ id =>
			User(username, password, id)
		}
	}

	def create(user: User) = DB.withConnection{
		implicit connection =>
	    SQL(s"""
	    INSERT INTO $TableName (username, password)
	    VALUES ({username}, {password})
	    """).on(
	      "username" -> user.username,
	      "password" -> user.password
	      ).executeInsert().get.toInt
	}

	def read(id: Int) = getSingleWithID[User](userParser, id, "id", TableName) 

	def update(user: User) = DB.withConnection{
		implicit connection =>
		SQL(
			s"""
			UPDATE $TableName
			SET
			username = {username},
			password = {password}
			WHERE id = {id}
			"""
		).on(
			"username" -> user.username,
			"password" -> user.password,
			"id" -> user.id
		).executeUpdate() == 1
	}

	def delete(id: Int) =  deleteSingle(id, "id", TableName)

	def checkCredentials(username: String, password: String) = DB.withConnection{
		implicit connection =>
		SQL(s"""
			SELECT COUNT(*) as c FROM $TableName
			WHERE username = {username} AND password = {password}
			""").on(
			"username" -> username,
			"password" -> password
		).apply().head[Long]("c") == 1
	}
	
}


















