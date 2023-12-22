package com.github.hhhzzzsss.songplayer.stage;

import net.minecraft.util.math.BlockPos;

import java.util.Collection;

public class WideStage implements StageType {
    @Override
    public String getIdentifier() {
        return "wide";
    }

    @Override
    public void getBlocks(Collection<BlockPos> noteblockLocations, Collection<BlockPos> breakLocations) {
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                if (withinBreakingDistance(dx, 2, dz)) {
                    noteblockLocations.add(new BlockPos(dx, 2, dz));
                    if (withinBreakingDistance(dx, -1, dz)) {
                        noteblockLocations.add(new BlockPos(dx, -1, dz));
                        breakLocations.add(new BlockPos(dx, 0, dz));
                        breakLocations.add(new BlockPos(dx, 1, dz));
                    } else if (withinBreakingDistance(dx, 0, dz)) {
                        noteblockLocations.add(new BlockPos(dx, 0, dz));
                        breakLocations.add(new BlockPos(dx, 1, dz));
                    }
                }
                if (withinBreakingDistance(dx, -3, dz)) {
                    noteblockLocations.add(new BlockPos(dx, -3, dz));
                }
                if (withinBreakingDistance(dx, 4, dz)) {
                    noteblockLocations.add(new BlockPos(dx, 4, dz));
                }
            }
        }
    }
}
