package io.github.diskria.advancements_fullscreen.client

import com.llamalad7.mixinextras.injector.ModifyExpressionValue
import com.llamalad7.mixinextras.injector.wrapoperation.Operation
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
import com.llamalad7.mixinextras.sugar.Local
import com.mojang.renderpearl.api.pipeline.RenderPipeline
import io.github.diskria.lapis.annotations.Env
import io.github.diskria.lapis.annotations.KMixin
import io.github.diskria.lapis.annotations.Origin
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.advancements.AdvancementTabType
import net.minecraft.client.gui.screens.advancements.AdvancementTabType.Sprites
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen
import net.minecraft.resources.Identifier
import org.spongepowered.asm.mixin.injection.At

@KMixin(AdvancementTabType::class, Env.Client)
abstract class AdvancementTabTypeMixin(@Origin val type: AdvancementTabType) {

    private val advancementsScreen: AdvancementsScreen?
        get() = Minecraft.getInstance().gui.screen() as? AdvancementsScreen

    @ModifyExpressionValue(
        method = ["getX(I)I"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.WINDOW_WIDTH - 4}"])]
    )
    fun overrideRightX(original: Int): Int =
        advancementsScreen?.let { it.fullscreenWindowWidth - it.horizontalTabOffset } ?: original

    @ModifyExpressionValue(
        method = ["getY(I)I"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.WINDOW_HEIGHT - 4}"])]
    )
    fun overrideBelowY(original: Int): Int =
        advancementsScreen?.let { it.fullscreenWindowHeight - it.verticalTabOffset } ?: original

    @WrapOperation(
        method = ["extractRenderState(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIZI)V"],
        at = [At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;" +
                "blitSprite(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIII)V",
        )]
    )
    fun fixSpriteAlignment(
        instance: GuiGraphicsExtractor,
        renderPipeline: RenderPipeline, sprite: Identifier, x: Int, y: Int, width: Int, height: Int,
        original: Operation<Void>,
        @Local(name = ["sprites"]) sprites: Sprites
    ) {
        val sprite = advancementsScreen?.let {
            val isVertical = type == AdvancementTabType.ABOVE || type == AdvancementTabType.BELOW
            val tabPosition = if (isVertical) x else y
            val tabSize = if (isVertical) width else height
            val screenMargin = if (isVertical) it.fullscreenHorizontalMargin else it.fullscreenVerticalMargin
            val screenSize = if (isVertical) it.width else it.height
            when {
                tabPosition == screenMargin -> sprites.first()
                tabPosition + tabSize == screenSize - screenMargin -> sprites.last()
                else -> sprites.middle()
            }
        } ?: sprite
        original.call(instance, renderPipeline, sprite, x, y, width, height)
    }
}
