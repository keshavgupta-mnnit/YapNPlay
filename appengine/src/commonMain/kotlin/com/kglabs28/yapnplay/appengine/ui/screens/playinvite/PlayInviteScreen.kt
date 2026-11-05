package com.kglabs28.yapnplay.appengine.ui.screens.playinvite

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
            }
        }
    }
    // Findable by phones around only while the app is in front (and Nearby is allowed).
    LifecycleStartEffect(viewModel) {
        send(PlayInviteIntent.Foreground(inFront = true, permitted = nearby.isGranted()))
        onStopOrDispose { send(PlayInviteIntent.Foreground(inFront = false, permitted = false)) }
    }

    val invite = state.invite?.takeIf { popupAllowed }
    BlockingOverlay(visible = invite != null) {
        if (invite != null) PlayInviteCard(
            invite = invite,
            onAccept = { send(PlayInviteIntent.Accept) },
            onDecline = { send(PlayInviteIntent.Decline) },
            enabled = !state.answering,
        )
    }
    val notice = state.notice
    BlockingOverlay(visible = notice != null, onDismiss = { send(PlayInviteIntent.DismissNotice) }) {
        if (notice != null) ModalCard(title = notice.title, subtitle = notice.body, onClose = { send(PlayInviteIntent.DismissNotice) })
    }
}
