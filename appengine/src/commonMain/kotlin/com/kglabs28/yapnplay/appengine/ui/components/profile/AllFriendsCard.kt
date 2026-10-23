package com.kglabs28.yapnplay.appengine.ui.components.profile

import com.kglabs28.yapnplay.appengine.ui.components.common.PillTabs
import com.kglabs28.yapnplay.appengine.core.ui.ProfileStrings
import com.kglabs28.yapnplay.appengine.core.designsystem.AppBrushes
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import com.kglabs28.yapnplay.appengine.ui.components.common.glassScrollbar
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.ui.components.common.EmptyStateText
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.playrooms.FriendAvatarItem
import com.kglabs28.yapnplay.appengine.ui.screens.profile.FriendsTab

/** "See All Friends": All / Online / Offline tabs over a grid; a tap opens that friend's card. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AllFriendsCard(
    friends: List<Friend>,
    tab: FriendsTab,
    allCount: Int,
    onlineCount: Int,
    offlineCount: Int,
    onSelectTab: (FriendsTab) -> Unit,
    onFriendClick: (userId: String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val listScroll = rememberScrollState()
    val dimens = YapNPlayTheme.dimens
    ModalCard(
        surfaceBrush = AppBrushes.dialogGeneric,title = ProfileStrings.profileAllFriendsTitle, onClose = onClose, modifier = modifier.width(dimens.profileWideDialogWidth)) {
        PillTabs(
            tabs = listOf(
                FriendsTab.ALL to ProfileStrings.profileTabAll(allCount),
                FriendsTab.ONLINE to ProfileStrings.profileTabOnline(onlineCount),
                FriendsTab.OFFLINE to ProfileStrings.profileTabOffline(offlineCount),
            ),
            selected = tab,
            onSelect = onSelectTab,
        )
        Column(modifier = Modifier.fillMaxWidth().heightIn(max = dimens.friendsGridMaxHeight).glassScrollbar(listScroll).verticalScroll(listScroll).padding(end = YapNPlayTheme.dimens.scrollbarWidth * 2)) {
            if (friends.isEmpty()) {
                EmptyStateText(text = if (tab == FriendsTab.ONLINE) ProfileStrings.profileNoneOnline else ProfileStrings.profileNoneOffline, modifier = Modifier.fillMaxWidth())
            } else {
                FlowRow(
                    modifier = Modifier.fillMaxWidth(),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Md, Alignment.Start),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
                ) {
                    friends.forEach { friend -> FriendAvatarItem(friend = friend, onClick = { onFriendClick(friend.userId) }, size = dimens.profileFriendAvatar) }
                }
            }
        }
    }
}
