package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke

/** Where the board sits in the canvas: its top left and one tile's size (px). */
data class CyberGrid(val left: Float, val top: Float, val tile: Float) {
    fun x(col: Float): Float = left + col * tile
    fun y(row: Float): Float = top + row * tile
}

/** The arena's frame: a glowing rim round the board, one tile thick. */
fun DrawScope.drawArenaFrame(grid: CyberGrid, cols: Int, rows: Int, p: CyberBoardPalette) {
    val t = grid.tile
    val outer = Size((cols + 2) * t, (rows + 2) * t)
    val origin = Offset(grid.left - t, grid.top - t)
    val radius = CornerRadius(t * FRAME_ROUND)
    drawRoundRect(p.frame, origin, outer, radius)
    for (i in GLOW_RINGS downTo 1) {
        drawRoundRect(p.frameGlow.copy(alpha = GLOW_ALPHA / i), origin, outer, radius, style = Stroke(t * GLOW_WIDTH * i))
    }
    // The inner edge, a thin neon line round the floor.
    drawRoundRect(p.frameGlow, Offset(grid.left, grid.top), Size(cols * t, rows * t), CornerRadius(t * INNER_ROUND), style = Stroke(t * EDGE_WIDTH))
}

/** The floor: a soft checker of dark tiles with thin lines between them. */
fun DrawScope.drawFloor(grid: CyberGrid, cols: Int, rows: Int, p: CyberBoardPalette) {
    val t = grid.tile
    for (r in 0 until rows) for (c in 0 until cols) {
        drawRect(if ((r + c) % 2 == 0) p.floor else p.floorAlt, Offset(grid.x(c.toFloat()), grid.y(r.toFloat())), Size(t, t))
    }
    for (c in 1 until cols) drawLine(p.floorLine, Offset(grid.x(c.toFloat()), grid.top), Offset(grid.x(c.toFloat()), grid.y(rows.toFloat())), t * LINE_WIDTH)
    for (r in 1 until rows) drawLine(p.floorLine, Offset(grid.left, grid.y(r.toFloat())), Offset(grid.x(cols.toFloat()), grid.y(r.toFloat())), t * LINE_WIDTH)
}

/** A metal pillar: a dark block on a base, a raised cap with a recessed screen in it, light catching its top edges. */
fun DrawScope.drawPillar(grid: CyberGrid, col: Int, row: Int, p: CyberBoardPalette) {
    val t = grid.tile
    val x = grid.x(col.toFloat())
    val y = grid.y(row.toFloat())
    fun box(l: Float, top: Float, w: Float, h: Float, colour: Color, round: Float) =
        drawRoundRect(colour, Offset(x + t * l, y + t * top), Size(t * w, t * h), CornerRadius(t * round))
    // Shadow on the floor, then the body and its darker base.
    box(0.1f, 0.2f, 0.84f, 0.78f, p.floorLine, 0.12f)
    box(0.12f, 0.12f, 0.76f, 0.8f, p.pillarSide, 0.12f)
    box(0.14f, 0.72f, 0.72f, 0.16f, p.pillarBase, 0.06f)
    // The cap: lighter, with its shine along the top.
    box(0.16f, 0.1f, 0.68f, 0.58f, p.pillarTop, 0.12f)
    drawLine(p.pillarShine, Offset(x + t * 0.22f, y + t * 0.13f), Offset(x + t * 0.78f, y + t * 0.13f), t * 0.03f)
    // The recessed screen in the cap.
    box(0.26f, 0.22f, 0.48f, 0.34f, p.pillarSide, 0.07f)
    drawLine(p.pillarShine.copy(alpha = SCREEN_SHINE), Offset(x + t * 0.3f, y + t * 0.53f), Offset(x + t * 0.7f, y + t * 0.53f), t * 0.02f)
    drawRoundRect(p.pillarEdge, Offset(x + t * 0.12f, y + t * 0.1f), Size(t * 0.76f, t * 0.82f), CornerRadius(t * 0.12f), style = Stroke(t * 0.025f))
}

/**
 * A breakable glass blocker drawn as a cube seen from a little above: a lit
 * top face in perspective with a small screen and its diagonal shine, a darker
 * front face with a chevron, neon edges (cyan, or purple on some tiles) and a
 * shadow on the floor.
 */
