package com.github.hhhzzzsss.songplayer.stage

import net.minecraft.util.math.BlockPos

class WideStage : StageType {
    override val identifier = "wide"

    override fun getBlocks(noteLocations: MutableList<BlockPos>, breakLocations: MutableList<BlockPos>) {
        for (dx in -5..5) {
            for (dz in -5..5) {
                if (withinBreakingDistance(dx, 2, dz)) {
                    noteLocations.add(BlockPos(dx, 2, dz))
                    if (withinBreakingDistance(dx, -1, dz)) {
                        noteLocations.add(BlockPos(dx, -1, dz))
                        breakLocations.add(BlockPos(dx, 0, dz))
                        breakLocations.add(BlockPos(dx, 1, dz))
                    } else if (withinBreakingDistance(dx, 0, dz)) {
                        noteLocations.add(BlockPos(dx, 0, dz))
                        breakLocations.add(BlockPos(dx, 1, dz))
                    }
                }
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
