package dev.nts.expdxr_extras.misc.block.client

import com.mojang.blaze3d.vertex.DefaultVertexFormat
import com.mojang.renderpearl.api.pipeline.ColorTargetState
import com.mojang.renderpearl.api.pipeline.DepthStencilState
import com.mojang.renderpearl.api.pipeline.PrimitiveTopology
import com.mojang.renderpearl.api.pipeline.RenderPipeline
import dev.nts.expdxr_extras.IllusionCreation
import dev.nts.expdxr_extras.IllusionCreation.Companion.MODID
import net.minecraft.client.renderer.BindGroupLayouts
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.client.renderer.rendertype.RenderSetup
import net.minecraft.client.renderer.rendertype.RenderType
import net.minecraft.resources.Identifier

object SkyBlockPipeline {
    val SHADER_ID: Identifier = Identifier.fromNamespaceAndPath(MODID, "core/sky_block")

    private val PIPELINE_ID: Identifier = Identifier.fromNamespaceAndPath(MODID, "pipeline/sky_block")

    val PIPELINE: RenderPipeline = RenderPipeline.builder(RenderPipelines.GLOBALS_SNIPPET)
        .withLocation(PIPELINE_ID)
        .withVertexShader(SHADER_ID)
        .withFragmentShader(SHADER_ID)
        .withBindGroupLayout(BindGroupLayouts.PROJECTION)
        .withBindGroupLayout(BindGroupLayouts.DYNAMIC_TRANSFORMS)
        .withBindGroupLayout(BindGroupLayouts.SAMPLER0)
        .withVertexBinding(0, DefaultVertexFormat.POSITION_TEX)
        .withPrimitiveTopology(PrimitiveTopology.QUADS)
        .withColorTargetState(ColorTargetState.DEFAULT)
        .withDepthStencilState(DepthStencilState.DEFAULT)
        .withCull(false)
        .build()

    fun isShaderPresent(): Boolean = shaderPresent

    private val shaderPresent: Boolean by lazy {
        val path = "assets/$MODID/shaders/${SHADER_ID.path}.vsh"
        val present = SkyBlockPipeline::class.java.classLoader?.getResource(path) != null
        if (!present) IllusionCreation.LOGGER.warn("[$MODID] Could not find $path. This path is optional, skipping it.")
        present
    }

    private var blockRenderType: RenderType? = null

    fun renderType(): RenderType = blockRenderType ?: RenderType.create("dxr_mc_extras:sky_block", RenderSetup.builder(PIPELINE).withTexture("Sampler0", SkyBlockCapture.TEXTURE_ID).createRenderSetup()).also { blockRenderType = it }
}
