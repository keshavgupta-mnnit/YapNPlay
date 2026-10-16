package com.kglabs28.yapnplay.appengine.ui.screens.home

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.PrimaryButton
import com.kglabs28.yapnplay.appengine.ui.components.SecondaryButton

/**
 * Placeholder home: two zone cards side by side on tablets (stacked on
 * phones), plus debug entries to the gallery and the About sample.
 * Content width is capped and centred.
 */
@Composable
fun HomeScreen(
    onQuickPlay: () -> Unit,
    onHostLobby: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Column(
        modifier = modifier
            .fillMaxSize()
            .background(colors.Background)
            .padding(Spacing.Lg),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Lg, Alignment.CenterVertically),
    ) {
        BasicText(
            text = Strings.homeTitle,
            style = YapNPlayTheme.typography.display.copy(color = colors.TextPrimary),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
        )
        Row(
            modifier = Modifier.widthIn(max = dimens.contentMaxWidth),
            horizontalArrangement = Arrangement.spacedBy(Spacing.Lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            ZoneCard(
                title = Strings.homeEnterGameZone,
                primaryLabel = Strings.homeQuickPlay,
                onPrimary = onQuickPlay,
                modifier = Modifier.weight(1f),
            )
            ZoneCard(
                title = Strings.homePlayWithFriends,
                primaryLabel = Strings.homeHostLobby,
                onPrimary = onHostLobby,
                modifier = Modifier.weight(1f),
            )
        }
    }
}

@Composable
private fun ZoneCard(
    title: String,
    primaryLabel: String,
    onPrimary: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Md),
    ) {
        BasicText(
            text = title,
            style = YapNPlayTheme.typography.headline.copy(
                color = YapNPlayTheme.colors.TextPrimary,
                textAlign = TextAlign.Center,
            ),
            maxLines = 2,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.fillMaxWidth(),
        )
        PrimaryButton(text = primaryLabel, onClick = onPrimary)
    }
}