fun DrawScope.drawCrate(grid: CyberGrid, col: Int, row: Int, p: CyberBoardPalette) {
    val t = grid.tile
    val x = grid.x(col.toFloat())
    val y = grid.y(row.toFloat())
    fun at(px: Float, py: Float) = Offset(x + t * px, y + t * py)
    fun shape(pts: List<Offset>) = Path().apply {
        moveTo(pts[0].x, pts[0].y)
        pts.drop(1).forEach { lineTo(it.x, it.y) }
        close()
    }
    val neon = if ((col * 3 + row) % PURPLE_EVERY == 0) p.crateMark else p.crateEdge
    val top = shape(listOf(at(0.2f, 0.06f), at(0.8f, 0.06f), at(0.92f, 0.36f), at(0.08f, 0.36f)))
    val front = shape(listOf(at(0.08f, 0.36f), at(0.92f, 0.36f), at(0.92f, 0.92f), at(0.08f, 0.92f)))
    // Shadow, then a soft neon glow round the cube.
    drawOval(Color.Black.copy(alpha = 0.35f), at(0.06f, 0.82f), Size(t * 0.9f, t * 0.16f))
    drawPath(shape(listOf(at(0.17f, 0.02f), at(0.83f, 0.02f), at(0.96f, 0.36f), at(0.96f, 0.96f), at(0.04f, 0.96f), at(0.04f, 0.36f))), neon.copy(alpha = CRATE_GLOW))
    // Glass faces: the top catches the light, the front is deeper.
    drawPath(front, Brush.verticalGradient(listOf(p.crateFill, p.crateScreen), y + t * 0.36f, y + t * 0.92f))
    drawPath(top, Brush.verticalGradient(listOf(lerp(p.crateFill, neon, TOP_TINT), p.crateFill), y + t * 0.06f, y + t * 0.36f))
    // The screen on the top face and its shine.
    val screen = shape(listOf(at(0.3f, 0.12f), at(0.7f, 0.12f), at(0.76f, 0.3f), at(0.24f, 0.3f)))
    drawPath(screen, p.crateScreen)
    drawPath(screen, neon.copy(alpha = SCREEN_EDGE), style = Stroke(t * 0.02f))
    drawLine(neon, at(0.38f, 0.26f), at(0.6f, 0.16f), t * 0.045f, StrokeCap.Round)
    // The chevron on the front.
    val chevron = Path().apply {
        moveTo(x + t * 0.32f, y + t * 0.56f)
        lineTo(x + t * 0.5f, y + t * 0.72f)
        lineTo(x + t * 0.68f, y + t * 0.56f)
    }
    drawPath(chevron, neon, style = Stroke(t * 0.055f, cap = StrokeCap.Round))
    // Neon edges, and a glint down the front's left edge.
    drawPath(top, neon, style = Stroke(t * 0.04f))
    drawPath(front, neon, style = Stroke(t * 0.045f))
    drawLine(Color.White.copy(alpha = GLINT_ALPHA), at(0.14f, 0.42f), at(0.14f, 0.7f), t * 0.025f, StrokeCap.Round)
}

/** A tile the closing arena filled: a dark slab with a warning edge. */
fun DrawScope.drawClosedWall(grid: CyberGrid, col: Int, row: Int, p: CyberBoardPalette) {
    val t = grid.tile
    val o = Offset(grid.x(col.toFloat()), grid.y(row.toFloat()))
    drawRect(p.wall, o, Size(t, t))
    drawRect(p.wallEdge.copy(alpha = WALL_EDGE_ALPHA), o, Size(t, t), style = Stroke(t * EDGE_WIDTH))
}

/** A blocker breaking ([age] 0–1): glass shards flying apart and fading. */
fun DrawScope.drawShatter(grid: CyberGrid, col: Int, row: Int, age: Float, p: CyberBoardPalette) {
    val t = grid.tile
    val cx = grid.x(col + 0.5f)
    val cy = grid.y(row + 0.5f)
    val spread = t * SHATTER_SPREAD * age
    val size = t * SHARD * (1 - age)
    val alpha = 1 - age
    listOf(-1f to -1f, 1f to -1f, -1f to 1f, 1f to 1f).forEach { (dx, dy) ->
        drawRect(p.crateEdge.copy(alpha = alpha), Offset(cx + dx * spread - size / 2, cy + dy * spread - size / 2), Size(size, size))
    }
}

private const val FRAME_ROUND = 0.6f
private const val INNER_ROUND = 0.15f
private const val GLOW_RINGS = 3
private const val GLOW_ALPHA = 0.35f
private const val GLOW_WIDTH = 0.12f
private const val EDGE_WIDTH = 0.05f
private const val LINE_WIDTH = 0.02f
private const val INSET = 0.06f
private const val RAISE = 0.1f
private const val BLOCK_ROUND = 0.14f
private const val SHINE_INSET = 0.14f
private const val SHINE_WIDTH = 0.04f
private const val BOLT = 0.04f
private const val MARK_SIDE = 0.28f
private const val MARK_TOP = 0.36f
private const val MARK_BOTTOM = 0.62f
private const val MARK_WIDTH = 0.06f
private const val GLINT_FROM = 0.14f
private const val GLINT_TO = 0.34f
private const val GLINT_ALPHA = 0.45f
private const val TOP_TINT = 0.3f
private const val WALL_EDGE_ALPHA = 0.7f
private const val SHATTER_SPREAD = 0.45f
private const val SHARD = 0.22f
private const val SCREEN_SHINE = 0.5f
private const val CRATE_GLOW = 0.25f
private const val SCREEN_EDGE = 0.6f
/** Every this many tiles (by a mix of column and row) a blocker glows purple instead of cyan. */
private const val PURPLE_EVERY = 4
