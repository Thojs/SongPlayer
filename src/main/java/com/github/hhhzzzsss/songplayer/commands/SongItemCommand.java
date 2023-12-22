package com.github.hhhzzzsss.songplayer.commands;

import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.github.hhhzzzsss.songplayer.item.SongItemCreatorThread;
import com.github.hhhzzzsss.songplayer.item.SongItemUtils;
import com.github.hhhzzzsss.songplayer.utils.SuggestionUtil;
import com.mojang.brigadier.arguments.StringArgumentType;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.util.Hand;
import net.minecraft.world.GameMode;

import java.io.IOException;

import static com.github.hhhzzzsss.songplayer.SongPlayer.MC;

class SongItemCommand extends Command {
    @Override
    public String getName() {
        return "songItem";
    }

    @Override
    public String getDescription() {
        return "Assigns/edits song data for the item in your hand";
    }

    @Override
    public void buildNode(LiteralArgumentBuilder<FabricClientCommandSource> node) {
        node.then(ClientCommandManager.literal("create")
            .then(ClientCommandManager.argument("song", StringArgumentType.greedyString())
                .suggests(SuggestionUtil.safeSuggestions(SuggestionUtil::giveSongSuggestions))
                .executes(context -> {
                    if (MC.interactionManager.getCurrentGameMode() != GameMode.CREATIVE) {
                        SongPlayer.addChatMessage("§cYou must be in creative mode to use this command");
                        return 1;
                    }

                    String location = context.getArgument("song", String.class);
                    try {
                        (new SongItemCreatorThread(location)).start();
                    } catch (IOException e) {
                        SongPlayer.addChatMessage("§cError creating song item: §4" + e.getMessage());
                    }

                    return 1;
                }
            ))
        );

        node.then(ClientCommandManager.literal("setname")
            .then(ClientCommandManager.argument("name", StringArgumentType.greedyString()).executes(context -> {
                if (MC.interactionManager.getCurrentGameMode() != GameMode.CREATIVE) {
                    SongPlayer.addChatMessage("§cYou must be in creative mode to use this command");
                    return 1;
                }

                ItemStack stack = MC.player.getMainHandStack();
                NbtCompound songPlayerNBT = SongItemUtils.getSongItemTag(stack);

                if (songPlayerNBT == null) {
                    SongPlayer.addChatMessage("§cYou must be holding a song item");
                    return 1;
                }

                String name = context.getArgument("name", String.class);
                songPlayerNBT.putString(SongItemUtils.DISPLAY_NAME_KEY, name);
                SongItemUtils.addSongItemDisplay(stack);
                MC.player.setStackInHand(Hand.MAIN_HAND, stack);
                MC.interactionManager.clickCreativeStack(MC.player.getStackInHand(Hand.MAIN_HAND), 36 + MC.player.getInventory().selectedSlot);
                SongPlayer.addChatMessage("§6Set song's display name to §3" + name);

                return 1;
            }))
        );
    }
}