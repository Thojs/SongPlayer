package com.github.hhhzzzsss.songplayer.stage;

import net.minecraft.util.math.BlockPos;

import java.util.Collection;

public class SphericalStage implements StageType {
    @Override
    public String getIdentifier() {
        return "spherical";
    }

    // This code was taken from Sk8kman fork of SongPlayer
    // Thanks Sk8kman and Lizard16 for this spherical stage design!
    @Override
    public void getBlocks(Collection<BlockPos> noteblockLocations, Collection<BlockPos> breakLocations) {
        int[] yLayers = {-4, -2, -1, 0, 1, 2, 3, 4, 5, 6};

        for (int dx = -5; dx <= 5; dx++) {
            for (int dz = -5; dz <= 5; dz++) {
                for (int dy : yLayers) {
                    int adx = Math.abs(dx);
                    int adz = Math.abs(dz);
                    switch(dy) {
                        case -4: {
                            if (adx < 3 && adz < 3) {
                                noteblockLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            if ((adx == 3 ^ adz == 3) && (adx == 0 ^ adz == 0)) {
                                noteblockLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            break;
                        }
                        case -2: { // also takes care of -3
                            if (adz == 0 && adx == 0) break; // prevent placing in the center

                            if (adz * adx > 9) break; // prevents building out too far

                            if (adz + adx == 5 && adx != 0 && adz != 0) {
                                // add noteblocks above and below here
                                noteblockLocations.add(new BlockPos(dx, dy + 1, dz));
                                noteblockLocations.add(new BlockPos(dx, dy - 1, dz));
                                break;
                            }
                            if (adz * adx == 3) {
                                // add noteblocks above and below here
                                noteblockLocations.add(new BlockPos(dx, dy + 1, dz));
                                noteblockLocations.add(new BlockPos(dx, dy - 1, dz));
                                break;
                            }
                            if (adx < 3 && adz < 3) {
                                noteblockLocations.add(new BlockPos(dx, dy, dz));
                                breakLocations.add(new BlockPos(dx, 0, dz));
                                break;
                            }
                            if (adz == 0 ^ adx == 0) {
                                noteblockLocations.add(new BlockPos(dx, dy, dz));
                                breakLocations.add(new BlockPos(dx, 0, dz));
                                break;
                            }
                            if (adz * adx == 10) { // expecting one to be 2, and one to be 5.
                                noteblockLocations.add(new BlockPos(dx, dy, dz));
                                breakLocations.add(new BlockPos(dx, 0, dz));
                                break;
                            }
                            if (adz + adx == 6) {
                                noteblockLocations.add(new BlockPos(dx, dy, dz));
                                if (adx == 5 ^ adz == 5) {
                                    breakLocations.add(new BlockPos(dx, 0, dz));
                                }
                                break;
                            }
                            break;
                        }
                        case -1: {
                            if (adx + adz == 7 || adx + adz == 0) {
                                noteblockLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            break;
                        }
                        case 0: {
                            int check = adx + adz;
                            if ((check == 8 || check == 6) && adx * adz > 5) {
                                noteblockLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            break;
                        }
                        case 1: {
                            int addl1 = adx + adz;
                            if (addl1 == 7 || addl1 == 3 || addl1 == 2) {
                                noteblockLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            if (adx == 5 ^ adz == 5 && addl1 < 7) {
                                noteblockLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            if (addl1 == 4 && adx * adz != 0) {
                                noteblockLocations.add(new BlockPos(dx, dy, dz));
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
                                noteblockLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            if ((addl2 == 4) && (adx == 0 ^ adz == 0)) {
                                noteblockLocations.add(new BlockPos(dx, dy, dz));
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
                                noteblockLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            if ((adx == 5 ^ adz == 5) && (adx < 2 ^ adz < 2)) {
                                noteblockLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            if (adx > 3 || adz > 3) { // don't allow any more checks past 3 blocks out
                                break;
                            }
                            if (adx + adz > 1 && adx + adz < 5) {
                                noteblockLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            break;
                        }
                        case 4: {
                            if (adx == 5 || adz == 5) {
                                break;
                            }
                            if (adx + adz == 4 && adx * adz == 0) {
                                noteblockLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            int addl4 = adx + adz;
                            if (addl4 == 1 || addl4 == 5 || addl4 == 6) {
                                noteblockLocations.add(new BlockPos(dx, dy, dz));
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
                                noteblockLocations.add(new BlockPos(dx, dy, dz));
                                break;
                            }
                            break;
                        }
                        case 6: {
                            if (adx + adz < 2) {
                                noteblockLocations.add(new BlockPos(dx, dy, dz));
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
}
