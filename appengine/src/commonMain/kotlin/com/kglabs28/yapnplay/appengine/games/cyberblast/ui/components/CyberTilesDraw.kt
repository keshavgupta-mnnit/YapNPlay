package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Path
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

/** A metal pillar: a raised block, its side darker, a shine along its top. */
fun DrawScope.drawPillar(grid: CyberGrid, col: Int, row: Int, p: CyberBoardPalette) {
    val t = grid.tile
    val x = grid.x(col.toFloat()) + t * INSET
    val y = grid.y(row.toFloat()) + t * INSET
    val w = t * (1 - 2 * INSET)
    val radius = CornerRadius(t * BLOCK_ROUND)
    drawRoundRect(p.pillarSide, Offset(x, y + t * RAISE), Size(w, w - t * RAISE), radius)
    drawRoundRect(p.pillarTop, Offset(x, y), Size(w, w - t * RAISE * 2), radius)
    drawLine(p.pillarShine, Offset(x + t * SHINE_INSET, y + t * SHINE_INSET), Offset(x + w - t * SHINE_INSET, y + t * SHINE_INSET), t * SHINE_WIDTH)
    // Bolts in the corners.
    val bolt = t * BOLT
    listOf(Offset(x + bolt * 2, y + bolt * 2), Offset(x + w - bolt * 2, y + bolt * 2)).forEach { drawCircle(p.pillarSide, bolt, it) }
}

/** A breakable glass blocker: see-through fill, neon edge, a chevron mark. */
fun DrawScope.drawCrate(grid: CyberGrid, col: Int, row: Int, p: CyberBoardPalette) {
    val t = grid.tile
    val x = grid.x(col.toFloat()) + t * INSET
    val y = grid.y(row.toFloat()) + t * INSET
    val w = t * (1 - 2 * INSET)
    val radius = CornerRadius(t * BLOCK_ROUND)
    drawRoundRect(p.crateFill, Offset(x, y), Size(w, w), radius)
    drawRoundRect(p.crateEdge, Offset(x, y), Size(w, w), radius, style = Stroke(t * EDGE_WIDTH))
    val mark = Path().apply {
        moveTo(x + w * MARK_SIDE, y + w * MARK_TOP)
        lineTo(x + w / 2, y + w * MARK_BOTTOM)
        lineTo(x + w * (1 - MARK_SIDE), y + w * MARK_TOP)
    }
    drawPath(mark, p.crateMark, style = Stroke(t * MARK_WIDTH))
    drawLine(p.crateEdge.copy(alpha = GLINT_ALPHA), Offset(x + w * GLINT_FROM, y + w * GLINT_TO), Offset(x + w * GLINT_TO, y + w * GLINT_FROM), t * LINE_WIDTH * 2)
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
private const val GLINT_ALPHA = 0.6f
private const val WALL_EDGE_ALPHA = 0.7f
private const val SHATTER_SPREAD = 0.45f
private const val SHARD = 0.22f
