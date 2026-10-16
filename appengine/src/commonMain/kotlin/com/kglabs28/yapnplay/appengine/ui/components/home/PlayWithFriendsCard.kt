package com.kglabs28.yapnplay.appengine.ui.components.home

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.ZoneCardHeader
import com.kglabs28.yapnplay.appengine.ui.placeholders.CreateRoomBanner
import com.kglabs28.yapnplay.appengine.ui.placeholders.SocialNetworkOrbitGraphic

/**
 * Right home card ("PLAY WITH FRIENDS"): header, friends orbit art and
 * the create-room banner. The whole card is tappable.
 */
@Composable
fun PlayWithFriendsCard(
    onPlayWithFriends: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors

    Column(
        modifier = modifier
            .fillMaxSize()
            .glassPanel(
                glowColor = colors.SocialZoneAccent,
                surface = colors.SocialZoneSurface.copy(alpha = 0.85f),
            )
            .clickable(onClick = onPlayWithFriends)
            .padding(horizontal = Spacing.Md, vertical = Spacing.Sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        ZoneCardHeader(
            icon = Icons.Rounded.Groups,
            iconFill = colors.SocialZoneIconFill,
            title = Strings.homePlayWithFriendsTitlePart1,
            titleAccent = Strings.homePlayWithFriendsTitlePart2,
            accentColor = colors.SocialZoneAccent,
            subtitle = Strings.homePlayWithFriendsSubtitle,
        )

        Box(
            modifier = Modifier
                .weight(1f)
                .fillMaxWidth(),
            contentAlignment = Alignment.Center,
        ) {
            SocialNetworkOrbitGraphic()
        }

        CreateRoomBanner(onCreateRoom = onPlayWithFriends)
    }
}
