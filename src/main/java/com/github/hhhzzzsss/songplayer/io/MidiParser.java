package com.github.hhhzzzsss.songplayer.io;

import com.github.hhhzzzsss.songplayer.song.Instrument;
import com.github.hhhzzzsss.songplayer.song.Note;
import com.github.hhhzzzsss.songplayer.song.Song;
import org.jetbrains.annotations.NotNull;

import javax.sound.midi.*;
import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.*;

import static com.github.hhhzzzsss.songplayer.io.MidiMappings.getInstrumentMap;
import static com.github.hhhzzzsss.songplayer.io.MidiMappings.getPercussionMap;

public class MidiParser implements SongParser {
	public static final int SET_INSTRUMENT = 0xC0;
	public static final int SET_TEMPO = 0x51;
	public static final int NOTE_ON = 0x90;
    public static final int NOTE_OFF = 0x80;

	@Override
	public Song parse(byte @NotNull [] bytes, @NotNull String name) {
        try {
			return getSong(MidiSystem.getSequence(new ByteArrayInputStream(bytes)), name);
        } catch (InvalidMidiDataException | IOException e) {
            return null;
        }
	}
    
	public static Song getSong(Sequence sequence, String name) {
		Song song  = new Song(name);
		
		long tpq = sequence.getResolution();
		
		ArrayList<MidiEvent> tempoEvents = new ArrayList<>();
		for (Track track : sequence.getTracks()) {
			for (int i = 0; i < track.size(); i++) {
				MidiEvent event = track.get(i);
				MidiMessage message = event.getMessage();
				if (message instanceof MetaMessage mm && mm.getType() == SET_TEMPO) {
					tempoEvents.add(event);
				}
			}
		}
		
		tempoEvents.sort(Comparator.comparingLong(MidiEvent::getTick));
		
		for (Track track : sequence.getTracks()) {
			long microTime = 0;
			int[] instrumentIds = new int[16];
			int mpq = 500000;
			int tempoEventIdx = 0;
			long prevTick = 0;
			
			for (int i = 0; i < track.size(); i++) {
				MidiEvent event = track.get(i);
				MidiMessage message = event.getMessage();
				
				while (tempoEventIdx < tempoEvents.size() && event.getTick() > tempoEvents.get(tempoEventIdx).getTick()) {
					long deltaTick = tempoEvents.get(tempoEventIdx).getTick() - prevTick;
					prevTick = tempoEvents.get(tempoEventIdx).getTick();
					microTime += (mpq/tpq) * deltaTick;
					
					MetaMessage mm = (MetaMessage) tempoEvents.get(tempoEventIdx).getMessage();
					byte[] data = mm.getData();
					int new_mpq = (data[2]&0xFF) | ((data[1]&0xFF)<<8) | ((data[0]&0xFF)<<16);
					if (new_mpq != 0) mpq = new_mpq;
					tempoEventIdx++;
				}
				
				if (message instanceof ShortMessage sm) {
                    if (sm.getCommand() == SET_INSTRUMENT) {
						instrumentIds[sm.getChannel()] = sm.getData1();
					} else if (sm.getCommand() == NOTE_ON) {
						if (sm.getData2() == 0) continue;
						int pitch = sm.getData1();
						long deltaTick = event.getTick() - prevTick;
						prevTick = event.getTick();
						microTime += (mpq/tpq) * deltaTick;

						Note note;
						if (sm.getChannel() == 9) {
							note = getMidiPercussionNote(pitch, microTime);
						} else {
							note = getMidiInstrumentNote(instrumentIds[sm.getChannel()], pitch, microTime);
						}

						if (note != null) song.add(note);

						long time = microTime / 1000L;
						if (time > song.length) {
							song.length = time;
						}
					} else if (sm.getCommand() == NOTE_OFF) {
						long deltaTick = event.getTick() - prevTick;
						prevTick = event.getTick();
						microTime += (mpq/tpq) * deltaTick;
						long time = microTime / 1000L;
						if (time > song.length) {
							song.length = time;
						}
					}
				}
			}
		}

		song.sort();
		
		return song;
	}

	public static Note getMidiInstrumentNote(int midiInstrument, int midiPitch, long microTime) {
		Instrument instrument = null;
		List<Instrument> instrumentList = getInstrumentMap().get(midiInstrument);
		if (instrumentList != null) {
			for (Instrument candidateInstrument : instrumentList) {
				if (midiPitch >= candidateInstrument.midiOffset && midiPitch <= candidateInstrument.midiOffset +24) {
					instrument = candidateInstrument;
					break;
				}
			}
		}

		if (instrument == null) {
			return null;
		}

		int pitch = midiPitch-instrument.midiOffset;
		int noteId = pitch + instrument.instrumentId*25;
		long time = microTime / 1000L;

		return new Note(noteId, time);
	}

	private static Note getMidiPercussionNote(int midiPitch, long microTime) {
		if (getPercussionMap().containsKey(midiPitch)) {
			int noteId = getPercussionMap().get(midiPitch);
			long time = microTime / 1000L;

			return new Note(noteId, time);
		}
		return null;
	}

	@Override
	public List<String> getFileExtensions() {
		return List.of("mid", "midi");
	}

	@NotNull
	@Override
	public List<String> getMimeTypes() {
		return List.of("audio/midi", "audio/x-midi");
	}
}