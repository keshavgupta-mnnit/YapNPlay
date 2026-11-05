package com.kglabs28.yapnplay.appengine.ui.screens.playinvite

import kotlinx.coroutines.delay
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.kglabs28.yapnplay.appengine.ui.components.nearby.NearbyReadyDialog
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.lifecycle.compose.LifecycleStartEffect
import com.kglabs28.yapnplay.appengine.ui.components.common.AppPermission
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.rememberPermissionRequester
import com.kglabs28.yapnplay.appengine.ui.components.playinvite.PlayInviteCard

/**
 * Sits over every screen. Shows a play-now invite when [popupAllowed]
 * (not in a lobby or at a table: it waits until the user is out), and
 * Nearby play's messages anywhere.
 */
@Composable
fun PlayInviteScreen(
    popupAllowed: Boolean,
    onOpenRoom: (String) -> Unit,
    onPartyEnded: () -> Unit,
    viewModel: PlayInviteViewModel? = null,
) {
    val state = viewModel?.state?.collectAsState()?.value ?: PlayInviteState()
    val send: (PlayInviteIntent) -> Unit = { viewModel?.onIntent(it) }
    val nearby = rememberPermissionRequester(AppPermission.NEARBY)
    LaunchedEffect(viewModel) {
        viewModel?.onIntent(PlayInviteIntent.Load)
        viewModel?.effects?.collect { effect ->
            when (effect) {
                is PlayInviteEffect.OpenRoom -> onOpenRoom(effect.roomId)
                PlayInviteEffect.PartyEnded -> onPartyEnded()
                PlayInviteEffect.RequestNearbyPermission -> nearby.request { send(PlayInviteIntent.NearbyPermission(it)) }
                PlayInviteEffect.OpenAppSettings -> nearby.openSettings()
            }
        }
    }
    // Findable by phones around only while the app is in front (and Nearby is allowed).
    LifecycleStartEffect(viewModel) {
        send(PlayInviteIntent.Foreground(inFront = true, permitted = nearby.isGranted()))
        onStopOrDispose { send(PlayInviteIntent.Foreground(inFront = false, permitted = false)) }
    }
    // Back from Settings: the open checklist checks again.
    LifecycleResumeEffect(viewModel) {
        send(PlayInviteIntent.RecheckReady(nearby.isGranted()))
        onPauseOrDispose { }
    }

    val invite = state.invite?.takeIf { popupAllowed && state.ready == null }
    BlockingOverlay(visible = invite != null) {
        if (invite != null) PlayInviteCard(
            invite = invite,
            onAccept = { send(PlayInviteIntent.Accept(nearby.isGranted())) },
            onDecline = { send(PlayInviteIntent.Decline) },
            enabled = !state.answering,
        )
    }
    val ready = state.ready
    // While it's open the checklist checks again every second: a switch flipped from the quick settings counts at once.
    LaunchedEffect(ready != null) {
        while (ready != null) {
            delay(AppConstants.NEARBY_READY_RECHECK_MS)
            send(PlayInviteIntent.RecheckReady(nearby.isGranted()))
        }
    }
    BlockingOverlay(visible = ready != null, onDismiss = { send(PlayInviteIntent.CloseReady) }) {
        if (ready != null) NearbyReadyDialog(
            ready = ready,
            onFix = { send(PlayInviteIntent.FixReady(it)) },
            onContinue = { send(PlayInviteIntent.ContinueReady) },
            onClose = { send(PlayInviteIntent.CloseReady) },
        )
    }
    val notice = state.notice
    BlockingOverlay(visible = notice != null, onDismiss = { send(PlayInviteIntent.DismissNotice) }) {
        if (notice != null) ModalCard(title = notice.title, subtitle = notice.body, onClose = { send(PlayInviteIntent.DismissNotice) })
    }
}
