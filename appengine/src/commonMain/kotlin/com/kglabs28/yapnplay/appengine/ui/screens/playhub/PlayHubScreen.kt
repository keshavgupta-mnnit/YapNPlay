package com.kglabs28.yapnplay.appengine.ui.screens.playhub

import kotlinx.coroutines.delay
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import androidx.lifecycle.compose.LifecycleResumeEffect
import com.kglabs28.yapnplay.appengine.ui.components.nearby.NearbyReadyDialog
import androidx.compose.material.icons.rounded.CloudOff
import com.kglabs28.yapnplay.appengine.core.ui.NearbyStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppPermission
import com.kglabs28.yapnplay.appengine.ui.components.common.rememberPermissionRequester
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Public
import androidx.compose.material.icons.rounded.WifiOff
import androidx.compose.material.icons.rounded.WifiTethering
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.playhub.FriendFacesStack
import com.kglabs28.yapnplay.appengine.ui.components.playhub.PlayModeCard
import com.kglabs28.yapnplay.appengine.ui.components.playhub.PlayModeStatusPill
import com.kglabs28.yapnplay.appengine.ui.placeholders.BuiltInImage
import com.kglabs28.yapnplay.appengine.ui.placeholders.PlayHubArtwork

/**
 * The Games tab's first page: Play With Randoms (the game catalog), Play
 * With Friends (a temporary room) and Offline Nearby, side by side.
 */
@Composable
fun PlayHubScreen(
    onOpenRandoms: () -> Unit,
    onOpenRoom: (String) -> Unit,
    modifier: Modifier = Modifier,
    viewModel: PlayHubViewModel? = null,
) {
    val state = viewModel?.state?.collectAsState()?.value ?: PlayHubState()
    val colors = YapNPlayTheme.colors
    val send: (PlayHubIntent) -> Unit = { viewModel?.onIntent(it) }
    val nearbyPermission = rememberPermissionRequester(AppPermission.NEARBY)
    LaunchedEffect(viewModel) {
        viewModel?.onIntent(PlayHubIntent.Load)
        viewModel?.effects?.collect { effect ->
            when (effect) {
                PlayHubEffect.OpenRandoms -> onOpenRandoms()
                is PlayHubEffect.OpenRoom -> onOpenRoom(effect.roomId)
                PlayHubEffect.RequestNearbyPermission -> nearbyPermission.request { send(PlayHubIntent.NearbyPermission(it)) }
                PlayHubEffect.OpenAppSettings -> nearbyPermission.openSettings()
            }
        }
    }
    // Back from Settings (or the permission prompt): the open checklist checks again.
    LifecycleResumeEffect(viewModel) {
        send(PlayHubIntent.RecheckReady(nearbyPermission.isGranted()))
        onPauseOrDispose { }
    }

    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(top = Spacing.Xs, bottom = Spacing.Xs),
        horizontalArrangement = Arrangement.spacedBy(Spacing.Md),
    ) {
        PlayModeCard(
            icon = Icons.Rounded.Language,
            title = Strings.playHubRandomsTitle,
            titleAccent = Strings.playHubRandomsAccent,
            titleColor = colors.PlayRandomsAccent,
            accent = colors.PlayRandomsAccent,
            description = Strings.playHubRandomsBody,
            buttonText = Strings.playHubRandomsButton,
            buttonColor = colors.PlayRandomsCta,
            buttonRim = colors.PlayRandomsCtaRim,
            onClick = { send(PlayHubIntent.PlayWithRandoms) },
            enabled = state.online,
            status = {
                PlayModeStatusPill(
                    icon = if (state.online) Icons.Rounded.Public else Icons.Rounded.CloudOff,
                    text = if (state.online) Strings.playHubRandomsStatus else NearbyStrings.needsInternet,
                    tint = colors.PlayRandomsAccent,
                )
            },
            art = { PlayHubArtwork(BuiltInImage.PLAY_HUB_RANDOMS, it) },
            modifier = Modifier.weight(1f),
        )
        PlayModeCard(
            icon = Icons.Rounded.Groups,
            title = Strings.playHubFriendsTitle,
            titleAccent = Strings.playHubFriendsAccent,
            titleColor = colors.TextPrimary,
            accent = colors.PlayFriendsAccent,
            description = Strings.playHubFriendsBody,
            buttonText = Strings.playHubFriendsButton,
            buttonColor = colors.PlayFriendsCta,
            buttonRim = colors.PlayFriendsCtaRim,
            onClick = { send(PlayHubIntent.PlayWithFriends) },
            enabled = state.online && !state.creatingParty,
            status = {
                if (state.online) {
                    PlayModeStatusPill(icon = Icons.Rounded.People, text = state.friendsOnlineLabel, tint = colors.LiveGreen) {
                        FriendFacesStack(avatarIds = state.friendFaces)
                    }
                } else {
                    PlayModeStatusPill(icon = Icons.Rounded.CloudOff, text = NearbyStrings.needsInternet, tint = colors.PlayFriendsAccent)
                }
            },
            art = { PlayHubArtwork(BuiltInImage.PLAY_HUB_FRIENDS, it) },
            modifier = Modifier.weight(1f),
        )
        PlayModeCard(
            icon = Icons.Rounded.WifiTethering,
            title = Strings.playHubNearbyTitle,
            titleAccent = Strings.playHubNearbyAccent,
            titleColor = colors.PlayNearbyAccent,
            accent = colors.PlayNearbyAccent,
            description = Strings.playHubNearbyBody,
            buttonText = Strings.playHubNearbyButton,
            buttonColor = colors.PlayNearbyCta,
            buttonRim = colors.PlayNearbyCtaRim,
            onClick = { send(PlayHubIntent.ScanNearby(nearbyPermission.isGranted())) },
            enabled = !state.creatingParty,
            status = { PlayModeStatusPill(icon = Icons.Rounded.WifiOff, text = Strings.playHubNearbyStatus, tint = colors.PlayNearbyAccent) },
            art = { PlayHubArtwork(BuiltInImage.PLAY_HUB_NEARBY, it) },
            modifier = Modifier.weight(1f),
        )
    }

    val notice = state.notice
    BlockingOverlay(visible = notice != null, onDismiss = { send(PlayHubIntent.DismissNotice) }) {
        if (notice != null) ModalCard(
            title = notice.title,
            subtitle = notice.body,
            onClose = { send(PlayHubIntent.DismissNotice) },
        )
    }
    val ready = state.ready
    // While it's open the checklist checks again every second: a switch flipped from the quick settings counts at once.
    LaunchedEffect(ready != null) {
        while (ready != null) {
            delay(AppConstants.NEARBY_READY_RECHECK_MS)
            send(PlayHubIntent.RecheckReady(nearbyPermission.isGranted()))
        }
    }
    BlockingOverlay(visible = ready != null, onDismiss = { send(PlayHubIntent.CloseReady) }) {
        if (ready != null) NearbyReadyDialog(
            ready = ready,
            onFix = { send(PlayHubIntent.FixReady(it)) },
            onContinue = { send(PlayHubIntent.ContinueReady) },
            onClose = { send(PlayHubIntent.CloseReady) },
        )
    }
}
