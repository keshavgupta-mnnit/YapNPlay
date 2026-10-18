package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.playrooms.FriendAvatarItem

/** Every friend in a grid (online first); a tap opens their card. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun AllFriendsCard(
    friends: List<Friend>,
    onFriendClick: (userId: String) -> Unit,
    onClose: () -> Unit,
    modifier: Modifier = Modifier,
) {
    ModalCard(title = Strings.profileAllFriendsTitle(friends.size), onClose = onClose, modifier = modifier) {
        Column(modifier = Modifier.fillMaxWidth().heightIn(max = YapNPlayTheme.dimens.friendsGridMaxHeight).verticalScroll(rememberScrollState())) {
            FlowRow(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Md, Alignment.CenterHorizontally),
                verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
            ) {
                friends.forEach { friend -> FriendAvatarItem(friend = friend, onClick = { onFriendClick(friend.userId) }) }
            }
        }
    }
}
