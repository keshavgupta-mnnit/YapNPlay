package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.board

import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeJoin
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.lerp
import com.kglabs28.yapnplay.appengine.games.royalludo.utils.LudoBoardGeometry
import kotlin.math.PI
import kotlin.math.cos
import kotlin.math.sin

/**
 * Draws the Royal Ludo board as in the design: coloured light spilling
 * from each base onto the floor, a dark rounded slab with a visible edge
 * and a soft lit rim, dark track tiles, frosted safe tiles with a star in
 * their arm's colour, each home lane as a glowing neon tube, each base
 * as a raised dark tray with a neon outline and glowing sockets, and the
 * dark centre plate the dice stands on.
 */
internal fun DrawScope.drawLudoBoard(p: LudoBoardProjection, colours: LudoBoardColours, glow: Float) {
    val cell = p.cellSize(CENTRE)
    drawSpill(p, colours, cell)
    drawSlab(p, colours, cell)
    drawTiles(p, colours, cell)
    for (colour in 0 until COLOURS) drawLane(p, colour, colours, cell, glow)
    for (colour in 0 until COLOURS) drawBase(p, colour, colours.players[colour], colours, cell, glow)
    drawCentre(p, colours, cell, glow)
}

private fun DrawScope.drawSpill(p: LudoBoardProjection, colours: LudoBoardColours, cell: Float) {
    for (colour in 0 until COLOURS) {
        val (bx, by) = LudoBoardGeometry.baseOrigin(colour)
        val centre = p.project(Offset(bx + BASE_HALF, by + BASE_HALF))
        val radius = cell * SPILL_RADIUS
        drawCircle(
            brush = Brush.radialGradient(listOf(colours.players[colour].copy(alpha = SPILL_ALPHA), Color.Transparent), centre, radius),
            radius = radius,
            center = centre,
        )
    }
}

private fun DrawScope.drawSlab(p: LudoBoardProjection, colours: LudoBoardColours, cell: Float) {
    val lo = -LudoBoardProjection.FRAME
    val hi = LudoBoardGeometry.GRID + LudoBoardProjection.FRAME
    val top = roundedRect(p, lo, lo, hi, hi, SLAB_CORNER)
    val side = Path().apply { addPath(top, Offset(0f, p.slabDepth)) }
    val bounds = top.getBounds()
    drawPath(side, Brush.verticalGradient(listOf(colours.bottom, colours.side), bounds.top, bounds.bottom + p.slabDepth))
    drawPath(top, Brush.verticalGradient(listOf(colours.top, colours.bottom), bounds.top, bounds.bottom))
    drawPath(top, colours.rim.copy(alpha = RIM_GLOW_ALPHA), style = Stroke(cell * RIM_GLOW_WIDTH, join = StrokeJoin.Round))
    drawPath(top, colours.rim.copy(alpha = RIM_ALPHA), style = Stroke(cell * RIM_WIDTH, join = StrokeJoin.Round))
}

/**
 * Every square as a raised block (track and home lanes), drawn from the
 * far side of the board to the near side so nearer blocks overlap the
 * sides of farther ones. Safe squares are frosted tiles with a star.
 */
private fun DrawScope.drawTiles(p: LudoBoardProjection, colours: LudoBoardColours, cell: Float) {
    val safe = LudoBoardGeometry.track.withIndex().filter { LudoBoardGeometry.isSafe(it.index) }.associate { it.value to it.index }
    val squares = LudoBoardGeometry.track + (0 until COLOURS).flatMap { LudoBoardGeometry.lane(it) }
    squares.sortedBy { (col, row) -> p.project(Offset(col + HALF, row + HALF)).y }.forEach { square ->
        val (col, row) = square
        val top = roundedRect(p, col + TILE_INSET, row + TILE_INSET, col + 1 - TILE_INSET, row + 1 - TILE_INSET, TILE_CORNER)
        val index = safe[square]
        if (index == null) {
            raised(top, cell * TILE_HEIGHT, colours.cellSide, Brush.verticalGradient(listOf(colours.cellEdge, colours.cell), top.getBounds().top, top.getBounds().bottom))
        } else {
            safeTile(p, top, col, row, colours.players[LudoBoardGeometry.ownerOf(index)], colours, cell)
        }
    }
}

/** A block: a soft shadow, its side (the top dropped by [height]), the top face and a lit upper edge. */
private fun DrawScope.raised(top: Path, height: Float, side: Color, face: Brush) {
    drawPath(Path().apply { addPath(top, Offset(height * SHADOW_SHIFT, height * SHADOW_DROP)) }, Color.Black.copy(alpha = BLOCK_SHADOW_ALPHA))
    drawPath(Path().apply { addPath(top, Offset(0f, height)) }, side)
    drawPath(top, face)
    val bounds = top.getBounds()
    drawPath(
        top,
        Brush.verticalGradient(listOf(Color.White.copy(alpha = EDGE_LIGHT_ALPHA), Color.Transparent), bounds.top, bounds.bottom),
        style = Stroke(height * EDGE_LIGHT_WIDTH),
    )
}

