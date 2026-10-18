package com.kglabs28.yapnplay.appengine.ui.components.playrooms

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.style.TextOverflow
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.ui.components.PresenceDot
import com.kglabs28.yapnplay.appengine.ui.components.profile.PresetAvatar

/**
 * One friend in the online row: ringed avatar with a presence dot and
 * the name underneath; their preset avatar (person glyph if none). A tap
 * ([onClick]) opens their card where that's offered.
 */
@Composable
fun FriendAvatarItem(
    friend: Friend,
    modifier: Modifier = Modifier,
    onClick: (() -> Unit)? = null,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Column(
        modifier = modifier.then(if (onClick != null) Modifier.clickable(onClick = onClick) else Modifier),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs),
    ) {
        Box(contentAlignment = Alignment.BottomEnd) {
            PresetAvatar(avatarId = friend.avatarId, size = dimens.friendAvatar, rimColor = colors.SocialZoneAccent)
            PresenceDot(
                online = friend.online,
                size = dimens.statusDotSmall,
                onlineColor = colors.LiveGreen,
            )
        }
        BasicText(
            text = friend.name,
            style = YapNPlayTheme.typography.micro.copy(
                color = colors.TextPrimary,
                textAlign = TextAlign.Center,
            ),
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.widthIn(max = dimens.friendAvatar * 2),
        )
    }
}
