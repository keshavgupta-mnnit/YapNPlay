package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.Stroke
import com.kglabs28.yapnplay.appengine.games.cyberblast.BLAST_TILE
import com.kglabs28.yapnplay.appengine.games.cyberblast.BlastGrid
import com.kglabs28.yapnplay.appengine.games.cyberblast.ui.screens.match.CyberBoardFeed

/**
 * The room's Mini-map: the whole arena small (pillars, blockers, closed
 * walls) with a dot for every player still in, in their colour; the user's
 * own dot has a white ring. It redraws with each frame.
 */
@Composable
fun CyberMiniMap(feed: CyberBoardFeed, seatColours: List<Color>, modifier: Modifier = Modifier) {
    val palette = cyberBoardPalette(seatColours)
    Canvas(modifier = modifier) {
        feed.version.intValue
        val cols = feed.cols
        val rows = feed.rows
        val s = minOf(size.width / cols, size.height / rows)
        val left = (size.width - cols * s) / 2
        val top = (size.height - rows * s) / 2
        drawRoundRect(palette.frame.copy(alpha = BACK_ALPHA), Offset(left - s, top - s), Size((cols + 2) * s, (rows + 2) * s), CornerRadius(s * 2))
        drawRoundRect(palette.frameGlow, Offset(left - s, top - s), Size((cols + 2) * s, (rows + 2) * s), CornerRadius(s * 2), style = Stroke(s * 0.4f))
        fun cell(tile: Int, colour: Color) = drawRect(colour, Offset(left + BlastGrid.col(tile) * s, top + BlastGrid.row(tile) * s), Size(s, s))
        for (r in 0 until rows) for (c in 0 until cols) {
            val t = BlastGrid.of(c, r)
            if (BlastGrid.isPillar(t)) cell(t, palette.pillarTop)
        }
        feed.walls.forEach { cell(it, palette.wallEdge.copy(alpha = WALL_ALPHA)) }
        feed.crates.forEach { cell(it, palette.crateEdge.copy(alpha = CRATE_ALPHA)) }
        feed.current?.bodies?.filter { !it.out }?.forEach { body ->
            val at = Offset(left + body.x.toFloat() / BLAST_TILE * s, top + body.y.toFloat() / BLAST_TILE * s)
            drawCircle(palette.seat(body.seat), s * DOT, at)
            if (body.seat == feed.mySeat) drawCircle(palette.trim, s * DOT, at, style = Stroke(s * 0.35f))
        }
    }
}

private const val BACK_ALPHA = 0.85f
private const val WALL_ALPHA = 0.6f
private const val CRATE_ALPHA = 0.45f
private const val DOT = 0.9f
