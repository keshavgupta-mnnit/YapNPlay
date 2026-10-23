package com.kglabs28.yapnplay.appengine.ui.screens.history

import com.kglabs28.yapnplay.appengine.ui.components.table.arenaGlass
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.HistoryStrings
import com.kglabs28.yapnplay.appengine.games.base.identity.GameIds
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants
import com.kglabs28.yapnplay.appengine.ui.components.common.BackTitleBar
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.history.MatchHistoryRow
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.MatchLeaderboard
import com.kglabs28.yapnplay.appengine.ui.placeholders.CityBazaarArtwork
import com.kglabs28.yapnplay.appengine.ui.placeholders.MonoArtwork
import com.kglabs28.yapnplay.appengine.ui.placeholders.MonoChaosArtwork
import com.kglabs28.yapnplay.appengine.ui.placeholders.RoyalLudoArtwork
import com.kglabs28.yapnplay.appengine.ui.placeholders.CozyHomeBackground

/**
 * The user's past matches, newest first: one row each (game, place, date,
 * coins, XP). A tap shows that match's leaderboard again; its button
 * closes it.
 */
@Composable
fun MatchHistoryScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MatchHistoryViewModel? = null,
) {
    val state by (viewModel?.state ?: return).collectAsState()
    val send: (MatchHistoryIntent) -> Unit = { viewModel.onIntent(it) }
    LaunchedEffect(viewModel) {
        viewModel.onIntent(MatchHistoryIntent.Load)
        viewModel.effects.collect { effect ->
            when (effect) {
                MatchHistoryEffect.Close -> onBack()
            }
        }
    }
    val colors = YapNPlayTheme.colors
    CozyHomeBackground(modifier = modifier, frosted = true) {
        Column(
            modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.Md, vertical = Spacing.Xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
        ) {
            BackTitleBar(title = HistoryStrings.historyTitle, backDescription = HistoryStrings.historyBack, onBack = { send(MatchHistoryIntent.Back) }, modifier = Modifier.fillMaxWidth())
            // A dark frosted panel (as on Feedback) so every row reads over the background.
            Column(
                modifier = Modifier
                    .fillMaxWidth()
                    .weight(1f)
                    .arenaGlass(YapNPlayTheme.shapes.panel)
                    .padding(Spacing.Md),
                verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
            ) {
                val message = state.error ?: HistoryStrings.historyEmpty.takeIf { state.loaded && state.rows.isEmpty() }
                if (message != null) {
                    BasicText(text = message, style = YapNPlayTheme.typography.label.copy(color = colors.TextSecondary))
                }
                LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
                    items(state.rows, key = { it.matchId }) { row ->
                        MatchHistoryRow(row = row, onClick = { send(MatchHistoryIntent.Open(row.matchId)) })
                    }
                }
            }
        }
    }
    val board = state.board
    BlockingOverlay(visible = board != null) {
        if (board != null) {
            MatchLeaderboard(
                board = board,
                gameArt = { art ->
                    when (state.boardGameId) {
                        GameIds.CITY_BAZAAR -> CityBazaarArtwork(modifier = art)
                        GameIds.ROYAL_LUDO -> RoyalLudoArtwork(modifier = art)
                        MonoChaosConstants.ID -> MonoChaosArtwork(modifier = art)
                        MonoConstants.ID -> MonoArtwork(modifier = art)
                    }
                },
                onPlayAgain = { send(MatchHistoryIntent.CloseBoard) },
            )
        }
    }
}
