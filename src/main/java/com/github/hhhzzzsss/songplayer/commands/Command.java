package com.github.hhhzzzsss.songplayer.commands;

import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

abstract class Command {
    public abstract String getName();

    public abstract String getDescription();

    public abstract void buildNode(LiteralArgumentBuilder<FabricClientCommandSource> node);
}