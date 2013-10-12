package models

object MusicXMLGenerator{

	//Right hand valid Finale
	val finaleMap = Map(
		"" -> "",
		"M" -> "",
		"maj" -> "",
		"min" -> "m",
		"m" -> "m",
		"-" -> "m",
		"dim" -> "dim",
		"aug" -> "aug",
		"+" -> "aug",
		"maj7" -> "maj7",
		"?7" -> "maj7",
		"?" -> "maj7",
		"M7" -> "maj7",
		"min7" -> "m7",
		"m7" -> "m7",
		"-7" -> "m7",
		"7" -> "7",
		"maj9" -> "maj9",
		"?9" -> "maj9",
		"M9" -> "maj9",
		"min9" -> "m9",
		"m9" -> "m9",
		"-9" -> "m9",
		"9" -> "9",
		"maj11" -> "maj11",
		"?11" -> "maj11",
		"M11" -> "maj11",
		"min11" -> "m11",
		"m11" -> "m11",
		"-11" -> "m11",
		"11" -> "11",
		"maj13" -> "maj13",
		"?13" -> "maj13",
		"M13" -> "maj13",
		"min13" -> "m13",
		"m13" -> "m13",
		"-13" -> "m13",
		"13" -> "13",
		"sus2" -> "sus",
		"sus4" -> "sus",
		"sus" -> "sus",
		"sus7" -> "sus"
	)

	def xmlPitch(note: String) = {
		val step = note.head
		val accidental = note.filter(_ != step)
		val alter = accidental match {
			case "" => 0
			case "#" =>  1
			case "##" => 2
			case "b" => -1
			case "bb" => -2
		}
		Tuple2(step.toString, alter.toString)
	}

	def xmlKind(quality: String) = {
		
		quality.toLowerCase match {
			case "" | "maj" => "major"
			case "m" => "minor"
			case "aug" | "+" => "augmented"
			case "dim" | "\u00B0" => "diminished"
			case "7" => "dominant"
			case "maj7" => "major-seventh"
			case "min7" | "m7" => "minor-seventh"
			case "dim7" | "\u00B07"=> "diminished-seventh"
			case "halfdim7" | "m7b5" | "\u00D8"=> "half-diminished"
			case "aug7" | "7#5" => "augmented-seventh"
			case "mM7" => "major-minor"
			case "6" => "major-sixth"
			case "m6" => "minor-sixth"
			case "sus2" => "suspended-second"
			case "sus4" => "suspended-fourth"
			case "9" => "dominant-ninth"
			case "maj9" => "major-ninth"
			case "min9" | "m9" => "minor-ninth"
			case "11" => "dominant-11th"
			case "maj11" => "major-11th"
			case "min11" | "m11" => "minor-11th"
			case "13" => "dominant-13th"
			case "maj13" => "major-13th"
			case "min13" | "m11" => "minor-13th"
			case _ => "other"
		}
	}

	val xmlKey = (destKey: String) => destKey match {
				case "C" => 0
				case "F" => -1
				case "Bb" => -2
				case "Eb" => -3
				case "Ab" => -4
				case "Db" => -5
				case "Gb" => -6
				case "Cb" => -7
				case "G" => 1
				case "D" => 2
				case "A" => 3
				case "E" => 4
				case "B" => 5
				case "F#" => 6
				case "C#" => 7
	 			case _ => 0
	}


	def musicXML(measures: List[Measure], keySig: String, timeSig: Int, title: String, composer: String) = {
		// <?xml version="1.0" encoding="UTF-8" standalone="no"?>
		// <!DOCTYPE score-partwise PUBLIC
		// "-//Recordare//DTD MusicXML 3.0 Partwise//EN"
		// "http://www.musicxml.org/dtds/partwise.dtd">

		val measuresNumbered = measures.zipWithIndex.map{case (measure, index) => (measure, index + 1)}
		val key = xmlKey(keySig)

		<score-partwise version="3.0">

			<movement-title>title</movement-title>
			<identification>
				<creator type="composer">composer</creator>
			</identification>
			<part-list>
			<score-part id="P1">
			<part-name></part-name>
			</score-part>
			</part-list>

			<part id="P1">
			{for(m <- measuresNumbered) yield m match {
				case (measure, measureNumber) => 
					<measure number={measureNumber.toString}>
							<attributes>
							{if (measureNumber == 1)
								<divisions>1</divisions>
								<key>
								<fifths>{key}</fifths>
								</key>
								<time>
								<beats>{timeSig}</beats>
								<beat-type>4</beat-type>
								</time>
								<clef>
								<sign>G</sign>
								<line>2</line>
								</clef>
								<measure-style>
          				<slash type = "start"></slash>
        				</measure-style>
							}
							</attributes>
						
					{for( i <- 1 to timeSig) yield {

							val activeChord = measure.chords.find(chord => chord.beat == i)
							val rest = activeChord.isEmpty 
							
							//Need to indicate somehow that this is first part is part of the xml
							//Add an outer tag
							
								if (rest) {
									<note>
										<rest></rest>
										<duration>1</duration>
										<type>quarter</type>
									</note>	
								}else{
									val chord = activeChord.get

									<harmony>	
								        <root>
								          <root-step>{xmlPitch(chord.root)._1}</root-step>
								          <root-alter>{xmlPitch(chord.root)._2}</root-alter>
								        </root>
								        <kind halign="center" text= {chord.suffix}>{xmlKind(chord.suffix)}</kind>
								        {if (!chord.bass.isEmpty)
								        <bass>
						          			<bass-step>{xmlPitch(chord.bass.get)._1}</bass-step>
						          			<bass-alter>{xmlPitch(chord.bass.get)._2}</bass-alter>
						       		    </bass>
						       			}
						       	 	</harmony>
						       	 	<note>
										<pitch>
											<step>{xmlPitch(chord.root)._1}</step>
											<alter>{xmlPitch(chord.root)._2}</alter>
											<octave>4</octave>
										</pitch>
										<duration>1</duration>
										<type>quarter</type>
									</note>
								}						
						}
					}
					</measure>
				}
			}
			</part>
		</score-partwise>
	}
}