package models.minecraft

import anorm._
import anorm.RowParser
import anorm.SqlParser._
import play.api.Play.current
import play.api.db.DB
import play.api.Logger
import org.joda.time.DateTime
import models.JodaHelper

case class Offer(businessmanid: Int, offertype: String, title: String, offering: String, inExchangeFor: String, description: String,
	active: Boolean, completed: Boolean, clientid: Int, postDate: DateTime, completeDate: DateTime, id: Int)


// CREATE TABLE minecraftOffer (
// 	businessmanid 		int 			NOT NULL,
// 	offertype			varchar(255)	NOT NULL,
// 	title				varchar(255)	NOT NULL,
// 	offering   			text 			NOT NULL,
// 	inExchangeFor		text			NOT NULL,
// 	description			text,
// 	active 				boolean			NOT NULL,
// 	completed			boolean 		NOT NULL,
// 	clientid			int,
// 	postDate			datetime 		NOT NULL,
// 	completeDate		datetime,
// 	id 					int 			NOT NULL AUTO_INCREMENT,
// 	PRIMARY KEY(id)
// );








// object Offer extends models.website.DatabaseObject{

// 	val TableName = "minecraftOffer"

// 	val offerParser: RowParser[Offer] = {
// 		import anorm.~
// 		get[Int]("businessmanid") ~
// 		get[String]("offertype") ~
// 		get[String]("title") ~
// 		get[String]("offering") ~
// 		get[String]("inExchangeFor") ~
// 		get[String]("description") ~
// 		get[Boolean]("active") ~
// 		get[Boolean]("completed") ~
// 		get[Option[Int]]("clientid") ~
// 		get[DateTime]("postDate") ~
// 		get[Option[DateTime]]("completeDate") ~
// 		get[String]("id") map{
// 			case businessmanid ~ offertype ~ title ~ offering ~ inExchangeFor ~ description ~ description ~ active ~ completed ~ clientid ~ postDate ~ completeDate ~ id =>
// 			Offer(businessmanid, offertype, title, offering, inExchangeFor, description, active, completed, clientid, postDate, completeDate, id)
// 		}
// 	}

// 	def create(offer: Offer) = DB.withConnection{
// 		implicit connection =>
// 	    SQL(s"""
// 	    INSERT INTO $TableName (offername, password)
// 	    VALUES ({offername}, {password})
// 	    """).on(
// 	      "offername" -> offer.offername,
// 	      "password" -> offer.password
// 	      ).executeInsert().get.toInt
// 	}

// 	def read(id: Int) = getSingleWithID[Offer](offerParser, id, "id", TableName) 

// 	def update(offer: Offer) = DB.withConnection{
// 		implicit connection =>
// 		SQL(
// 			s"""
// 			UPDATE $TableName
// 			SET
// 			offername = {offername},
// 			password = {password}
// 			"""
// 		).on(
// 			"offername" -> offer.offername,
// 			"password" -> offer.password
// 		).executeUpdate() == 1
// 	}

// 	def delete(id: Int) =  deleteSingle(id, "id", TableName)

// 	def checkCredentials(offername: String, password: String) = DB.withConnection{
// 		implicit connection =>
// 		SQL(s"""
// 			SELECT COUNT(*) as c FROM $TableName
// 			WHERE offername = {offername} AND password = {password}
// 			""").on(
// 			"offername" -> offername,
// 			"password" -> password
// 		).apply().head[Long]("c") == 1
// 	}
	
// }


















