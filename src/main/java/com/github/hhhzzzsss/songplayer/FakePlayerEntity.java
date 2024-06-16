package com.github.hhhzzzsss.songplayer;

import com.github.hhhzzzsss.songplayer.mixin.ClientPlayNetworkHandlerAccessor;
import com.github.hhhzzzsss.songplayer.playing.SongHandler;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.util.math.BlockPos;

import java.util.UUID;

public class FakePlayerEntity extends OtherClientPlayerEntity {
	public static final UUID FAKE_PLAYER_UUID = UUID.randomUUID();

	ClientPlayerEntity player = SongPlayer.MC.player;

	public FakePlayerEntity() {
		super(SongPlayer.MC.world, getProfile());

		copyStagePosAndPlayerLook();
		syncWithPlayer();

		headYaw = player.headYaw;
		bodyYaw = player.bodyYaw;

		if (player.isSneaking()) {
			setSneaking(true);
			setPose(EntityPose.CROUCHING);
		}

		capeX = getX();
		capeY = getY();
		capeZ = getZ();

		clientWorld.addEntity(this);
	}

	public void syncWithPlayer() {
		getInventory().clone(player.getInventory());

		Byte playerModel = player.getDataTracker().get(PlayerEntity.PLAYER_MODEL_PARTS);
		getDataTracker().set(PlayerEntity.PLAYER_MODEL_PARTS, playerModel);
	}
	
	public void copyStagePosAndPlayerLook() {
		BlockPos pos = SongHandler.instance.getStagePosition();
		if (pos != null) {
			refreshPositionAndAngles(pos.getX()+0.5, pos.getY(), pos.getZ()+0.5, player.getYaw(), player.getPitch());
			headYaw = player.headYaw;
		} else {
			copyPositionAndRotation(player);
		}
	}

	private static GameProfile getProfile() {
		GameProfile profile = new GameProfile(FAKE_PLAYER_UUID, SongPlayer.MC.player.getGameProfile().getName());
		profile.getProperties().putAll(SongPlayer.MC.player.getGameProfile().getProperties());
		PlayerListEntry playerListEntry = new PlayerListEntry(SongPlayer.MC.player.getGameProfile(), false);
		((ClientPlayNetworkHandlerAccessor) SongPlayer.MC.getNetworkHandler()).getPlayerListEntries().put(FAKE_PLAYER_UUID, playerListEntry);
		return profile;
	}
}
