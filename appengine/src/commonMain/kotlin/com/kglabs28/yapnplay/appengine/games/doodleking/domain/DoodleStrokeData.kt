package com.kglabs28.yapnplay.appengine.games.doodleking.domain

/**
 * One stroke: its [colour] (0 black, 1 red, 2 blue, 3 green, 4 yellow),
 * [size] (0 thin, 1 medium, 2 thick), eraser or ink, and its points as
 * x,y pairs on the 0–4095 canvas.
 */
data class DoodleStrokeData(
    val id: Int,
    val colour: Int,
    val size: Int,
    val eraser: Boolean,
    val points: List<Int>,
)
