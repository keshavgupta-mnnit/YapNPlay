package com.kglabs28.yapnplay.appengine.ui.screens.games

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.HeadlineFontFamily
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.home.EnterGameZoneCard

/**
 * Games tab: hub header plus the game-zone card, full width.
 */
@Composable
fun GamesScreen(
    onPlayNow: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GamesViewModel? = null,
) {
    val state = viewModel?.state?.collectAsState()?.value ?: GamesState()
    LaunchedEffect(viewModel) {
        viewModel?.onIntent(GamesIntent.Refresh)
    }

    Column(
        modifier = modifier.fillMaxSize(),
        verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
    ) {
        BasicText(
            text = state.title,
            style = YapNPlayTheme.typography.headline.copy(
                fontFamily = HeadlineFontFamily,
                fontWeight = FontWeight.Bold,
                color = YapNPlayTheme.colors.TextPrimary,
            ),
        )
        BasicText(
            text = state.subtitle,
            style = YapNPlayTheme.typography.body.copy(
                color = YapNPlayTheme.colors.TextSecondary,
            ),
        )
        EnterGameZoneCard(
            onEnterGameZone = onPlayNow,
            modifier = Modifier.weight(1f),
        )
    }
}
