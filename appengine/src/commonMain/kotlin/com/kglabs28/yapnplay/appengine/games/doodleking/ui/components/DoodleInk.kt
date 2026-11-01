package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.ui.graphics.Color

/**
 * The drawing's colours and line widths, the same on every phone: they are
 * part of the picture, not the app's theme. Colour index as on the wire.
 */
object DoodleInk {
    val COLOURS = listOf(
        Color(0xFF1B1B1F),
        Color(0xFFE53935),
        Color(0xFF1E88E5),
        Color(0xFF43A047),
        Color(0xFFFDD835),
    )

    /** The canvas paper, which the eraser draws with. */
    val PAPER = Color(0xFFF7F9FC)

    /** Line widths (thin, medium, thick) as a share of the canvas width; the eraser is wider. */
    val WIDTHS = listOf(0.006f, 0.013f, 0.026f)
    const val ERASER_GROW = 2.5f

    fun colour(index: Int): Color = COLOURS.getOrElse(index) { COLOURS.first() }
}
