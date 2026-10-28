package dev.nts.expdxr_extras.misc.block.client

import com.mojang.blaze3d.pipeline.TextureTarget
import com.mojang.blaze3d.systems.RenderSystem
import com.mojang.renderpearl.api.GpuFormat
import com.mojang.renderpearl.api.textures.FilterMode
import com.mojang.renderpearl.api.textures.GpuSampler
import com.mojang.renderpearl.api.textures.GpuTexture
import com.mojang.renderpearl.api.textures.GpuTextureView
import dev.nts.expdxr_extras.IllusionCreation.Companion.MODID
import java.util.Optional
import java.util.OptionalDouble
import net.minecraft.client.CloudStatus
import net.minecraft.client.Minecraft
import net.minecraft.client.renderer.state.level.LevelRenderState
import net.minecraft.client.renderer.texture.AbstractTexture
import net.minecraft.resources.Identifier
import net.minecraft.util.ARGB
import org.joml.Vector4f

object SkyBlockCapture {
    val TEXTURE_ID = Identifier.fromNamespaceAndPath(MODID, "sky_block_capture")

    private var target: TextureTarget? = null
    private var installed = false

    private var requested = false

    private var guiPhase = false

    fun beginWorldPhase() {
        guiPhase = false
    }

    fun beginGuiPhase() {
        guiPhase = true
    }

    fun projectedScaleX(): Float = 1.0f / projectedSize(Minecraft.getInstance().window.width, captureWidth())

    fun projectedScaleY(): Float = 1.0f / projectedSize(Minecraft.getInstance().window.height, captureHeight())

    private fun projectedSize(windowSize: Int, captureSize: Int): Int {
        val size = if (guiPhase && windowSize > 0) windowSize else captureSize
        return size.coerceAtLeast(1)
    }

    fun maintain() {
        val minecraft = Minecraft.getInstance()
        if (minecraft.level == null) {
            return
        }
        val source = minecraft.gameRenderer.mainRenderTarget().colorTexture ?: return
        val width = source.getWidth(0)
        val height = source.getHeight(0)
        if (width <= 0 || height <= 0) {
            return
        }
        val colorFormat = source.format
        val current = target
        current?.destroyBuffers()
        val created = TextureTarget("dxr_mc_extras sky block", width, height, colorFormat, GpuFormat.D32_FLOAT)
        target = created
        val colorTexture = created.colorTexture
        if (colorTexture != null) {
            // 首帧拷贝之前先清成不透明黑，避免采样到未初始化显存。
            RenderSystem.getDevice().createCommandEncoder()
                .clearColorTexture(colorTexture, Vector4f(0.0f, 0.0f, 0.0f, 1.0f))
        }
        installTexture(minecraft)
    }

    fun isReady(): Boolean = target?.colorTextureView != null

    fun captureWidth(): Int = target?.width ?: 0

    fun captureHeight(): Int = target?.height ?: 0

    fun requestCapture() {
        requested = true
    }

    fun captureIfRequested(levelRenderState: LevelRenderState) {
        if (!requested)  return
        requested = false
        val created = target ?: return
        val source = Minecraft.getInstance().gameRenderer.mainRenderTarget().colorTexture ?: return
        val destination = created.colorTexture ?: return
        if (source.format != destination.format) return
        val width = minOf(source.getWidth(0), destination.getWidth(0))
        val height = minOf(source.getHeight(0), destination.getHeight(0))
        if (width <= 0 || height <= 0) return
        RenderSystem.getDevice().createCommandEncoder().copyTextureToTexture(
            source, destination, 0, 0, 0, 0, 0, width, height)
        renderClouds(created, levelRenderState)
    }

    internal fun colorTexture(): GpuTexture? = target?.colorTexture

    internal fun colorTextureView(): GpuTextureView? = target?.colorTextureView

    private fun renderClouds(created: TextureTarget, levelRenderState: LevelRenderState) {
        val minecraft = Minecraft.getInstance()
        val cloudStatus = minecraft.options.cloudStatus

        if (cloudStatus == CloudStatus.OFF || ARGB.alpha(levelRenderState.cloudColor) <= 0) return
        if (levelRenderState.customCloudsRenderer != null) return

        val colorView = created.colorTextureView ?: return
        val depthTexture = created.depthTexture ?: return
        val depthView = created.depthTextureView ?: return
        val encoder = RenderSystem.getDevice().createCommandEncoder()

        encoder.clearDepthTexture(depthTexture, 0.0)

        val pass = encoder.createRenderPass(
            { "dxr_mc_extras sky block clouds" },
            colorView,
            Optional.empty(),
            depthView,
            OptionalDouble.empty(),
        )
        pass.use { pass -> minecraft.levelRenderer.cloudRenderer().render(cloudStatus, pass) }
    }

    private fun installTexture(minecraft: Minecraft) {
        if (installed) return
        minecraft.textureManager.register(TEXTURE_ID, SkyBlockCaptureTexture())
        installed = true
    }
}

private class SkyBlockCaptureTexture : AbstractTexture() {
    private val clampSampler: GpuSampler = RenderSystem.getSamplerCache().getClampToEdge(FilterMode.LINEAR)

    override fun getTexture(): GpuTexture = SkyBlockCapture.colorTexture()
        ?: throw IllegalStateException("sky block capture texture is not initialised yet")

    override fun getTextureView(): GpuTextureView = SkyBlockCapture.colorTextureView()
        ?: throw IllegalStateException("sky block capture texture view is not initialised yet")

    override fun getSampler(): GpuSampler = clampSampler
}
