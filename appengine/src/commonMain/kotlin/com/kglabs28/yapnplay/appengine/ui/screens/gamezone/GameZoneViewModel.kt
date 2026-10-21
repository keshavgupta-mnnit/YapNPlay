package com.kglabs28.yapnplay.appengine.ui.screens.gamezone

import com.kglabs28.yapnplay.appengine.ui.components.coins.LeaguePickerUi
import com.kglabs28.yapnplay.appengine.ui.components.coins.CoinsUiMapper
import com.kglabs28.yapnplay.appengine.ui.components.coins.CoinsOfferUi
import com.kglabs28.yapnplay.appengine.core.ui.CoinsStrings
import com.kglabs28.yapnplay.appengine.features.matchmaking.domain.MatchmakingCancelReason
import com.kglabs28.yapnplay.appengine.features.coins.domain.CoinLeagues
import com.kglabs28.yapnplay.appengine.features.coins.usecase.WatchAdForCoinsUseCase
import com.kglabs28.yapnplay.appengine.features.coins.usecase.GetCoinLeaguesUseCase
import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import com.kglabs28.yapnplay.appengine.ui.components.games.GamePickerViewMode
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
import com.kglabs28.yapnplay.appengine.features.moderation.usecase.ClearPenaltyWithAdUseCase
import com.kglabs28.yapnplay.appengine.features.moderation.usecase.ObservePenaltyUseCase
import kotlin.math.ceil
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.datetime.Instant

/**
 * Game Zone: loads the game catalog and online count, keeps the focused
 * game and view mode, and runs Quick Play — the user picks a coin league
 * (short of the fee: a rewarded ad for coins, or Starter), the server searches, starts
 * early when every seat has a human, and fills the rest with CPU players
 * at the deadline; this view model only shows progress and navigates.
 */
