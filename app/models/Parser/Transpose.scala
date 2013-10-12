package models


class Roman(currentKey: String){
	import Note._

	def romanize(chord: Chord) = {
		def getRoman(note: String) = {
			val currentMidi = midify(note)
			val currentLetter = note.head.toString
			val currentKeyLetter = currentKey.head.toString

			//Interval distance (1, 2, 3, 4, 5, 6, 7)
			val intervalInt = (doremiMod(currentLetter) - doremiMod(currentKeyLetter) + 7)%7 + 1
			//Distance from root, measure ascending
			val midiDistance = (midify(note) - midify(currentKey) + 12)%12

			//Map perfect/major intervals to midi note differences
			val intervalMap = Map(
				1 -> 0,
				2 -> 2,
				3 -> 4,
				4 -> 5,
				5 -> 7,
				6 -> 9,
				7 -> 11
			)

			val romanExtension = intervalInt match {
				case 1 | 4 | 5 => { //perfect intervals
					(midiDistance - intervalMap(intervalInt)) match { //Discrepancy between interval and perfect
						case 0 => ""
						case 1 => "#" //Up 1
						case -1 => "b" //Down 1
						case 2 => "##"
						case -2 => "bb"
						case _ => "X"
					}
				}
				case 2 | 3 | 6 |7 => {
					(midiDistance - intervalMap(intervalInt)) match { //Discrepancy between interval and major
						case 0 => ""
						case 1 => "#" //Up one
						case -1 => "b" //Down one
						case -2 => "bb" //Down two
						case -11 => "#" //Special case:
						//For #VII, midiDistance is 0 but intervalMap(intervalInt) is 11
						//0 -11 = -11
						case _ => "X"
					}
				}
			}

			val romanMap = Map(
				1 -> "I",
				2 -> "II",
				3 -> "III",
				4 -> "IV",
				5 -> "V",
				6 -> "VI",
				7 -> "VII"
			)
			romanExtension + romanMap(intervalInt)
		}
		Chord(getRoman(chord.root), chord.suffix, chord.bass.map(getRoman(_)), chord.beat)
	}
}

class Transpose(currentKey: String, destinationKey: String){
	import Note._

	val keyMidi = midify(currentKey)
	val keyLetter = doremiMod(currentKey.head.toString)
	val newKeyMidi = midify(destinationKey)
	val newKeyLetter = doremiMod(destinationKey.head.toString)

	def transpose(chord: Chord) = {
		def newNoteName(note: String) = {
			//Measure each note with relation to root
			//Apply same relationship to new key
			val midiDelta = midify(note) - keyMidi
			val letterDelta = doremiMod(note.head.toString) - keyLetter
			val transposedMIDI = (newKeyMidi + midiDelta + 12)%12
			val desiredLetter = reverseDoremiMod((newKeyLetter + letterDelta + 7)%7)
			findSpelling(transposedMIDI, desiredLetter)
		}

		Chord(newNoteName(chord.root), chord.suffix, chord.bass.map(newNoteName(_)), chord.beat)
	}
}