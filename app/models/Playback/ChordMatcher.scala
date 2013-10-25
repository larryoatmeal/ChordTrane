package models

case class ChordOption(chord: Array[Int], variance: Int, mean: Int)


object ChordMatcher{

	//Find closest chord of alternatives
	
	
	def closestChord(precedingChord: Array[Int], potentialDestinationChords: Array[Array[Int]]) = {
		val potentialDestinationChordsWithVariance = potentialDestinationChords.map{
			potentialChord => 
			Tuple2(potentialChord, chordVariance(precedingChord, potentialChord))
		}
		potentialDestinationChordsWithVariance.minBy(_._2)._1//get chord with minimum variance
	}


	//Distance squared to whichever chord note is closest to the note in question
	def distanceSquaredToClosestNote(note: Int, noteOptions: Array[Int]) = {
		noteOptions.map{
			noteOption => 
			val difference = note - noteOption
			difference * difference
		}.min
	}

	def chordVariance(xChord: Array[Int], yChord: Array[Int]) = {
		yChord.map(yNote => distanceSquaredToClosestNote(yNote, xChord)).sum
	}

	def chordMean(chord: Array[Int]) = {
		chord.sum / chord.length
	}



	def normalizeChord(precedingChord: Array[Int], potentialChord: Array[Int]) = {
		//Compare lowest note in preceding chord to lowest note in potential chord
		val lowestOctaveAdjust = Note.octaveAdjust(potentialChord.head, precedingChord.head)
		val lowestDiffence = Math.abs(potentialChord.head + lowestOctaveAdjust - precedingChord.head)

		//Compare highest note in preceding chord to highest note in potential chord
		val highestOctaveAdjust = Note.octaveAdjust(potentialChord.last, precedingChord.last)
		val highestDiffence = Math.abs(potentialChord.last + highestOctaveAdjust - precedingChord.last)

		//choose whichever tranposition has smallest distance
		//For example CEGB => CFGA should find octave that finds closest match bewteen C and C, not B and A
		if (lowestDiffence > highestDiffence){
			potentialChord.map(_ + highestOctaveAdjust)
		}else{
			potentialChord.map(_ + lowestOctaveAdjust)
		}
	}

	def nextChord(precedingChord: Array[Int], potentialDestinationChords: Array[Array[Int]]) = {
		val chordsWithVariance = potentialDestinationChords.map{
			potentialChord => {
				val normalizedChord = normalizeChord(precedingChord, potentialChord)
				Tuple2(normalizedChord, chordVariance(normalizedChord, precedingChord))
			}
		}


		//chordsWithVariance.map(t => println(t._1.toList + t._2.toString))
		chordsWithVariance.minBy(_._2)._1//get chord with minimum variance
	}

	def chordOptions(precedingChord: Array[Int], potentialDestinationChords: Array[Array[Int]]) = {
		potentialDestinationChords.map{
			potentialChord => {
				val normalizedChord = normalizeChord(precedingChord, potentialChord)
				ChordOption(normalizedChord, chordVariance(normalizedChord, precedingChord), chordMean(normalizedChord))
			}
		}
	}
	


}
















//DEPRECATED ***********************************************


// def chordVariance(precedingChord: Array[Int], destinationChord: Array[Int]): Int = {
	// 	precedingChord.foldLeft(0){
	// 		(acc, precedingChordNote) => {
	// 			val sumOfDifferencesSquared = destinationChord.foldLeft(0){
	// 				(acc, destinationChordNote) => {
	// 					val difference = destinationChordNote - precedingChordNote
	// 					val differenceSquared = difference * difference
	// 					println(differenceSquared)
	// 					acc + differenceSquared
	// 				}
	// 			}
	// 			acc + sumOfDifferencesSquared
	// 		}
	// 	}
	// }