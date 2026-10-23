package com.kglabs28.yapnplay.appengine.ui.screens.history

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.core.ui.HistoryStrings
import com.kglabs28.yapnplay.appengine.features.matchhistory.domain.PastMatch
import com.kglabs28.yapnplay.appengine.features.matchhistory.usecase.GetMatchHistoryUseCase

/** The user's match history: loads the rows; a tap opens that match's leaderboard again. */
class MatchHistoryViewModel(
    private val getHistory: GetMatchHistoryUseCase,
    dispatchers: DispatcherProvider,
) : MviViewModel<MatchHistoryState, MatchHistoryIntent, MatchHistoryEffect>(MatchHistoryState(), dispatchers) {

    private var matches: List<PastMatch> = emptyList()

    override suspend fun handleIntent(intent: MatchHistoryIntent) {
        when (intent) {
            MatchHistoryIntent.Load -> load()
            is MatchHistoryIntent.Open -> matches.firstOrNull { it.matchId == intent.matchId }?.let { match ->
                setState { copy(board = MatchHistoryUiMapper.board(match), boardGameId = match.gameId) }
            }
            MatchHistoryIntent.CloseBoard -> setState { copy(board = null) }
            MatchHistoryIntent.Back -> emitEffect(MatchHistoryEffect.Close)
        }
    }

    private suspend fun load() {
        when (val result = getHistory()) {
            is AppResult.Success -> {
                matches = result.data
                setState { copy(loaded = true, error = null, rows = matches.map(MatchHistoryUiMapper::row)) }
            }
            is AppResult.Failure -> setState { copy(loaded = true, error = HistoryStrings.historyFailed) }
        }
    }
}
