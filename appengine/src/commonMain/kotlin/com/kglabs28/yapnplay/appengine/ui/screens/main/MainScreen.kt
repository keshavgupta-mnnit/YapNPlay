package com.kglabs28.yapnplay.appengine.ui.screens.main

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.navigation.AppNavTab
import com.kglabs28.yapnplay.appengine.ui.components.common.AppTopBar
import com.kglabs28.yapnplay.appengine.ui.components.home.EnterGameZoneCard
import com.kglabs28.yapnplay.appengine.ui.components.home.PlayWithFriendsCard
import com.kglabs28.yapnplay.appengine.ui.placeholders.CozyHomeBackground
import com.kglabs28.yapnplay.appengine.ui.screens.games.GamesScreen
import com.kglabs28.yapnplay.appengine.ui.screens.games.GamesViewModel
import com.kglabs28.yapnplay.appengine.ui.screens.rooms.RoomsScreen
import com.kglabs28.yapnplay.appengine.ui.screens.rooms.RoomsViewModel

/**
 * Dashboard scaffold: shared top bar plus Home, Games and Rooms tabs.
 * The home tab is two cards switching to the other tabs; anything
 * else navigates outside this scaffold.
 */
@Composable
fun MainScreen(
    onOpenMatch: () -> Unit,
    onOpenRoom: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: MainViewModel? = null,
    gamesViewModel: GamesViewModel? = null,
    roomsViewModel: RoomsViewModel? = null,
) {
    val state = viewModel?.state?.collectAsState()?.value ?: MainState()
    LaunchedEffect(viewModel) {
        viewModel?.onIntent(MainIntent.LoadProfile)
    }

    CozyHomeBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(start = Spacing.Xs, end = Spacing.Xs, bottom = Spacing.Xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
        ) {
            AppTopBar(
                userName = state.userName,
                level = state.level,
                coinBalance = state.coinBalance,
                diamondBalance = state.diamondBalance,
                selectedTab = state.selectedTab,
                onSelectTab = { tab ->
                    viewModel?.onIntent(MainIntent.SelectTab(tab))
                },
                onOpenSettings = {
                    viewModel?.onIntent(MainIntent.OpenSettings)
                },
                onOpenNotifications = {
                    viewModel?.onIntent(MainIntent.OpenNotifications)
                },
            )

            // Constant tab frame: 80% width with 10% left/right/bottom
            // margins over the home background, same for every tab.
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                contentAlignment = Alignment.TopCenter,
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth(AppConstants.TAB_CONTENT_WIDTH_FRACTION)
                        .fillMaxHeight(AppConstants.TAB_CONTENT_HEIGHT_FRACTION),
                ) {
                    when (state.selectedTab) {
                    AppNavTab.HOME -> Row(
                        modifier = Modifier.fillMaxSize(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
                    ) {
                        EnterGameZoneCard(
                            onEnterGameZone = {
                                viewModel?.onIntent(MainIntent.SelectTab(AppNavTab.GAMES))
                            },
                            modifier = Modifier.weight(1f),
                        )
                        PlayWithFriendsCard(
                            onPlayWithFriends = {
                                viewModel?.onIntent(MainIntent.SelectTab(AppNavTab.ROOMS))
                            },
                            modifier = Modifier.weight(1f),
                        )
                    }
                    AppNavTab.GAMES -> GamesScreen(
                        onPlayNow = onOpenMatch,
                        viewModel = gamesViewModel,
                    )
                    AppNavTab.ROOMS -> RoomsScreen(
                        onHostRoom = onOpenRoom,
                        viewModel = roomsViewModel,
                    )
                }
            }
        }
    }
}

}
