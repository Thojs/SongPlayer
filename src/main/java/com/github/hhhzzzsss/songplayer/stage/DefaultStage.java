package com.github.hhhzzzsss.songplayer.stage;

import net.minecraft.util.math.BlockPos;

import java.util.Collection;

public class DefaultStage implements StageType {
    @Override
    public void getBlocks(BlockPos position, Collection<BlockPos> noteblockLocations, Collection<BlockPos> breakLocations) {
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
                if (Math.abs(dx) == 4 && Math.abs(dz) == 4)  {
                    noteblockLocations.add(new BlockPos(position.getX() + dx, position.getY(), position.getZ() + dz));
                    noteblockLocations.add(new BlockPos(position.getX() + dx, position.getY() + 2, position.getZ() + dz));
                    breakLocations.add(new BlockPos(position.getX() + dx, position.getY() + 1, position.getZ() + dz));
                } else {
                    noteblockLocations.add(new BlockPos(position.getX() + dx, position.getY() - 1, position.getZ() + dz));
                    noteblockLocations.add(new BlockPos(position.getX() + dx, position.getY() + 2, position.getZ() + dz));
                    breakLocations.add(new BlockPos(position.getX() + dx, position.getY(), position.getZ() + dz));
                    breakLocations.add(new BlockPos(position.getX() + dx, position.getY() + 1, position.getZ() + dz));
                }
            }
        }
        for (int dx = -4; dx <= 4; dx++) {
            for (int dz = -4; dz <= 4; dz++) {
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
