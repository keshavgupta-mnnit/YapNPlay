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
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.placeholders.CreateRoomBanner
import com.kglabs28.yapnplay.appengine.ui.placeholders.SocialNetworkOrbitGraphic

/**
 * Right Card ("PLAY WITH FRIENDS").
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
            .glassPanel(colors.SocialZoneAccent)
            .clickable(onClick = onPlayWithFriends)
            .padding(Spacing.Md),
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
                        .size(dimens.cardHeaderIconSize)
                        .clip(CircleShape)
                        .background(colors.Panel)
                        .border(dimens.borderThin, colors.SocialZoneAccent, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(
                        text = "👥",
                        style = YapNPlayTheme.typography.body,
                    )
                }

                Column {
                    Row {
                        BasicText(
                            text = Strings.homePlayWithFriendsTitlePart1,
                            style = YapNPlayTheme.typography.body.copy(
                                fontFamily = HeadlineFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = colors.TextPrimary,
                            ),
                        )
                        BasicText(
                            text = Strings.homePlayWithFriendsTitlePart2,
                            style = YapNPlayTheme.typography.body.copy(
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

            // Central Social Orbit Graphic — clipped so it stays between
            // the header and the bottom banner, never painting over them.
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(dimens.panelRadius))
                    .padding(vertical = Spacing.Xxs),
                contentAlignment = Alignment.Center,
            ) {
                SocialNetworkOrbitGraphic()
            }

            // Bottom Create Room Banner
            CreateRoomBanner()
        }
    }
}
