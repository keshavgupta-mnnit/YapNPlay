package com.kglabs28.yapnplay.appengine.ui.screens.playhub

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
    dispatchers: DispatcherProvider,
) : MviViewModel<PlayHubState, PlayHubIntent, PlayHubEffect>(PlayHubState(), dispatchers) {

    override suspend fun handleIntent(intent: PlayHubIntent) {
        when (intent) {
            PlayHubIntent.Load -> load()
            PlayHubIntent.PlayWithRandoms -> if (state.value.online) emitEffect(PlayHubEffect.OpenRandoms)
            PlayHubIntent.PlayWithFriends -> if (state.value.online) startParty()
            PlayHubIntent.ScanNearby -> if (!state.value.creatingParty) emitEffect(PlayHubEffect.RequestNearbyPermission)
            is PlayHubIntent.NearbyPermission -> if (intent.granted) {
                startNearbyParty()
            } else {
                setState { copy(notice = PlayHubNotice(NearbyStrings.permissionTitle, NearbyStrings.permissionBody)) }
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
    }

    /** Nearby: this phone hosts the party; its lobby opens and phones around can be added. */
    private suspend fun startNearbyParty() {
        if (state.value.creatingParty) return
        setState { copy(creatingParty = true) }
        val roomId = hostNearbyParty()
        setState { copy(creatingParty = false) }
        if (roomId != null) {
            emitEffect(PlayHubEffect.OpenRoom(roomId))
        } else {
            setState { copy(notice = PlayHubNotice(NearbyStrings.problemRadioTitle, NearbyStrings.problemRadioBody)) }
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
