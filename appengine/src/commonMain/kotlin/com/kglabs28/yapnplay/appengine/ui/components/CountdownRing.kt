package com.kglabs28.yapnplay.appengine.ui.components

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Circular turn timer. [progress] is 1.0 for a full ring, 0.0 for empty.
 * Static drawing — the caller drives progress from its own timer.
 */
@Composable
fun CountdownRing(
    progress: Float,
    modifier: Modifier = Modifier,
    size: Dp? = null,
    accent: Color = YapNPlayTheme.colors.Cyan,
    content: (@Composable () -> Unit)? = null,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val resolvedSize = size ?: dimens.timerRing
    Box(
        modifier = modifier.size(resolvedSize),
        contentAlignment = Alignment.Center,
    ) {
        Canvas(modifier = Modifier.size(resolvedSize)) {
            drawArc(
                color = colors.Raised,
                startAngle = -90f,
                sweepAngle = 360f,
                useCenter = false,
                style = Stroke(width = dimens.timerStroke.toPx(), cap = StrokeCap.Round),
            )
            drawArc(
                color = accent,
                startAngle = -90f,
                sweepAngle = 360f * progress.coerceIn(0f, 1f),
                useCenter = false,
                style = Stroke(width = dimens.timerStroke.toPx(), cap = StrokeCap.Round),
            )
        }
        content?.invoke()
    }
}
