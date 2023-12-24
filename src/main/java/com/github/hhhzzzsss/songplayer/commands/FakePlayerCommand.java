package com.github.hhhzzzsss.songplayer.commands;

import com.github.hhhzzzsss.songplayer.Config;
import com.github.hhhzzzsss.songplayer.SongPlayer;
import com.mojang.brigadier.builder.LiteralArgumentBuilder;
import net.fabricmc.fabric.api.client.command.v2.FabricClientCommandSource;

class FakePlayerCommand extends Command {
    @Override
    public String getName() {
        return "fakePlayer";
    }

    @Override
    public String getDescription() {
        return "Shows a fake player representing your true position when playing songs";
    }

    @Override
    public void buildNode(LiteralArgumentBuilder<FabricClientCommandSource> node) {
        node.executes(context -> {
            Config.getConfig().showFakePlayer ^= true;

            if (Config.getConfig().showFakePlayer) {
                SongPlayer.addChatMessage("§6Enabled fake player");
            } else {
                SongPlayer.addChatMessage("§6Disabled fake player");
            }

            Config.saveConfigWithErrorHandling();

            return 1;
        });
    }
}