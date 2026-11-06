package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.CyberBlastStrings

/** The big round "Drop bomb" button: drops on touch (no wait for the finger to lift), dims when it can't. */
@Composable
fun CyberBombButton(onDrop: () -> Unit, modifier: Modifier = Modifier, size: Dp = YapNPlayTheme.dimens.blastBombButton, enabled: Boolean = true) {
    val colors = YapNPlayTheme.colors
    val drop by rememberUpdatedState(onDrop)
    var pressed by remember { mutableStateOf(false) }
    Box(
        modifier = modifier
            .size(size)
            .graphicsLayer {
                alpha = if (enabled) 1f else DISABLED_ALPHA
                val s = if (pressed) PRESSED_SCALE else 1f
                scaleX = s
                scaleY = s
            }
            .semantics { contentDescription = CyberBlastStrings.blastDropBombLabel }
            .pointerInput(enabled) {
                awaitEachGesture {
                    val down = awaitFirstDown(requireUnconsumed = false)
                    if (!enabled) return@awaitEachGesture
                    pressed = true
                    drop()
                    down.consume()
                    while (awaitPointerEvent().changes.any { it.pressed }) Unit
                    pressed = false
                }
            },
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val r = this.size.minDimension / 2
            drawCircle(Brush.radialGradient(listOf(colors.BlastBombButtonEdge, colors.BlastBombButton), Offset(center.x - r * 0.3f, center.y - r * 0.3f), r * 1.4f), r, center)
            drawCircle(colors.BlastBombButtonEdge, r - r * RIM / 2, center, style = Stroke(r * RIM))
            drawCircle(colors.BlastBombBody, r * BOMB, Offset(center.x, center.y - r * BOMB_UP))
            drawCircle(colors.BlastSpark, r * SPARK, Offset(center.x + r * BOMB * 0.7f, center.y - r * (BOMB_UP + BOMB)))
        }
        BasicText(
            text = CyberBlastStrings.blastDropBomb,
            style = YapNPlayTheme.typography.caption.copy(color = colors.TextPrimary, textAlign = TextAlign.Center),
            modifier = Modifier.align(Alignment.BottomCenter).graphicsLayer { translationY = -this.size.height * LABEL_LIFT },
        )
    }
}

private const val DISABLED_ALPHA = 0.45f
private const val PRESSED_SCALE = 0.92f
private const val RIM = 0.07f
private const val BOMB = 0.26f
private const val BOMB_UP = 0.22f
private const val SPARK = 0.08f
private const val LABEL_LIFT = 0.25f
