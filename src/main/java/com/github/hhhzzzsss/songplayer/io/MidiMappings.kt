package com.github.hhhzzzsss.songplayer.io

import com.github.hhhzzzsss.songplayer.song.Instrument

object MidiMappings {
    @JvmStatic
    val instrumentMap: HashMap<Int, List<Instrument>> = HashMap()

    init {
        // Piano (HARP BASS BELL)
        instrumentMap[0] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)      // Acoustic Grand Piano
        instrumentMap[1] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)      // Bright Acoustic Piano
        instrumentMap[2] = listOf(Instrument.BIT, Instrument.DIDGERIDOO, Instrument.BELL) // Electric Grand Piano
        instrumentMap[3] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)      // Honky-tonk Piano
        instrumentMap[4] = listOf(Instrument.BIT, Instrument.DIDGERIDOO, Instrument.BELL) // Electric Piano 1
        instrumentMap[5] = listOf(Instrument.BIT, Instrument.DIDGERIDOO, Instrument.BELL) // Electric Piano 2
        instrumentMap[6] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)      // Harpsichord
        instrumentMap[7] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)      // Clavinet

        // Chromatic Percussion (IRON_XYLOPHONE XYLOPHONE BASS)
        instrumentMap[8] = listOf(Instrument.IRON_XYLOPHONE, Instrument.BASS, Instrument.XYLOPHONE)  // Celesta
        instrumentMap[9] = listOf(Instrument.IRON_XYLOPHONE, Instrument.BASS, Instrument.XYLOPHONE)  // Glockenspiel
        instrumentMap[10] = listOf(Instrument.IRON_XYLOPHONE, Instrument.BASS, Instrument.XYLOPHONE) // Music Box
        instrumentMap[11] = listOf(Instrument.IRON_XYLOPHONE, Instrument.BASS, Instrument.XYLOPHONE) // Vibraphone
        instrumentMap[12] = listOf(Instrument.IRON_XYLOPHONE, Instrument.BASS, Instrument.XYLOPHONE) // Marimba
        instrumentMap[13] = listOf(Instrument.IRON_XYLOPHONE, Instrument.BASS, Instrument.XYLOPHONE) // Xylophone
        instrumentMap[14] = listOf(Instrument.IRON_XYLOPHONE, Instrument.BASS, Instrument.XYLOPHONE) // Tubular Bells
        instrumentMap[15] = listOf(Instrument.IRON_XYLOPHONE, Instrument.BASS, Instrument.XYLOPHONE) // Dulcimer

        // Organ (BIT DIDGERIDOO BELL)
        instrumentMap[16] = listOf(Instrument.DIDGERIDOO, Instrument.BIT, Instrument.XYLOPHONE) // Drawbar Organ
        instrumentMap[17] = listOf(Instrument.DIDGERIDOO, Instrument.BIT, Instrument.XYLOPHONE) // Percussive Organ
        instrumentMap[18] = listOf(Instrument.DIDGERIDOO, Instrument.BIT, Instrument.XYLOPHONE) // Rock Organ
        instrumentMap[19] = listOf(Instrument.DIDGERIDOO, Instrument.BIT, Instrument.XYLOPHONE) // Church Organ
        instrumentMap[20] = listOf(Instrument.DIDGERIDOO, Instrument.BIT, Instrument.XYLOPHONE) // Reed Organ
        instrumentMap[21] = listOf(Instrument.DIDGERIDOO, Instrument.BIT, Instrument.XYLOPHONE) // Accordian
        instrumentMap[22] = listOf(Instrument.DIDGERIDOO, Instrument.BIT, Instrument.XYLOPHONE) // Harmonica
        instrumentMap[23] = listOf(Instrument.DIDGERIDOO, Instrument.BIT, Instrument.XYLOPHONE) // Tango Accordian

        // Guitar (BIT DIDGERIDOO BELL)
        instrumentMap[24] = listOf(Instrument.GUITAR, Instrument.HARP, Instrument.BASS, Instrument.BELL) // Acoustic Guitar (nylon)
        instrumentMap[25] = listOf(Instrument.GUITAR, Instrument.HARP, Instrument.BASS, Instrument.BELL) // Acoustic Guitar (steel)
        instrumentMap[26] = listOf(Instrument.GUITAR, Instrument.HARP, Instrument.BASS, Instrument.BELL) // Electric Guitar (jazz)
        instrumentMap[27] = listOf(Instrument.GUITAR, Instrument.HARP, Instrument.BASS, Instrument.BELL) // Electric Guitar (clean)
        instrumentMap[28] = listOf(Instrument.GUITAR, Instrument.HARP, Instrument.BASS, Instrument.BELL) // Electric Guitar (muted)
        instrumentMap[29] = listOf(Instrument.DIDGERIDOO, Instrument.BIT, Instrument.XYLOPHONE)          // Overdriven Guitar
        instrumentMap[30] = listOf(Instrument.DIDGERIDOO, Instrument.BIT, Instrument.XYLOPHONE)          // Distortion Guitar
        instrumentMap[31] = listOf(Instrument.GUITAR, Instrument.HARP, Instrument.BASS, Instrument.BELL) // Guitar Harmonics

        // Bass
        instrumentMap[32] = listOf(Instrument.BASS, Instrument.HARP, Instrument.BELL)           // Acoustic Bass
        instrumentMap[33] = listOf(Instrument.BASS, Instrument.HARP, Instrument.BELL)           // Electric Bass (finger)
        instrumentMap[34] = listOf(Instrument.BASS, Instrument.HARP, Instrument.BELL)           // Electric Bass (pick)
        instrumentMap[35] = listOf(Instrument.BASS, Instrument.HARP, Instrument.BELL)           // Fretless Bass
        instrumentMap[36] = listOf(Instrument.DIDGERIDOO, Instrument.BIT, Instrument.XYLOPHONE) // Slap Bass 1
        instrumentMap[37] = listOf(Instrument.DIDGERIDOO, Instrument.BIT, Instrument.XYLOPHONE) // Slap Bass 2
        instrumentMap[38] = listOf(Instrument.DIDGERIDOO, Instrument.BIT, Instrument.XYLOPHONE) // Synth Bass 1
        instrumentMap[39] = listOf(Instrument.DIDGERIDOO, Instrument.BIT, Instrument.XYLOPHONE) // Synth Bass 2

        // Strings
        instrumentMap[40] = listOf(Instrument.FLUTE, Instrument.GUITAR, Instrument.BASS, Instrument.BELL) // Violin
        instrumentMap[41] = listOf(Instrument.FLUTE, Instrument.GUITAR, Instrument.BASS, Instrument.BELL) // Viola
        instrumentMap[42] = listOf(Instrument.FLUTE, Instrument.GUITAR, Instrument.BASS, Instrument.BELL) // Cello
        instrumentMap[43] = listOf(Instrument.FLUTE, Instrument.GUITAR, Instrument.BASS, Instrument.BELL) // Contrabass
        instrumentMap[44] = listOf(Instrument.BIT, Instrument.DIDGERIDOO, Instrument.BELL)                // Tremolo Strings
        instrumentMap[45] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)                     // Pizzicato Strings
        instrumentMap[46] = listOf(Instrument.HARP, Instrument.BASS, Instrument.CHIME)                    // Orchestral Harp
        instrumentMap[47] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)                     // Timpani

        // Ensenble
        instrumentMap[48] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL) // String Ensemble 1
        instrumentMap[49] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL) // String Ensemble 2
        instrumentMap[50] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL) // Synth Strings 1
        instrumentMap[51] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL) // Synth Strings 2
        instrumentMap[52] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL) // Choir Aahs
        instrumentMap[53] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL) // Voice Oohs
        instrumentMap[54] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL) // Synth Choir
        instrumentMap[55] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL) // Orchestra Hit

        // Brass
        instrumentMap[56] = listOf(Instrument.BIT, Instrument.DIDGERIDOO, Instrument.BELL)
        instrumentMap[57] = listOf(Instrument.BIT, Instrument.DIDGERIDOO, Instrument.BELL)
        instrumentMap[58] = listOf(Instrument.BIT, Instrument.DIDGERIDOO, Instrument.BELL)
        instrumentMap[59] = listOf(Instrument.BIT, Instrument.DIDGERIDOO, Instrument.BELL)
        instrumentMap[60] = listOf(Instrument.BIT, Instrument.DIDGERIDOO, Instrument.BELL)
        instrumentMap[61] = listOf(Instrument.BIT, Instrument.DIDGERIDOO, Instrument.BELL)
        instrumentMap[62] = listOf(Instrument.BIT, Instrument.DIDGERIDOO, Instrument.BELL)
        instrumentMap[63] = listOf(Instrument.BIT, Instrument.DIDGERIDOO, Instrument.BELL)

        // Reed
        instrumentMap[64] = listOf(Instrument.FLUTE, Instrument.DIDGERIDOO, Instrument.IRON_XYLOPHONE, Instrument.BELL)
        instrumentMap[65] = listOf(Instrument.FLUTE, Instrument.DIDGERIDOO, Instrument.IRON_XYLOPHONE, Instrument.BELL)
        instrumentMap[66] = listOf(Instrument.FLUTE, Instrument.DIDGERIDOO, Instrument.IRON_XYLOPHONE, Instrument.BELL)
        instrumentMap[67] = listOf(Instrument.FLUTE, Instrument.DIDGERIDOO, Instrument.IRON_XYLOPHONE, Instrument.BELL)
        instrumentMap[68] = listOf(Instrument.FLUTE, Instrument.DIDGERIDOO, Instrument.IRON_XYLOPHONE, Instrument.BELL)
        instrumentMap[69] = listOf(Instrument.FLUTE, Instrument.DIDGERIDOO, Instrument.IRON_XYLOPHONE, Instrument.BELL)
        instrumentMap[70] = listOf(Instrument.FLUTE, Instrument.DIDGERIDOO, Instrument.IRON_XYLOPHONE, Instrument.BELL)
        instrumentMap[71] = listOf(Instrument.FLUTE, Instrument.DIDGERIDOO, Instrument.IRON_XYLOPHONE, Instrument.BELL)

        // Pipe
        instrumentMap[72] = listOf(Instrument.FLUTE, Instrument.DIDGERIDOO, Instrument.IRON_XYLOPHONE, Instrument.BELL)
        instrumentMap[73] = listOf(Instrument.FLUTE, Instrument.DIDGERIDOO, Instrument.IRON_XYLOPHONE, Instrument.BELL)
        instrumentMap[74] = listOf(Instrument.FLUTE, Instrument.DIDGERIDOO, Instrument.IRON_XYLOPHONE, Instrument.BELL)
        instrumentMap[75] = listOf(Instrument.FLUTE, Instrument.DIDGERIDOO, Instrument.IRON_XYLOPHONE, Instrument.BELL)
        instrumentMap[76] = listOf(Instrument.FLUTE, Instrument.DIDGERIDOO, Instrument.IRON_XYLOPHONE, Instrument.BELL)
        instrumentMap[77] = listOf(Instrument.FLUTE, Instrument.DIDGERIDOO, Instrument.IRON_XYLOPHONE, Instrument.BELL)
        instrumentMap[78] = listOf(Instrument.FLUTE, Instrument.DIDGERIDOO, Instrument.IRON_XYLOPHONE, Instrument.BELL)
        instrumentMap[79] = listOf(Instrument.FLUTE, Instrument.DIDGERIDOO, Instrument.IRON_XYLOPHONE, Instrument.BELL)

        // Synth Lead
        instrumentMap[80] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)
        instrumentMap[81] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)
        instrumentMap[82] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)
        instrumentMap[83] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)
        instrumentMap[84] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)
        instrumentMap[85] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)
        instrumentMap[86] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)
        instrumentMap[87] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)

        // Synth Pad
        instrumentMap[88] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)
        instrumentMap[89] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)
        instrumentMap[90] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)
        instrumentMap[91] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)
        instrumentMap[92] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)
        instrumentMap[93] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)
        instrumentMap[94] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)
        instrumentMap[95] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)

        // Synth Effects
