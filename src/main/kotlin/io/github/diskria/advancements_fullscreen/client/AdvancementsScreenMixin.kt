package io.github.diskria.advancements_fullscreen.client

import com.llamalad7.mixinextras.injector.ModifyExpressionValue
import com.llamalad7.mixinextras.injector.wrapmethod.WrapMethod
import com.llamalad7.mixinextras.injector.wrapoperation.Operation
import com.llamalad7.mixinextras.injector.wrapoperation.WrapOperation
import com.mojang.renderpearl.api.pipeline.RenderPipeline
import io.github.diskria.lapis.annotations.*
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.client.Minecraft
import net.minecraft.client.gui.Font
import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.components.AbstractWidget
import net.minecraft.client.gui.layouts.HeaderAndFooterLayout
import net.minecraft.client.gui.layouts.LayoutElement
import net.minecraft.client.gui.screens.advancements.AdvancementTab
import net.minecraft.client.gui.screens.advancements.AdvancementTabType
import net.minecraft.client.gui.screens.advancements.AdvancementsScreen
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import org.spongepowered.asm.mixin.injection.At
import java.util.function.Consumer
import javax.lang.model.element.Modifier.FINAL
import javax.lang.model.element.Modifier.PRIVATE

@KMixin(AdvancementsScreen::class, Env.Client)
abstract class AdvancementsScreenMixin(@Origin private val screen: AdvancementsScreen) {

    @Extension
    val fullscreenHorizontalMargin: Int
        get() = horizontalTabWidth - horizontalTabOffset + SCREEN_MARGIN

    @Extension
    val fullscreenVerticalMargin: Int
        get() = verticalTabHeight - verticalTabOffset + SCREEN_MARGIN

    @Extension
    val fullscreenWindowWidth: Int
        get() = screen.width - fullscreenHorizontalMargin * 2

    @Extension
    val fullscreenWindowHeight: Int
        get() = screen.height - fullscreenVerticalMargin * 2

    @Extension
    val fullscreenBackgroundWidth: Int
        get() = fullscreenWindowWidth - (AdvancementsScreen.WINDOW_INSIDE_X * 2)

    @Extension
    val fullscreenBackgroundHeight: Int
        get() = fullscreenWindowHeight - (AdvancementsScreen.WINDOW_INSIDE_Y + AdvancementsScreen.WINDOW_INSIDE_X)

    @Extension
    val horizontalTabOffset: Int
        get() = AdvancementTabType.LEFT.getX(0) + horizontalTabWidth

    @Extension
    val verticalTabOffset: Int
        get() = AdvancementTabType.ABOVE.getY(0) + verticalTabHeight

    private val horizontalTabWidth: Int
        get() = AdvancementTabType.LEFT.width

    private val verticalTabHeight: Int
        get() = AdvancementTabType.ABOVE.height

    @WrapMethod(method = ["repositionElements()V"])
    fun calculateOnReposition(original: Operation<Void>) {
        original.call()
        tabs.values.forEach { it.centered = false }
    }

