package dev.nts.expdxr_extras.mixin

import dev.nts.expdxr_extras.misc.outline.OutlineConfig
import net.minecraft.client.renderer.LevelRenderer
import org.spongepowered.asm.mixin.Mixin
import org.spongepowered.asm.mixin.injection.At
import org.spongepowered.asm.mixin.injection.ModifyArg

/**
 * `LevelRenderer.submitHitOutline` 的签名；颜色与线宽都是它的普通参数。
 *
 * 26.3 把线宽做成逐顶点属性（`ShapeOutlineFeatureRenderer` 里写 `setLineWidth`），不再有
 * 1.19 时代的 `RenderPhase.LineWidth` / `RenderSystem.lineWidth`，所以这里只改参数，
 * 不需要自建 RenderLayer、BufferBuilder 或 accesswidener。
 */
private const val SUBMIT_HIT_OUTLINE =
    "Lnet/minecraft/client/renderer/LevelRenderer;submitHitOutline(" +
        "Lcom/mojang/blaze3d/vertex/PoseStack;" +
        "Lnet/minecraft/client/renderer/SubmitNodeCollector;" +
        "Lnet/minecraft/client/renderer/rendertype/RenderType;" +
        "Lnet/minecraft/client/renderer/state/level/BlockOutlineRenderState;IFZ)V"

/** `submitHitOutline(PoseStack, SubmitNodeCollector, RenderType, BlockOutlineRenderState, int color, float width, boolean)` 的参数下标，不含接收者。 */
private const val COLOR_ARG = 4
private const val WIDTH_ARG = 5

/**
 * 把方块选中轮廓换成本模组配置的颜色与线宽。
 *
 * `submitBlockOutline` 里 `submitHitOutline` 被调用两次：ordinal 0 是高对比度设置下额外交给
 * `secondaryBlockOutline` 的那圈黑色外描边，保持原样；ordinal 1 才是正常轮廓。
 *
 * Kotlin 标识符不能带 `$`，所以注入处理器用 `dxrMcExtras` 前缀代替工作区约定的 `dxr_mc$`。
 */
@Mixin(LevelRenderer::class)
abstract class LevelRendererOutlineMixin {
    @ModifyArg(
        method = ["submitBlockOutline"],
        at = [At(value = "INVOKE", target = SUBMIT_HIT_OUTLINE, ordinal = 1)],
        index = COLOR_ARG
    )
    private fun dxrMcExtrasOutlineColor(color: Int): Int {
        val settings = OutlineConfig.settings ?: return color
        return if (settings.enabled) settings.argb else color
    }

    @ModifyArg(
        method = ["submitBlockOutline"],
        at = [At(value = "INVOKE", target = SUBMIT_HIT_OUTLINE, ordinal = 1)],
        index = WIDTH_ARG
    )
    private fun dxrMcExtrasOutlineWidth(width: Float): Float {
        val settings = OutlineConfig.settings ?: return width
        return if (settings.enabled) width * settings.width / OutlineConfig.VANILLA_WIDTH else width
    }
}
