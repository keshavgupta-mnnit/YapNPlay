package com.kglabs28.yapnplay.appengine.ui.components.seat

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/** "0:45" with a clock, for a timed seat dialog. */
@Composable
fun SeatTimerChip(
    text: String,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    Row(
        modifier = modifier
            .glassPill(glowColor = colors.IconMuted, surface = colors.RoomsSmallButton, shape = YapNPlayTheme.shapes.pill)
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xxs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        AppIcon(imageVector = Icons.Rounded.Schedule, tint = colors.TextSecondary, size = YapNPlayTheme.dimens.iconSmall)
        BasicText(text = text, style = YapNPlayTheme.typography.title.copy(color = colors.TextPrimary))
    }
}
