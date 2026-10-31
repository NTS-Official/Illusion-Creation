package dev.nts.expdxr_extras.misc.outline

import net.neoforged.neoforge.common.ModConfigSpec

/**
 * 方块选中轮廓的配色与线宽，落在 `config/dxr_mc_extras-client.toml` 的 `[outline]` 表。
 *
 * 默认值刻意与原版一致（纯黑、alpha 102、宽度 5），所以只装模组不动配置时观感不变；
 * 不想要自定义轮廓时把 `enabled` 关掉即可。
 */
object OutlineConfig {
    private const val PREFIX = "dxr_mc_extras.configuration.outline"

    /** 宽度配置里与原版线宽等价的值；其它值按它的比例缩放。 */
    const val VANILLA_WIDTH = 5

    private val BUILDER = ModConfigSpec.Builder()

    private val ENABLED: ModConfigSpec.BooleanValue
    private val RED: ModConfigSpec.IntValue
    private val GREEN: ModConfigSpec.IntValue
    private val BLUE: ModConfigSpec.IntValue
    private val ALPHA: ModConfigSpec.IntValue
    private val WIDTH: ModConfigSpec.IntValue

    val SPEC: ModConfigSpec

    init {
        BUILDER.comment(
            "Colour and line width of the block selection outline.",
            "The defaults match vanilla, so nothing changes until a value is edited."
        ).translation(PREFIX).push("outline")

        ENABLED = BUILDER
            .comment("Apply the colour and width below to the block selection outline.")
            .translation("$PREFIX.enabled")
            .define("enabled", true)

        RED = BUILDER
            .comment("Red channel of the outline colour (0-255).")
            .translation("$PREFIX.red")
            .defineInRange("red", 0, 0, 255)

        GREEN = BUILDER
            .comment("Green channel of the outline colour (0-255).")
            .translation("$PREFIX.green")
            .defineInRange("green", 0, 0, 255)

        BLUE = BUILDER
            .comment("Blue channel of the outline colour (0-255).")
            .translation("$PREFIX.blue")
            .defineInRange("blue", 0, 0, 255)

        ALPHA = BUILDER
            .comment(
                "Opacity of the outline colour (0-255); 102 matches vanilla.",
                "255 routes the outline through the solid phase, where terrain depth-tests it",
                "and can hide it behind blocks; anything below 255 stays translucent and is",
                "drawn after terrain, so the outline stays visible through them."
            )
            .translation("$PREFIX.alpha")
            .defineInRange("alpha", 102, 0, 255)

        WIDTH = BUILDER
            .comment(
                "Line width in units where 5 is the vanilla outline thickness (1-20).",
                "The window's own outline width scales the result, so the outline keeps the",
                "same visual weight on a HiDPI display as vanilla does."
            )
            .translation("$PREFIX.width")
            .defineInRange("width", VANILLA_WIDTH, 1, 20)

        BUILDER.pop()
        SPEC = BUILDER.build()
    }

    /**
     * 当前生效的轮廓设置；配置还没加载时是 `null`。
     *
     * `ConfigValue.get()` 在配置加载前会抛 `IllegalStateException`，而轮廓每帧都会提交一次，
     * 不能把这个异常留给渲染线程，所以先问 [ModConfigSpec.isLoaded]。
     */
    val settings: OutlineSettings?
        get() = if (SPEC.isLoaded) {
            OutlineSettings(
                enabled = ENABLED.get(),
                red = RED.get(),
                green = GREEN.get(),
                blue = BLUE.get(),
                alpha = ALPHA.get(),
                width = WIDTH.get()
            )
        } else {
            null
        }
}

/** 一次轮廓提交用的颜色与线宽。 */
data class OutlineSettings(
    val enabled: Boolean,
    val red: Int,
    val green: Int,
    val blue: Int,
    val alpha: Int,
    val width: Int
) {
    /** 打包成 `submitShapeOutline` 用的 ARGB int。 */
    val argb: Int get() = (alpha shl 24) or (red shl 16) or (green shl 8) or blue
}
