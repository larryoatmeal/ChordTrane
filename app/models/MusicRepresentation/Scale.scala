package models



object Scale{

	import models.Interval._

	val Major = 0
	val Minor = 1
	val Dominant = 2

	val scaleMap = Map(
		Major -> Ionian,
		Minor -> Dorian,
		Dominant -> Mixolydian
	)


	val Ionian = Array(P1, M2, M3, P4, P5, M6, M7)
	val Dorian = Array(P1, M2, m3, P4, P5, M6, m7)
	val Mixolydian = Array(P1, M2, M3, P4, P5, M6, m7)


	
	def getScale(root: Int, scaleType: Int){
		val scaleArray = scaleMap(scaleType)
		scaleArray.map(_ + root)
	}	


}