    @WrapOperation(
        method = ["mouseScrolled(DDDD)Z"],
        at = [At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/screens/advancements/AdvancementTab;scroll(DD)V"
        )]
    )
    fun invertScrollWhenShiftDown(
        instance: AdvancementTab,
        scrollX: Double, scrollY: Double,
        original: Operation<Void>,
    ) {
        if (Minecraft.getInstance().hasShiftDown()) {
            original.call(instance, scrollY, 0.toDouble())
        } else {
            original.call(instance, scrollX, scrollY)
        }
    }

    @WrapOperation(
        method = ["extractWindow(Lnet/minecraft/client/gui/GuiGraphicsExtractor;II)V"],
        at = [At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/GuiGraphicsExtractor;" +
                "blit(Lcom/mojang/renderpearl/api/pipeline/RenderPipeline;Lnet/minecraft/resources/Identifier;IIFFIIII)V",
        )]
    )
    fun overrideWindowBackgroundRender(
        instance: GuiGraphicsExtractor,
        renderPipeline: RenderPipeline,
        texture: Identifier,
        x: Int,
        y: Int,
        u: Float,
        v: Float,
        width: Int,
        height: Int,
        textureWidth: Int,
        textureHeight: Int,
        original: Operation<Void>,
    ) {
        FullscreenRenderer.render(instance, renderPipeline, x, y, fullscreenWindowWidth, fullscreenWindowHeight)
    }

    @WrapOperation(
        method = ["init()V"],
        at = [At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/layouts/HeaderAndFooterLayout;" +
                "addTitleHeader(Lnet/minecraft/network/chat/Component;Lnet/minecraft/client/gui/Font;)V",
        )]
    )
    fun hideTitleHeader(
        receiver: HeaderAndFooterLayout,
        component: Component, font: Font,
        original: Operation<Void>,
    ) {
    }

    @WrapOperation(
        method = ["init()V"],
        at = [At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/layouts/HeaderAndFooterLayout;" +
                "addToFooter(Lnet/minecraft/client/gui/layouts/LayoutElement;)" +
                "Lnet/minecraft/client/gui/layouts/LayoutElement;",
        )]
    )
    fun hideFooter(
        instance: HeaderAndFooterLayout,
        element: LayoutElement,
        original: Operation<LayoutElement>,
    ): LayoutElement? = null

    @WrapOperation(
        method = ["init()V"],
        at = [At(
            value = "INVOKE",
            target = "Lnet/minecraft/client/gui/layouts/HeaderAndFooterLayout;" +
                "visitWidgets(Ljava/util/function/Consumer;)V",
        )]
    )
    fun hideWidgets(
        instance: HeaderAndFooterLayout,
        consumer: Consumer<AbstractWidget>,
        original: Operation<Void>,
    ) {
    }

    @ModifyExpressionValue(
        method = ["repositionElements()V"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.WINDOW_WIDTH}"])]
    )
    fun overrideWindowX(original: Int): Int = fullscreenWindowWidth

    @ModifyExpressionValue(
        method = ["repositionElements()V"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.WINDOW_HEIGHT}"])]
    )
    fun overrideWindowY(original: Int): Int = fullscreenWindowHeight

    @ModifyExpressionValue(
        method = ["mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.WINDOW_WIDTH}"])]
    )
    fun overrideClickableAreaX(original: Int): Int = fullscreenWindowWidth

    @ModifyExpressionValue(
        method = ["mouseClicked(Lnet/minecraft/client/input/MouseButtonEvent;Z)Z"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.WINDOW_HEIGHT}"])]
    )
    fun overrideClickableAreaY(original: Int): Int = fullscreenWindowHeight

    @ModifyExpressionValue(
        method = ["extractInside(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.WINDOW_INSIDE_WIDTH}"])]
    )
    fun overrideEmptyBackgroundWidth(original: Int): Int = fullscreenBackgroundWidth

    @ModifyExpressionValue(
        method = ["extractInside(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.WINDOW_INSIDE_HEIGHT}"])]
    )
    fun overrideEmptyBackgroundHeight(original: Int): Int = fullscreenBackgroundHeight

    @ModifyExpressionValue(
        method = ["extractInside(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.WINDOW_INSIDE_WIDTH / 2}"])]
    )
    fun overrideEmptyLabelsX(original: Int): Int = fullscreenBackgroundWidth / 2

    @ModifyExpressionValue(
        method = ["extractInside(Lnet/minecraft/client/gui/GuiGraphicsExtractor;)V"],
        at = [At(value = "CONSTANT", args = ["intValue=${AdvancementsScreen.WINDOW_INSIDE_HEIGHT / 2}"])]
    )
    fun overrideNoAdvancementsLabelY(original: Int): Int = fullscreenBackgroundHeight / 2

    @KShadow(PRIVATE, FINAL)
    abstract val tabs: Map<AdvancementHolder, AdvancementTab>

    companion object {
        private const val SCREEN_MARGIN: Int = 4
    }
}
