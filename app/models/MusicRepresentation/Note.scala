
package models

case class Note(noteNumber: Int)
case class Pitch(midi: Int)



object Note{
  import Helper._

  //Map midi note
  val notenameMap = Map(
    0 -> "C",
    1 -> "Db",
    2 -> "D",
    3 -> "Eb",
    4 -> "E",
    5 -> "F",
    6 -> "Gb",
    7 -> "G",
    8 -> "Ab",
    9 -> "A",
    10 -> "Bb",
    11 -> "B"
  )

  //Convert letter into number
  def midify(notename: String) = {
    notename match {
      case "C" | "Dbb" | "B#"  => 0 
      case "C#"| "Db"  | "B##" => 1
      case "D" | "Ebb" | "C##" => 2
      case "D#"| "Eb"  | "Fbb" => 3
      case "E" | "Fb"  | "D##" => 4
      case "F" | "Gbb" | "E#"  => 5
      case "F#"| "Gb"  | "E##" => 6
      case "G" | "Abb" | "F##" => 7
      case "G#"| "Ab"        => 8
      case "A" | "Bbb" | "G##" => 9
      case "A#"| "Bb"  | "Cbb" => 10
      case "B" | "Cb"  | "A##" => 11
      case _ => println("Notematch error: " + notename); 0
    }
  }
  //Major scale contains one of each note. Must be able to use % 
  val doremiMod = Map(
    "C" -> 0,
    "D" -> 1,
    "E" -> 2,
    "F" -> 3,
    "G" -> 4,
    "A" -> 5,
    "B" -> 6
  )
  val reverseDoremiMod = Map(
    0 -> "C",
    1 -> "D",
    2 -> "E",
    3 -> "F",
    4 -> "G",
    5 -> "A",
    6 -> "B"
  )

  def findSpelling(midinote: Int, letter: String): String = {//get spelling for note letter and midinote number
    //For each char, test every permutation with that letter for the required note
    //Natural
    if (midify(letter) == midinote) {
      letter
    }//Sharp
    else if (mod12(midify(letter) + 1) == midinote){
      letter + "#"
    }//Flat
    else if (mod12(midify(letter) - 1) == midinote){
      letter + "b"
    }//Double sharp
    else if (mod12(midify(letter) + 2) == midinote){
      letter + "##"
    }//Double flat
    else if (mod12(midify(letter) - 2) == midinote){
      letter + "bb"
    }else{
      "X"
    }
  }

  def noteName(midi: Int) = {
    val pitch = notenameMap(mod12(midi))//real modulo
    val octave = floorDivision(midi, 12) - 1

    pitch + octave
  }

  def getMidiNote(root: String, octave: Int) = {
    val rootMidi = Note.midify(root)
    rootMidi + ( octave + 1) * 12 
  }

  
  
  def closestPitch(have: Int, want: Int) = {//D0, B7 => D8
    val delta = mod12(want) - mod12(have)

    val idealDelta = delta match {//Find smallest delta. For example -11 should be translated to +1
      case d if d > 6 => d - 12
      case d if d >= -6 => d //For d between -6 and 6
      case d => 12 + d //For d lower than -6
    }
    have + idealDelta
  }

  def octaveAdjust(have: Int, want: Int) = {//how many octaves to shift have to get closest have to in semitones
    (want - closestPitch(have, want))
  }

  def printNotes(notes: Array[Int]) = {
    notes.map(noteName(_)).mkString(" ")
  }

  def closestInterval(startingNote: Int, endingNote: Int) = {
    val interval = Helper.mod12(endingNote) - Helper.mod12(startingNote)
    val alternateInterval = if(interval > 0){interval - 12}else{interval + 12}//+M3 becomes -m6

    if(Math.abs(interval) < Math.abs(alternateInterval)){interval}else{alternateInterval}
  }


}

