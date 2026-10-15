package com.kglabs28.yapnplay.appengine.ui.components

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.defaultMinSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/** Pill for short statuses (Ready, Online, Admin). Colour set by the caller. */
@Composable
fun StatusPill(
    text: String,
    modifier: Modifier = Modifier,
    color: Color = YapNPlayTheme.colors.Success,
) {
    Box(
        modifier = modifier
            .clip(YapNPlayTheme.shapes.pill)
            .background(color.copy(alpha = 0.2f))
            .defaultMinSize(minHeight = YapNPlayTheme.dimens.chipHeight),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = text,
            style = YapNPlayTheme.typography.label.copy(color = color),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.padding(horizontal = Spacing.Md),
        )
    }
}
