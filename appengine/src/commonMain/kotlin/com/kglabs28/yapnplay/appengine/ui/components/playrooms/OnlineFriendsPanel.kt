package com.kglabs28.yapnplay.appengine.ui.components.playrooms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.runtime.Composable
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.core.designsystem.revealWhen
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.ui.components.common.EmptyStateText
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassIconButton
import com.kglabs28.yapnplay.appengine.ui.components.common.SectionHeader
import kotlinx.coroutines.launch

/**
 * "Online Friends" panel: header with prev/next arrows and a sideways
 * row of avatars. Fades in once [loaded]; nobody online shows the
 * empty text instead.
 */
@Composable
fun OnlineFriendsPanel(
    friends: List<Friend>,
    loaded: Boolean,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()

    Column(
        modifier = modifier
            .glassPanel(
                glowColor = colors.IconMuted,
                surface = colors.RoomsPanel.copy(alpha = 0.9f),
            )
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        SectionHeader(
            icon = Icons.Rounded.Groups,
            title = Strings.roomsOnlineFriendsTitle,
            subtitle = Strings.roomsOnlineFriendsSubtitle,
            modifier = Modifier.fillMaxWidth(),
        ) {
            Row(horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
                GlassIconButton(
                    imageVector = Icons.Rounded.ChevronLeft,
                    contentDescription = Strings.roomsPreviousFriends,
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(
                                (listState.firstVisibleItemIndex - AppConstants.FRIENDS_SCROLL_STEP)
                                    .coerceAtLeast(0),
                            )
                        }
                    },
                    size = dimens.smallButtonHeight,
                    surface = colors.RoomsSmallButton,
                )
                GlassIconButton(
                    imageVector = Icons.Rounded.ChevronRight,
                    contentDescription = Strings.roomsNextFriends,
                    onClick = {
                        scope.launch {
                            listState.animateScrollToItem(
                                listState.firstVisibleItemIndex + AppConstants.FRIENDS_SCROLL_STEP,
                            )
                        }
                    },
                    size = dimens.smallButtonHeight,
                    surface = colors.RoomsSmallButton,
                )
            }
        }
        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth()
                .revealWhen(loaded),
            contentAlignment = Alignment.CenterStart,
        ) {
            if (loaded && friends.isEmpty()) {
                EmptyStateText(
                    text = Strings.roomsNoFriends,
                    modifier = Modifier.fillMaxSize(),
                )
            } else {
                LazyRow(
                    state = listState,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
                    verticalAlignment = Alignment.CenterVertically,
                ) {
                    items(friends, key = { it.userId }) { friend ->
                        FriendAvatarItem(friend = friend)
                    }
                }
            }
        }
    }
}
