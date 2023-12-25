package com.github.hhhzzzsss.songplayer.commands;

import com.mojang.brigadier.CommandDispatcher;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import com.mojang.brigadier.tree.LiteralCommandNode;
import net.fabricmc.fabric.api.client.command.v2.ClientCommandManager;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

import java.util.List;

public class SongPlayerCommand {
    private static final List<Command> commands = List.of(
            new AnnouncementCommand(),
            new GotoCommand(),
            new LoopCommand(),
            new PlayCommand(),
            new QueueCommand(),
            new SetCommandsCommand(),
            new StageTypeCommand(),
            new SkipCommand(),
            new SongItemCommand(),
            new SongsCommand(),
            new StatusCommand(),
            new StopCommand(),
            new TestSongCommand(),
            new FakePlayerCommand(),
            new MovementCommand()
    );

    public void registerCommand(CommandDispatcher<FabricClientCommandSource> dispatcher) {
        LiteralArgumentBuilder<FabricClientCommandSource> rootCommand = ClientCommandManager.literal("songplayer");

        // Add sub-commands
        for (Command command : commands) {
            LiteralArgumentBuilder<FabricClientCommandSource> commandNode = ClientCommandManager.literal(command.getName());
            command.buildNode(commandNode);

            rootCommand.then(commandNode.build());
        }

        // Register to dispatcher
        LiteralCommandNode<FabricClientCommandSource> rootCommandNode = dispatcher.register(rootCommand);
        dispatcher.register(ClientCommandManager.literal("sp").redirect(rootCommandNode));
    }
}