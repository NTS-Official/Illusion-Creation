package dev.nts.expdxr_extras.misc.block.client

import com.mojang.blaze3d.vertex.PoseStack
import com.mojang.serialization.MapCodec
import java.util.function.Consumer
import net.minecraft.client.renderer.SubmitNodeCollector
import net.minecraft.client.renderer.special.NoDataSpecialModelRenderer
import net.minecraft.client.renderer.special.SpecialModelRenderer
import net.minecraft.core.Direction
import org.joml.Vector3fc

object SkyBlockSpecialRenderer : NoDataSpecialModelRenderer {
    override fun submit(
        poseStack: PoseStack,
        submitNodeCollector: SubmitNodeCollector,
        lightCoords: Int,
        overlayCoords: Int,
        hasFoil: Boolean,
        outlineColor: Int
    ) {
        if (!SkyBlockPipeline.isShaderPresent()) {
            return
        }
        if (!SkyBlockCapture.isReady()) {
            return
        }
        // 只要手上/界面里有天空方块，下一帧就要有新的离屏天空（当前帧的拷贝发生在世界渲染
        // 的天空 pass 之后，晚于手持物品的提交，所以这里置位的是「下一帧」）。
        SkyBlockCapture.requestCapture()
        val scaleX = SkyBlockCapture.projectedScaleX()
        val scaleY = SkyBlockCapture.projectedScaleY()
        val renderType = SkyBlockPipeline.renderType()
        submitNodeCollector.submitCustomGeometry(poseStack, renderType) { pose, buffer ->
            for (direction in Direction.entries) {
                for (corner in SkyBlockGeometry.FACE_CORNERS.getValue(direction)) {
                    buffer.addVertex(pose, corner.x(), corner.y(), corner.z()).setUv(scaleX, scaleY)
                }
            }
        }
    }

    override fun getExtents(output: Consumer<Vector3fc>) {
        SkyBlockGeometry.extents(output)
    }

    object Unbaked : NoDataSpecialModelRenderer.Unbaked {
        @JvmField
        val MAP_CODEC: MapCodec<Unbaked> = MapCodec.unit(this)

        override fun type(): MapCodec<out NoDataSpecialModelRenderer.Unbaked> = MAP_CODEC

        override fun bake(context: SpecialModelRenderer.BakingContext): SpecialModelRenderer<Void> =
            SkyBlockSpecialRenderer
    }
}
