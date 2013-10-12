package models


case class ComperTemplate(chordGenerator: ChordGenerator.ChordGenerator, tick: Int)


object RhythmSectionCoordinator{

	//Chords with their literal positions in music
	def generateChordTemplate(measures: List[Measure], timeSig: Int, subdivisions: Int) = {
		val ticksPerMeasure = subdivisions * timeSig

		//Convert beat to linear time position
		def beatToTick(beat: Int, measureIndex: Int) = {
			(beat -1) * subdivisions + ticksPerMeasure * measureIndex
		}

		measures.zipWithIndex.flatMap{
			case (measure, index) => {
				measure.chords.map(chord => ComperTemplate(ChordLogic.getChordGenerator(chord),beatToTick(chord.beat, index)))
			}
		}.toArray
	}

	val dummyMeasures = Helper.measuresOnly(Master.parseSong(Master.DummySong))

	val dummyRhythmSection = new RhythmSectionCoordinator(dummyMeasures, 4, 2, PianoPlayer.JAZZ_SWING4)


}

class RhythmSectionCoordinator(measures: List[Measure], timeSig: Int, subdivisions: Int, style: Int){

	val chordTemplate = RhythmSectionCoordinator.generateChordTemplate(measures, timeSig, subdivisions)

	def piano = PianoPlayer.getCompingMidi(chordTemplate, style, measures.length)

	def bass = BassPlayer.bassSkeleton(chordTemplate)


	def midi{
		val master = new MidiCreator
		


		//Piano
		val pianoMidiData = MidiCreator.PianoProgram(0) +: MidiCreator.midiChordsToMidiEvent(piano, subdivisions) 
		master.createTrack(pianoMidiData)

		//Bass 
		val bassMidiData = MidiCreator.BassProgram(1) +: MidiCreator.singleNotesToMidiEvent(bass, subdivisions) 
		master.createTrack(bassMidiData)

		master.createMidi("C:/misty.midi")

	}








}