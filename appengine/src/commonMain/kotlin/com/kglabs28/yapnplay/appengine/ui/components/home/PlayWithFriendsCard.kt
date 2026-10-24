package com.kglabs28.yapnplay.appengine.ui.components.home

import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.coerceAtLeast
import androidx.compose.ui.unit.Dp
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
    onCreateRoom: () -> Unit,
    /** How far its left edge leans (in from the top left to the bottom left). */
    slant: Dp,
    /** Where Game Zone's bottom right corner sits, from this card's left: the panel stops short of it. */
    gameZoneEdge: Dp,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    // The glass is its own layer and takes the taps, clipped to the leaning shape (a press lights the
    // card's own outline); the content is not clipped, so the Create Room banner can stand out past
    // the card's leaning edge like a cutout (design).
    val shape = SlantedCardShape(dimens.cardRadius, slant, SlantedSide.START, dimens.homeCardInnerRadius)
    Box(modifier = modifier.fillMaxSize()) {
        Box(
            Modifier
                .matchParentSize()
                .glassPanel(
                    shape = shape,
                    glowColor = colors.FriendsAccent,
                    surfaceBrush = Brush.verticalGradient(listOf(colors.HomeCardFrostTop, colors.HomeCardFrostBottom)),
                )
                .clip(shape)
                .clickable(onClick = onPlayWithFriends),
        )
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(start = Spacing.Md, end = Spacing.Md, top = Spacing.Sm, bottom = Spacing.Sm),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            ZoneCardHeader(
                icon = Icons.Rounded.Groups,
                iconFill = colors.HomeIconFrost,
                title = Strings.homePlayWithFriendsTitlePart1,
                titleAccent = Strings.homePlayWithFriendsTitlePart2,
                accentColor = colors.FriendsAccent,
                subtitle = Strings.homePlayWithFriendsSubtitle,
                // Its icon clear of the rounded top left corner.
                modifier = Modifier.padding(start = Spacing.Sm),
            )

            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    // Clear of the leaning edge, but with most of the card's width.
                    .padding(start = slant / FRIENDS_ART_INSET),
                contentAlignment = Alignment.Center,
            ) {
                SocialNetworkOrbitGraphic()
            }

            // At the bottom the card starts the whole lean in; the panel spreads out past it into the gap,
            // stopping a little short of Game Zone's corner.
            CreateRoomBanner(onCreateRoom = onCreateRoom, modifier = Modifier.padding(start = (gameZoneEdge + dimens.homeBannerClearance - Spacing.Md).coerceAtLeast(dimens.zero)))
        }
    }
}

/** The friends art keeps clear of the leaning edge by the lean over this. */
private const val FRIENDS_ART_INSET = 3
