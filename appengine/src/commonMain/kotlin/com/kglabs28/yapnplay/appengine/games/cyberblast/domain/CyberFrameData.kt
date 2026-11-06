package com.kglabs28.yapnplay.appengine.games.cyberblast.domain

import com.kglabs28.yapnplay.appengine.games.cyberblast.BlastDir
import com.kglabs28.yapnplay.appengine.games.cyberblast.BlastPhase
import com.kglabs28.yapnplay.appengine.games.cyberblast.FlameShape

/**
 * One frame of the board, from the server (see the contract's CyberFrame).
 * Positions are thousandths of a tile; times are ms left. [crates] and
 * [walls] are null when they didn't change.
 */
data class CyberFrameData(
    val number: Long,
    val time: Long,
    val phase: BlastPhase,
    val countdownMs: Long,
    val closingInMs: Long?,
    val bodies: List<CyberBody>,
    val bombs: List<CyberBombData>,
    val flames: List<CyberFlameData>,
    val items: List<CyberItemData>,
    val crates: Set<Int>?,
    val walls: Set<Int>?,
    val hits: List<CyberHitData>,
    val broken: List<Int>,
    val booms: List<Int>,
) {
    fun body(seat: Int): CyberBody? = bodies.firstOrNull { it.seat == seat }
}

/** A player on the board. */
data class CyberBody(
    val seat: Int,
    val x: Int,
    val y: Int,
    val facing: BlastDir,
    val moving: Boolean,
    val lives: Int,
    val score: Int,
    val radius: Int,
    val boostMs: Long,
    val shieldMs: Long,
    val out: Boolean,
    /** Players they hit in all, and in a row since they last lost a heart. */
    val kills: Int = 0,
    val streak: Int = 0,
)

data class CyberBombData(val id: Int, val tile: Int, val owner: Int, val radius: Int, val fuseMs: Long)

data class CyberFlameData(val tile: Int, val owner: Int, val shape: FlameShape, val msLeft: Long)

data class CyberItemData(val tile: Int, val msLeft: Long)

/** [victim] lost a heart to [by] ([self]: own bomb; [crushed]: the closing arena). */
data class CyberHitData(val victim: Int, val by: Int, val livesLeft: Int, val victimPoints: Int, val byPoints: Int, val self: Boolean, val crushed: Boolean)
