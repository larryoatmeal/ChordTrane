package models

import java.io.File
import java.io.IOException

import javax.sound.midi.Sequence
import javax.sound.midi.MidiEvent
import javax.sound.midi.MidiMessage
import javax.sound.midi.MetaMessage
import javax.sound.midi.MidiSystem
import javax.sound.midi.ShortMessage
import javax.sound.midi.Track
import javax.sound.midi.InvalidMidiDataException

object MidiCreator{

	val DivisionType = Sequence.PPQ
	val Resolution = 24 //6 ticks per quarter note
	val NumberOfTracks = 1
	val FileType = 1

	//val sequence = new Sequence(DivisionType, Resolution)

	val QUARTER = Resolution
	val HALF = Resolution * 2
	val WHOLE = Resolution * 4
	val EIGHTH = Resolution / 2
	val TRIPLET = Resolution / 3
	val SIXTEENTH = Resolution / 4

	val Velocity = 60


	val PianoChannel = 0
	val BassChannel = 1
	val DrumChannel = 9



	def noteEvent(command: Int, channel: Int, key: Int, velocity: Int, tick: Long) = {
		val message = new ShortMessage()
		message.setMessage(command, channel, key, velocity)
		new MidiEvent(message, tick)
	}

	// //no channel
	// def noteEvent(command: Int, key: Int, velocity: Int, tick: Long) = {
	// 	val message = new ShortMessage()
	// 	message.setMessage(command, key, velocity)
	// 	new MidiEvent(message, tick)
	// }

	def noteOn(key: Int, tick: Int, channel: Int) = {
		noteEvent(ShortMessage.NOTE_ON, channel, key, Velocity, tick)
	}

	def noteOff(key: Int, tick: Int, channel: Int) = {
		noteEvent(ShortMessage.NOTE_OFF, channel, key, Velocity, tick)
	}

	def noteOnOff(key: Int, startingTick: Int, duration: Int, channel: Int) = {
		Array(noteOn(key, startingTick, channel), noteOff(key, startingTick + duration, channel))
	}

	def programChange(patch: Int, channel: Int, tick: Int) = {
		val message = new ShortMessage()
		message.setMessage(ShortMessage.PROGRAM_CHANGE, channel, patch, 0)//2nd byte ignored
		new MidiEvent(message, tick)
	}

	def BassProgram(channel: Int) = programChange(33, channel, 0)
	def PianoProgram(channel: Int) = programChange(1, channel, 0)
	def DrumProgram(channel: Int) = programChange(1, channel, 0)

	// val sampleSequence = Array(
	// 	Array(programChange(12, 0, 0)),
	// 	noteOnOff(Note.getMidiNote("C", 6), 0, QUARTER),
	// 	noteOnOff(Note.getMidiNote("D", 6), 24, QUARTER),
	// 	noteOnOff(Note.getMidiNote("E", 6), 48, QUARTER),
	// 	noteOnOff(Note.getMidiNote("F", 6), 72, QUARTER)
	// ).flatten

	def midiChordsToMidiEvent(midiChords: Array[MidiChord], subdivisions: Int, channel: Int) = {
		midiChords.map{
			midiChord => {
				val midiTick = midiChord.tick * Resolution / subdivisions
				midiChord.chord.map(noteOnOff(_, midiTick, EIGHTH, channel)).flatten
			}
		}.flatten
	}
	def singleNotesToMidiEvent(notes: Array[SingleNote], subdivisions: Int, channel: Int) = {
		notes.map{
			snote => {
				val midiTick = snote.tick * Resolution / subdivisions
				noteOnOff(snote.note, midiTick, QUARTER, channel)
			}
		}.flatten
	}

	def dummy(bpm: Int) = {
		val microsecondsPerQuarterNote = 60000000 / bpm

		val b3 = microsecondsPerQuarterNote & 0xFF
		val b2 = (microsecondsPerQuarterNote >> 8) & 0xFF
		val b1 = (microsecondsPerQuarterNote >> 16) & 0xFF

		Array(b1, b2, b3)
	}

	def tempoMessage(bpm: Int) = {
		val microsecondsPerQuarterNote = 60000000 / bpm

		val b3 = microsecondsPerQuarterNote & 0xFF
		val b2 = (microsecondsPerQuarterNote >> 8) & 0xFF
		val b1 = (microsecondsPerQuarterNote >> 16) & 0xFF
		val msg = new MetaMessage
		msg.setMessage(0x51, Array(b1.toByte, b2.toByte, b3.toByte), 3)
		msg
	}

	def tempoEvent(bpm: Int) =  new MidiEvent(tempoMessage(bpm), 0)

	// def test = {
	// 	val midiEvents = midiChordsToMidiEvent(PianoComper.testChords, 2)
	// 	val c = new MidiCreator
	// 	c.createTrack(midiEvents)
	// 	c.createMidi("C:/misty.midi")
	// }

}

class MidiCreator{
	val sequence = new Sequence(MidiCreator.DivisionType, MidiCreator.Resolution)

	def createTrack(midiEvents: Array[MidiEvent]){
		val track = sequence.createTrack()
		midiEvents.foreach{
			midiEvent => track.add(midiEvent)
		}
	}

	def createMidi(filePath: String){
		//println(sequence.getPatchList())
		val outputFile = new File(filePath)
		MidiSystem.write(sequence, 1, outputFile)
	}
}