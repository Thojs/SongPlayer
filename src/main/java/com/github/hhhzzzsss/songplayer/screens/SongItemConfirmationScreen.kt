package com.github.hhhzzzsss.songplayer.screens

import com.github.hhhzzzsss.songplayer.SongPlayer
import com.github.hhhzzzsss.songplayer.item.SongItemLoaderThread
import com.github.hhhzzzsss.songplayer.playing.SongHandler
import com.github.hhhzzzsss.songplayer.utils.Util.humanReadableByteCountSI
import net.minecraft.client.font.MultilineText
import net.minecraft.client.gui.DrawContext
import net.minecraft.client.gui.screen.Screen
import net.minecraft.client.gui.widget.ButtonWidget
import net.minecraft.item.ItemStack
import net.minecraft.screen.ScreenTexts
import net.minecraft.text.Text
import java.util.*
import java.util.stream.Collectors

class SongItemConfirmationScreen(stack: ItemStack) : Screen(Text.literal("Use song item")) {
    private val loaderThread = SongItemLoaderThread(stack)
    private lateinit var unloadedText: MultilineText
    private var loadedText: MultilineText? = null
    private var loaded = false

    init {
        loaderThread.start()
    }

    override fun init() {
        super.init()
        val unloadedMessage = "§7Loading song..."
        this.unloadedText = MultilineText.create(this.textRenderer, Text.literal(unloadedMessage))
    }

    private fun addButtons(y: Int) {
        val centerX = this.width / 2

        this.addDrawableChild(ButtonWidget.builder(CONFIRM) {
            try {
                SongHandler.instance.songQueue.loadSong(loaderThread)
            } catch (e: IllegalStateException) {
                SongPlayer.addChatMessage("§cFailed to load song: §4" + e.message)
            }
            client!!.setScreen(null)
        }.dimensions(centerX - 105, y, 100, 20).build())

        this.addDrawableChild(ButtonWidget.builder(CANCEL) {
            client!!.setScreen(null)
        }.dimensions(centerX + 5, y, 100, 20).build())
    }

    override fun render(context: DrawContext, mouseX: Int, mouseY: Int, delta: Float) {
        super.render(context, mouseX, mouseY, delta)

        context.drawCenteredTextWithShadow(textRenderer, this.title, this.width / 2, 60, 0xFFFFFF)

        if (!loaderThread.isAlive) {
            if (loaderThread.exception != null) {
                SongPlayer.addChatMessage("§cError loading song item: §4" + loaderThread.exception!!.message)
                client!!.setScreen(null)
                return
            }

            if (loadedText == null) {
                val loadedMessages = arrayOf(
                    "§3" + loaderThread.song!!.name,
                    "",
                    String.format(
                        "§7Size: %s", humanReadableByteCountSI(
                            loaderThread.data!!.songData.size.toLong()
                        )
                    ),
                    String.format(
                        "§7Max notes per second: %s%d",
                        getNumberColor(loaderThread.maxNotesPerSecond.toDouble()),
                        loaderThread.maxNotesPerSecond
                    ),
                    String.format(
                        "§7Avg notes per second: %s%.2f",
                        getNumberColor(loaderThread.avgNotesPerSecond),
                        loaderThread.avgNotesPerSecond
                    ),
                )
                val messageList = Arrays.stream(loadedMessages).map { string: String? -> Text.literal(string) }.collect(Collectors.toList())
                this.loadedText = MultilineText.createFromTexts(this.textRenderer, messageList.toList())

                val loadedTextHeight = loadedText!!.count() * textRenderer.fontHeight
                addButtons(80 + loadedTextHeight + 12)

                loaded = true
            }
        }

        if (loaded) {
            loadedText!!.drawCenterWithShadow(context, this.width / 2, 80)
        } else {
            unloadedText.drawCenterWithShadow(context, this.width / 2, 80)
        }
    }

    private fun getNumberColor(number: Double): String {
        return if (number < 50) {
            "§a"
        } else if (number < 100) {
            "§e"
        } else if (number < 300) {
            "§6"
        } else if (number < 600) {
            "§c"
        } else {
            "§4"
        }
    }

    companion object {
        private val CONFIRM = Text.literal("Play")
        private val CANCEL = ScreenTexts.CANCEL
    }
}
