package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.ScrollState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * A thin frosted-glass scrollbar on the right edge of a scrolling list
 * (a faint track, a frosted white thumb sized to how much is visible).
 * Put it before `verticalScroll(state)` on the same element; nothing shows
 * when everything fits.
 */
@Composable
fun Modifier.glassScrollbar(state: ScrollState): Modifier {
    val width = YapNPlayTheme.dimens.scrollbarWidth
    val shine = YapNPlayTheme.colors.GlassShine
    return drawWithContent {
        drawContent()
        val max = state.maxValue
        if (max <= 0 || max == Int.MAX_VALUE) return@drawWithContent
        val w = width.toPx()
        val viewport = size.height
        val thumb = (viewport * viewport / (viewport + max)).coerceAtLeast(w * MIN_THUMB_WIDTHS)
        val top = (viewport - thumb) * state.value / max
        val x = size.width - w
        val radius = CornerRadius(w / 2, w / 2)
        drawRoundRect(shine.copy(alpha = TRACK_ALPHA), Offset(x, 0f), Size(w, viewport), radius)
        drawRoundRect(shine.copy(alpha = THUMB_ALPHA), Offset(x, top), Size(w, thumb), radius)
    }
}

private const val TRACK_ALPHA = 0.08f
private const val THUMB_ALPHA = 0.45f
private const val MIN_THUMB_WIDTHS = 6f
