package models.website

import models._
import anorm._
import anorm.RowParser
import anorm.SqlParser._
import play.api.Play.current
import play.api.db.DB
import play.api.Logger


case class PlaybackSettings(songId: Int, bpm: Int, repeats: Int, pianoSettings: PianoSettings, bassSettings: BassSettings)


//case class CompingOptions(minRange: Int, maxRange: Int, closedness: Double, rangeCurvePower: Double)
//case class BassProperties(minRange: Int, maxRange: Int, connectedness: Double, rangeCurvePower: Double)

object PlaybackSettings extends DatabaseObject{

  val playbackSettingsParser: RowParser[PlaybackSettings] = {
    import anorm.~
    get[Int]("songId") ~ 
    get[Int]("bpm") ~ 
    get[Int]("repeats") ~                   
    get[Int]("pianoMinRange") ~     
    get[Int]("pianoMaxRange") ~     
    get[Double]("pianoStayInTessitura") ~ 
    get[Double]("pianoConnectivity") ~  
    get[Int]("bassMinRange") ~  
    get[Int]("bassMaxRange") ~      
    get[Double]("bassStayInTessitura") ~    
    get[Double]("bassConnectivity") map {
    case songId ~ bpm ~ repeats ~ pianoMinRange ~ pianoMaxRange ~ pianoStayInTessitura ~ pianoConnectivity    ~ bassMinRange ~ bassMaxRange   ~ bassStayInTessitura   ~ bassConnectivity =>
        PlaybackSettings(songId, bpm, repeats, 
            PianoSettings(pianoMinRange, pianoMaxRange, pianoStayInTessitura, pianoConnectivity),
            BassSettings(bassMinRange, bassMaxRange, bassStayInTessitura, bassConnectivity)
        )
    }
  }

  def getPlaybackSettings(songId: Int) = getSingleWithID[PlaybackSettings](playbackSettingsParser, songId, "songId", "playbackSettings")

  def updatePlaybackSettings(playbackSettings: PlaybackSettings) = DB.withConnection{
    implicit connection=>
    SQL("""
    UPDATE playbackSettings
    SET
    bpm = {bpm},
    repeats = {repeats},
    pianoMinRange = {pianoMinRange},
    pianoMaxRange = {pianoMaxRange},
    pianoStayInTessitura = {pianoStayInTessitura},
    pianoConnectivity = {pianoConnectivity},
    bassMinRange = {bassMinRange},
    bassMaxRange = {bassMaxRange},
    bassStayInTessitura = {bassStayInTessitura},
    bassConnectivity = {bassConnectivity}
    WHERE songId = {songId}
    """
    ).on(
    "songId" -> playbackSettings.songId,
    "bpm" -> playbackSettings.bpm,
    "repeats" -> playbackSettings.repeats,
    "pianoMinRange" -> playbackSettings.pianoSettings.lower,
    "pianoMaxRange" -> playbackSettings.pianoSettings.upper,
    "pianoStayInTessitura" -> playbackSettings.pianoSettings.stayInTessitura,
    "pianoConnectivity" -> playbackSettings.pianoSettings.connectivity,
    "bassMinRange" -> playbackSettings.bassSettings.lower,
    "bassMaxRange" -> playbackSettings.bassSettings.upper,
    "bassStayInTessitura" -> playbackSettings.bassSettings.stayInTessitura,
    "bassConnectivity" -> playbackSettings.bassSettings.connectivity
   ).executeUpdate()
  }


}

  
  



	  

