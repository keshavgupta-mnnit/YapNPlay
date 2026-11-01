package com.kglabs28.yapnplay.appengine.ui.components.emojigift

import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.IntRect
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.unit.LayoutDirection
import androidx.compose.ui.window.PopupPositionProvider

/**
 * Places the emoji picker by the seat it opened from: just below it when it
 * fits, else just above, else beside it (right, or left at the right edge),
 * always kept inside the screen. [gap] (px) keeps it off the seat.
 */
class EmojiGiftPopupPosition(private val gap: Int) : PopupPositionProvider {

    override fun calculatePosition(anchorBounds: IntRect, windowSize: IntSize, layoutDirection: LayoutDirection, popupContentSize: IntSize): IntOffset {
        val width = popupContentSize.width
        val height = popupContentSize.height
        val centredX = clamp(anchorBounds.center.x - width / 2, windowSize.width - width)
        val below = anchorBounds.bottom + gap
        if (below + height <= windowSize.height) return IntOffset(centredX, below)
        val above = anchorBounds.top - gap - height
        if (above >= 0) return IntOffset(centredX, above)
        // Neither fits: beside the seat, as high as the screen allows.
        val right = anchorBounds.right + gap
        val x = if (right + width <= windowSize.width) right else anchorBounds.left - gap - width
        return IntOffset(clamp(x, windowSize.width - width), clamp(anchorBounds.center.y - height / 2, windowSize.height - height))
    }

    /** [value] kept within 0..[max] (0 when the picker is bigger than the screen). */
    private fun clamp(value: Int, max: Int): Int = value.coerceIn(0, max.coerceAtLeast(0))
}
