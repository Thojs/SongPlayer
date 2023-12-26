package com.github.hhhzzzsss.songplayer.mixin;

import com.github.hhhzzzsss.songplayer.playing.SongPlayer;
import com.github.hhhzzzsss.songplayer.playing.StageBuilder;
import net.minecraft.block.BlockState;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.util.math.BlockPos;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientWorld.class)
public class ClientWorldMixin {
    @Inject(at = @At("HEAD"), method = "handleBlockUpdate(Lnet/minecraft/util/math/BlockPos;Lnet/minecraft/block/BlockState;I)V", cancellable = true)
    public void onHandleBlockUpdate(BlockPos pos, BlockState state, int flags, CallbackInfo ci) {
        StageBuilder stageBuilder = SongPlayer.instance.stageBuilder;
        if (state == null || SongPlayer.instance.building) return;

        for (BlockPos nbp : stageBuilder.noteblockPositions.values()) {
            if (!nbp.equals(pos)) continue;

            BlockState oldState = com.github.hhhzzzsss.songplayer.SongPlayer.MC.world.getBlockState(pos);
            if (oldState.equals(state)) return;
            com.github.hhhzzzsss.songplayer.SongPlayer.addChatMessage(String.format("§7Block in stage changed from §2%s §7to §2%s", oldState, state));
            break;
        }
    }
}