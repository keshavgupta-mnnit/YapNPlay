package com.kglabs28.yapnplay.appengine.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.HeadlineFontFamily
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings

/**
 * Right Card ("PLAY WITH FRIENDS") matching the mockup.
 * Clicking anywhere on the entire card triggers [onPlayWithFriends].
 */
@Composable
fun PlayWithFriendsCard(
    onPlayWithFriends: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Box(
        modifier = modifier
            .fillMaxSize()
            .clip(RoundedCornerShape(dimens.cardRadius))
            .background(colors.GlassSurface)
            .border(dimens.borderThin, colors.SocialZoneBorder, RoundedCornerShape(dimens.cardRadius))
            .clickable(onClick = onPlayWithFriends)
            .padding(Spacing.Lg),
    ) {
        Column(
            modifier = Modifier.fillMaxSize(),
            verticalArrangement = Arrangement.SpaceBetween,
        ) {
            // Header Row
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
            ) {
                // People Icon Badge
                Box(
                    modifier = Modifier
                        .size(dimens.badgeLarge)
                        .clip(CircleShape)
                        .background(colors.Panel)
                        .border(dimens.borderThin, colors.SocialZoneAccent, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(
                        text = "👥",
                        style = YapNPlayTheme.typography.title,
                    )
                }

                Column {
                    Row {
                        BasicText(
                            text = Strings.homePlayWithFriendsTitlePart1,
                            style = YapNPlayTheme.typography.title.copy(
                                fontFamily = HeadlineFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = colors.TextPrimary,
                            ),
                        )
                        BasicText(
                            text = Strings.homePlayWithFriendsTitlePart2,
                            style = YapNPlayTheme.typography.title.copy(
                                fontFamily = HeadlineFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = colors.SocialZoneAccent,
                            ),
                        )
                    }
                    BasicText(
                        text = Strings.homePlayWithFriendsSubtitle,
                        style = YapNPlayTheme.typography.caption.copy(
                            color = colors.TextSecondary,
                        ),
                    )
                }
            }

            // Central Social Orbit Graphic
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .padding(vertical = Spacing.Xs),
                contentAlignment = Alignment.Center,
            ) {
                SocialNetworkOrbitGraphic()
            }

            // Bottom Create Room Banner
            CreateRoomBanner()
        }
    }
}
