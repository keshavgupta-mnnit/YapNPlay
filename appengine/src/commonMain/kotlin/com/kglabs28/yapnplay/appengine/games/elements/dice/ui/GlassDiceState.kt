package com.kglabs28.yapnplay.appengine.games.elements.dice.ui

import com.kglabs28.yapnplay.appengine.games.elements.dice.utils.DiceMeshGeometry
import com.kglabs28.yapnplay.appengine.games.elements.dice.utils.DiceOrientation
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.CubicBezierEasing
import androidx.compose.animation.core.tween
import androidx.compose.runtime.Composable
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import kotlin.random.Random

/**
 * State of the glass die. [roll] tumbles it so the given face lands on
 * top, turning about all three axes so neighbouring faces stay true
 * (opposite faces sum to 7 throughout).
 */
@Stable
class GlassDiceState(initialFace: Int = 1) {
    var topFace by mutableIntStateOf(initialFace)
        private set
    var isRolling by mutableStateOf(false)
        private set

    internal var angles by mutableStateOf(DiceMeshGeometry.restingAngles(initialFace))
        private set
    /** 0 → 1 during a roll (drives the hop and the extra glow). */
    internal var progress by mutableFloatStateOf(0f)
        private set

    /** Animates a roll ending with [result] on top. */
    suspend fun roll(result: Int, durationMs: Int = AppConstants.DICE_ROLL_MS): Int {
        require(result in 1..DICE_FACES)
        if (isRolling) return topFace
        isRolling = true
        val from = angles
        val spin = DiceMeshGeometry.QUARTER_TURN * Random.nextInt(QUARTER_SPINS)
        val to = DiceOrientation(
            x = DiceMeshGeometry.landing(from.x, DiceMeshGeometry.baseX(result), Random.nextInt(MIN_TURNS_XY, MAX_TURNS_XY)),
            y = DiceMeshGeometry.landing(from.y, DiceMeshGeometry.YAW + spin, Random.nextInt(MIN_TURNS_XY, MAX_TURNS_XY)),
            z = DiceMeshGeometry.landing(from.z, DiceMeshGeometry.baseZ(result), Random.nextInt(MIN_TURNS_Z, MAX_TURNS_Z)),
        )
        try {
            Animatable(0f).animateTo(1f, tween(durationMs, easing = ROLL_EASING)) {
                val t = value
                progress = t
                angles = DiceOrientation(
                    from.x + (to.x - from.x) * t,
                    from.y + (to.y - from.y) * t,
                    from.z + (to.z - from.z) * t,
                )
            }
        } finally {
            val turn = DiceMeshGeometry.FULL_TURN
            angles = DiceOrientation(to.x.mod(turn), to.y.mod(turn), to.z.mod(turn))
            progress = 0f
            topFace = result
            isRolling = false
        }
        return result
    }

    /** Shows [face] on top at rest, without animating. */
    fun show(face: Int) {
        if (isRolling || face !in 1..DICE_FACES) return
        angles = DiceMeshGeometry.restingAngles(face)
        topFace = face
    }

    private companion object {
        const val DICE_FACES = 6
        const val QUARTER_SPINS = 4
        const val MIN_TURNS_XY = 2
        const val MAX_TURNS_XY = 4
        const val MIN_TURNS_Z = 1
        const val MAX_TURNS_Z = 3
        val ROLL_EASING = CubicBezierEasing(0.15f, 0.6f, 0.15f, 1f)
    }
}

@Composable
fun rememberGlassDiceState(initialFace: Int = 1) = remember { GlassDiceState(initialFace) }
