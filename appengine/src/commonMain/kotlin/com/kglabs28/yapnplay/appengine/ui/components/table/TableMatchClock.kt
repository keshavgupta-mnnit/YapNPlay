package com.kglabs28.yapnplay.appengine.ui.components.table

import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/** How long the match has been running ("12:05"), in a frosted chip. */
@Composable
fun TableMatchClock(
    elapsed: String,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val radius = dimens.ludoGlossRadius
    Row(
        modifier = modifier
            .tableFrostedGlass(tint = colors.IconMuted, shape = RoundedCornerShape(radius), radius = radius, glow = dimens.zero)
            .padding(horizontal = Spacing.Sm)
            .semantics(mergeDescendants = true) { contentDescription = LudoStrings.ludoMatchTime },
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        AppIcon(imageVector = Icons.Rounded.Schedule, tint = colors.TextSecondary, size = dimens.iconSmall)
        BasicText(text = elapsed, style = YapNPlayTheme.typography.title.copy(color = colors.TextPrimary))
    }
}
