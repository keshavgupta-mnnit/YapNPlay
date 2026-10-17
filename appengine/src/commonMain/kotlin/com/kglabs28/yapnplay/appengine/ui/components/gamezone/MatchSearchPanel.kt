package com.kglabs28.yapnplay.appengine.ui.components.gamezone

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton

/**
 * Quick Play search in progress, on a dark pill so it reads over any art:
 * headline ("Finding players…" / "Match found!") with the CPU-fill hint,
 * players found with time left, and Cancel. All text arrives formatted;
 * [onCancel] is null once the match is found (nothing to cancel).
 */
@Composable
fun MatchSearchPanel(
    headline: String,
    progress: String,
    timeLeft: String,
    hint: String,
    cancelLabel: String,
    onCancel: (() -> Unit)?,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography

    Row(
        modifier = modifier
            .glassPanel(
                glowColor = colors.GameZoneAccent,
                shape = YapNPlayTheme.shapes.pill,
                surface = colors.Background.copy(alpha = 0.94f),
            )
            .padding(horizontal = Spacing.Lg, vertical = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Lg),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs)) {
            BasicText(
                text = headline,
                style = typography.title.copy(color = colors.GameZoneAccent),
            )
            BasicText(
                text = hint,
                style = typography.label.copy(color = colors.TextPrimary),
            )
        }
        Column(
            horizontalAlignment = Alignment.End,
            verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs),
        ) {
            BasicText(
                text = progress,
                style = typography.title.copy(color = colors.TextPrimary),
            )
            BasicText(
                text = timeLeft,
                style = typography.label.copy(color = colors.GameZoneAccent),
            )
        }
        onCancel?.let {
            GlassPillButton(
                text = cancelLabel,
                onClick = it,
                leadingIcon = Icons.Rounded.Close,
                height = YapNPlayTheme.dimens.ctaHeight,
            )
        }
    }
}
