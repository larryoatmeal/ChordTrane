package controllers

import play.api._
import play.api.mvc._
import models.website._
import play.api.libs.json._
import play.api.libs.functional.syntax._
import play.api.Logger

import models.website._


//Song
object JSONmaster extends Controller with JSONconversions{

	def getJson(code: JsValue => play.api.mvc.Result) = Action(parse.json) { 
  	request =>
  	code(request.body)
  }

	def getSong(id: Int) = Action{
		implicit request =>
    val song =  Song.getSong(id)
    println(song)
		Ok(Json.toJson(Song.getSong(id)))
	}
  def getSongs(userId: Int) = Action{
		implicit request =>
		Ok(Json.toJson(Song.getSongsByUser(userId)))
	}


  def saveSong = getJson{
    json =>
    val updatedSong = json.as[Song]
    //Logger.debug("Hello")
    Song.updateSong(updatedSong)
    Ok("Succesfully saved song")
  }

  def newSong(userId: Int) = Action{
    implicit request => 
    val id = Song.newSong(userId) //WARNING: Check if this always works
    Ok(id.toString)
  }

  def deleteSong(songId: Int) = Action{
    implicit request => 
    Song.deleteSong(songId)
    Ok(s"Deleted $songId")
  }

  def getPlaybackSettings(songId: Int) = Action{
		implicit request =>
		Ok(Json.toJson(PlaybackSettings.getPlaybackSettings(songId)))
	}

  def savePlaybackSettings = getJson{
    json =>
    val updatedPlaybackSettings = json.as[PlaybackSettings]
    //Logger.debug("Hello")
    PlaybackSettings.updatePlaybackSettings(updatedPlaybackSettings)
    Ok("Succesfully saved PlaybackSettings")
  }

  def formatText = getJson{
    json =>
    val song = json.as[Song]
    //Logger.debug("Hello")
    Ok(Song.formatText(song))
  }

  def exportMusicXML = getJson{
    json =>
    val song = json.as[Song]
    val path = Song.exportMusicXML(song)
    Ok(path)
  }

  def download(path: String) = Action{
    Ok.sendFile(new java.io.File(path))
  }

  def playback = getJson{
    json =>
    val song = json.as[Song]
    val path = Song.playback(song)
    Ok(path)
  }

  def midiAsBase64(path: String) = Action{
    implicit request =>
    import org.apache.commons.codec.binary.Base64
    import org.apache.commons.io.FileUtils.readFileToByteArray
    import java.io.File

    val file = new File(path)
    val bytes = readFileToByteArray(file)
    val encoded = Base64.encodeBase64(bytes)

    val encodedString = new String(encoded)

    def Midi64Coded(string: String) = {
      "data:audio/midi;base64," + string
    }

    Ok(Midi64Coded(encodedString))
  }

  def midiString64(path: String) = Action{
    implicit request => 
    
    Ok("Midi string")
  }






}
  