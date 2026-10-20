package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronLeft
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
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
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.EmptyStateText
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassIconButton
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.playrooms.FriendAvatarItem
import com.kglabs28.yapnplay.appengine.ui.screens.profile.FriendsTab
import kotlinx.coroutines.launch

/**
 * "My Friends", full width: the title, Online / Offline tabs, then (at the
 * end of that row) See All and the slider arrows; under it the sliding
 * row of friends, as on the Rooms tab. A tap opens a friend's card.
 */
@Composable
fun ProfileFriendsSection(
    friends: List<Friend>,
    hasFriends: Boolean,
    tab: FriendsTab,
    onlineCount: Int,
    offlineCount: Int,
    loaded: Boolean,
    onSelectTab: (FriendsTab) -> Unit,
    onFriendClick: (userId: String) -> Unit,
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val listState = rememberLazyListState()
    val scope = rememberCoroutineScope()
    LaunchedEffect(tab) { listState.scrollToItem(0) }
    Column(
        modifier = modifier
            .glassPanel(glowColor = colors.IconMuted, surface = colors.RoomsPanel.copy(alpha = PANEL_ALPHA))
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xxs),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
            AppIcon(imageVector = Icons.Rounded.Groups, tint = colors.SocialZoneAccent, size = dimens.iconMedium)
            BasicText(text = Strings.profileMyFriends, style = YapNPlayTheme.typography.sectionTitle.copy(color = colors.TextPrimary))
            FriendsTabs(
                tabs = listOf(FriendsTab.ONLINE to Strings.profileTabOnline(onlineCount), FriendsTab.OFFLINE to Strings.profileTabOffline(offlineCount)),
                selected = tab,
                onSelect = onSelectTab,
            )
            Spacer(Modifier.weight(1f))
            // See All and the arrows are as small as the tabs.
            GlassPillButton(
                text = Strings.profileSeeAll,
                onClick = onSeeAll,
                trailingIcon = Icons.Rounded.ChevronRight,
                enabled = hasFriends,
                height = dimens.profileTabHeight,
                iconSize = dimens.iconTiny,
                textStyle = YapNPlayTheme.typography.micro,
            )
            GlassIconButton(
                imageVector = Icons.Rounded.ChevronLeft,
                contentDescription = Strings.profilePreviousFriends,
                onClick = { scope.launch { listState.animateScrollToItem((listState.firstVisibleItemIndex - AppConstants.FRIENDS_SCROLL_STEP).coerceAtLeast(0)) } },
                size = dimens.profileTabHeight,
                iconSize = dimens.iconSmall,
                surface = colors.RoomsSmallButton,
            )
            GlassIconButton(
                imageVector = Icons.Rounded.ChevronRight,
                contentDescription = Strings.profileNextFriends,
                onClick = { scope.launch { listState.animateScrollToItem(listState.firstVisibleItemIndex + AppConstants.FRIENDS_SCROLL_STEP) } },
                size = dimens.profileTabHeight,
                iconSize = dimens.iconSmall,
                surface = colors.RoomsSmallButton,
            )
        }
        Box(modifier = Modifier.fillMaxWidth().revealWhen(loaded), contentAlignment = Alignment.CenterStart) {
            if (loaded && friends.isEmpty()) {
                EmptyStateText(
                    text = when {
                        !hasFriends -> Strings.profileNoFriends
                        tab == FriendsTab.ONLINE -> Strings.profileNoneOnline
                        else -> Strings.profileNoneOffline
                    },
                    modifier = Modifier.fillMaxWidth(),
                )
            } else {
                LazyRow(state = listState, horizontalArrangement = Arrangement.spacedBy(Spacing.Md), verticalAlignment = Alignment.CenterVertically) {
                    items(friends, key = { it.userId }) { friend ->
                        FriendAvatarItem(friend = friend, onClick = { onFriendClick(friend.userId) }, size = dimens.profileFriendAvatar)
                    }
                }
            }
        }
    }
}

private const val PANEL_ALPHA = 0.9f
