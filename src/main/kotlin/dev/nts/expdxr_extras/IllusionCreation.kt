package dev.nts.expdxr_extras

import com.mojang.logging.LogUtils
import dev.nts.expdxr_extras.misc.block.SkyBlocks
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.common.Mod

@Mod(IllusionCreation.MODID)
class IllusionCreation(modEventBus: IEventBus) {
    companion object {
        const val MODID = "dxr_mc_extras"
        const val MOD_NAME = "Illusion: Creation"
        const val CLASSPATH = "dev.nts.expdxr_extras"

        @JvmField
        val LOGGER = LogUtils.getLogger()
    }

    init {
        // 天空方块：方块 / 物品 / 方块实体都要在注册表冻结之前挂上。
        SkyBlocks.register(modEventBus)
        LOGGER.info("[$MODID] $MOD_NAME is constructing...")
    }
}
