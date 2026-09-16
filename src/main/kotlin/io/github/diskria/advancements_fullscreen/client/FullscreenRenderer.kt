package io.github.diskria.advancements_fullscreen.client

import com.mojang.blaze3d.platform.NativeImage
import com.mojang.renderpearl.api.pipeline.RenderPipeline
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen
import net.minecraft.client.renderer.texture.SpriteContents
import net.minecraft.client.renderer.texture.TextureAtlasSprite
import net.minecraft.client.resources.metadata.animation.FrameSize
import net.minecraft.client.resources.metadata.gui.GuiSpriteScaling

object FullscreenRenderer {

    private val sprite: TextureAtlasSprite by lazy {
        TextureAtlasSprite(
            AdvancementsScreen.WINDOW_LOCATION,
            SpriteContents(
                AdvancementsScreen.WINDOW_LOCATION,
                FrameSize(
                    AdvancementsScreen.WINDOW_WIDTH,
                    AdvancementsScreen.WINDOW_HEIGHT,
                ),
                NativeImage(
                    AdvancementsScreen.BACKGROUND_TEXTURE_WIDTH,
                    AdvancementsScreen.BACKGROUND_TEXTURE_HEIGHT,
                    false,
                )
            ),
            AdvancementsScreen.BACKGROUND_TEXTURE_WIDTH,
            AdvancementsScreen.BACKGROUND_TEXTURE_HEIGHT,
            0, 0,
            0,
        )
    }

    private val nineSlice: GuiSpriteScaling.NineSlice by lazy {
        val shadowDepth = 6
        GuiSpriteScaling.NineSlice(
            AdvancementsScreen.WINDOW_WIDTH, AdvancementsScreen.WINDOW_HEIGHT,
            GuiSpriteScaling.NineSlice.Border(
                AdvancementsScreen.WINDOW_INSIDE_X + shadowDepth,
                AdvancementsScreen.WINDOW_INSIDE_Y + shadowDepth,
                AdvancementsScreen.WINDOW_INSIDE_X + shadowDepth,
                AdvancementsScreen.WINDOW_INSIDE_X + shadowDepth,
            ),
            true,
        )
    }

    fun render(
        graphics: GuiGraphicsExtractor,
        pipeline: RenderPipeline,
        x: Int, y: Int,
        width: Int, height: Int,
    ) {
        graphics.blitNineSlicedSprite(pipeline, sprite, nineSlice, x, y, width, height, -1)
    }
}
