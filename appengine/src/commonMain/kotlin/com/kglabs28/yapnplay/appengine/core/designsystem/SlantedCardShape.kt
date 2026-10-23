package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Outline
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.unit.Density
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.LayoutDirection

/** Which side of a [SlantedCardShape] leans. */
enum class SlantedSide { START, END }

/**
 * A card with one side leaning (the home cards, design): the [side] edge
 * runs from [slant] in at the top to straight at the bottom ([SlantedSide.END])
 * or straight at the top to [slant] in at the bottom ([SlantedSide.START]),
 * so two cards side by side share one diagonal gap. Corners are rounded by
 * [cornerRadius].
 */
class SlantedCardShape(
    private val cornerRadius: Dp,
    private val slant: Dp,
    private val side: SlantedSide,
) : Shape {
    override fun createOutline(size: Size, layoutDirection: LayoutDirection, density: Density): Outline {
        val s = with(density) { slant.toPx() }
        val r = with(density) { cornerRadius.toPx() }
        val w = size.width
        val h = size.height
        val corners = when (side) {
            SlantedSide.END -> listOf(Offset(0f, 0f), Offset(w - s, 0f), Offset(w, h), Offset(0f, h))
            SlantedSide.START -> listOf(Offset(0f, 0f), Offset(w, 0f), Offset(w, h), Offset(s, h))
        }
        return Outline.Generic(roundedPolygon(corners, r))
    }

    /** The polygon through [corners], each corner cut by a curve [radius] long on either side. */
    private fun roundedPolygon(corners: List<Offset>, radius: Float): Path = Path().apply {
        corners.forEachIndexed { i, corner ->
            val before = corners[(i + corners.size - 1) % corners.size]
            val after = corners[(i + 1) % corners.size]
            val start = corner + (before - corner).toward(radius)
            val end = corner + (after - corner).toward(radius)
            if (i == 0) moveTo(start.x, start.y) else lineTo(start.x, start.y)
            quadraticTo(corner.x, corner.y, end.x, end.y)
        }
        close()
    }

    /** This direction, [length] long (or the whole way when shorter than twice that). */
    private fun Offset.toward(length: Float): Offset {
        val full = getDistance()
        if (full == 0f) return Offset.Zero
        val reach = minOf(length, full / 2)
        return this * (reach / full)
    }
}
