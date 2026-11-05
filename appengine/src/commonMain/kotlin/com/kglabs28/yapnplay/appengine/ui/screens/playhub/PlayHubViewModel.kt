package com.kglabs28.yapnplay.appengine.ui.screens.playhub

import com.kglabs28.yapnplay.appengine.core.common.TextUtils
import com.kglabs28.yapnplay.appengine.features.matchmaking.usecase.GetOnlinePlayerCountUseCase
import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyHostResult
import com.kglabs28.yapnplay.appengine.features.nearby.presentation.NearbyCheck
import com.kglabs28.yapnplay.appengine.features.nearby.presentation.NearbyReadyChecklist
import com.kglabs28.yapnplay.appengine.core.ui.NearbyStrings
import com.kglabs28.yapnplay.appengine.features.connection.usecase.ObserveBackendReachableUseCase
import com.kglabs28.yapnplay.appengine.features.nearby.usecase.HostNearbyPartyUseCase
import kotlinx.coroutines.launch
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.CreateTempPlayRoomUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.GetFriendsUseCase

/**
 * Quick Play hub. Randoms opens the game catalog; Friends sets up a
 * temporary room on the server and opens its lobby; Nearby hosts the same
 * kind of party on this phone (no internet needed) and opens its lobby.
 * With no internet only Nearby works.
 */
class PlayHubViewModel(
    private val getFriends: GetFriendsUseCase,
    private val createTempRoom: CreateTempPlayRoomUseCase,
    private val hostNearbyParty: HostNearbyPartyUseCase,
    private val observeReachable: ObserveBackendReachableUseCase,
    private val checklist: NearbyReadyChecklist,
    private val getOnlineCount: GetOnlinePlayerCountUseCase,
    dispatchers: DispatcherProvider,
) : MviViewModel<PlayHubState, PlayHubIntent, PlayHubEffect>(PlayHubState(), dispatchers) {

    override suspend fun handleIntent(intent: PlayHubIntent) {
        when (intent) {
            PlayHubIntent.Load -> load()
            PlayHubIntent.PlayWithRandoms -> if (state.value.online) emitEffect(PlayHubEffect.OpenRandoms)
            PlayHubIntent.PlayWithFriends -> if (state.value.online) startParty()
            is PlayHubIntent.ScanNearby -> if (!state.value.creatingParty) {
                permitted = intent.permitted
                startFailure = null
                val ready = checklist.build(permitted)
                if (ready.allReady) startNearbyParty() else setState { copy(ready = ready) }
            }
            is PlayHubIntent.NearbyPermission -> if (intent.granted) recheck(true) else emitEffect(PlayHubEffect.OpenAppSettings)
            is PlayHubIntent.RecheckReady -> recheck(intent.permitted)
            is PlayHubIntent.FixReady -> when (val check = intent.check) {
                NearbyCheck.Permission -> emitEffect(PlayHubEffect.RequestNearbyPermission)
                is NearbyCheck.Radio -> checklist.openSettings(check.radio)
                NearbyCheck.Start -> {
                    startFailure = null
                    startNearbyParty()
                }
            }
            PlayHubIntent.ContinueReady -> if (state.value.ready?.allReady == true) startNearbyParty()
            PlayHubIntent.CloseReady -> {
                startFailure = null
                setState { copy(ready = null) }
            }
            PlayHubIntent.DismissNotice -> setState { copy(notice = null) }
        }
    }

    private var watching = false

    private suspend fun load() {
        if (!watching) {
            watching = true
            scope.launch { observeReachable().collect { setState { copy(online = it) } } }
        }
        loadFriends()
        loadPlayersOnline()
    }

    /** Everyone online now, on the Play with Randoms card. */
    private suspend fun loadPlayersOnline() {
        val online = (getOnlineCount() as? AppResult.Success)?.data ?: return
        setState { copy(playersOnlineLabel = Strings.playHubPlayersOnline(TextUtils.groupedThousands(online))) }
    }

    /** The Nearby permission, as last known. */
    private var permitted = false
    /** Everything was on but Nearby wouldn't start: its reason ("" when none), shown as a row until tried again. */
    private var startFailure: String? = null

    /** The open checklist, checked again (after the permission prompt, or back from Settings). */
    private fun recheck(permitted: Boolean) {
        this.permitted = permitted
        if (state.value.ready == null) return
        setState { copy(ready = checklist.build(permitted, startFailure)) }
    }

    /**
     * Nearby: only once the checklist is all ticked does this phone host, and
     * its lobby opens only when the radios really started looking for phones.
     * If they didn't, the checklist says so (a "Start Nearby" row to try again).
     */
    private suspend fun startNearbyParty() {
        if (state.value.creatingParty) return
        setState { copy(creatingParty = true) }
        val result = hostNearbyParty()
        setState { copy(creatingParty = false) }
        when (result) {
            is NearbyHostResult.Hosted -> {
                startFailure = null
                setState { copy(ready = null) }
                emitEffect(PlayHubEffect.OpenRoom(result.roomId))
            }
            is NearbyHostResult.Failed -> {
                startFailure = result.reason.orEmpty()
                setState { copy(ready = checklist.build(permitted, startFailure)) }
            }
        }
    }

    /** The online friends' count and a few of their faces. */
    private suspend fun loadFriends() {
        val online = (getFriends() as? AppResult.Success)?.data.orEmpty().filter { it.online }
        setState {
            copy(
                friendsOnlineLabel = Strings.playHubFriendsOnline(online.size),
                friendFaces = online.take(AppConstants.PLAY_HUB_MAX_FACES).map { it.avatarId },
            )
        }
    }

    private suspend fun startParty() {
        if (state.value.creatingParty) return
        setState { copy(creatingParty = true) }
        when (val result = createTempRoom()) {
            is AppResult.Success -> {
                setState { copy(creatingParty = false) }
                emitEffect(PlayHubEffect.OpenRoom(result.data.roomId))
            }
            is AppResult.Failure -> setState {
                copy(creatingParty = false, notice = PlayHubNotice(Strings.playHubPartyFailedTitle, Strings.playHubPartyFailedBody))
            }
        }
    }
}
