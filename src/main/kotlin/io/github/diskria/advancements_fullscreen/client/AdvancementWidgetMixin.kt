package io.github.diskria.advancements_fullscreen.client

import com.llamalad7.mixinextras.sugar.Local
import io.github.diskria.lapis.annotations.Env
import io.github.diskria.lapis.annotations.KMixin
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.screens.advancements.AdvancementWidget
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.ModifyVariable

@KMixin(AdvancementWidget::class, Env.Client)
abstract class AdvancementWidgetMixin {

    private val advancementsScreen: AdvancementsScreen?
        get() = Minecraft.getInstance().gui.screen() as? AdvancementsScreen

    @ModifyVariable(
        method = ["extractHover(Lnet/minecraft/client/gui/GuiGraphicsExtractor;IIFIII)V"],
        name = ["topSide"],
        at = [At(value = "STORE")]
    )
    fun fixHoverOutOfScreen(
        original: Boolean,
        @Local(name = ["titleTop"]) titleTop: Int,
        @Local(name = ["titleBarBottom"]) titleBarBottom: Int,
        @Local(name = ["descriptionTextHeight"]) descriptionTextHeight: Int,
        @Local(name = ["descriptionHeight"]) descriptionHeight: Int,
    ): Boolean {
        val screen = advancementsScreen ?: return original
        val hoverBottom = titleBarBottom + descriptionHeight
        val hoverTop = titleTop - descriptionTextHeight + 1
        val backgroundTop = descriptionHeight - descriptionTextHeight
        val windowBottom = screen.fullscreenBackgroundHeight +
            AdvancementsScreen.WINDOW_INSIDE_Y +
            screen.fullscreenVerticalMargin
        val windowTop = -(AdvancementsScreen.WINDOW_INSIDE_X + screen.fullscreenVerticalMargin)
        return when {
            hoverBottom < screen.fullscreenBackgroundHeight -> false
            hoverTop >= backgroundTop -> true
            hoverBottom <= windowBottom -> false
            hoverTop >= windowTop -> true
            else -> Minecraft.getInstance().hasAltDown()
        }
    }
}
