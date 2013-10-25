package controllers

import play.api._
import play.api.mvc._
import models.website._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Logger
import play.api.libs.Files

import models.website._

trait JSONconversions{

  //object to JSON
  implicit object SongWrites extends Writes[Song] {
  	def writes(s: Song) = Json.obj(
			"id" -> Json.toJson(s.id),
			"rawText" -> Json.toJson(s.rawText),
			"title" -> Json.toJson(s.title),
			"composer" -> Json.toJson(s.composer),
			"dateCreated" -> Json.toJson(s.dateCreated),
			"timeSig" -> Json.toJson(s.timeSig),
			"currentKey" -> Json.toJson(s.currentKey),
			"destinationKey" -> Json.toJson(s.destinationKey),
			"transposeOn" -> Json.toJson(s.transposeOn),
			"romanNumeral" -> Json.toJson(s.romanNumeral),
			"userId" -> Json.toJson(s.userId)	
		) 
  }
  implicit object UserWrites extends Writes[User] {
  	def writes(u: User) = Json.obj(
			"id" -> Json.toJson(u.id),
			"email" -> Json.toJson(u.email),
			"password" -> Json.toJson(u.password),
			"firstName" -> Json.toJson(u.firstName),
			"lastName" -> Json.toJson(u.lastName)
		) 
  }
  implicit object PianoSettingsWrites extends Writes[models.PianoSettings] {
  	def writes(p: models.PianoSettings) = Json.obj(
			"lower" -> Json.toJson(p.lower),
			"upper" -> Json.toJson(p.upper),
			"stayInTessitura" -> Json.toJson(p.stayInTessitura),
			"connectivity" -> Json.toJson(p.connectivity)
		) 
  }
  implicit object BassSettingsWrites extends Writes[models.BassSettings] {
  	def writes(b: models.BassSettings) = Json.obj(
			"lower" -> Json.toJson(b.lower),
			"upper" -> Json.toJson(b.upper),
			"stayInTessitura" -> Json.toJson(b.stayInTessitura),
			"connectivity" -> Json.toJson(b.connectivity)
		) 
  }
  implicit object PlaybackSettingsWrites extends Writes[PlaybackSettings] {
  	def writes(p: PlaybackSettings) = Json.obj(
			"songId" -> Json.toJson(p.songId),
			"bpm" -> Json.toJson(p.bpm),
			"repeats" -> Json.toJson(p.repeats),
			"pianoSettings" -> Json.toJson(p.pianoSettings),
			"bassSettings" -> Json.toJson(p.bassSettings)
		) 
  }
  implicit object LabelAndIdWrites extends Writes[LabelAndId] {
  	def writes(l: LabelAndId) = Json.obj(
			"label" -> Json.toJson(l.label),
			"id" -> Json.toJson(l.id)
		) 
  }
  
  //JSon to object
  implicit val SongReads: Reads[Song] = (
    (JsPath \ "id").read[Int] and
		(JsPath \ "rawText").read[String] and
		(JsPath \ "title").read[String] and
		(JsPath \ "composer").read[String] and
		(JsPath \ "dateCreated").read[String] and
		(JsPath \ "timeSig").read[Int] and
		(JsPath \ "currentKey").read[String] and
		(JsPath \ "destinationKey").read[String] and
		(JsPath \ "transposeOn").read[Boolean] and
		(JsPath \ "romanNumeral").read[Boolean] and
		(JsPath \ "userId").read[Int]
  )(Song.apply _) 
  implicit val UserReads: Reads[User] = (
    (JsPath \ "id").read[Int] and
		(JsPath \ "email").read[String] and
		(JsPath \ "password").read[String] and
		(JsPath \ "firstName").read[String] and
		(JsPath \ "lastName").read[String]
  )(User.apply _) 
  implicit val PianoSettingsReads: Reads[models.PianoSettings] = (
    (JsPath \ "lower").read[Int] and
		(JsPath \ "upper").read[Int] and
		(JsPath \ "stayInTessitura").read[Double] and
		(JsPath \ "connectivity").read[Double]
  )(models.PianoSettings.apply _)
  implicit val BassSettingsReads: Reads[models.BassSettings] = (
    (JsPath \ "lower").read[Int] and
		(JsPath \ "upper").read[Int] and
		(JsPath \ "stayInTessitura").read[Double] and
		(JsPath \ "connectivity").read[Double]
  )(models.BassSettings.apply _)
  implicit val PlaybackSettingsReads: Reads[PlaybackSettings] = (
    (JsPath \ "songId").read[Int] and
		(JsPath \ "bpm").read[Int] and
		(JsPath \ "repeats").read[Int] and
		(JsPath \ "pianoSettings").read[models.PianoSettings] and
		(JsPath \ "bassSettings").read[models.BassSettings]
  )(PlaybackSettings.apply _)
  implicit val LabelAndIdReads: Reads[LabelAndId] = (
    (JsPath \ "label").read[String] and
		(JsPath \ "id").read[Int]
  )(LabelAndId.apply _)
}