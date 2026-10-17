package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.board

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoPieceUi

/**
 * One glass marble (Royal Ludo design): a bright caustic of coloured
 * light on the board under it, a soft outer glow, then a see-through
 * body — clear in the middle, rich colour toward the edge (fresnel) —
 * with light gathering inside its lower half, a sharp highlight top-left
 * and a thin lit rim. [ground] is where it stands, [lift] raises it
 * (hops). A [selectable] piece gets a ring under it pulsing with [pulse].
 */
internal fun DrawScope.drawGlassPiece(
    ground: Offset,
    radius: Float,
    lift: Float,
    tint: Color,
    selectable: Boolean,
    pulse: Float,
) {
    val light = lerp(tint, Color.White, LIGHT_MIX)
    val deep = lerp(tint, Color.Black, DEEP_MIX)
    val poolW = radius * POOL_WIDTH * (1f - lift / (radius * LIFT_FADE)).coerceIn(POOL_MIN, 1f)
    val poolH = poolW * POOL_FLATTEN
    drawOval(
        brush = Brush.radialGradient(
            0f to light.copy(alpha = CAUSTIC_ALPHA),
            CAUSTIC_STOP to tint.copy(alpha = POOL_ALPHA),
            1f to Color.Transparent,
            center = ground,
            radius = poolW,
        ),
        topLeft = Offset(ground.x - poolW, ground.y - poolH),
        size = Size(poolW * 2, poolH * 2),
    )
    if (selectable) {
        val ringW = radius * (RING_WIDTH + RING_GROW * pulse)
        val ringH = ringW * POOL_FLATTEN
        drawOval(
            color = Color.White.copy(alpha = RING_ALPHA_BASE + RING_ALPHA_PULSE * pulse),
            topLeft = Offset(ground.x - ringW, ground.y - ringH),
            size = Size(ringW * 2, ringH * 2),
            style = Stroke(radius * RING_STROKE),
        )
    }
    val centre = Offset(ground.x, ground.y - radius * STAND - lift)
    drawCircle(
        brush = Brush.radialGradient(listOf(tint.copy(alpha = GLOW_ALPHA), Color.Transparent), center = centre, radius = radius * GLOW_RADIUS),
        radius = radius * GLOW_RADIUS,
        center = centre,
    )
    // Body: clear centre, dense coloured edge.
    drawCircle(
        brush = Brush.radialGradient(
            0f to tint.copy(alpha = CORE_ALPHA),
            EDGE_START to tint.copy(alpha = MID_ALPHA),
            EDGE_PEAK to light.copy(alpha = EDGE_ALPHA),
            1f to deep,
            center = centre,
            radius = radius,
        ),
        radius = radius,
        center = centre,
    )
    // Light gathered in the lower half (refraction).
    drawOval(
        brush = Brush.radialGradient(
            listOf(light.copy(alpha = INNER_ALPHA), Color.Transparent),
            center = centre + Offset(0f, radius * INNER_Y),
            radius = radius * INNER_RADIUS,
        ),
        topLeft = Offset(centre.x - radius * INNER_RADIUS, centre.y + radius * (INNER_Y - INNER_RADIUS * INNER_FLATTEN)),
        size = Size(radius * INNER_RADIUS * 2, radius * INNER_RADIUS * 2 * INNER_FLATTEN),
    )
    // Soft sheen, then the sharp highlight.
    drawOval(
        color = Color.White.copy(alpha = SHEEN_ALPHA),
        topLeft = Offset(centre.x - radius * SHEEN_X, centre.y - radius * SHEEN_Y),
        size = Size(radius * SHEEN_W, radius * SHEEN_H),
    )
    drawCircle(Color.White.copy(alpha = SPARK_ALPHA), radius * SPARK_RADIUS, centre + Offset(-radius * SPARK_X, -radius * SPARK_Y))
    drawCircle(light.copy(alpha = RIM_ALPHA), radius, centre, style = Stroke(radius * RIM_STROKE))
}

/** Every piece, back to front, where its motion currently is. */
internal fun DrawScope.drawLudoPieces(
    projection: LudoBoardProjection,
    pieces: List<LudoPieceUi>,
    motions: Map<String, PieceMotionState>,
    scales: Map<String, Float>,
    colours: LudoBoardColours,
    pulse: Float,
) {
    LudoPieceLayout.drawOrder(pieces, { motions.getValue(it).current }, projection).forEach { piece ->
        val motion = motions.getValue(piece.id)
        val cell = projection.cellSize(motion.current)
        drawGlassPiece(
            ground = projection.project(motion.current),
            radius = cell * PIECE_RADIUS * (scales[piece.id] ?: 1f),
            lift = motion.lift * cell * LIFT_SCALE,
            tint = colours.players[piece.colour.ordinal],
            selectable = piece.selectable,
            pulse = pulse,
        )
    }
}

private const val PIECE_RADIUS = 0.34f
private const val LIFT_SCALE = 0.8f
private const val LIGHT_MIX = 0.55f
private const val DEEP_MIX = 0.45f
private const val POOL_WIDTH = 1.45f
private const val POOL_FLATTEN = 0.42f
private const val POOL_MIN = 0.55f
private const val LIFT_FADE = 3f
private const val CAUSTIC_ALPHA = 0.85f
private const val CAUSTIC_STOP = 0.3f
private const val POOL_ALPHA = 0.45f
private const val RING_WIDTH = 1.15f
private const val RING_GROW = 0.25f
private const val RING_STROKE = 0.12f
private const val RING_ALPHA_BASE = 0.35f
private const val RING_ALPHA_PULSE = 0.55f
private const val STAND = 0.95f
private const val GLOW_ALPHA = 0.5f
private const val GLOW_RADIUS = 1.8f
private const val CORE_ALPHA = 0.28f
private const val MID_ALPHA = 0.5f
private const val EDGE_ALPHA = 0.95f
private const val EDGE_START = 0.45f
private const val EDGE_PEAK = 0.86f
private const val INNER_ALPHA = 0.75f
private const val INNER_Y = 0.42f
private const val INNER_RADIUS = 0.62f
private const val INNER_FLATTEN = 0.55f
private const val SHEEN_ALPHA = 0.22f
private const val SHEEN_X = 0.62f
private const val SHEEN_Y = 0.78f
private const val SHEEN_W = 1.0f
private const val SHEEN_H = 0.6f
private const val SPARK_ALPHA = 0.95f
private const val SPARK_RADIUS = 0.15f
private const val SPARK_X = 0.36f
private const val SPARK_Y = 0.48f
private const val RIM_ALPHA = 0.85f
private const val RIM_STROKE = 0.06f
