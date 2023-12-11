package com.github.hhhzzzsss.songplayer.stage;

import net.minecraft.util.math.BlockPos;

import java.util.Collection;

public class WideStage implements StageType {
    @Override
    public void getBlocks(BlockPos position, Collection<BlockPos> noteblockLocations, Collection<BlockPos> breakLocations) {
        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                if (withinBreakingDistance(dx, 2, dz)) {
                    noteblockLocations.add(new BlockPos(position.getX() + dx, position.getY() + 2, position.getZ() + dz));
                    if (withinBreakingDistance(dx, -1, dz)) {
                        noteblockLocations.add(new BlockPos(position.getX() + dx, position.getY() - 1, position.getZ() + dz));
                        breakLocations.add(new BlockPos(position.getX() + dx, position.getY(), position.getZ() + dz));
                        breakLocations.add(new BlockPos(position.getX() + dx, position.getY() + 1, position.getZ() + dz));
                    } else if (withinBreakingDistance(dx, 0, dz)) {
                        noteblockLocations.add(new BlockPos(position.getX() + dx, position.getY(), position.getZ() + dz));
                        breakLocations.add(new BlockPos(position.getX() + dx, position.getY() + 1, position.getZ() + dz));
                    }
                }
                if (withinBreakingDistance(dx, -3, dz)) {
                    noteblockLocations.add(new BlockPos(position.getX() + dx, position.getY() - 3, position.getZ() + dz));
                }
                if (withinBreakingDistance(dx, 4, dz)) {
                    noteblockLocations.add(new BlockPos(position.getX() + dx, position.getY() + 4, position.getZ() + dz));
                }
            }
        }
    }
}
