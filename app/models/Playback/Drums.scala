package models

import models.ChordGenerator._



object Drums{
	
	val RideCymbalNote = 59
	val HiHatNote = 44 	

	val Ride = Array(1, 3, 4, 5, 7, 8).map(_ -1)
	val HiHat = Array(3, 7).map(_ -1)

	def SwingDrumBasic(measures: Int) = {
		val notes = (0 until measures).map{
			measure => 
			val ridePattern = Ride.map(beat => SingleNote(RideCymbalNote, measure * 8 + beat))
			val hiHatPattern = HiHat.map(beat => SingleNote(HiHatNote, measure * 8 + beat))
			ridePattern ++ hiHatPattern
		}.flatten.toArray

		swing(notes)
	} 
	
	def swing(notes: Array[SingleNote]) = {
		notes.map{
			note => {
				val swungTick = note.tick match {
					case t if t % 2 == 0 => {//on beats
						t * 3 /2
					}
					case t => {//off beats
						(t - 1) * 3/2 + 2
					}
				}
				SingleNote(note.note, swungTick) 
			}
		}
	}
}