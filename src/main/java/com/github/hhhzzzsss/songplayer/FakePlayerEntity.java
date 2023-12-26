package com.github.hhhzzzsss.songplayer;

import com.github.hhhzzzsss.songplayer.mixin.ClientPlayNetworkHandlerAccessor;
import com.github.hhhzzzsss.songplayer.playing.NotePlayer;
import com.github.hhhzzzsss.songplayer.playing.StageBuilder;
import com.mojang.authlib.GameProfile;
import net.minecraft.client.network.ClientPlayerEntity;
import net.minecraft.client.network.OtherClientPlayerEntity;
import net.minecraft.client.network.PlayerListEntry;
import net.minecraft.client.world.ClientWorld;
import net.minecraft.entity.EntityPose;
import net.minecraft.entity.player.PlayerEntity;

import java.util.UUID;

public class FakePlayerEntity extends OtherClientPlayerEntity {
	public static final UUID FAKE_PLAYER_UUID = UUID.randomUUID();

	ClientPlayerEntity player = com.github.hhhzzzsss.songplayer.SongPlayer.MC.player;
	ClientWorld world = com.github.hhhzzzsss.songplayer.SongPlayer.MC.world;
	
	public FakePlayerEntity() {
		super(com.github.hhhzzzsss.songplayer.SongPlayer.MC.world, getProfile());
		
		copyStagePosAndPlayerLook();
		
		getInventory().clone(player.getInventory());
		
		Byte playerModel = player.getDataTracker().get(PlayerEntity.PLAYER_MODEL_PARTS);
		getDataTracker().set(PlayerEntity.PLAYER_MODEL_PARTS, playerModel);
		
		headYaw = player.headYaw;
		bodyYaw = player.bodyYaw;

		if (player.isSneaking()) {
			setSneaking(true);
			setPose(EntityPose.CROUCHING);
		}

		capeX = getX();
		capeY = getY();
		capeZ = getZ();
		
		world.addEntity(this);
	}
	
	public void copyStagePosAndPlayerLook() {
		StageBuilder stageBuilder = NotePlayer.instance.stageBuilder;
		if (stageBuilder != null) {
			refreshPositionAndAngles(stageBuilder.position.getX()+0.5, stageBuilder.position.getY(), stageBuilder.position.getZ()+0.5, player.getYaw(), player.getPitch());
			headYaw = player.headYaw;
		} else {
			copyPositionAndRotation(player);
		}
	}

	private static GameProfile getProfile() {
		GameProfile profile = new GameProfile(FAKE_PLAYER_UUID, com.github.hhhzzzsss.songplayer.SongPlayer.MC.player.getGameProfile().getName());
		profile.getProperties().putAll(com.github.hhhzzzsss.songplayer.SongPlayer.MC.player.getGameProfile().getProperties());
		PlayerListEntry playerListEntry = new PlayerListEntry(com.github.hhhzzzsss.songplayer.SongPlayer.MC.player.getGameProfile(), false);
		((ClientPlayNetworkHandlerAccessor) com.github.hhhzzzsss.songplayer.SongPlayer.MC.getNetworkHandler()).getPlayerListEntries().put(FAKE_PLAYER_UUID, playerListEntry);
		return profile;
	}
}
