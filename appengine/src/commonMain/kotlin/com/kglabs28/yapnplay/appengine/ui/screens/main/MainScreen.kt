package com.kglabs28.yapnplay.appengine.ui.screens.main

import com.kglabs28.yapnplay.appengine.ui.components.moderation.AppSettingsDialog
import com.kglabs28.yapnplay.appengine.ui.components.coins.CoinsOfferCard
import com.kglabs28.yapnplay.appengine.ui.components.common.DailyBonusDialog
import androidx.lifecycle.compose.LifecycleStartEffect
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import androidx.compose.ui.Alignment
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
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.common.RejoinMatchCard
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
import com.kglabs28.yapnplay.appengine.ui.screens.playrooms.PlayRoomsIntent
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
    onOpenProfile: () -> Unit = {},
    onOpenHistory: () -> Unit = {},
    pendingInviteCode: String? = null,
    onInviteConsumed: () -> Unit = {},
    pendingCreateGroupWith: String? = null,
    onCreateGroupConsumed: () -> Unit = {},
    viewModel: MainViewModel? = null,
    gameZoneViewModel: GameZoneViewModel? = null,
    playRoomsViewModel: PlayRoomsViewModel? = null,
) {
    val state = viewModel?.state?.collectAsState()?.value ?: MainState()
    // Every time home comes back (after a match, from the background): fresh coins and level.
    LifecycleStartEffect(viewModel) {
        viewModel?.onIntent(MainIntent.LoadProfile)
        onStopOrDispose { }
    }
    LaunchedEffect(viewModel) {
        viewModel?.effects?.collect { effect ->
            when (effect) {
                is MainEffect.OpenRoom -> onOpenRoom(effect.roomId)
                is MainEffect.OpenMatch -> onOpenMatch(effect.gameId, effect.matchId)
                MainEffect.OpenHistory -> onOpenHistory()
            }
        }
    }
    state.rejoin?.let { rejoin ->
        BlockingOverlay(visible = true, onDismiss = { viewModel?.onIntent(MainIntent.DismissRejoin) }) {
            RejoinMatchCard(
                title = rejoin.title,
                onRejoin = { viewModel?.onIntent(MainIntent.Rejoin) },
                onDismiss = { viewModel?.onIntent(MainIntent.DismissRejoin) },
            )
        }
    }
    // An invite link was opened: show the Rooms tab, which asks to join.
    LaunchedEffect(pendingInviteCode) {
        if (pendingInviteCode != null) viewModel?.onIntent(MainIntent.SelectTab(AppNavTab.ROOMS))
    }
    // "New Group" on a friend's card: show the Rooms tab, which opens Create New Group.
    LaunchedEffect(pendingCreateGroupWith) {
        if (pendingCreateGroupWith != null) viewModel?.onIntent(MainIntent.SelectTab(AppNavTab.ROOMS))
    }

    val bonus = state.dailyBonus
    val coinsOffer = state.coinsOffer
    BlockingOverlay(visible = coinsOffer != null, onDismiss = { viewModel?.onIntent(MainIntent.CloseGetCoins) }) {
        if (coinsOffer != null) CoinsOfferCard(
            offer = coinsOffer,
            onWatchAd = { viewModel?.onIntent(MainIntent.WatchAdForCoins) },
            onPlayFree = {},
            onClose = { viewModel?.onIntent(MainIntent.CloseGetCoins) },
        )
    }

    val settings = state.settings
    AppSettingsDialog(
        settings = settings,
        onToggleEffects = { viewModel?.onIntent(MainIntent.ToggleEffects) },
        onToggleMusic = { viewModel?.onIntent(MainIntent.ToggleMusic) },
        onHistory = { viewModel?.onIntent(MainIntent.OpenHistory) },
        onClose = { viewModel?.onIntent(MainIntent.CloseSettings) },
    )

    BlockingOverlay(visible = bonus != null) {
        if (bonus != null) DailyBonusDialog(amount = bonus, onCollect = { viewModel?.onIntent(MainIntent.CollectDailyBonus) })
    }

    CozyHomeBackground(
        modifier = modifier,
        frosted = state.selectedTab.frostedBackground,
    ) {
      Box(modifier = Modifier.fillMaxSize()) {
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
                avatarId = state.avatarId,
                onOpenProfile = onOpenProfile,
                coinBalance = state.coinBalance,
                level = state.level,
                levelProgress = state.levelProgress,
                xpLabel = state.xpLabel,
                onAddCoins = { viewModel?.onIntent(MainIntent.OpenGetCoins) },
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
                            // The whole card opens Create New Group (on the Rooms tab).
                            onPlayWithFriends = {
                                viewModel?.onIntent(MainIntent.SelectTab(AppNavTab.ROOMS))
                                playRoomsViewModel?.onIntent(PlayRoomsIntent.CreateRoom)
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
                        createGroupWith = pendingCreateGroupWith,
                        onCreateGroupConsumed = onCreateGroupConsumed,
                    )
                }
            }
        }
      }
    }
}