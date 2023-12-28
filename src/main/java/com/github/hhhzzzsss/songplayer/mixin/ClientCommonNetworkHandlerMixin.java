package com.github.hhhzzzsss.songplayer.mixin;

import com.github.hhhzzzsss.songplayer.Config;
import com.github.hhhzzzsss.songplayer.FakePlayerEntity;
import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.playing.SongHandler;
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
        FakePlayerEntity fakePlayer = SongHandler.fakePlayer;

        if (SongHandler.instance.isActive() && packet instanceof PlayerMoveC2SPacket) {
            ci.cancel();

            if (Config.getConfig().rotate) return;

            connection.send(new PlayerMoveC2SPacket.LookAndOnGround(
                    SongPlayer.MC.player.getYaw(), SongPlayer.MC.player.getPitch(),
                    true
            ));

            if (fakePlayer != null) fakePlayer.copyStagePosAndPlayerLook();
        } else if (packet instanceof ClientCommandC2SPacket) {
            ClientCommandC2SPacket.Mode mode = ((ClientCommandC2SPacket) packet).getMode();
            if (fakePlayer == null) return;

            if (mode == ClientCommandC2SPacket.Mode.PRESS_SHIFT_KEY) {
                fakePlayer.setSneaking(true);
                fakePlayer.setPose(EntityPose.CROUCHING);
            } else if (mode == ClientCommandC2SPacket.Mode.RELEASE_SHIFT_KEY) {
                fakePlayer.setSneaking(false);
                fakePlayer.setPose(EntityPose.STANDING);
            }
        }
    }
}
