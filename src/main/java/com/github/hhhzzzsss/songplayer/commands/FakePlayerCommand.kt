package com.github.hhhzzzsss.songplayer.commands

import com.github.hhhzzzsss.songplayer.Config
import com.github.hhhzzzsss.songplayer.SongPlayer
import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

internal class FakePlayerCommand : Command {
    override val name = "fakePlayer"

    override val description = "Shows a fake player representing your true position when playing songs"

    override fun buildNode(node: LiteralArgumentBuilder<FabricClientCommandSource>) {
        node.executes {
            Config.getConfig().showFakePlayer = !Config.getConfig().showFakePlayer

            if (Config.getConfig().showFakePlayer) {
                SongPlayer.addChatMessage("§6Enabled fake player")
            } else {
                SongPlayer.addChatMessage("§6Disabled fake player")
            }

            Config.saveConfigWithErrorHandling()
            1
        }
    }
}