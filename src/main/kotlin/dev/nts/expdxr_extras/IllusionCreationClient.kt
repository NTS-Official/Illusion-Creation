package dev.nts.expdxr_extras

import dev.nts.expdxr_extras.misc.block.client.SkyBlockClientSetup
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod

@Mod(value = IllusionCreation.MODID, dist = [Dist.CLIENT])
class IllusionCreationClient(container: ModContainer) {
    init {
        container.eventBus?.let { SkyBlockClientSetup.register(it) }
    }
}
