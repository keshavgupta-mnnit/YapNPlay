package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.ClipOp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.addOutline
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.clipPath
import androidx.compose.ui.unit.Dp

/**
 * A soft glow of [color] around [shape], fading out over [spread] — drawn
 * only OUTSIDE the shape. The app's one way to make anything glow.
 *
 * Why not `Modifier.shadow`: an elevation shadow is painted under the whole
 * shape, so through a see-through glass fill it shows as a dark box inside
 * the card or pill. This glow never reaches inside the shape, so a glass
 * surface of any opacity stays clean. Never use `Modifier.shadow` for glows.
 */
fun Modifier.edgeGlow(color: Color, shape: Shape, spread: Dp, strength: Float = DEFAULT_STRENGTH): Modifier = drawBehind {
    val spreadPx = spread.toPx()
    if (spreadPx <= 0f || color.alpha == 0f) return@drawBehind
    val path = Path().apply { addOutline(shape.createOutline(size, layoutDirection, this@drawBehind)) }
    clipPath(path, ClipOp.Difference) {
        // Strokes centred on the edge, widest first: they pile up into a glow that is strongest at the edge.
        for (layer in 1..LAYERS) {
            val reach = layer.toFloat() / LAYERS
            drawPath(
                path = path,
                color = color.copy(alpha = color.alpha * strength * (1f - reach) * 2f / LAYERS),
                style = Stroke(width = spreadPx * 2f * reach),
            )
        }
    }
}

private const val LAYERS = 8
/** How bright the glow is right at the edge (share of [color]'s alpha). */
private const val DEFAULT_STRENGTH = 0.6f
