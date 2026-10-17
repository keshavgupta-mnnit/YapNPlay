package com.kglabs28.yapnplay.appengine.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.WindowInsetsSides
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.only
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.windowInsetsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.navigation.AppNavTab
import com.kglabs28.yapnplay.appengine.ui.components.common.AppTopBar
import com.kglabs28.yapnplay.appengine.ui.components.home.EnterGameZoneCard
import com.kglabs28.yapnplay.appengine.ui.components.home.PlayWithFriendsCard
import com.kglabs28.yapnplay.appengine.ui.placeholders.CozyHomeBackground
import com.kglabs28.yapnplay.appengine.ui.screens.gamezone.GameZoneScreen
import com.kglabs28.yapnplay.appengine.ui.screens.gamezone.GameZoneViewModel
import com.kglabs28.yapnplay.appengine.ui.screens.playrooms.PlayRoomsScreen
import com.kglabs28.yapnplay.appengine.ui.screens.playrooms.PlayRoomsViewModel

/**
 * Dashboard scaffold: shared top bar plus Home, Games and Rooms tabs.
 * The home tab is two cards switching to the other tabs; anything
 * else navigates outside this scaffold.
 */
@Composable
fun MainScreen(
    onOpenMatch: (gameId: String, matchId: String) -> Unit,
    onOpenRoom: (String) -> Unit,
    onCreateRoom: () -> Unit,
    modifier: Modifier = Modifier,
    pendingInviteCode: String? = null,
    onInviteConsumed: () -> Unit = {},
    viewModel: MainViewModel? = null,
    gameZoneViewModel: GameZoneViewModel? = null,
    playRoomsViewModel: PlayRoomsViewModel? = null,
) {
    val state = viewModel?.state?.collectAsState()?.value ?: MainState()
    LaunchedEffect(viewModel) {
        viewModel?.onIntent(MainIntent.LoadProfile)
    }
    LaunchedEffect(viewModel) {
        viewModel?.effects?.collect { effect ->
            when (effect) {
                is MainEffect.OpenRoom -> onOpenRoom(effect.roomId)
                MainEffect.OpenSettingsDialog -> Unit
            }
        }
    }
    // An invite link was opened: show the Rooms tab, which asks to join.
    LaunchedEffect(pendingInviteCode) {
        if (pendingInviteCode != null) viewModel?.onIntent(MainIntent.SelectTab(AppNavTab.ROOMS))
    }

    CozyHomeBackground(
        modifier = modifier,
        frosted = state.selectedTab.frostedBackground,
    ) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(bottom = Spacing.Sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
        ) {
            // Edge to edge: the bar owns its own horizontal padding.
            AppTopBar(
                online = state.online,
                profileLoaded = state.profileLoaded,
                coinBalance = state.coinBalance,
                diamondBalance = state.diamondBalance,
                selectedTab = state.selectedTab,
                onSelectTab = { tab ->
                    viewModel?.onIntent(MainIntent.SelectTab(tab))
                },
                onOpenSettings = {
                    viewModel?.onIntent(MainIntent.OpenSettings)
                },
                onToggleNotifications = {
                    viewModel?.onIntent(MainIntent.ToggleNotifications)
                },
                notifications = state.notifications,
                unreadNotifications = state.unreadNotifications,
                notificationsOpen = state.notificationsOpen,
                onDismissNotifications = {
                    viewModel?.onIntent(MainIntent.DismissNotifications)
                },
                onNotificationClick = { roomId ->
                    viewModel?.onIntent(MainIntent.NotificationClicked(roomId))
                },
            )

            // Shared tab frame: every tab gets the full width under the bar,
            // clear of the camera cutout, with one slim margin on the sides
            // and bottom over the home background.
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .windowInsetsPadding(WindowInsets.displayCutout.only(WindowInsetsSides.Horizontal))
                    .padding(horizontal = Spacing.Md),
            ) {
                when (state.selectedTab) {
                    AppNavTab.HOME -> Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
                    ) {
                        PlayWithFriendsCard(
                            onPlayWithFriends = {
                                viewModel?.onIntent(MainIntent.SelectTab(AppNavTab.ROOMS))
                            },
                            modifier = Modifier.weight(1f),
                        )
                        EnterGameZoneCard(
                            onEnterGameZone = {
                                viewModel?.onIntent(MainIntent.SelectTab(AppNavTab.GAMES))
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    AppNavTab.GAMES -> GameZoneScreen(
                        onOpenMatch = onOpenMatch,
                        viewModel = gameZoneViewModel,
                    )
                    AppNavTab.ROOMS -> PlayRoomsScreen(
                        onOpenRoom = onOpenRoom,
                        onCreateRoom = onCreateRoom,
                        viewModel = playRoomsViewModel,
                        inviteCode = pendingInviteCode,
                        onInviteConsumed = onInviteConsumed,
                    )
                }
            }
        }
    }
}
