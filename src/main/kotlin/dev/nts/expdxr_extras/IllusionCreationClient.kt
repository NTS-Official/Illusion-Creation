package dev.nts.expdxr_extras

import dev.nts.expdxr_extras.misc.block.client.SkyBlockClientSetup
import dev.nts.expdxr_extras.misc.outline.OutlineConfig
import net.neoforged.api.distmarker.Dist
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.client.gui.ConfigurationScreen
import net.neoforged.neoforge.client.gui.IConfigScreenFactory

@Mod(value = IllusionCreation.MODID, dist = [Dist.CLIENT])
class IllusionCreationClient(container: ModContainer) {
    init {
        // 轮廓配置只在客户端有意义（方块选中轮廓是纯客户端渲染），所以在客户端入口注册。
        container.registerConfig(ModConfig.Type.CLIENT, OutlineConfig.SPEC)
        container.registerExtensionPoint(
            IConfigScreenFactory::class.java,
            IConfigScreenFactory { modContainer, parent -> ConfigurationScreen(modContainer, parent) }
        )
        container.eventBus?.let { SkyBlockClientSetup.register(it) }
    }
}
