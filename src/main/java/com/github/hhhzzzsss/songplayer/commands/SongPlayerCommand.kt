package com.github.hhhzzzsss.songplayer.commands

import com.mojang.brigadier.CommandDispatcher
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource

class SongPlayerCommand {
    fun registerCommand(dispatcher: CommandDispatcher<FabricClientCommandSource>) {
        val rootCommand = ClientCommandManager.literal("songplayer")

        // Add sub-commands
        for (command in commands) {
            val commandNode = ClientCommandManager.literal(command.name)
            command.buildNode(commandNode)

            rootCommand.then(commandNode.build())
        }

        // Register to dispatcher
        val rootCommandNode = dispatcher.register(rootCommand)
        dispatcher.register(ClientCommandManager.literal("sp").redirect(rootCommandNode))
    }

    companion object {
        private val commands = listOf(
            AnnouncementCommand(),
            SetCommandsCommand(),
            FakePlayerCommand(),
            StageTypeCommand(),
            TestSongCommand(),
            MovementCommand(),
            SongItemCommand(),
            StatusCommand(),
            QueueCommand(),
            SeekCommand(),
            LoopCommand(),
            PlayCommand(),
            StopCommand(),
            SkipCommand()
        )
    }
}