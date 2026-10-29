package com.kglabs28.yapnplay.appengine.ui.components.gamezone

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill

/**
 * Glass "● 1,248 Online" pill. [label] is already formatted.
 */
@Composable
fun OnlineCountPill(
    label: String,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = YapNPlayTheme.shapes.pill

    Row(
        modifier = modifier
            .height(dimens.smallButtonHeight)
            // The same purple-rimmed glass as the Grid View button beside it.
            .glassPill(glowColor = colors.SocialZoneAccent, surface = colors.RoomsPanel)
            .padding(horizontal = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        Box(
            modifier = Modifier
                .size(dimens.statusDotSmall)
                .clip(shape)
                .background(colors.LiveGreen),
        )
        BasicText(
            text = label,
            style = YapNPlayTheme.typography.sectionTitle.copy(color = colors.TextPrimary),
        )
    }
}
