package com.kglabs28.yapnplay.appengine.ui.components.table

import kotlin.math.exp

/**
 * The hand's magnifier: with the thumb resting on the hand at [focusX],
 * the cards around it spread out and come forward (like a dock), while
 * the rest close up so the whole hand still fits the same width. With no
 * thumb, every card sits at the same step.
 */
object HandLens {

    /** One card's place: its left edge ([x], px) and how much it is in focus (0 none … 1 under the thumb). */
    class Spot(val x: Float, val focus: Float)

    /** Where each of [count] cards sits, starting at [start] at [step] apart (px), each [cardWidth] wide. */
    fun spots(count: Int, start: Float, step: Float, cardWidth: Float, focusX: Float?): List<Spot> {
        if (count <= 0) return emptyList()
        if (focusX == null || count < 2 || step <= 0f) return List(count) { Spot(start + step * it, 0f) }
        val centres = List(count) { start + step * it + cardWidth / 2 }
        val radius = cardWidth * RADIUS
        fun focusAt(x: Float): Float {
            val d = (x - focusX) / radius
            return exp(-d * d)
        }
        // Under the thumb the cards open up to most of a card's width; elsewhere they close up to make room.
        val boost = (cardWidth * FOCUS_STEP).coerceAtLeast(step) / step - 1f
        val gaps = List(count - 1) { i -> step * (1f + boost * focusAt((centres[i] + centres[i + 1]) / 2)) }
        val keepSpan = step * (count - 1) / gaps.sum()
        var x = start
        return List(count) { i ->
            Spot(x, focusAt(centres[i])).also { if (i < count - 1) x += gaps[i] * keepSpan }
        }
    }

    /** How wide the magnified area is (× a card's width). */
    private const val RADIUS = 1.6f
    /** The step between cards under the thumb (× a card's width): their corners read clearly. */
    private const val FOCUS_STEP = 0.9f
}
