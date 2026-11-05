package com.kglabs28.yapnplay.appengine.ui.components.playhub

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.profile.PresetAvatar

/** A few online friends' avatars, overlapping left to right (the "N Friends Online" pill's tail). */
@Composable
fun FriendFacesStack(
    avatarIds: List<String>,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(-dimens.playHubAvatarOverlap),
    ) {
        avatarIds.forEach { id ->
            PresetAvatar(avatarId = id, size = dimens.playHubPillAvatar, rimColor = colors.PlayFriendsAccent)
        }
    }
}
