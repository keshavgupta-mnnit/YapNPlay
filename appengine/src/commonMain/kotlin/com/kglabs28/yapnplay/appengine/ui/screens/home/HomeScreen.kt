package com.kglabs28.yapnplay.appengine.ui.screens.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.ui.components.home.EnterGameZoneCard
import com.kglabs28.yapnplay.appengine.ui.components.home.HomeHeaderBar
import com.kglabs28.yapnplay.appengine.ui.components.home.PlayWithFriendsCard
import com.kglabs28.yapnplay.appengine.ui.placeholders.CozyHomeBackground

/**
 * Home screen assembling floating top header bar and two main direction cards.
 * Clicking anywhere on either card takes the user to that section.
 */
@Composable
fun HomeScreen(
    onQuickPlay: () -> Unit,
    onHostLobby: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: HomeViewModel? = null,
) {
    val state = viewModel?.state?.collectAsState()?.value ?: HomeState()
    LaunchedEffect(viewModel) {
        viewModel?.onIntent(HomeIntent.LoadProfile)
    }

    CozyHomeBackground(modifier = modifier) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .statusBarsPadding()
                .padding(start = Spacing.Md, end = Spacing.Md, bottom = Spacing.Md),
            verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
        ) {
            // Top Header Bar
            HomeHeaderBar(
                userName = state.userName,
                level = state.level,
                levelProgressText = state.levelProgressText,
                coinBalance = state.coinBalance,
                diamondBalance = state.diamondBalance,
                onOpenSettings = {
                    viewModel?.onIntent(HomeIntent.OpenSettings)
                },
                onOpenNotifications = {
                    viewModel?.onIntent(HomeIntent.OpenNotifications)
                },
            )

            // Two Main Direction Cards
            Row(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxSize(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Md),
            ) {
                // Enter Game Zone Card (Entire card clickable)
                EnterGameZoneCard(
                    onEnterGameZone = {
                        viewModel?.onIntent(HomeIntent.EnterGameZone)
                        onQuickPlay()
                    },
                    modifier = Modifier.weight(1f),
                )

                // Play With Friends Card (Entire card clickable)
                PlayWithFriendsCard(
                    onPlayWithFriends = {
                        viewModel?.onIntent(HomeIntent.PlayWithFriends)
                        onHostLobby()
                    },
                    modifier = Modifier.weight(1f),
                )
            }
        }
    }
}
