package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.ChevronRight
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.core.designsystem.revealWhen
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.ui.components.common.EmptyStateText
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.SectionHeader
import com.kglabs28.yapnplay.appengine.ui.components.playrooms.FriendAvatarItem

/** The user's friends in a sliding row (online first), with "See all" for the grid; a tap opens a friend's card. */
@Composable
fun ProfileFriendsPanel(
    friends: List<Friend>,
    subtitle: String,
    loaded: Boolean,
    onFriendClick: (userId: String) -> Unit,
    onSeeAll: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    Column(
        modifier = modifier
            .glassPanel(glowColor = colors.IconMuted, surface = colors.RoomsPanel.copy(alpha = PANEL_ALPHA))
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        SectionHeader(icon = Icons.Rounded.Groups, title = Strings.profileFriendsTitle, subtitle = subtitle, modifier = Modifier.fillMaxWidth()) {
            GlassPillButton(text = Strings.profileSeeAll, onClick = onSeeAll, trailingIcon = Icons.Rounded.ChevronRight, enabled = friends.isNotEmpty())
        }
        Box(modifier = Modifier.weight(1f).fillMaxWidth().revealWhen(loaded), contentAlignment = Alignment.CenterStart) {
            if (loaded && friends.isEmpty()) {
                EmptyStateText(text = Strings.profileNoFriends, modifier = Modifier.fillMaxSize())
            } else {
                LazyRow(horizontalArrangement = Arrangement.spacedBy(Spacing.Sm), verticalAlignment = Alignment.CenterVertically) {
                    items(friends, key = { it.userId }) { friend ->
                        FriendAvatarItem(friend = friend, onClick = { onFriendClick(friend.userId) })
                    }
                }
            }
        }
    }
}

private const val PANEL_ALPHA = 0.9f
