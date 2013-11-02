package models

object Formatter{
	
	def chordString(chord: Chord) = {
		chord.root + chord.suffix + chord.bass.map("/" + _).getOrElse("")
	}

	def print(allLines: AllLines, timeSig: Int) = {
		//Find longest chord
		val musicLines = allLines.lines.collect{case musicLine: MusicLine => musicLine}
		val lengthsOfChordStrings = musicLines.flatMap(_.measures.flatMap(_.chords.map(chordString(_).length))) 
		val longestChordLength = if(lengthsOfChordStrings.isEmpty){0}else{lengthsOfChordStrings.max}


		//println(longestChordLength)

		def printMeasure(measure: Measure) = {
			val chords= measure.chords

			def padWhiteSpace(chordString: String) = {
				chordString + " "*(longestChordLength-chordString.length)
			}
			def slash = {
				"/" + " "*(longestChordLength-1)
			}
			//Template
			// |1111 2222 3333 4444 5555 |
			val measureString = "|" + (1 to timeSig).foldLeft(""){
				(acc, beat) => { 
					val chordOnThisBeat = chords.find{c => c.beat == beat}
					val chordOrSlash = chordOnThisBeat match {
						case Some(chord) => padWhiteSpace(chordString(chord))
						case None => slash
					}
					acc + chordOrSlash + " "
				}
			}

			measureString 
		}

		def printMusicLine(musicLine: MusicLine) = {
			musicLine.measures.map(printMeasure(_)).mkString("","","") + "|" //add ending pipe
		}

		allLines.lines.foldLeft(""){
			(acc, line) => {
				val lineString = line match {
					case m: MusicLine => printMusicLine(m)
					case t: TextLine => t.text
					case e: EmptyLine => ""
				}
				acc + lineString + "\n"
			}
		}
	}
}