package dev.nts.expdxr_extras.misc.block.client

import java.util.EnumSet
import net.minecraft.client.renderer.blockentity.state.BlockEntityRenderState
import net.minecraft.core.Direction

class SkyBlockRenderState : BlockEntityRenderState() {
    val facesToShow: MutableSet<Direction> = EnumSet.noneOf(Direction::class.java)
}
