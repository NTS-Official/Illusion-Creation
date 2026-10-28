package dev.nts.expdxr_extras.misc.block.client

import java.util.function.Consumer
import net.minecraft.client.renderer.FaceInfo
import net.minecraft.core.Direction
import org.joml.Vector3f
import org.joml.Vector3fc

object SkyBlockGeometry {
    private val FROM = Vector3f(0.0f, 0.0f, 0.0f)
    private val TO = Vector3f(1.0f, 1.0f, 1.0f)

    val FACE_CORNERS: Map<Direction, List<Vector3fc>> = buildMap {
        for (direction in Direction.entries) {
            val faceInfo = FaceInfo.fromFacing(direction)
            put(direction, List(4) { index -> faceInfo.getVertexInfo(index).select(FROM, TO) })
        }
    }

    fun extents(output: Consumer<Vector3fc>) {
        for (x in 0..1) {
            for (y in 0..1) {
                for (z in 0..1) {
                    output.accept(Vector3f(x.toFloat(), y.toFloat(), z.toFloat()))
                }
            }
        }
    }
}
