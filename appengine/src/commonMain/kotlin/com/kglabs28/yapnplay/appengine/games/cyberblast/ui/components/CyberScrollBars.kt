package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components

import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import com.kglabs28.yapnplay.appengine.games.cyberblast.utils.CyberWindow

/**
 * The bars along the bottom and right of a window onto a bigger arena: a
 * thin track with a thumb as long as the share of the arena on screen,
 * where the window is. A board that fits shows none.
 */
fun DrawScope.drawScrollBars(viewLeft: Float, viewTop: Float, window: CyberWindow, cols: Int, rows: Int, tile: Float, track: Color, thumb: Color) {
    val thick = tile * BAR
    val radius = CornerRadius(thick / 2)
    val width = window.cols * tile
    val height = window.rows * tile
    if (window.cols < cols) {
        val y = viewTop + height - thick - tile * INSET
        drawRoundRect(track, Offset(viewLeft + tile * INSET, y), Size(width - tile * INSET * 2, thick), radius)
        val usable = width - tile * INSET * 2
        drawRoundRect(thumb, Offset(viewLeft + tile * INSET + usable * window.left / cols, y), Size(usable * window.cols / cols, thick), radius)
    }
    if (window.rows < rows) {
        val x = viewLeft + width - thick - tile * INSET
        drawRoundRect(track, Offset(x, viewTop + tile * INSET), Size(thick, height - tile * INSET * 2), radius)
        val usable = height - tile * INSET * 2
        drawRoundRect(thumb, Offset(x, viewTop + tile * INSET + usable * window.top / rows), Size(thick, usable * window.rows / rows), radius)
    }
}

private const val BAR = 0.14f
private const val INSET = 0.12f
