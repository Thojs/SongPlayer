package com.github.hhhzzzsss.songplayer.playing

import com.github.hhhzzzsss.songplayer.SongPlayer
import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.DrawContext
import net.minecraft.text.MutableText
import net.minecraft.text.Text

object ProgressDisplay {
    private var topText: MutableText = Text.empty()
    private var bottomText: MutableText = Text.empty()
    private var fade = 0

    fun setText(bottomText: MutableText, topText: MutableText) {
        this.bottomText = bottomText
        this.topText = topText
        fade = 100
    }

    fun onRenderHUD(context: DrawContext, heldItemTooltipFade: Int) {
        if (fade <= 0) return

        val bottomTextWidth = SongPlayer.MC.textRenderer.getWidth(bottomText)
        val topTextWidth = SongPlayer.MC.textRenderer.getWidth(topText)
        val bottomTextX = (SongPlayer.MC.window.scaledWidth - bottomTextWidth) / 2
        val topTextX = (SongPlayer.MC.window.scaledWidth - topTextWidth) / 2
        var bottomTextY = SongPlayer.MC.window.scaledHeight - 59

        if (!SongPlayer.MC.interactionManager!!.hasStatusBars()) {
            bottomTextY += 14
        }

        if (heldItemTooltipFade > 0) {
            bottomTextY -= 12
        }

        val topTextY = bottomTextY - 12

        var opacity = (fade.toFloat() * 256.0f / 10.0f).toInt()
        if (opacity > 255) {
            opacity = 255
        }

        RenderSystem.enableBlend()
        RenderSystem.defaultBlendFunc()
        context.drawTextWithShadow(
            SongPlayer.MC.textRenderer,
            bottomText,
            bottomTextX,
            bottomTextY,
            16777215 + (opacity shl 24)
        )
        context.drawTextWithShadow(SongPlayer.MC.textRenderer, topText, topTextX, topTextY, 16777215 + (opacity shl 24))
        RenderSystem.disableBlend()
    }

    fun onTick() {
        if (fade > 0) fade--
    }
}