package io.github.diskria.advancements_fullscreen.client

import com.llamalad7.mixinextras.injector.ModifyExpressionValue
import io.github.diskria.lapis.annotations.KMixin
import io.github.diskria.lapis.annotations.KShadow
import io.github.diskria.lapis.annotations.Side
import net.minecraft.client.gui.screens.advancements.AdvancementTab
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen
import org.spongepowered.asm.mixin.injection.At
import javax.lang.model.element.Modifier.PRIVATE
import javax.lang.model.element.Modifier.PUBLIC

@KMixin(AdvancementTab::class, Side.Client)
abstract class AdvancementTabMixin {

    private val advancementsScreen: AdvancementsScreen get() = getScreen()

    @ModifyExpressionValue(
        method = ["scroll(DD)V"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.WINDOW_INSIDE_WIDTH}"])]
    )
    fun overrideScrollXLimit(original: Int): Int = advancementsScreen.fullscreenBackgroundWidth

    @ModifyExpressionValue(
        method = ["scroll(DD)V"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.WINDOW_INSIDE_HEIGHT}"])]
    )
    fun overrideScrollYLimit(original: Int): Int = advancementsScreen.fullscreenBackgroundHeight

    @ModifyExpressionValue(
        method = ["tick(II)V"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.WINDOW_INSIDE_WIDTH}"])]
    )
    fun overrideTickXLimit(original: Int): Int = advancementsScreen.fullscreenBackgroundWidth

    @ModifyExpressionValue(
        method = ["tick(II)V"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.WINDOW_INSIDE_HEIGHT}"])]
    )
    fun overrideTickYLimit(original: Int): Int = advancementsScreen.fullscreenBackgroundHeight

    @ModifyExpressionValue(method = ["tick(II)V"], at = [At(value = "CONSTANT", args = ["floatValue=0.06"])])
    fun overrideFadeInSpeed(original: Float): Float {
        val target = 0.3f
        val distance = target - fade
        return (distance * 0.25f).coerceAtLeast(0.01f)
    }

    @ModifyExpressionValue(method = ["tick(II)V"], at = [At(value = "CONSTANT", args = ["floatValue=0.12"])])
    fun overrideFadeOutSpeed(original: Float): Float {
        val target = 0.0f
        val distance = fade - target
        return (distance * 0.1f).coerceAtLeast(0.002f)
    }

    @ModifyExpressionValue(
        method = ["canScrollHorizontally()Z"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.WINDOW_INSIDE_WIDTH}"])]
    )
    fun overrideScrollXLimitCheck(original: Int): Int = advancementsScreen.fullscreenBackgroundWidth

    @ModifyExpressionValue(
        method = ["canScrollVertically()Z"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.WINDOW_INSIDE_HEIGHT}"])]
    )
    fun overrideScrollYLimitCheck(original: Int): Int = advancementsScreen.fullscreenBackgroundHeight

    @ModifyExpressionValue(
        method = ["extractTooltips(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.WINDOW_INSIDE_WIDTH}"])]
    )
    fun overrideHoverOverlayWidth(original: Int): Int = advancementsScreen.fullscreenBackgroundWidth

    @ModifyExpressionValue(
        method = ["extractTooltips(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.WINDOW_INSIDE_HEIGHT}"])]
    )
    fun overrideHoverOverlayHeight(original: Int): Int = advancementsScreen.fullscreenBackgroundHeight

    @ModifyExpressionValue(
        method = ["extractContents(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.WINDOW_INSIDE_WIDTH}"])]
    )
    fun overrideBackgroundWidth(original: Int): Int = advancementsScreen.fullscreenBackgroundWidth

    @ModifyExpressionValue(
        method = ["extractContents(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.WINDOW_INSIDE_HEIGHT}"])]
    )
    fun overrideBackgroundHeight(original: Int): Int = advancementsScreen.fullscreenBackgroundHeight

    @ModifyExpressionValue(
        method = ["extractContents(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.WINDOW_INSIDE_WIDTH / 2}"])]
    )
    fun overrideBackgroundX(original: Int): Int = advancementsScreen.fullscreenBackgroundWidth / 2

    @ModifyExpressionValue(
        method = ["extractContents(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.WINDOW_INSIDE_HEIGHT / 2}"])]
    )
    fun overrideBackgroundY(original: Int): Int = advancementsScreen.fullscreenBackgroundHeight / 2

    @ModifyExpressionValue(
        method = ["extractContents(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.BACKGROUND_TILE_COUNT_X + 1}"])]
    )
    fun overrideBackgroundColumns(original: Int): Int = advancementsScreen.fullscreenBackgroundWidth / 16 + 1

    @ModifyExpressionValue(
        method = ["extractContents(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.BACKGROUND_TILE_COUNT_Y + 1}"])]
    )
    fun overrideBackgroundRows(original: Int): Int = advancementsScreen.fullscreenBackgroundHeight / 16 + 1

    @KShadow(PRIVATE)
    abstract val fade: Float

    @KShadow(PUBLIC)
    abstract fun getScreen(): AdvancementsScreen
}