/** Frosted glass block with a glowing star in [tint] (start and star squares). */
private fun DrawScope.safeTile(p: LudoBoardProjection, top: Path, col: Int, row: Int, tint: Color, colours: LudoBoardColours, cell: Float) {
    val bounds = top.getBounds()
    raised(
        top,
        cell * TILE_HEIGHT,
        lerp(colours.safeTile, Color.Black, SAFE_SIDE_DARKEN),
        Brush.verticalGradient(listOf(colours.safeTile.copy(alpha = SAFE_TOP_ALPHA), colours.safeTile.copy(alpha = SAFE_BOTTOM_ALPHA)), bounds.top, bounds.bottom),
    )
    val star = star(p, Offset(col + HALF, row + HALF))
    drawPath(star, tint.copy(alpha = STAR_GLOW_ALPHA), style = Stroke(cell * STAR_GLOW_WIDTH, join = StrokeJoin.Round))
    drawPath(star, lerp(tint, Color.White, STAR_LIGHTEN))
}

/** A colour's home lane: a glowing neon frame (square-ended, as in the design) round its blocks. */
private fun DrawScope.drawLane(p: LudoBoardProjection, colour: Int, colours: LudoBoardColours, cell: Float, glow: Float) {
    val cells = LudoBoardGeometry.lane(colour)
    val tint = colours.players[colour]
    val tube = roundedRect(
        p,
        cells.minOf { it.first } + TUBE_INSET,
        cells.minOf { it.second } + TUBE_INSET,
        cells.maxOf { it.first } + 1 - TUBE_INSET,
        cells.maxOf { it.second } + 1 - TUBE_INSET,
        TUBE_CORNER,
    )
    drawPath(tube, tint.copy(alpha = TUBE_FILL_ALPHA))
    neon(tube, tint, cell * TUBE_WIDTH, glow)
}

/** A raised dark tray with a neon outline and four glowing sockets. */
private fun DrawScope.drawBase(p: LudoBoardProjection, colour: Int, tint: Color, colours: LudoBoardColours, cell: Float, glow: Float) {
    val (bx, by) = LudoBoardGeometry.baseOrigin(colour)
    val tray = roundedRect(p, bx + TRAY_INSET, by + TRAY_INSET, bx + BASE_SIZE - TRAY_INSET, by + BASE_SIZE - TRAY_INSET, TRAY_CORNER)
    raised(tray, cell * TRAY_HEIGHT, colours.cellSide, Brush.verticalGradient(listOf(colours.cellEdge, colours.basePad), tray.getBounds().top, tray.getBounds().bottom))
    drawPath(tray, tint.copy(alpha = TRAY_TINT_ALPHA))
    neon(tray, tint, cell * TRAY_LINE, glow)
    for (piece in 0 until PIECES) {
        val spot = LudoBoardGeometry.position(colour, -1, piece)
        val socket = circle(p, spot, SOCKET_RADIUS)
        drawPath(socket, Color.Black.copy(alpha = SOCKET_SHADE))
        drawPath(socket, tint.copy(alpha = SOCKET_GLOW_ALPHA * (GLOW_FLOOR + GLOW_SWING * glow)), style = Stroke(cell * SOCKET_GLOW_WIDTH))
        drawPath(socket, lerp(tint, Color.White, SOCKET_LIGHTEN), style = Stroke(cell * SOCKET_LINE))
    }
}

/** The dark plate in the middle with a lit rim; the dice stands on it. */
private fun DrawScope.drawCentre(p: LudoBoardProjection, colours: LudoBoardColours, cell: Float, glow: Float) {
    val plate = roundedRect(p, HOME_LO, HOME_LO, HOME_HI, HOME_HI, PLATE_CORNER)
    raised(plate, cell * PLATE_HEIGHT, colours.cellSide, Brush.verticalGradient(listOf(colours.cellEdge, colours.centre), plate.getBounds().top, plate.getBounds().bottom))
    val centre = p.project(CENTRE)
    drawCircle(
        brush = Brush.radialGradient(listOf(Color.White.copy(alpha = PLATE_GLOW_ALPHA), Color.Transparent), centre, cell * PLATE_GLOW_RADIUS),
        radius = cell * PLATE_GLOW_RADIUS,
        center = centre,
    )
    neon(plate, colours.rim, cell * PLATE_LINE, glow)
}

/**
 * A glowing neon outline: wide soft halos that breathe with [glow] (0..1),
 * the coloured line, then a pale hot core.
 */
private fun DrawScope.neon(shape: Path, color: Color, width: Float, glow: Float) {
    val strength = GLOW_FLOOR + GLOW_SWING * glow
    GLOW_LAYERS.forEach { (scale, alpha) ->
        drawPath(shape, color.copy(alpha = (alpha * strength).coerceAtMost(1f)), style = Stroke(width * scale * (1f + HALO_GROW * glow), join = StrokeJoin.Round))
    }
    drawPath(shape, color, style = Stroke(width, join = StrokeJoin.Round))
    drawPath(shape, lerp(color, Color.White, NEON_CORE_LIGHTEN), style = Stroke(width * NEON_CORE, join = StrokeJoin.Round))
}

