package com.kglabs28.yapnplay.appengine.ui.components.games

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Position dots under the focused game: green for the current one,
 * faint for the rest.
 */
@Composable
fun CarouselDots(
    count: Int,
    selectedIndex: Int,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = YapNPlayTheme.shapes.pill

    Row(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        repeat(count) { index ->
            Box(
                modifier = Modifier
                    .size(dimens.carouselDot)
                    .clip(shape)
                    .background(if (index == selectedIndex) colors.LiveGreen else colors.TextSecondary.copy(alpha = 0.5f)),
            )
        }
    }
}
