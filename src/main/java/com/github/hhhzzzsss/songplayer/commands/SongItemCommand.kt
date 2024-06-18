package com.github.hhhzzzsss.songplayer.commands

import com.github.hhhzzzsss.songplayer.SongPlayer
import com.github.hhhzzzsss.songplayer.item.SongItemCreatorThread
import com.github.hhhzzzsss.songplayer.item.SongItemUtils
import com.github.hhhzzzsss.songplayer.item.SongItemUtils.setItemDisplayData
import com.github.hhhzzzsss.songplayer.utils.SuggestionUtil
import com.github.hhhzzzsss.songplayer.utils.SuggestionUtil.safeSuggestions
import com.mojang.brigadier.arguments.StringArgumentType
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource
import net.minecraft.util.Hand
import net.minecraft.world.GameMode
import java.io.IOException

internal class SongItemCommand : Command {
    override val name = "item"
    override val description = "Assigns/edits song data for the item in your hand"

    override fun buildNode(node: LiteralArgumentBuilder<FabricClientCommandSource>) {
        node.then(ClientCommandManager.literal("create")
            .then(ClientCommandManager.argument("song", StringArgumentType.greedyString())
                .suggests(safeSuggestions(SuggestionUtil::giveSongSuggestions))
                .executes { context ->
                    if (SongPlayer.MC.interactionManager!!.currentGameMode != GameMode.CREATIVE) {
                        SongPlayer.addChatMessage("§cYou must be in creative mode to use this command")
                        return@executes 1
                    }
                    val location = context.getArgument("song", String::class.java)
                    try {
                        SongItemCreatorThread(location).start()
                    } catch (e: IOException) {
                        SongPlayer.addChatMessage("§cError creating song item: §4" + e.message)
                    }

                    1
                }
            )
        )

        node.then(ClientCommandManager.literal("setname")
            .then(ClientCommandManager.argument("name", StringArgumentType.greedyString())
                .executes { context ->
                    if (SongPlayer.MC.interactionManager!!.currentGameMode != GameMode.CREATIVE) {
                        SongPlayer.addChatMessage("§cYou must be in creative mode to use this command")
                        return@executes 1
                    }
                    val stack = SongPlayer.MC.player!!.mainHandStack

                    if (!SongItemUtils.isSongItem(stack)) {
                        SongPlayer.addChatMessage("§cYou must be holding a song item")
                        return@executes 1
                    }

                    val songItemData = SongItemUtils.getData(stack) ?: return@executes 1

                    val name = context.getArgument("name", String::class.java)

                    songItemData.displayName = name
                    SongItemUtils.setData(stack, songItemData)

                    setItemDisplayData(stack)

                    SongPlayer.MC.player!!.setStackInHand(Hand.MAIN_HAND, stack)
                    SongPlayer.MC.interactionManager!!.clickCreativeStack(
                        SongPlayer.MC.player!!.getStackInHand(Hand.MAIN_HAND),
                        36 + SongPlayer.MC.player!!.inventory.selectedSlot
                    )
                    SongPlayer.addChatMessage("§6Set song's display name to §3$name")
                    1
                }
            )
        )
    }
}