package com.kglabs28.yapnplay.appengine.ui.components.playrooms

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings

/**
 * Green "● 6 live" chip: how many members are in the room right now.
 */
@Composable
fun LiveBadge(
    count: Int,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = YapNPlayTheme.shapes.pill

    Row(
        modifier = modifier
            .clip(shape)
            .background(colors.LiveGreen.copy(alpha = 0.12f))
            .padding(horizontal = Spacing.Xs, vertical = Spacing.Xxxs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        Box(
            modifier = Modifier
                .size(dimens.statusDotSmall)
                .clip(shape)
                .background(colors.LiveGreen),
        )
        BasicText(
            text = Strings.roomLive(count),
            style = YapNPlayTheme.typography.micro.copy(color = colors.LiveGreen),
            maxLines = 1,
        )
    }
}
