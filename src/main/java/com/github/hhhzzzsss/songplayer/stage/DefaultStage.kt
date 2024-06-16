package com.github.hhhzzzsss.songplayer.stage

import net.minecraft.util.math.BlockPos
import kotlin.math.abs

class DefaultStage : StageType {
    override val identifier = "default"

    override fun getBlocks(noteLocations: MutableList<BlockPos>, breakLocations: MutableList<BlockPos>) {
        for (dx in -4..4) {
            for (dz in -4..4) {
                if (abs(dx.toDouble()) == 4.0 && abs(dz.toDouble()) == 4.0) {
                    noteLocations.add(BlockPos(dx, 0, dz))
                    noteLocations.add(BlockPos(dx, 2, dz))
                    breakLocations.add(BlockPos(dx, 1, dz))
                } else {
                    noteLocations.add(BlockPos(dx, -1, dz))
                    noteLocations.add(BlockPos(dx, 2, dz))
                    breakLocations.add(BlockPos(dx, 0, dz))
                    breakLocations.add(BlockPos(dx, 1, dz))
                }
            }
        }

        for (dx in -4..4) {
            for (dz in -4..4) {
                if (withinBreakingDistance(dx, -3, dz)) {
                    noteLocations.add(BlockPos(dx, -3, dz))
                }
                if (withinBreakingDistance(dx, 4, dz)) {
                    noteLocations.add(BlockPos(dx, 4, dz))
                }
            }
        }
    }
}
