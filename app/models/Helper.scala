package models

import scala.util.Random

object Helper{

	val randomGenerator = new Random

	def stringToInt(s: String) = {
		try {
			Some(s.toInt)
		}catch {
			case e:Throwable => {
				None
			}
		}
	}

	def mod(a: Int, n: Int) = {//Real modulo
		((a % n) + n) % n
	}

	def mod12(a: Int) = mod(a, 12)

	def floorDivision(n: Int, d: Int) = {
		if (n < 0){  // -1/12 = -1  -13/12 = -2
			n/d - 1
		}
		else{
			n/d
		}
	}

	def measuresOnly(allLines: AllLines) = allLines.lines.collect{case m: MusicLine => m}.flatMap(_.measures)

	def randomOneToZero = randomGenerator.nextDouble

	def getRandomFromArray[T](array: Array[T]) = {
		val randomIndex = randomGenerator.nextInt(array.length)
		array(randomIndex)
	}

	def rollDice(probability: Double) = {
		val roll = randomGenerator.nextDouble
		if (roll < probability) true 
		else false
	}

	def getRandomWithinRange(min: Int, max: Int) = {
		randomGenerator.nextInt(max - min + 1) + min 
	}

	def powerProbabilityCurve(center: Int, deviation: Int, power: Double) = {
		//Generate power function such that y = 0 at mean, y = 1 at deviations
		//center + deviation = max, center - deviation = min
		//Higher power, less steep curve

		//y = 1/deviation^power * (x-center)^power

		(note: Int) => 1.0/ Math.pow(deviation, power) * Math.pow(note - center, power)
	}

	//Ex:
	//Generate curve for numbers 0 to 8, where 0 to 8 is on y-axis, and 0-1 is on x-axis
	//When random number is generated, plug in the random number, corresponds to y number
	//Round off
	//Higher power, shallower slope, more weighted toward min
	//Max will never be included. Only values min to max - 1
	def randomInRange(min: Int, max: Int, power: Double) = {
		(numberFromOneToZero: Double) => {
			//y = min + (max-min)*x^power
			if(numberFromOneToZero >= 1.0){//If in rare case answer is 1, return max -1, not max
				(max - 1).toInt
			}else{
				val result = min + (max-min) * Math.pow(numberFromOneToZero, power)
				Math.floor(result).toInt
			}
		}
	}




}