/** A rounded rectangle laid on the board (grid units), projected. */
private fun roundedRect(p: LudoBoardProjection, x0: Float, y0: Float, x1: Float, y1: Float, radius: Float): Path {
    val corners = listOf(
        Offset(x1 - radius, y0 + radius) to -QUARTER_PI * 2,
        Offset(x1 - radius, y1 - radius) to 0f,
        Offset(x0 + radius, y1 - radius) to QUARTER_PI * 2,
        Offset(x0 + radius, y0 + radius) to PI.toFloat(),
    )
    val points = corners.flatMap { (centre, start) ->
        (0..ARC_STEPS).map { i ->
            val a = start + QUARTER_PI * 2 * i / ARC_STEPS
            p.project(centre + Offset(cos(a) * radius, sin(a) * radius))
        }
    }
    return polygon(points)
}

private fun circle(p: LudoBoardProjection, centre: Offset, radius: Float) = polygon(
    (0 until CIRCLE_SEGMENTS).map { i ->
        val a = 2 * PI.toFloat() * i / CIRCLE_SEGMENTS
        p.project(centre + Offset(cos(a) * radius, sin(a) * radius))
    },
)

private fun star(p: LudoBoardProjection, centre: Offset) = polygon(
    (0 until STAR_POINTS * 2).map { i ->
        val r = if (i % 2 == 0) STAR_OUTER else STAR_INNER
        val a = -PI.toFloat() / 2 + PI.toFloat() * i / STAR_POINTS
        p.project(centre + Offset(cos(a) * r, sin(a) * r))
    },
)

private fun polygon(pts: List<Offset>) = Path().apply {
    moveTo(pts[0].x, pts[0].y)
    for (k in 1 until pts.size) lineTo(pts[k].x, pts[k].y)
    close()
}

private const val COLOURS = 4
private const val PIECES = 4
private const val HALF = 0.5f
private val CENTRE = Offset(LudoBoardGeometry.GRID / 2f, LudoBoardGeometry.GRID / 2f)
private const val QUARTER_PI = (PI / 4).toFloat()
private const val ARC_STEPS = 6
private const val CIRCLE_SEGMENTS = 28
private const val BASE_SIZE = 6f
private const val BASE_HALF = 3f
private const val SPILL_RADIUS = 9f
private const val SPILL_ALPHA = 0.22f
private const val SLAB_CORNER = 1.4f
private const val RIM_ALPHA = 0.35f
private const val RIM_WIDTH = 0.035f
private const val RIM_GLOW_ALPHA = 0.08f
private const val RIM_GLOW_WIDTH = 0.25f
private const val TILE_INSET = 0.06f
private const val TILE_CORNER = 0.12f
private const val TILE_HEIGHT = 0.16f
private const val TRAY_HEIGHT = 0.22f
private const val PLATE_HEIGHT = 0.26f
private const val SHADOW_SHIFT = 0.4f
private const val SHADOW_DROP = 1.9f
private const val BLOCK_SHADOW_ALPHA = 0.45f
private const val EDGE_LIGHT_ALPHA = 0.35f
private const val EDGE_LIGHT_WIDTH = 0.35f
private const val SAFE_SIDE_DARKEN = 0.45f
private const val SAFE_TOP_ALPHA = 0.7f
private const val SAFE_BOTTOM_ALPHA = 0.35f
private const val STAR_POINTS = 5
private const val STAR_OUTER = 0.34f
private const val STAR_INNER = 0.15f
private const val STAR_GLOW_ALPHA = 0.6f
private const val STAR_GLOW_WIDTH = 0.1f
private const val STAR_LIGHTEN = 0.25f
private const val TUBE_INSET = 0.04f
private const val TUBE_CORNER = 0.08f
private const val TUBE_WIDTH = 0.07f
private const val TUBE_FILL_ALPHA = 0.06f
private const val TRAY_INSET = 0.55f
private const val TRAY_CORNER = 0.8f
private const val TRAY_LINE = 0.06f
private const val TRAY_TINT_ALPHA = 0.06f
private const val SOCKET_RADIUS = 0.6f
private const val SOCKET_SHADE = 0.35f
private const val SOCKET_GLOW_ALPHA = 0.3f
private const val SOCKET_GLOW_WIDTH = 0.18f
private const val SOCKET_LINE = 0.045f
private const val SOCKET_LIGHTEN = 0.2f
private const val HOME_LO = 6.1f
private const val HOME_HI = 8.9f
private const val PLATE_CORNER = 0.45f
private const val PLATE_LINE = 0.04f
private const val PLATE_GLOW_ALPHA = 0.1f
private const val PLATE_GLOW_RADIUS = 1.6f
private const val NEON_CORE = 0.35f
private const val NEON_CORE_LIGHTEN = 0.6f
private val GLOW_LAYERS = listOf(7f to 0.07f, 4f to 0.14f, 2.2f to 0.3f)
private const val GLOW_FLOOR = 0.65f
private const val GLOW_SWING = 0.7f
private const val HALO_GROW = 0.25f
