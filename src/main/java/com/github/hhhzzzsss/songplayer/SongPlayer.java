package com.github.hhhzzzsss.songplayer;

import com.github.hhhzzzsss.songplayer.commands.SongPlayerCommand;
import com.github.hhhzzzsss.songplayer.conversion.SongParserRegistry;
import com.github.hhhzzzsss.songplayer.conversion.MidiParser;
import com.github.hhhzzzsss.songplayer.conversion.NBSParser;
import com.github.hhhzzzsss.songplayer.conversion.SPParser;
import com.github.hhhzzzsss.songplayer.stage.DefaultStage;
import com.github.hhhzzzsss.songplayer.stage.SphericalStage;
import com.github.hhhzzzsss.songplayer.stage.StageTypeRegistry;
import com.github.hhhzzzsss.songplayer.stage.WideStage;
import com.github.hhhzzzsss.songplayer.utils.Util;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.client.MinecraftClient;
import net.minecraft.entity.Entity;
import net.minecraft.text.Text;

import java.nio.file.Files;
import java.nio.file.Path;

public class SongPlayer implements ModInitializer {
	public static final MinecraftClient MC = MinecraftClient.getInstance();
	public static final int NOTEBLOCK_BASE_ID = Block.getRawIdFromState(Blocks.NOTE_BLOCK.getDefaultState())-1;

	public static final Path SONGPLAYER_DIR = Path.of("SongPlayer");
	public static final Path PLAYLISTS_DIR = SONGPLAYER_DIR.resolve("playlists");
	public static final Path SONG_DIR = SONGPLAYER_DIR.resolve("songs");
	public static FakePlayerEntity fakePlayer;

	@Override
	public void onInitialize() {
		if (!Files.exists(SONG_DIR)) {
			Util.createDirectoriesSilently(SONG_DIR);
		}
		if (!Files.exists(SONGPLAYER_DIR)) {
			Util.createDirectoriesSilently(SONGPLAYER_DIR);
		}
		if (!Files.exists(PLAYLISTS_DIR)) {
			Util.createDirectoriesSilently(PLAYLISTS_DIR);
		}

		// Register StageTypes
		StageTypeRegistry.instance.registerStageTypes(
				new DefaultStage(),
				new WideStage(),
				new SphericalStage()
		);

		// Register converters
		SongParserRegistry.instance.registerConverters(
				new MidiParser(),
				new NBSParser(),
				new SPParser()
		);

		// Register commands
		ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> new SongPlayerCommand().registerCommand(dispatcher)));
	}

	public static void addChatMessage(String message) {
		addChatMessage(Text.of(message));
	}

	public static void addChatMessage(Text text) {
		MC.player.sendMessage(text, false);
	}

	public static void removeFakePlayer() {
		if (fakePlayer != null) {
			fakePlayer.remove(Entity.RemovalReason.DISCARDED);
			fakePlayer = null;
		}
	}
}
