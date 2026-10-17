package com.kglabs28.yapnplay.appengine.ui.screens.gamezone

import com.kglabs28.yapnplay.appengine.ui.components.games.GamePickerViewMode
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Bolt
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.revealWhen
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.common.EmptyStateText
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton
import com.kglabs28.yapnplay.appengine.ui.components.games.GameCarousel
import com.kglabs28.yapnplay.appengine.ui.components.games.GameGrid
import com.kglabs28.yapnplay.appengine.ui.components.gamezone.MatchSearchPanel
import com.kglabs28.yapnplay.appengine.ui.components.gamezone.OnlineCountPill
import com.kglabs28.yapnplay.appengine.ui.components.gamezone.ViewModeToggle
import com.kglabs28.yapnplay.appengine.ui.components.moderation.PenaltyCard

/**
 * Game Zone tab (the scaffold frosts the whole background for it, see
 * AppNavTab.frostedBackground): online count and view toggle on top, the
 * looping carousel with QUICK PLAY under the dots — or the grid, where a
 * tile tap starts that game. While matching, the matching snackbar is a
 * centred blocking modal (BlockingOverlay): the whole app is frosted
 * behind it and nothing else can be touched until the user cancels or
 * the match is found.
 */
@Composable
fun GameZoneScreen(
    onOpenMatch: (matchId: String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: GameZoneViewModel? = null,
) {
    val state = viewModel?.state?.collectAsState()?.value ?: GameZoneState()
    val colors = YapNPlayTheme.colors
    LaunchedEffect(viewModel) {
        viewModel?.onIntent(GameZoneIntent.Load)
        viewModel?.effects?.collect { effect ->
            when (effect) {
                is GameZoneEffect.OpenMatch -> onOpenMatch(effect.matchId)
            }
        }
    }

    Column(
        modifier = modifier
            .fillMaxSize()
            .padding(bottom = Spacing.Xs),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        // The game area fills from the top; the online pill and view toggle
        // float over its top-right corner instead of taking their own row.
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
        ) {
            Box(
                modifier = Modifier
                    .fillMaxSize()
                    .revealWhen(state.loaded),
                contentAlignment = Alignment.Center,
            ) {
                when {
                    state.loaded && state.games.isEmpty() -> EmptyStateText(
                        text = Strings.gameZoneNoGames,
                        modifier = Modifier.fillMaxSize(),
                    )
                    state.games.isEmpty() -> Unit
                    state.viewMode == GamePickerViewMode.GRID -> GameGrid(
                        games = state.games,
                        focusedIndex = state.focusedIndex,
                        onGameClick = { viewModel?.onIntent(GameZoneIntent.PlayGame(it)) },
                        modifier = Modifier
                            .fillMaxSize()
                            .padding(top = YapNPlayTheme.dimens.smallButtonHeight + Spacing.Xs),
                    )
                    else -> GameCarousel(
                        games = state.games,
                        focusedIndex = state.focusedIndex,
                        onFocusChange = { viewModel?.onIntent(GameZoneIntent.FocusGame(it)) },
                        modifier = Modifier.fillMaxSize(),
                    )
                }
            }
            Row(
                modifier = Modifier
                    .align(Alignment.TopEnd)
                    .revealWhen(state.loaded),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
                verticalAlignment = Alignment.CenterVertically,
            ) {
                OnlineCountPill(label = state.onlineLabel)
                ViewModeToggle(
                    isGrid = state.viewMode == GamePickerViewMode.GRID,
                    onToggle = { viewModel?.onIntent(GameZoneIntent.ToggleViewMode) },
                )
            }
        }

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
        ) {
            if (state.viewMode == GamePickerViewMode.CAROUSEL) {
                // Fades in together with the games (never ahead of them).
                val ready = state.loaded && state.games.isNotEmpty()
                PillButton(
                    text = Strings.homeQuickPlay,
                    onClick = { viewModel?.onIntent(GameZoneIntent.PlayGame(state.focusedIndex)) },
                    gradient = listOf(colors.CtaGreenStart, colors.CtaGreenMid, colors.CtaGreenEnd),
                    contentColor = colors.CtaGreenContent,
                    leadingIcon = Icons.Rounded.Bolt,
                    height = YapNPlayTheme.dimens.ctaLargeHeight,
                    textStyle = YapNPlayTheme.typography.title,
                    enabled = ready,
                    modifier = Modifier
                        .fillMaxWidth(AppConstants.GAME_ZONE_CTA_WIDTH_FRACTION)
                        .revealWhen(ready),
                )
            }
            // Matching snackbar: centred, blocking modal over the frosted app.
            val search = state.search
            BlockingOverlay(visible = search != null) {
                if (search != null) MatchSearchPanel(
                    headline = search.headline,
                    progress = search.progress,
                    timeLeft = search.timeLeft,
                    hint = Strings.gameZoneCpuFillHint,
                    cancelLabel = Strings.gameZoneCancel,
                    onCancel = if (search.matchFound) null else {
                        { viewModel?.onIntent(GameZoneIntent.CancelSearch) }
                    },
                )
            }
            val penalty = state.penalty
            BlockingOverlay(
                visible = penalty != null,
                onDismiss = { viewModel?.onIntent(GameZoneIntent.DismissPenalty) },
            ) {
                if (penalty != null) PenaltyCard(
                    title = penalty.title,
                    body = penalty.body,
                    watchingAd = penalty.watchingAd,
                    onWatchAd = { viewModel?.onIntent(GameZoneIntent.WatchAd) },
                    onClose = { viewModel?.onIntent(GameZoneIntent.DismissPenalty) },
                )
            }
            state.searchMessage?.let {
                BasicText(
                    text = it,
                    style = YapNPlayTheme.typography.label.copy(color = colors.Error),
                )
            }
        }
    }
}
