package com.github.hhhzzzsss.songplayer.mixin;

import com.github.hhhzzzsss.songplayer.Config;
import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.playing.SongHandler;
import com.github.hhhzzzsss.songplayer.playing.StageBuilder;
import net.minecraft.client.network.ClientCommonNetworkHandler;
import net.minecraft.entity.EntityPose;
import net.minecraft.network.ClientConnection;
import net.minecraft.network.packet.Packet;
import net.minecraft.network.packet.c2s.play.ClientCommandC2SPacket;
import net.minecraft.network.packet.c2s.play.PlayerMoveC2SPacket;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(ClientCommonNetworkHandler.class)
public class ClientCommonNetworkHandlerMixin {
    @Shadow
    private final ClientConnection connection;

    public ClientCommonNetworkHandlerMixin() {
        connection = null;
    }

    @Inject(at = @At("HEAD"), method = "sendPacket(Lnet/minecraft/network/packet/Packet;)V", cancellable = true)
    private void onSendPacket(Packet<?> packet, CallbackInfo ci) {
        StageBuilder stageBuilder = SongHandler.instance.stageBuilder;

        if (stageBuilder != null && packet instanceof PlayerMoveC2SPacket) {
            ci.cancel();
            if (Config.getConfig().rotate) return;

            connection.send(new PlayerMoveC2SPacket.Full(stageBuilder.position.getX() + 0.5, stageBuilder.position.getY(), stageBuilder.position.getZ() + 0.5, SongPlayer.MC.player.getYaw(), SongPlayer.MC.player.getPitch(), true));
            if (SongPlayer.fakePlayer != null) {
                SongPlayer.fakePlayer.copyStagePosAndPlayerLook();
            }
        } else if (packet instanceof ClientCommandC2SPacket) {
            ClientCommandC2SPacket.Mode mode = ((ClientCommandC2SPacket) packet).getMode();
            if (SongPlayer.fakePlayer == null) return;

            if (mode == ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY) {
                SongPlayer.fakePlayer.setSneaking(true);
                SongPlayer.fakePlayer.setPose(EntityPose.CROUCHING);
            } else if (mode == ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY) {
                SongPlayer.fakePlayer.setSneaking(false);
                SongPlayer.fakePlayer.setPose(EntityPose.STANDING);
            }
        }
    }
}
