package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableLongStateOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberUpdatedState
import androidx.compose.runtime.setValue
import androidx.compose.runtime.withFrameMillis
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.CyberBlastStrings

/**
 * The Drop Bomb button: a big glass bomb in the user's [colour] (the same
 * bomb they drop on the board) with "DROP BOMB" under it. It drops on touch
 * (no wait for the finger to lift) and dims when it can't.
 */
@Composable
fun CyberBombButton(onDrop: () -> Unit, colour: Color, seatColours: List<Color>, modifier: Modifier = Modifier, size: Dp = YapNPlayTheme.dimens.blastBombButton, enabled: Boolean = true) {
    val palette = cyberBoardPalette(seatColours)
    val drop by rememberUpdatedState(onDrop)
    var pressed by remember { mutableStateOf(false) }
    val tick = remember { mutableLongStateOf(0L) }
    LaunchedEffect(Unit) { while (true) withFrameMillis { tick.longValue = it } }
    Column(
        modifier = modifier
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
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        Canvas(modifier = Modifier.size(size)) {
            val r = this.size.minDimension * SPHERE
            val c = Offset(center.x - r * 0.1f, center.y + r * 0.2f)
            drawCircle(Brush.radialGradient(listOf(colour.copy(alpha = HALO_ALPHA), Color.Transparent), c, r * HALO), r * HALO, c)
            drawBombSphere(c, r, colour, tick.longValue, palette)
        }
        BasicText(text = CyberBlastStrings.blastDropBombLabel.uppercase(), style = YapNPlayTheme.typography.label.copy(color = YapNPlayTheme.colors.TextPrimary))
    }
}

private const val DISABLED_ALPHA = 0.45f
private const val PRESSED_SCALE = 0.92f
/** The sphere's radius, of the button's size (the fuse and spark reach above it). */
private const val SPHERE = 0.34f
private const val HALO = 1.5f
private const val HALO_ALPHA = 0.45f
