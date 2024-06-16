package com.github.hhhzzzsss.songplayer.song

import java.util.*

enum class Instrument(offset: Int) {
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

    @JvmField
	val instrumentName: String = name.lowercase(Locale.getDefault())
    @JvmField
	val instrumentId: Int = ordinal
    @JvmField
	val midiOffset: Int = offset

    companion object {
        @JvmStatic
		fun getInstrumentFromId(instrumentId: Int): Instrument {
            return entries[instrumentId]
        }
    }
}