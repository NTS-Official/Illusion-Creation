package dev.nts.expdxr_extras.misc.block

import dev.nts.expdxr_extras.IllusionCreation.Companion.MODID
import java.util.function.Supplier
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.CreativeModeTabs
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockBehaviour
import net.neoforged.bus.api.IEventBus
import net.neoforged.neoforge.event.BuildCreativeModeTabContentsEvent
import net.neoforged.neoforge.registries.DeferredRegister

object SkyBlocks {
    private val BLOCKS = DeferredRegister.createBlocks(MODID)
    private val ITEMS = DeferredRegister.createItems(MODID)
    private val BLOCK_ENTITIES = DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, MODID)

    private val SKY_BLOCK_PROPERTIES = Supplier { BlockBehaviour.Properties.ofFullCopy(Blocks.GLASS).noOcclusion() }

    val SKY_BLOCK = BLOCKS.registerBlock("sky_block", ::SkyBlock, SKY_BLOCK_PROPERTIES)

    val SKY_BLOCK_ITEM = ITEMS.registerSimpleBlockItem(SKY_BLOCK)

    val SKY_BLOCK_ENTITY = BLOCK_ENTITIES.register("sky_block",
        Supplier { BlockEntityType(::SkyBlockEntity, SKY_BLOCK.get()) })

   fun register(modEventBus: IEventBus) {
        BLOCKS.register(modEventBus)
        ITEMS.register(modEventBus)
        BLOCK_ENTITIES.register(modEventBus)
        modEventBus.addListener<BuildCreativeModeTabContentsEvent> { event ->
            if (event.tabKey == CreativeModeTabs.BUILDING_BLOCKS) event.accept(SKY_BLOCK_ITEM.get()) }
    }
}
