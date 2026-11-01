package com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match

import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleStrokeData

/**
 * One line on the board as it's drawn: its pen, and its points (x,y pairs
 * on the 0–4095 canvas) growing in place, so adding a point never copies
 * the line. Compared by identity: the canvas keeps each line's path.
 */
class DoodleLine(val id: Int, val colour: Int, val size: Int, val eraser: Boolean) {
    val points: MutableList<Int> = mutableListOf()

    companion object {
        fun of(stroke: DoodleStrokeData) = DoodleLine(stroke.id, stroke.colour, stroke.size, stroke.eraser).apply { points += stroke.points }
    }
}
