package com.github.hhhzzzsss.songplayer;

import com.github.hhhzzzsss.songplayer.commands.SongPlayerCommand;
import com.github.hhhzzzsss.songplayer.commands.TimestampArgumentType;
import com.github.hhhzzzsss.songplayer.io.SongParserRegistry;
import com.github.hhhzzzsss.songplayer.io.MidiParser;
import com.github.hhhzzzsss.songplayer.io.NBSParser;
import com.github.hhhzzzsss.songplayer.io.SPParser;
import com.github.hhhzzzsss.songplayer.item.SongItemUtils;
import com.github.hhhzzzsss.songplayer.stage.*;
import com.github.hhhzzzsss.songplayer.utils.Util;
import net.fabricmc.api.ModInitializer;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandRegistrationCallback;
import net.fabricmc.fabric.api.command.v2.ArgumentTypeRegistry;
import net.minecraft.client.MinecraftClient;
import net.minecraft.client.item.ModelPredicateProviderRegistry;
import net.minecraft.command.argument.serialize.ConstantArgumentSerializer;
import net.minecraft.item.Items;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

import java.nio.file.Path;

public class SongPlayer implements ModInitializer {
	public static final String MOD_ID = "songplayer";
	public static final MinecraftClient MC = MinecraftClient.getInstance();

	public static final Path SONGPLAYER_DIR = Path.of("SongPlayer");
	public static final Path SONG_DIR = SONGPLAYER_DIR.resolve("songs");

	@Override
	public void onInitialize() {
		// Create directories.
		Util.createDirectoriesSilently(SONG_DIR);
		Util.createDirectoriesSilently(SONGPLAYER_DIR);

		// Custom predicate for song item.
		ModelPredicateProviderRegistry.register(Items.PAPER, new Identifier("song_item"), (itemStack, clientWorld, livingEntity, seed) -> SongItemUtils.isSongItem(itemStack) ? 1F : 0F);

		// Register StageTypes
		StageTypeRegistry.instance.registerStageTypes(
				new JavaSphericalStage(),
				new DefaultStage(),
				new WideStage()
		);

		// Register parsers
		SongParserRegistry.INSTANCE.registerParsers(
				new MidiParser(),
				new NBSParser(),
				new SPParser()
		);

		// Register commands
		ArgumentTypeRegistry.registerArgumentType(Identifier.of("songplayer", "timestamp"), TimestampArgumentType.class, ConstantArgumentSerializer.of(TimestampArgumentType::timestamp));
		ClientCommandRegistrationCallback.EVENT.register(((dispatcher, registryAccess) -> new SongPlayerCommand().registerCommand(dispatcher)));
	}

	public static void addChatMessage(String message) {
		addChatMessage(Text.of(message));
	}

	public static void addChatMessage(Text text) {
		MC.player.sendMessage(text, false);
	}
}
