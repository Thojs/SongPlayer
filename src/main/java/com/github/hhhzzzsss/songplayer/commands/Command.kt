package com.github.hhhzzzsss.songplayer.commands

import com.mojang.brigadier.builder.LiteralArgumentBuilder
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

interface Command {
    val name: String

    val description: String

    fun buildNode(node: LiteralArgumentBuilder<FabricClientCommandSource>)
}