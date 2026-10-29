package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.IndicationNodeFactory
import androidx.compose.foundation.interaction.InteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.Paint
import androidx.compose.ui.graphics.drawscope.ContentDrawScope
import androidx.compose.ui.graphics.drawscope.drawIntoCanvas
import androidx.compose.ui.node.DelegatableNode
import androidx.compose.ui.node.DrawModifierNode
import androidx.compose.ui.node.invalidateDraw
import kotlinx.coroutines.launch

/**
 * The app's press feedback, for every tappable thing: while pressed, the
 * control itself dims a little. It changes only what the control draws,
 * so it always follows the button's or card's own shape: never the grey
 * box the default highlight laid over a tap area.
 */
object PressDimIndication : IndicationNodeFactory {
    override fun create(interactionSource: InteractionSource): DelegatableNode = PressDimNode(interactionSource)
    override fun equals(other: Any?): Boolean = other === this
    override fun hashCode(): Int = PRESS_DIM_HASH
}

private class PressDimNode(private val interactionSource: InteractionSource) : Modifier.Node(), DrawModifierNode {
    private val alpha = Animatable(1f)
    private val paint = Paint()

    override fun onAttach() {
        coroutineScope.launch {
            var presses = 0
            interactionSource.interactions.collect { interaction ->
                when (interaction) {
                    is PressInteraction.Press -> presses++
                    is PressInteraction.Release, is PressInteraction.Cancel -> presses = (presses - 1).coerceAtLeast(0)
                }
                launch {
                    alpha.animateTo(if (presses > 0) PRESSED_ALPHA else 1f, tween(PRESS_FADE_MS))
                    invalidateDraw()
                }
                invalidateDraw()
            }
        }
    }

    override fun ContentDrawScope.draw() {
        val shown = alpha.value
        if (shown >= 1f) {
            drawContent()
            return
        }
        // The control drawn through a see-through layer: dimmed exactly where it draws, nowhere else.
        paint.alpha = shown
        drawIntoCanvas { canvas ->
            canvas.saveLayer(Rect(0f, 0f, size.width, size.height), paint)
            drawContent()
            canvas.restore()
        }
    }
}

private const val PRESSED_ALPHA = 0.7f
private const val PRESS_FADE_MS = 90
private const val PRESS_DIM_HASH = 0x7d1e
