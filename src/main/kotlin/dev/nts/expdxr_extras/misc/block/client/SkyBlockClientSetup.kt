package dev.nts.expdxr_extras.misc.block.client

import dev.nts.expdxr_extras.IllusionCreation.Companion.MODID
import dev.nts.expdxr_extras.misc.block.SkyBlockEntity
import dev.nts.expdxr_extras.misc.block.SkyBlocks
import net.minecraft.resources.Identifier
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.client.event.ClientTickEvent
import net.neoforged.neoforge.client.event.EntityRenderersEvent
import net.neoforged.neoforge.client.event.RegisterRenderPipelinesEvent
import net.neoforged.neoforge.client.event.RegisterSpecialModelRendererEvent
import net.neoforged.neoforge.client.event.RenderFrameEvent
import net.neoforged.neoforge.client.event.RenderLevelStageEvent
import net.neoforged.neoforge.common.NeoForge

object SkyBlockClientSetup {
    private val SPECIAL_MODEL_ID: Identifier = Identifier.fromNamespaceAndPath(MODID, "sky_block")

    fun register(modEventBus: IEventBus) {
        modEventBus.addListener<RegisterRenderPipelinesEvent> { event -> event.registerOptionalPipeline(SkyBlockPipeline.PIPELINE) }
        modEventBus.addListener<EntityRenderersEvent.RegisterRenderers> { event ->
            event.registerBlockEntityRenderer<SkyBlockEntity, SkyBlockRenderState>(SkyBlocks.SKY_BLOCK_ENTITY.get()) { SkyBlockEntityRenderer() }
        }

        modEventBus.addListener<RegisterSpecialModelRendererEvent> { event ->
            event.register(SPECIAL_MODEL_ID, SkyBlockSpecialRenderer.Unbaked.MAP_CODEC)
        }
        NeoForge.EVENT_BUS.addListener<RenderLevelStageEvent.AfterSky> { event ->
            SkyBlockCapture.captureIfRequested(event.levelRenderState)
        }

        NeoForge.EVENT_BUS.addListener<RenderFrameEvent.Pre> { SkyBlockCapture.beginWorldPhase() }
        NeoForge.EVENT_BUS.addListener<RenderLevelStageEvent.AfterLevel> { SkyBlockCapture.beginGuiPhase() }
        NeoForge.EVENT_BUS.addListener<ClientTickEvent.Post> { SkyBlockCapture.maintain() }
    }
}
