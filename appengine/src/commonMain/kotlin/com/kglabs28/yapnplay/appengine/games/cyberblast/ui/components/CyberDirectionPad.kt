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
                        val next = if (active) CyberPadMath.dirOf(at - centre, this.size.width * DEAD_ZONE) else null
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
        drawCircle(colors.BlastPadFill, r, c)
        drawCircle(colors.BlastPadEdge.copy(alpha = RIM_ALPHA), r - r * RIM / 2, c, style = Stroke(r * RIM))
        drawCircle(colors.BlastPadEdge.copy(alpha = HUB_ALPHA), r * HUB, c, style = Stroke(r * RIM))
        BlastDir.entries.forEach { dir ->
            val lit = dir == pushed
            val tip = Offset(c.x + dir.dx * r * ARROW_OUT, c.y + dir.dy * r * ARROW_OUT)
            val base = Offset(c.x + dir.dx * r * ARROW_IN, c.y + dir.dy * r * ARROW_IN)
            val side = Offset(-dir.dy * r * ARROW_HALF, dir.dx * r * ARROW_HALF)
            val arrow = Path().apply {
                moveTo(tip.x, tip.y)
                lineTo(base.x + side.x, base.y + side.y)
                lineTo(base.x - side.x, base.y - side.y)
                close()
            }
            drawPath(arrow, if (lit) colors.BlastPadEdge else colors.BlastPadEdge.copy(alpha = IDLE_ALPHA))
        }
    }
}

private const val DEAD_ZONE = 0.12f
private const val RIM = 0.06f
private const val RIM_ALPHA = 0.7f
private const val HUB = 0.3f
private const val HUB_ALPHA = 0.4f
private const val ARROW_OUT = 0.82f
private const val ARROW_IN = 0.55f
private const val ARROW_HALF = 0.17f
private const val IDLE_ALPHA = 0.5f
