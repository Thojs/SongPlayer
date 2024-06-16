package com.github.hhhzzzsss.songplayer.stage

import net.minecraft.util.math.BlockPos

interface StageType {
    val identifier: String

    fun getBlocks(noteLocations: MutableList<BlockPos>, breakLocations: MutableList<BlockPos>)

    fun withinBreakingDistance(dx: Int, dy: Int, dz: Int): Boolean {
        val dy1 = dy + 0.5 - 1.62 // Standing eye height
        val dy2 = dy + 0.5 - 1.27 // Crouching eye height
        return dx * dx + dy1 * dy1 + dz * dz < 5.99999 * 5.99999 && dx * dx + dy2 * dy2 + dz * dz < 5.99999 * 5.99999
    }
}
