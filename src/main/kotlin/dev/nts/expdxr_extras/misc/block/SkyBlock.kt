package dev.nts.expdxr_extras.misc.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.BaseEntityBlock
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockBehaviour
import net.minecraft.world.level.block.state.BlockState

class SkyBlock(properties: BlockBehaviour.Properties) : BaseEntityBlock(properties) {
    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.INVISIBLE

    override fun newBlockEntity(pos: BlockPos, state: BlockState): BlockEntity = SkyBlockEntity(pos, state)
}