//		instrumentMap[96] = listOf()
//		instrumentMap[97] = listOf()
        instrumentMap[98] = listOf(Instrument.BIT, Instrument.DIDGERIDOO, Instrument.BELL)
        instrumentMap[99] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)
        instrumentMap[100] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)
        instrumentMap[101] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)
        instrumentMap[102] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)
        instrumentMap[103] = listOf(Instrument.HARP, Instrument.BASS, Instrument.BELL)

        // Ethnic
        instrumentMap[104] = listOf(Instrument.BANJO, Instrument.BASS, Instrument.BELL)
        instrumentMap[105] = listOf(Instrument.BANJO, Instrument.BASS, Instrument.BELL)
        instrumentMap[106] = listOf(Instrument.BANJO, Instrument.BASS, Instrument.BELL)
        instrumentMap[107] = listOf(Instrument.BANJO, Instrument.BASS, Instrument.BELL)
        instrumentMap[108] = listOf(Instrument.BANJO, Instrument.BASS, Instrument.BELL)
        instrumentMap[109] = listOf(Instrument.HARP, Instrument.DIDGERIDOO, Instrument.BELL)
        instrumentMap[110] = listOf(Instrument.HARP, Instrument.DIDGERIDOO, Instrument.BELL)
        instrumentMap[111] = listOf(Instrument.HARP, Instrument.DIDGERIDOO, Instrument.BELL)

        // Percussive
        instrumentMap[112] = listOf(Instrument.IRON_XYLOPHONE, Instrument.BASS, Instrument.XYLOPHONE)
        instrumentMap[113] = listOf(Instrument.IRON_XYLOPHONE, Instrument.BASS, Instrument.XYLOPHONE)
        instrumentMap[114] = listOf(Instrument.IRON_XYLOPHONE, Instrument.BASS, Instrument.XYLOPHONE)
        instrumentMap[115] = listOf(Instrument.IRON_XYLOPHONE, Instrument.BASS, Instrument.XYLOPHONE)
        instrumentMap[116] = listOf(Instrument.IRON_XYLOPHONE, Instrument.BASS, Instrument.XYLOPHONE)
        instrumentMap[117] = listOf(Instrument.IRON_XYLOPHONE, Instrument.BASS, Instrument.XYLOPHONE)
        instrumentMap[118] = listOf(Instrument.IRON_XYLOPHONE, Instrument.BASS, Instrument.XYLOPHONE)
        instrumentMap[119] = listOf(Instrument.IRON_XYLOPHONE, Instrument.BASS, Instrument.XYLOPHONE)
    }

    @JvmStatic
    val percussionMap: java.util.HashMap<Int, Int> = java.util.HashMap()

    init {
        percussionMap[35] = 10 + 25 * Instrument.BASEDRUM.instrumentId
        percussionMap[36] = 6 + 25 * Instrument.BASEDRUM.instrumentId
        percussionMap[37] = 6 + 25 * Instrument.HAT.instrumentId
        percussionMap[38] = 8 + 25 * Instrument.SNARE.instrumentId
        percussionMap[39] = 6 + 25 * Instrument.HAT.instrumentId
        percussionMap[40] = 4 + 25 * Instrument.SNARE.instrumentId
        percussionMap[41] = 6 + 25 * Instrument.BASEDRUM.instrumentId
        percussionMap[42] = 22 + 25 * Instrument.SNARE.instrumentId
        percussionMap[43] = 13 + 25 * Instrument.BASEDRUM.instrumentId
        percussionMap[44] = 22 + 25 * Instrument.SNARE.instrumentId
        percussionMap[45] = 15 + 25 * Instrument.BASEDRUM.instrumentId
        percussionMap[46] = 18 + 25 * Instrument.SNARE.instrumentId
        percussionMap[47] = 20 + 25 * Instrument.BASEDRUM.instrumentId
        percussionMap[48] = 23 + 25 * Instrument.BASEDRUM.instrumentId
        percussionMap[49] = 17 + 25 * Instrument.SNARE.instrumentId
        percussionMap[50] = 23 + 25 * Instrument.BASEDRUM.instrumentId
        percussionMap[51] = 24 + 25 * Instrument.SNARE.instrumentId
        percussionMap[52] = 8 + 25 * Instrument.SNARE.instrumentId
        percussionMap[53] = 13 + 25 * Instrument.SNARE.instrumentId
        percussionMap[54] = 18 + 25 * Instrument.HAT.instrumentId
        percussionMap[55] = 18 + 25 * Instrument.SNARE.instrumentId
        percussionMap[56] = 1 + 25 * Instrument.HAT.instrumentId
        percussionMap[57] = 13 + 25 * Instrument.SNARE.instrumentId
        percussionMap[58] = 2 + 25 * Instrument.HAT.instrumentId
        percussionMap[59] = 13 + 25 * Instrument.SNARE.instrumentId
        percussionMap[60] = 9 + 25 * Instrument.HAT.instrumentId
        percussionMap[61] = 2 + 25 * Instrument.HAT.instrumentId
        percussionMap[62] = 8 + 25 * Instrument.HAT.instrumentId
        percussionMap[63] = 22 + 25 * Instrument.BASEDRUM.instrumentId
        percussionMap[64] = 15 + 25 * Instrument.BASEDRUM.instrumentId
        percussionMap[65] = 13 + 25 * Instrument.SNARE.instrumentId
        percussionMap[66] = 8 + 25 * Instrument.SNARE.instrumentId
        percussionMap[67] = 8 + 25 * Instrument.HAT.instrumentId
        percussionMap[68] = 3 + 25 * Instrument.HAT.instrumentId
        percussionMap[69] = 20 + 25 * Instrument.HAT.instrumentId
        percussionMap[70] = 23 + 25 * Instrument.HAT.instrumentId
        percussionMap[71] = 24 + 25 * Instrument.HAT.instrumentId
        percussionMap[72] = 24 + 25 * Instrument.HAT.instrumentId
        percussionMap[73] = 17 + 25 * Instrument.HAT.instrumentId
        percussionMap[74] = 11 + 25 * Instrument.HAT.instrumentId
        percussionMap[75] = 18 + 25 * Instrument.HAT.instrumentId
        percussionMap[76] = 9 + 25 * Instrument.HAT.instrumentId
        percussionMap[77] = 5 + 25 * Instrument.HAT.instrumentId
        percussionMap[78] = 22 + 25 * Instrument.HAT.instrumentId
        percussionMap[79] = 19 + 25 * Instrument.SNARE.instrumentId
        percussionMap[80] = 17 + 25 * Instrument.HAT.instrumentId
        percussionMap[81] = 22 + 25 * Instrument.HAT.instrumentId
        percussionMap[82] = 22 + 25 * Instrument.SNARE.instrumentId
        percussionMap[83] = 24 + 25 * Instrument.CHIME.instrumentId
        percussionMap[84] = 24 + 25 * Instrument.CHIME.instrumentId
        percussionMap[85] = 21 + 25 * Instrument.HAT.instrumentId
        percussionMap[86] = 14 + 25 * Instrument.BASEDRUM.instrumentId
        percussionMap[87] = 7 + 25 * Instrument.BASEDRUM.instrumentId
    }
}