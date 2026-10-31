package com.kglabs28.yapnplay.appengine.ui.components.common

import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.verticalScroll
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.lazy.LazyListState
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
 * Put it before `verticalScroll(state)` on the same element (or use
 * [verticalScrollWithBar]); nothing shows when everything fits. Every
 * scrolling list in the app shows one. The position is read while drawing
 * only, so scrolling never recomposes the list for it.
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
    }.padding(end = width + Spacing.Xs)
}

private const val TRACK_ALPHA = 0.08f
private const val THUMB_ALPHA = 0.45f
private const val MIN_THUMB_WIDTHS = 6f

/** A scrolling column with the app's scrollbar: what every scrolling list uses. */
@Composable
fun Modifier.verticalScrollWithBar(state: ScrollState = rememberScrollState()): Modifier = glassScrollbar(state).verticalScroll(state)

/** The same scrollbar for a lazy list (long lists such as the match history): sized from the items in view. */
@Composable
fun Modifier.glassScrollbar(state: LazyListState): Modifier {
    val width = YapNPlayTheme.dimens.scrollbarWidth
    val shine = YapNPlayTheme.colors.GlassShine
    return drawWithContent {
        drawContent()
        val info = state.layoutInfo
        val total = info.totalItemsCount
        val visible = info.visibleItemsInfo
        if (total == 0 || visible.isEmpty() || (!state.canScrollForward && !state.canScrollBackward)) return@drawWithContent
        val w = width.toPx()
        val viewport = size.height
        // Items in view (counting the first one's part already scrolled away) out of all of them.
        val first = visible.first()
        val firstShown = state.firstVisibleItemIndex + state.firstVisibleItemScrollOffset.toFloat() / first.size.coerceAtLeast(1)
        val inView = visible.size.toFloat().coerceAtMost(total.toFloat())
        val thumb = (viewport * inView / total).coerceIn(w * MIN_THUMB_WIDTHS, viewport)
        val range = (total - inView).coerceAtLeast(1f)
        val top = (viewport - thumb) * (firstShown / range).coerceIn(0f, 1f)
        val x = size.width - w
        val radius = CornerRadius(w / 2, w / 2)
        drawRoundRect(shine.copy(alpha = TRACK_ALPHA), Offset(x, 0f), Size(w, viewport), radius)
        drawRoundRect(shine.copy(alpha = THUMB_ALPHA), Offset(x, top), Size(w, thumb), radius)
    }.padding(end = width + Spacing.Xs)
}
