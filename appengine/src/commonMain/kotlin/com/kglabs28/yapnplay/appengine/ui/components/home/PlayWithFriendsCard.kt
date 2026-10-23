package com.kglabs28.yapnplay.appengine.ui.components.home

import androidx.compose.ui.graphics.Brush
import com.kglabs28.yapnplay.appengine.core.designsystem.SlantedSide
import com.kglabs28.yapnplay.appengine.core.designsystem.SlantedCardShape
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
 * the create-room banner, which juts out past the card's leaning edge.
 * The whole card is tappable.
 */
@Composable
fun PlayWithFriendsCard(
    onPlayWithFriends: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val slant = dimens.homeFriendsSlant

    // The glass is its own layer (clipped to the leaning shape); the content is not clipped, so the
    // Create Room banner can stand out past the card's leaning edge like a cutout (design).
    Box(modifier = modifier.fillMaxSize().clickable(onClick = onPlayWithFriends)) {
        Box(
            Modifier
                .matchParentSize()
                .glassPanel(
                    shape = SlantedCardShape(dimens.cardRadius, slant, SlantedSide.START),
                    glowColor = colors.FriendsAccent,
                    surfaceBrush = Brush.verticalGradient(listOf(colors.HomeCardFrostTop, colors.HomeCardFrostBottom)),
                ),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = Spacing.Md, end = Spacing.Md, top = Spacing.Sm, bottom = Spacing.Sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            ZoneCardHeader(
                icon = Icons.Rounded.Groups,
                iconFill = colors.SocialZoneIconFill,
                title = Strings.homePlayWithFriendsTitlePart1,
                titleAccent = Strings.homePlayWithFriendsTitlePart2,
                accentColor = colors.FriendsAccent,
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

            // Juts out past the card's short bottom edge, still clear of the other card.
            CreateRoomBanner(onCreateRoom = onPlayWithFriends, modifier = Modifier.padding(start = dimens.homeBannerInset))
        }
    }
}
