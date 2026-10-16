package com.kglabs28.yapnplay.appengine.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
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
import com.kglabs28.yapnplay.appengine.ui.components.PresenceDot

/**
 * Left profile + level pill in the top header matching the home mockup.
 */
@Composable
fun HomeUserProfilePill(
    userName: String,
    level: String,
    levelProgressText: String,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(dimens.cardRadius))
            .background(colors.GlassSurface)
            .border(dimens.hairline, colors.TextDisabled.copy(alpha = 0.3f), RoundedCornerShape(dimens.cardRadius))
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
        ) {
            // User Avatar
            Box(
                modifier = Modifier
                    .size(dimens.avatarList)
                    .clip(CircleShape)
                    .background(colors.Panel)
                    .border(dimens.borderThin, colors.GameZoneAccent, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = "👦",
                    style = YapNPlayTheme.typography.body,
                )
            }

            // User Name + Online Status
            Column {
                BasicText(
                    text = userName,
                    style = YapNPlayTheme.typography.body.copy(
                        fontFamily = HeadlineFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = colors.TextPrimary,
                    ),
                )
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
                ) {
                    PresenceDot(online = true, onlineColor = colors.Success)
                    BasicText(
                        text = Strings.homeOnlineStatus,
                        style = YapNPlayTheme.typography.caption.copy(
                            color = colors.Success,
                            fontWeight = FontWeight.Medium,
                        ),
                    )
                }
            }

            // Level Badge Hexagon
            Box(
                modifier = Modifier
                    .size(dimens.chipHeight)
                    .clip(CircleShape)
                    .background(colors.Panel)
                    .border(dimens.borderThin, colors.GameZoneAccent, CircleShape),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = level,
                    style = YapNPlayTheme.typography.title.copy(
                        fontFamily = HeadlineFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = colors.GameZoneAccent,
                    ),
                )
            }

            // Level Text + Progress Bar
            Column(
                modifier = Modifier.padding(end = Spacing.Xs),
            ) {
                BasicText(
                    text = Strings.homeLevelText,
                    style = YapNPlayTheme.typography.caption.copy(
                        color = colors.TextSecondary,
                    ),
                )
                Box(
                    modifier = Modifier
                        .padding(vertical = Spacing.Xxs)
                        .width(dimens.progressWidth)
                        .height(dimens.progressHeight)
                        .clip(CircleShape)
                        .background(colors.TextDisabled.copy(alpha = 0.3f)),
                ) {
                    Box(
                        modifier = Modifier
                            .fillMaxWidth(0.81f)
                            .height(dimens.progressHeight)
                            .clip(CircleShape)
                            .background(colors.GameZoneAccent),
                    )
                }
                BasicText(
                    text = levelProgressText,
                    style = YapNPlayTheme.typography.caption.copy(
                        color = colors.TextDisabled,
                        fontWeight = FontWeight.Normal,
                    ),
                )
            }
        }
    }
}
