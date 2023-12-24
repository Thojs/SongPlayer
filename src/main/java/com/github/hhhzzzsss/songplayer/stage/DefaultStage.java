package com.github.hhhzzzsss.songplayer.stage;

import net.minecraft.util.math.BlockPos;

import java.util.Collection;

public class DefaultStage implements StageType {
    @Override
    public String getIdentifier() {
        return "default";
    }

    @Override
    public void getBlocks(Collection<BlockPos> noteLocations, Collection<BlockPos> breakLocations) {
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                if (Math.abs(dx) == 4 && Math.abs(dz) == 4)  {
                    noteLocations.add(new BlockPos(dx, 0, dz));
                    noteLocations.add(new BlockPos(dx, 2, dz));
                    breakLocations.add(new BlockPos(dx, 1, dz));
                } else {
                    noteLocations.add(new BlockPos(dx, -1, dz));
                    noteLocations.add(new BlockPos(dx, 2, dz));
                    breakLocations.add(new BlockPos(dx, 0, dz));
                    breakLocations.add(new BlockPos(dx, 1, dz));
                }
            }
        }

        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                if (withinBreakingDistance(dx, -3, dz)) {
                    noteLocations.add(new BlockPos(dx, -3, dz));
                }
                if (withinBreakingDistance(dx, 4, dz)) {
                    noteLocations.add(new BlockPos(dx, 4, dz));
                }
            }
        }
    }
}
