package com.github.hhhzzzsss.songplayer.stage;

import net.minecraft.util.math.BlockPos;

import java.util.Collection;

public interface StageType {
    String getIdentifier();

    void getBlocks(Collection<BlockPos> noteblockLocations, Collection<BlockPos> breakLocations);

    default boolean withinBreakingDistance(int dx, int dy, int dz) {
        double dy1 = dy + 0.5 - 1.62; // Standing eye height
        double dy2 = dy + 0.5 - 1.27; // Crouching eye height
        return dx*dx + dy1*dy1 + dz*dz < 5.99999*5.99999 && dx*dx + dy2*dy2 + dz*dz < 5.99999*5.99999;
    }
}