class GameZoneViewModel(
    private val getGameCatalog: GetGameCatalogUseCase,
    private val getOnlinePlayerCount: GetOnlinePlayerCountUseCase,
    private val findQuickPlayMatch: FindQuickPlayMatchUseCase,
    private val leaveQuickPlayQueue: LeaveQuickPlayQueueUseCase,
    private val observePenalty: ObservePenaltyUseCase,
    private val clearPenaltyWithAd: ClearPenaltyWithAdUseCase,
    private val getCoinLeagues: GetCoinLeaguesUseCase,
    private val watchAdForCoins: WatchAdForCoinsUseCase,
    private val clock: AppClock,
    dispatchers: DispatcherProvider,
) : MviViewModel<GameZoneState, GameZoneIntent, GameZoneEffect>(GameZoneState(), dispatchers) {

    private var searchJob: Job? = null
    /** The game being searched for (its table opens when a match is found). */
    private var searchGameId = ""
    private var tickJob: Job? = null
    /** The last league list, to tell a locked row from an open one. */
    private var lastLeagues: CoinLeagues? = null

    override suspend fun handleIntent(intent: GameZoneIntent) {
        when (intent) {
            GameZoneIntent.Load -> load()
            is GameZoneIntent.FocusGame -> focus(intent.index)
            GameZoneIntent.ToggleViewMode -> setState {
                copy(
                    viewMode = if (viewMode == GamePickerViewMode.GRID) {
                        GamePickerViewMode.CAROUSEL
                    } else {
                        GamePickerViewMode.GRID
                    },
                )
            }
            is GameZoneIntent.PlayGame -> {
                focus(intent.index)
                openLeagues()
            }
            is GameZoneIntent.PickLeague -> pickLeague(intent.entryCoins)
            GameZoneIntent.CloseLeagues -> setState { copy(leagues = null) }
            GameZoneIntent.WatchAdForCoins -> watchAdThenReload()
            GameZoneIntent.PlayFree -> {
                setState { copy(coinsOffer = null, leagues = null) }
                startSearch(FREE_LEAGUE)
            }
            GameZoneIntent.CloseCoinsOffer -> setState { copy(coinsOffer = null) }
            GameZoneIntent.CancelSearch -> cancelSearch()
            GameZoneIntent.DismissPenalty -> setState { copy(penalty = null) }
            GameZoneIntent.WatchAd -> {
                setState { copy(penalty = penalty?.copy(watchingAd = true)) }
                if (clearPenaltyWithAd() is AppResult.Success) {
                    setState { copy(penalty = null) }
                } else {
                    setState { copy(penalty = penalty?.copy(watchingAd = false)) }
                }
            }
        }
    }

    private suspend fun load() {
        if (state.value.loaded) return
        val games = getGameCatalog()
        // Games show at once; the online count follows when the backend answers.
        setState {
            copy(
                games = games,
                focusedIndex = focusedIndex.coerceIn(0, (games.size - 1).coerceAtLeast(0)),
                loaded = true,
            )
        }
        val online = (getOnlinePlayerCount() as? AppResult.Success)?.data ?: return
        setState { copy(onlineLabel = Strings.gameZoneOnline(TextUtils.groupedThousands(online))) }
    }

    private fun focus(index: Int) {
        setState { copy(focusedIndex = index.coerceIn(0, (games.size - 1).coerceAtLeast(0))) }
    }

    /** Quick Play: a play block first (an ad lifts it), then the league picker with today's balance. */
    private suspend fun openLeagues() {
        if (searchJob?.isActive == true) return
        observePenalty().value?.takeIf { it.blockedUntil > clock.now() }?.let { block ->
            val minutes = ceil((block.blockedUntil - clock.now()).inWholeSeconds / SECONDS_PER_MINUTE.toDouble()).toInt()
            setState { copy(penalty = PenaltyUi(LudoStrings.ludoPenaltyTitle, LudoStrings.ludoPenaltyBody(block.points, minutes), watchingAd = false)) }
            return
        }
        setState { copy(searchMessage = null, leagues = LeaguePickerUi(balanceLabel = "", leagues = emptyList())) }
        reloadLeagues(message = null)
    }

    /** Fills the open picker; without the server's list, the free league is the only choice. */
    private suspend fun reloadLeagues(message: String?) {
        when (val result = getCoinLeagues()) {
            is AppResult.Success -> {
                lastLeagues = result.data
                setState { copy(leagues = leagues?.let { CoinsUiMapper.picker(result.data, message) }) }
            }
            is AppResult.Failure -> {
                setState { copy(leagues = null) }
                startSearch(FREE_LEAGUE)
            }
        }
    }

    private fun pickLeague(entryCoins: Int) {
        val league = lastLeagues?.leagues?.firstOrNull { it.entryCoins == entryCoins }
        if (league != null && !league.canEnter) {
            setState {
                copy(
                    coinsOffer = CoinsOfferUi(
                        title = CoinsStrings.notEnoughTitle,
                        body = CoinsStrings.notEnoughBody(league.name, TextUtils.groupedThousands(league.entryCoins)),
                        offerFree = true,
                    ),
                )
            }
            return
        }
        setState { copy(leagues = null) }
        startSearch(entryCoins)
    }

    /** Ad watched to the end → the server adds coins; the picker reopens with the new balance. */
    private suspend fun watchAdThenReload() {
        setState { copy(coinsOffer = coinsOffer?.copy(watchingAd = true, message = null)) }
        val result = watchAdForCoins.invoke()
        val message = CoinsUiMapper.adMessage(result)
        if (result is AppResult.Success) {
            setState { copy(coinsOffer = null, leagues = leagues ?: LeaguePickerUi(balanceLabel = "", leagues = emptyList())) }
            reloadLeagues(message)
        } else {
            setState { copy(coinsOffer = coinsOffer?.copy(watchingAd = false, message = message)) }
        }
    }

    private fun startSearch(entryCoins: Int) {
        val game = state.value.games.getOrNull(state.value.focusedIndex) ?: return
        if (searchJob?.isActive == true) return
        setState { copy(searchMessage = null) }
        searchGameId = game.id
        searchJob = scope.launch {
            findQuickPlayMatch(game.id, entryCoins).collect { status ->
                when (status) {
                    is MatchmakingStatus.Searching -> showSearching(status)
                    is MatchmakingStatus.Found -> showFound(status)
                    is MatchmakingStatus.Cancelled -> {
                        tickJob?.cancel()
                        if (status.reason == MatchmakingCancelReason.NOT_ENOUGH_COINS) {
                            setState { copy(search = null, coinsOffer = CoinsOfferUi(CoinsStrings.notEnoughTitle, CoinsStrings.notEnoughBodyGeneric, offerFree = true)) }
                        } else {
                            setState { copy(search = null, searchMessage = Strings.gameZoneNoMatch) }
                        }
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
        emitEffect(GameZoneEffect.OpenMatch(searchGameId, status.matchId))
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

    private companion object {
        const val SECONDS_PER_MINUTE = 60L
        const val FREE_LEAGUE = 0
    }
}
