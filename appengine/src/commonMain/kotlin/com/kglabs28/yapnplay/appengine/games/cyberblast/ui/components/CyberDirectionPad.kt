package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.CyberBlastStrings
import com.kglabs28.yapnplay.appengine.games.cyberblast.BlastDir
import com.kglabs28.yapnplay.appengine.games.cyberblast.utils.CyberPadMath

/**
 * The round direction pad: press and hold anywhere on it, slide the thumb
 * round to turn; letting go stops. The way pushed lights up. [onDir] gets
 * each change (null: let go).
 */
@Composable
fun CyberDirectionPad(onDir: (BlastDir?) -> Unit, modifier: Modifier = Modifier, size: Dp = YapNPlayTheme.dimens.blastPadSize, enabled: Boolean = true) {
    val colors = YapNPlayTheme.colors
    val send by rememberUpdatedState(onDir)
    var pushed by remember { mutableStateOf<BlastDir?>(null) }
    val active by rememberUpdatedState(enabled)
    Canvas(
        modifier = modifier
            .size(size)
            .semantics { contentDescription = CyberBlastStrings.blastPadLabel }
            .pointerInput(Unit) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    val centre = Offset(this.size.width / 2f, this.size.height / 2f)
                    fun update(at: Offset) {
                        val next = if (active) CyberPadMath.dirOf(at - centre, this.size.width * DEAD_ZONE, pushed) else null
                        if (next != pushed) {
                            pushed = next
                            send(next)
                        }
                    }
                    update(down.position)
                    while (true) {
                        val event = awaitPointerEvent()
                        val change = event.changes.firstOrNull { it.id == down.id } ?: break
                        if (!change.pressed) break
                        update(change.position)
                        change.consume()
                    }
                    if (pushed != null) {
                        pushed = null
                        send(null)
                    }
                }
            },
    ) {
        val r = this.size.minDimension / 2
        val c = center
        // The see-through ring with a thin light rim.
        drawCircle(colors.BlastPadFill, r, c)
        drawCircle(colors.BlastPadRim, r - r * RIM / 2, c, style = Stroke(r * RIM))
        // Big soft arrows near the rim; the pushed one lights up.
        BlastDir.entries.forEach { dir -> drawArrow(c, dir, r * ARROW_OUT, r * ARROW_IN, r * ARROW_HALF, if (dir == pushed) colors.BlastPadEdge else colors.BlastPadArrow) }
        // The raised knob, slid a little the way the thumb pushes.
        val shift = pushed?.let { Offset(it.dx * r * KNOB_TRAVEL, it.dy * r * KNOB_TRAVEL) } ?: Offset.Zero
        val k = c + shift
        val kr = r * KNOB
        drawCircle(Color.Black.copy(alpha = SHADOW_ALPHA), kr * SHADOW_SCALE, k + Offset(0f, kr * SHADOW_DROP))
        drawCircle(Brush.radialGradient(listOf(colors.BlastKnobLight, colors.BlastKnob), k - Offset(kr * LIGHT_OFF, kr * LIGHT_OFF), kr * LIGHT_SPREAD), kr, k)
        drawCircle(colors.BlastKnobEdge, kr - kr * KNOB_RIM / 2, k, style = Stroke(kr * KNOB_RIM))
        drawCircle(colors.BlastKnobLight.copy(alpha = INNER_RIM_ALPHA), kr * INNER_RIM, k, style = Stroke(kr * KNOB_RIM / 2))
        // Small dark arrows on the knob.
        BlastDir.entries.forEach { dir -> drawArrow(k, dir, kr * SMALL_OUT, kr * SMALL_IN, kr * SMALL_HALF, colors.BlastKnobEdge) }
    }
}

/** A triangle pointing [dir] from [c]: its tip [out] away, its base [inner] away, [half] wide each side. */
private fun DrawScope.drawArrow(c: Offset, dir: BlastDir, out: Float, inner: Float, half: Float, colour: Color) {
    val tip = Offset(c.x + dir.dx * out, c.y + dir.dy * out)
    val base = Offset(c.x + dir.dx * inner, c.y + dir.dy * inner)
    val side = Offset(-dir.dy * half, dir.dx * half)
    drawPath(Path().apply {
        moveTo(tip.x, tip.y)
        lineTo(base.x + side.x, base.y + side.y)
        lineTo(base.x - side.x, base.y - side.y)
        close()
    }, colour)
}

private const val DEAD_ZONE = 0.12f
private const val RIM = 0.025f
private const val ARROW_OUT = 0.9f
private const val ARROW_IN = 0.72f
private const val ARROW_HALF = 0.14f
private const val KNOB = 0.4f
private const val KNOB_TRAVEL = 0.12f
private const val KNOB_RIM = 0.06f
private const val INNER_RIM = 0.88f
private const val INNER_RIM_ALPHA = 0.35f
private const val LIGHT_OFF = 0.35f
private const val LIGHT_SPREAD = 1.5f
private const val SHADOW_ALPHA = 0.35f
private const val SHADOW_SCALE = 1.04f
private const val SHADOW_DROP = 0.08f
private const val SMALL_OUT = 0.68f
private const val SMALL_IN = 0.5f
private const val SMALL_HALF = 0.12f
