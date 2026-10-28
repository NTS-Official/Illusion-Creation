package dev.nts.expdxr_extras.misc.block

import net.minecraft.core.BlockPos
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState

class SkyBlockEntity(pos: BlockPos, state: BlockState) :
    BlockEntity(SkyBlocks.SKY_BLOCK_ENTITY.get(), pos, state)
