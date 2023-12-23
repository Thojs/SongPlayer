package com.github.hhhzzzsss.songplayer.song;

public enum Instrument {
	HARP(54),
	BASEDRUM(0),
	SNARE(0),
	HAT(0),
	BASS(30),
	FLUTE(66),
	BELL(78),
	GUITAR(42),
	CHIME(78),
	XYLOPHONE(78),
	IRON_XYLOPHONE(54),
	COW_BELL(66),
	DIDGERIDOO(30),
	BIT(54),
	BANJO(54),
	PLING(54);

	public final String instrumentName;
	public final int instrumentId;
	public final int midiOffset;

	Instrument(int offset) {
		this.instrumentName = name().toLowerCase();
		this.instrumentId = ordinal();
		this.midiOffset = offset;
	}

	private static final Instrument[] values = values();
	public static Instrument getInstrumentFromId(int instrumentId) {
		return values[instrumentId];
	}
}
