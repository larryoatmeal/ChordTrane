package models


object Song{
	val DummySong = Song(
		"""(Misty)
		|Ebmaj7 | Bbm7 Eb7 | Abmaj7 | Abmin7 Db7|
		|Ebmaj7 Cm7 | Fm7 Bb7 | Gm7 C7 | Fm7 Bb7|
		(Hello)
		|Ebmaj7 Cm7 | Fm7 Bb7 | Gm7 C7 | Fm7 Bb7|
		|Ebmaj7 Cm7 | Fm7 Bb7 | Gm7 C7 | Fm7 Bb7|
		|Ebmaj7.2 Cm7.5 | Fm7/Db Bb7 | Gm7 C7 | Fm7 Bb7|
		|Ebmaj7 Cm7 | Fm7 Bb7 | Gm7 C7 | Fm7 Bb7|
		""",
		"Eb",
		"B",
		4,
		true,
		false
	)
	

	def parseSong(song: Song) = {
		val allLines = Parser.parse(song.rawMusic, song.timeSig)
		//Helper for updating deeply nested chords
		val linesModifyChords = (function: (Chord) => Chord) => allLines.copy(
			lines = allLines.lines.map{ line => line match {
					case musicLine: MusicLine => {
						musicLine.copy(
							measures = musicLine.measures.map{
								measure => measure.copy(
									chords = measure.chords.map{
										chord => function(chord)
									}
								)
							}
						)
					}
					case other => {
						other
					}
				}
			}
		)
		//tranpose if necessary
		val modifiedLines: AllLines = 
		if (song.transpose){
			val Transposer = new Transpose(song.currentKey, song.destinationKey)
			linesModifyChords(Transposer.transpose(_))
		}
		else if(song.roman){
			val Romanizer = new Roman(song.currentKey)
			linesModifyChords(Romanizer.romanize(_))
		}
		else{
				allLines
		}
		modifiedLines
	}

	def printSong(song: Song) = {
		val allLines = parseSong(song)
		Formatter.print(allLines, song.timeSig)
	}

	def exportXML(song: Song) = {
		val allLines = parseSong(song)
		val measures = measuresOnly(allLines)
		val musicXML = MusicXMLGenerator.musicXML(measures, song.destinationKey, song.timeSig, "Title", "Composer")
		
		import scala.xml.dtd.{DocType, PublicID} 
		val docType = new DocType("score-partwise", PublicID("-//Recordare//DTD MusicXML 3.0 Partwise//EN", "http://www.musicxml.org/dtds/partwise.dtd"), Nil)
    val filename = "Title" + "_" + "Composer" + ".xml"
    scala.xml.XML.save(s"MusicXML/$filename", musicXML, "UTF-8", true, docType)
	}

	def measuresOnly(allLines: AllLines) = allLines.lines.collect{case m: MusicLine => m}.flatMap(_.measures)

}



case class Song(
	rawMusic: String,
	currentKey: String, 
	destinationKey:String,
	timeSig: Int,
	transpose: Boolean,
	roman: Boolean)



//DEPRECATED*******************************************************************


// val transposedLines = allLines.lines.map{
			// 	line => line match {
			// 		case m: MusicLine => {
			// 			val transposedMeasures = m.measures.map{
			// 				measure => {
			// 					val tranposedChords = measure.chords.map{
			// 						chord => Transposer.transpose(chord)
			// 					}
			// 					Measure(tranposedChords)
			// 				}
			// 			}
			// 			MusicLine(transposedMeasures)
			// 		}
			// 		case other => {
			// 			other
			// 		}
			// 	}
			// }
			// AllLines(transposedLines)