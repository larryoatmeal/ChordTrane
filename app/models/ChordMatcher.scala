package models


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