package com.github.hhhzzzsss.songplayer.stage;

import net.minecraft.util.math.BlockPos;
import org.jetbrains.annotations.NotNull;

import java.util.List;

public class JavaSphericalStage implements StageType {
    @NotNull
    @Override
    public String getIdentifier() {
        return "spherical";
    }

    @Override
    public void getBlocks(@NotNull List<BlockPos> noteLocations, @NotNull List<BlockPos> breakLocations) {
        int[] yLayers = {-4, -2, -1, 0, 1, 2, 3, 4, 5, 6};

        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                for (int dy : yLayers) {
                    int adx = Math.abs(dx);
                    int adz = Math.abs(dz);
                    switch(dy) {
                        case -4: {
                            if (adx < 3 && adz < 3) {
                                noteLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            if ((adx == 3 ^ adz == 3) && (adx == 0 ^ adz == 0)) {
                                noteLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            break;
                        }
                        case -2: { // also takes care of -3
                            if (adz == 0 && adx == 0) { // prevents placing int the center
                                break;
                            }
                            if (adz * adx > 9) { // prevents building out too far
                                break;
                            }
                            if (adz + adx == 5 && adx != 0 && adz != 0) {
                                // add noteblocks above and below here
                                noteLocations.add(new BlockPos(dx, dy + 1, dz));
                                noteLocations.add(new BlockPos(dx, dy - 1, dz));
                                break;
                            }
                            if (adz * adx == 3) {
                                // add noteblocks above and below here
                                noteLocations.add(new BlockPos(dx, dy + 1, dz));
                                noteLocations.add(new BlockPos(dx, dy - 1, dz));
                                break;
                            }
                            if (adx < 3 && adz < 3 && adx + adz > 0) {
                                noteLocations.add(new BlockPos(dx, dy, dz));
                                breakLocations.add(new BlockPos(dx, dy + 2, dz));
                                break;
                            }
                            if (adz == 0 ^ adx == 0) {
                                noteLocations.add(new BlockPos(dx, dy, dz));
                                breakLocations.add(new BlockPos(dx, dy + 2, dz));
                                break;
                            }
                            if (adz * adx == 10) { // expecting one to be 2, and one to be 5.
                                noteLocations.add(new BlockPos(dx, dy, dz));
                                breakLocations.add(new BlockPos(dx, dy + 2, dz));
                                break;
                            }
                            if (adz + adx == 6) {
                                noteLocations.add(new BlockPos(dx, dy, dz));
                                if (adx == 5 ^ adz == 5) {
                                    breakLocations.add(new BlockPos(dx, dy + 2, dz));
                                }
                                break;
                            }
                            break;
                        }
                        case -1: {
                            if (adx + adz == 7 || adx + adz == 0) {
                                noteLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            break;
                        }
                        case 0: {
                            int check = adx + adz;
                            if ((check == 8 || check == 6) && adx * adz > 5) {
                                noteLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            break;
                        }
                        case 1: {
                            int addl1 = adx + adz;
                            if (addl1 == 7 || addl1 == 3 || addl1 == 2) {
                                noteLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            if (adx == 5 ^ adz == 5 && addl1 < 7) {
                                noteLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            if (addl1 == 4 && adx * adz != 0) {
                                noteLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            if (adx + adz < 7) {
                                breakLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            break;
                        }
                        case 2: {
                            int addl2 = adx + adz;
                            if (adx == 5 || adz == 5) {
                                break;
                            }
                            if (addl2 == 8 || addl2 == 6 || addl2 == 5 || addl2 == 1) {
                                noteLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            if ((addl2 == 4) && (adx == 0 ^ adz == 0)) {
                                noteLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            if (addl2 == 0) {
                                breakLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            break;
                        }
                        case 3: {
                            if (adx * adz == 12 || adx + adz == 0) {
                                noteLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            if ((adx == 5 ^ adz == 5) && (adx < 2 ^ adz < 2)) {
                                noteLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            if (adx > 3 || adz > 3) { // don't allow any more checks past 3 blocks out
                                break;
                            }
                            if (adx + adz > 1 && adx + adz < 5) {
                                noteLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            break;
                        }
                        case 4: {
                            if (adx == 5 || adz == 5) {
                                break;
                            }
                            if (adx + adz == 4 && adx * adz == 0) {
                                noteLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            int addl4 = adx + adz;
                            if (addl4 == 1 || addl4 == 5 || addl4 == 6) {
                                noteLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            break;
                        }
                        case 5: {
                            if (adx > 3 || adz > 3) {
                                break;
                            }
                            int addl5 = adx + adz;
                            if (addl5 > 1 && addl5 < 5) {
                                noteLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            break;
                        }
                        case 6: {
                            if (adx + adz < 2) {
                                noteLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            break;
                        }
                    }
                    //all breaks lead here
                }
            }
        }
    }

    @Override
    public boolean withinBreakingDistance(int dx, int dy, int dz) {
        double dy1 = dy + 0.5 - 1.62; // Standing eye height
        double dy2 = dy + 0.5 - 1.27; // Crouching eye height
        return dx * dx + dy1 * dy1 + dz * dz < 5.99999 * 5.99999 && dx * dx + dy2 * dy2 + dz * dz < 5.99999 * 5.99999;
    }
}
