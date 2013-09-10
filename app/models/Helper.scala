package models

object Helper{
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

}