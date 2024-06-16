package com.github.hhhzzzsss.songplayer.item

import com.github.hhhzzzsss.songplayer.SongPlayer
import com.github.hhhzzzsss.songplayer.io.SPParser
import com.github.hhhzzzsss.songplayer.item.SongItemUtils.createSongItem
import com.github.hhhzzzsss.songplayer.song.SongLoaderThread
import net.minecraft.item.Items
import net.minecraft.text.Text
import net.minecraft.util.Hand
import java.io.IOException

class SongItemCreatorThread(location: String?) : SongLoaderThread(location) {
    val slotId = SongPlayer.MC.player!!.inventory.selectedSlot
    val stack = SongPlayer.MC.player!!.inventory.getStack(slotId)

    override fun run() {
        super.run()

        val songData: ByteArray
        try {
            songData = SPParser.getBytesFromSong(song)
        } catch (e: IOException) {
            SongPlayer.addChatMessage("§cError creating song item: §4" + e.message)
            return
        }

        SongPlayer.MC.execute {
            if (SongPlayer.MC.world == null) return@execute

            if (SongPlayer.MC.player!!.inventory.getStack(slotId) != stack) {
                SongPlayer.addChatMessage("§cCould not create song item because item has moved")
            }

            var newStack = if (stack.isEmpty) Items.PAPER.defaultStack else stack.copy()

            newStack = createSongItem(newStack, songData, filename, song.name)
            SongPlayer.MC.player!!.inventory.setStack(slotId, newStack)
            SongPlayer.MC.interactionManager!!.clickCreativeStack(
                SongPlayer.MC.player!!.getStackInHand(Hand.MAIN_HAND),
                36 + slotId
            )
            SongPlayer.addChatMessage(Text.literal("§6Successfully assigned song data to §3" + song.name))
        }
    }
}
