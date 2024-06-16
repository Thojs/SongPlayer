package com.github.hhhzzzsss.songplayer.playing

import net.minecraft.world.GameMode

interface Phase {
    val requiredGamemode: GameMode?
}