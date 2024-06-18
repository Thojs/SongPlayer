package com.github.hhhzzzsss.songplayer.io

import com.github.hhhzzzsss.songplayer.io.MidiMappings.getInstrumentNoteId
import com.github.hhhzzzsss.songplayer.io.MidiMappings.getPercussionNoteId
import com.github.hhhzzzsss.songplayer.song.Note
import com.github.hhhzzzsss.songplayer.song.Song
import java.io.ByteArrayInputStream
import java.io.IOException
import javax.sound.midi.*

class MidiParser : SongParser {
    override fun parse(bytes: ByteArray, title: String): Song? {
        return try {
            getSong(MidiSystem.getSequence(ByteArrayInputStream(bytes)), title)
        } catch (e: InvalidMidiDataException) {
            null
        } catch (e: IOException) {
            null
        }
    }

    override val fileExtensions = listOf("mid", "midi")
    override val mimeTypes = listOf("audio/midi", "audio/x-midi")

    companion object {
        private const val SET_TEMPO = 0x51

        fun getSong(sequence: Sequence, name: String): Song {
            val song = Song(name)

            val tpq = sequence.resolution.toLong()

            // Tempo
            val tempoEvents = arrayListOf<MidiEvent>()
            for (track in sequence.tracks) {
                for (i in 0 until track.size()) {
                    val event = track[i]
                    val message = event.message
                    if (message is MetaMessage && message.type == SET_TEMPO) {
                        tempoEvents.add(event)
                    }
                }
            }

            tempoEvents.sortWith(Comparator.comparingLong { obj: MidiEvent -> obj.tick })

            // Notes
            for (track in sequence.tracks) {
                var microTime: Long = 0
                val instrumentIds = IntArray(16)
                var mpq = 500000
                var tempoEventIdx = 0
                var prevTick: Long = 0

                for (i in 0 until track.size()) {
                    val event = track[i]
                    val message = event.message

                    while (tempoEventIdx < tempoEvents.size && event.tick > tempoEvents[tempoEventIdx].tick) {
                        val deltaTick = tempoEvents[tempoEventIdx].tick - prevTick
                        prevTick = tempoEvents[tempoEventIdx].tick
                        microTime += (mpq / tpq) * deltaTick

                        val mm = tempoEvents[tempoEventIdx].message as MetaMessage
                        val data = mm.data
                        val new_mpq = (data[2].toInt() and 0xFF) or ((data[1].toInt() and 0xFF) shl 8) or ((data[0].toInt() and 0xFF) shl 16)
                        if (new_mpq != 0) mpq = new_mpq
                        tempoEventIdx++
                    }

                    if (message !is ShortMessage) continue

                    when (message.command) {
                        ShortMessage.PROGRAM_CHANGE -> {
                            instrumentIds[message.channel] = message.data1
                        }
                        ShortMessage.NOTE_ON -> {
                            if (message.data2 == 0) continue
                            val pitch: Int = message.data1
                            val deltaTick = event.tick - prevTick
                            prevTick = event.tick
                            microTime += (mpq / tpq) * deltaTick
                            val note = if (message.channel == 9) {
                                getMidiPercussionNote(pitch, microTime)
                            } else {
                                getMidiInstrumentNote(instrumentIds[message.channel], pitch, microTime)
                            }

                            if (note != null) song.add(note)

                            val time = microTime / 1000L
                            if (time > song.length) {
                                song.length = time
                            }
                        }
                        ShortMessage.NOTE_OFF -> {
                            val deltaTick = event.tick - prevTick
                            prevTick = event.tick
                            microTime += (mpq / tpq) * deltaTick
                            val time = microTime / 1000L
                            if (time > song.length) song.length = time
                        }
                    }
                }
            }

            song.sort()
            return song
        }

        fun getMidiInstrumentNote(midiInstrument: Int, midiPitch: Int, microTime: Long): Note? {
            val noteId = getInstrumentNoteId(midiInstrument, midiPitch) ?: return null
            val time = microTime / 1000L

            return Note(noteId, time)
        }

        private fun getMidiPercussionNote(midiPitch: Int, microTime: Long): Note? {
            val noteId = getPercussionNoteId(midiPitch) ?: return null
            val time = microTime / 1000L

            return Note(noteId, time)
        }
    }
}