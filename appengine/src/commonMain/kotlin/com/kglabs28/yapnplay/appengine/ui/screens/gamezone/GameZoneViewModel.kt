package com.kglabs28.yapnplay.appengine.ui.screens.gamezone

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.TextUtils
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.gamecatalog.usecase.GetGameCatalogUseCase
import com.kglabs28.yapnplay.appengine.features.matchmaking.domain.MatchmakingStatus
import com.kglabs28.yapnplay.appengine.features.matchmaking.usecase.FindQuickPlayMatchUseCase
import com.kglabs28.yapnplay.appengine.features.matchmaking.usecase.GetOnlinePlayerCountUseCase
import com.kglabs28.yapnplay.appengine.features.matchmaking.usecase.LeaveQuickPlayQueueUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

/**
 * Game Zone: loads the game catalog and online count, keeps the focused
 * game and view mode, and runs Quick Play — the server searches, starts
 * early when every seat has a human, and fills the rest with CPU players
 * at the deadline; this view model only shows progress and navigates.
 */
class GameZoneViewModel(
    private val getGameCatalog: GetGameCatalogUseCase,
    private val getOnlinePlayerCount: GetOnlinePlayerCountUseCase,
    private val findQuickPlayMatch: FindQuickPlayMatchUseCase,
    private val leaveQuickPlayQueue: LeaveQuickPlayQueueUseCase,
    private val clock: AppClock,
    dispatchers: DispatcherProvider,
) : MviViewModel<GameZoneState, GameZoneIntent, GameZoneEffect>(GameZoneState(), dispatchers) {

    private var searchJob: Job? = null
    private var tickJob: Job? = null

    override suspend fun handleIntent(intent: GameZoneIntent) {
        when (intent) {
            GameZoneIntent.Load -> load()
            is GameZoneIntent.FocusGame -> focus(intent.index)
            GameZoneIntent.ToggleViewMode -> setState {
                copy(
                    viewMode = if (viewMode == GameZoneViewMode.GRID) {
                        GameZoneViewMode.CAROUSEL
                    } else {
                        GameZoneViewMode.GRID
                    },
                )
            }
            is GameZoneIntent.PlayGame -> {
                focus(intent.index)
                startSearch()
            }
            GameZoneIntent.CancelSearch -> cancelSearch()
        }
    }

    private suspend fun load() {
        if (state.value.loaded) return
        val games = getGameCatalog()
        val online = (getOnlinePlayerCount() as? AppResult.Success)?.data
        setState {
            copy(
                games = games,
                focusedIndex = focusedIndex.coerceIn(0, (games.size - 1).coerceAtLeast(0)),
                onlineLabel = online?.let { Strings.gameZoneOnline(TextUtils.groupedThousands(it)) }.orEmpty(),
                loaded = true,
            )
        }
    }

    private fun focus(index: Int) {
        setState { copy(focusedIndex = index.coerceIn(0, (games.size - 1).coerceAtLeast(0))) }
    }

    private fun startSearch() {
        val game = state.value.games.getOrNull(state.value.focusedIndex) ?: return
        if (searchJob?.isActive == true) return
        setState { copy(searchMessage = null) }
        searchJob = scope.launch {
            findQuickPlayMatch(game.id).collect { status ->
                when (status) {
                    is MatchmakingStatus.Searching -> showSearching(status)
                    is MatchmakingStatus.Found -> showFound(status)
                    is MatchmakingStatus.Cancelled -> {
                        tickJob?.cancel()
                        setState { copy(search = null, searchMessage = Strings.gameZoneNoMatch) }
                    }
                }
            }
        }
    }

    private fun showSearching(status: MatchmakingStatus.Searching) {
        setState {
            copy(
                search = QuickPlaySearchUi(
                    headline = Strings.gameZoneSearching,
                    progress = Strings.gameZonePlayersFound(status.playersFound, status.playersNeeded),
                    timeLeft = Strings.gameZoneTimeLeft(secondsUntil(status.searchEndsAt)),
                    matchFound = false,
                ),
            )
        }
        startTicker(status.searchEndsAt)
    }

    private suspend fun showFound(status: MatchmakingStatus.Found) {
        tickJob?.cancel()
        val seats = status.humanPlayers + status.cpuPlayers
        setState {
            copy(
                search = QuickPlaySearchUi(
                    headline = Strings.gameZoneMatchFound(status.cpuPlayers),
                    progress = Strings.gameZonePlayersFound(seats, seats),
                    timeLeft = "",
                    matchFound = true,
                ),
            )
        }
        delay(AppConstants.MATCH_FOUND_HOLD_MS)
        setState { copy(search = null) }
        emitEffect(GameZoneEffect.OpenMatch(status.matchId))
    }

    /** Refreshes the countdown every second between server updates. */
    private fun startTicker(endsAt: Instant) {
        tickJob?.cancel()
        tickJob = scope.launch {
            while (isActive) {
                delay(AppConstants.SEARCH_TICK_MS)
                setState {
                    copy(search = search?.copy(timeLeft = Strings.gameZoneTimeLeft(secondsUntil(endsAt))))
                }
            }
        }
    }

    private suspend fun cancelSearch() {
        searchJob?.cancel()
        tickJob?.cancel()
        leaveQuickPlayQueue()
        setState { copy(search = null) }
    }

    private fun secondsUntil(instant: Instant): Long =
        (instant - clock.now()).inWholeSeconds.coerceAtLeast(0)
}
