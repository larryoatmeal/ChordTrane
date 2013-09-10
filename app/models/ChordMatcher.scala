package models


object ChordMatcher{

	//Find closest chord of alternatives

	def closestChord(precedingChord: Array[Int], potentialDestinationChords: Array[Array[Int]]){
		val chordVariances = potentialDestinationChords.map(chordVariance(precedingChord, _))

		
		val indexOfMinimum = chordVariances.zipWithIndex.minBy(_._1)._2 //get index with max variance
		potentialDestinationChords(indexOfMinimum)
	}

	def chordVariance(precedingChord: Array[Int], destinationChord: Array[Int]): Int = {
		precedingChord.foldLeft(0){
			(acc, precedingChordNote) => {
				val sumOfDifferencesSquared = destinationChord.foldLeft(0){
					(acc, destinationChordNote) => {
						val difference = destinationChordNote - precedingChordNote
						val differenceSquared = difference * difference
						println(differenceSquared)
						acc + differenceSquared
					}
				}
				acc + sumOfDifferencesSquared
			}
		}
	}






}