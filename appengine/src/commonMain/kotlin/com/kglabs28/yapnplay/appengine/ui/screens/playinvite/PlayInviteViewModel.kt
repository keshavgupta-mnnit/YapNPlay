package com.kglabs28.yapnplay.appengine.ui.screens.playinvite

import com.kglabs28.yapnplay.appengine.features.nearby.presentation.NearbyCheck
import com.kglabs28.yapnplay.appengine.features.nearby.presentation.NearbyReadyChecklist
import com.kglabs28.yapnplay.appengine.features.connection.usecase.ObserveBackendReachableUseCase
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.core.ui.NearbyStrings
import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyProblem
import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyRole
import com.kglabs28.yapnplay.appengine.features.nearby.usecase.ClearNearbyProblemUseCase
import com.kglabs28.yapnplay.appengine.features.nearby.usecase.ObserveNearbyUseCase
import com.kglabs28.yapnplay.appengine.features.nearby.usecase.SetNearbyFindableUseCase
import com.kglabs28.yapnplay.appengine.features.playinvites.domain.PlayInviteOffer
import com.kglabs28.yapnplay.appengine.features.playinvites.usecase.AnswerPlayInviteUseCase
import com.kglabs28.yapnplay.appengine.features.playinvites.usecase.ObservePlayInviteUseCase
import kotlinx.coroutines.launch

/**
 * Play-now invites wherever the user is: asks, answers, and opens the
 * party's lobby once in. Keeps this phone findable nearby while the app is
 * in front, and reports when a Nearby party ended under the user.
 */
class PlayInviteViewModel(
    private val observeInvite: ObservePlayInviteUseCase,
    private val answerInvite: AnswerPlayInviteUseCase,
    private val observeNearby: ObserveNearbyUseCase,
    private val setFindable: SetNearbyFindableUseCase,
    private val clearProblem: ClearNearbyProblemUseCase,
    private val observeReachable: ObserveBackendReachableUseCase,
    private val checklist: NearbyReadyChecklist,
    dispatchers: DispatcherProvider,
) : MviViewModel<PlayInviteState, PlayInviteIntent, PlayInviteEffect>(PlayInviteState(), dispatchers) {

    private var loaded = false
    private var offer: PlayInviteOffer? = null
    /** Accepted a nearby invite: its lobby opens once the host lets this phone in. */
    private var awaitingNearby = false
    private var lastRole = NearbyRole.IDLE
    private var inFront = false
    private var permitted = false
    /** The Nearby permission was asked this run (once is enough). */
    private var askedNearby = false

    override suspend fun handleIntent(intent: PlayInviteIntent) {
        when (intent) {
            PlayInviteIntent.Load -> load()
            is PlayInviteIntent.Accept -> accept(intent.permitted)
            is PlayInviteIntent.RecheckReady -> if (state.value.ready != null) setState { copy(ready = checklist.build(intent.permitted)) }
            is PlayInviteIntent.FixReady -> when (val check = intent.check) {
                NearbyCheck.Permission -> emitEffect(PlayInviteEffect.RequestNearbyPermission)
                is NearbyCheck.Radio -> checklist.openSettings(check.radio)
                // Only hosting adds this row; joining has nothing to start.
                NearbyCheck.Start -> Unit
            }
            PlayInviteIntent.ContinueReady -> if (state.value.ready?.allReady == true) {
                setState { copy(ready = null) }
                answer(accept = true)
            }
            PlayInviteIntent.CloseReady -> setState { copy(ready = null) }
            PlayInviteIntent.Decline -> answer(accept = false)
            PlayInviteIntent.DismissNotice -> {
                clearProblem()
                setState { copy(notice = null) }
            }
            is PlayInviteIntent.Foreground -> {
                inFront = intent.inFront
                permitted = intent.permitted
                setFindable(inFront && permitted)
            }
            is PlayInviteIntent.NearbyPermission -> {
                permitted = intent.granted
                setFindable(inFront && permitted)
                // A checklist row asked: refused for good, so the app's settings; else tick it.
                if (state.value.ready != null) {
                    if (intent.granted) setState { copy(ready = checklist.build(true)) } else emitEffect(PlayInviteEffect.OpenAppSettings)
                }
            }
        }
    }

    private fun load() {
        if (loaded) return
        loaded = true
        // Offline, a phone can only play nearby: ask once for the permission that lets others find it.
        scope.launch {
            observeReachable().collect { reachable ->
                if (!reachable && !permitted && !askedNearby) {
                    askedNearby = true
                    emitEffect(PlayInviteEffect.RequestNearbyPermission)
                }
            }
        }
        scope.launch {
            observeInvite().collect { next ->
                offer = next
                setState { copy(invite = next?.toUi(), answering = false) }
            }
        }
        scope.launch {
            observeNearby().collect { s ->
                val roomId = s.roomId
                if (awaitingNearby && s.role == NearbyRole.GUEST && roomId != null) {
                    awaitingNearby = false
                    emitEffect(PlayInviteEffect.OpenRoom(roomId))
                }
                // Out of a party with a problem (the host left, out of range): say so; a guest goes back to the dashboard.
                s.problem?.let { problem -> setState { copy(notice = problem.toNotice()) } }
                if (lastRole == NearbyRole.GUEST && s.role == NearbyRole.IDLE) emitEffect(PlayInviteEffect.PartyEnded)
                if (s.role == NearbyRole.IDLE) awaitingNearby = false
                // Out of a party again: phones around can find this one once more.
                if (lastRole != NearbyRole.IDLE && s.role == NearbyRole.IDLE) setFindable(inFront && permitted)
                lastRole = s.role
            }
        }
    }

    /** JOIN: a nearby party needs the permission and every switch ready first (the checklist); online joins at once. */
    private suspend fun accept(permitted: Boolean) {
        if (offer is PlayInviteOffer.Nearby) {
            val ready = checklist.build(permitted)
            if (!ready.allReady) {
                setState { copy(ready = ready) }
                return
            }
        }
        answer(accept = true)
    }

    private suspend fun answer(accept: Boolean) {
        val current = offer ?: return
        if (state.value.answering) return
        setState { copy(answering = true) }
        when (val result = answerInvite(current, accept)) {
            is AppResult.Success -> {
                val roomId = result.data
                if (accept && current is PlayInviteOffer.Nearby) awaitingNearby = true
                if (accept && roomId != null) emitEffect(PlayInviteEffect.OpenRoom(roomId))
            }
            is AppResult.Failure -> setState { copy(answering = false, notice = PlayInviteNotice(NearbyStrings.inviteTitle, NearbyStrings.inviteFailed)) }
        }
    }

    private fun PlayInviteOffer.toUi(): PlayInviteUi = PlayInviteUi(
        title = NearbyStrings.inviteTitle,
        body = when (this) {
            is PlayInviteOffer.Online -> NearbyStrings.inviteBody(fromName, roomName)
            is PlayInviteOffer.Nearby -> NearbyStrings.inviteBodyNearby(fromName)
        },
        avatarId = fromAvatarId,
    )

    private fun NearbyProblem.toNotice(): PlayInviteNotice = when (this) {
        NearbyProblem.JOIN_FAILED -> PlayInviteNotice(NearbyStrings.problemJoinTitle, NearbyStrings.problemJoinBody)
        NearbyProblem.HOST_LEFT -> PlayInviteNotice(NearbyStrings.problemHostLeftTitle, NearbyStrings.problemHostLeftBody)
        NearbyProblem.VERSION_MISMATCH -> PlayInviteNotice(NearbyStrings.problemVersionTitle, NearbyStrings.problemVersionBody)
    }
}
