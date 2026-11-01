package com.kglabs28.yapnplay.appengine.ui.components.gamezone

import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import androidx.compose.material.icons.rounded.Refresh
import androidx.compose.material.icons.rounded.ErrorOutline
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
 * [onCancel] is null once the match is found (nothing to cancel). When the
 * search [failed] the same pill says so in red, with an error sign, Try
 * again and Close.
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
    failed: Boolean = false,
    retryLabel: String = "",
    onRetry: () -> Unit = {},
) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography

    Row(
        modifier = modifier
            .glassPanel(
                glowColor = if (failed) colors.Error else colors.GameZoneAccent,
                shape = YapNPlayTheme.shapes.pill,
                surface = colors.Background.copy(alpha = 0.94f),
            )
            .padding(horizontal = Spacing.Lg, vertical = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Lg),
    ) {
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs)) {
            Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
                if (failed) AppIcon(imageVector = Icons.Rounded.ErrorOutline, tint = colors.Error, size = YapNPlayTheme.dimens.iconMedium)
                BasicText(
                    text = headline,
                    style = typography.title.copy(color = if (failed) colors.Error else colors.GameZoneAccent),
                )
            }
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
                style = typography.label.copy(color = if (failed) colors.Error else colors.GameZoneAccent),
            )
        }
        if (failed) {
            GlassPillButton(
                text = retryLabel,
                onClick = onRetry,
                leadingIcon = Icons.Rounded.Refresh,
                height = YapNPlayTheme.dimens.ctaHeight,
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
