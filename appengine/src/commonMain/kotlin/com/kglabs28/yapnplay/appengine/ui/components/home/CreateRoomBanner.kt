package com.kglabs28.yapnplay.appengine.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.HeadlineFontFamily
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings

/**
 * Bottom sub-banner inside "PLAY WITH FRIENDS" card matching mockup.
 */
@Composable
fun CreateRoomBanner(
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Box(
        modifier = modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(dimens.panelRadius))
            .background(colors.Panel.copy(alpha = 0.9f))
            .border(dimens.hairline, colors.SocialZoneAccent.copy(alpha = 0.4f), RoundedCornerShape(dimens.panelRadius))
            .padding(Spacing.Md),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
            ) {
                // Room Icon
                Box(
                    modifier = Modifier
                        .size(dimens.chipHeight)
                        .clip(CircleShape)
                        .background(colors.SocialZoneAccent.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(
                        text = "👥",
                        style = YapNPlayTheme.typography.body,
                    )
                }

                BasicText(
                    text = Strings.homeCreateRoomBannerText,
                    style = YapNPlayTheme.typography.caption.copy(
                        color = colors.TextSecondary,
                    ),
                    modifier = Modifier.padding(end = Spacing.Sm),
                )
            }

            // Purple Create Room Button
            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(colors.SocialZoneAccent, colors.Purple),
                        ),
                    )
                    .padding(horizontal = Spacing.Lg, vertical = Spacing.Sm),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
                ) {
                    BasicText(
                        text = "➕",
                        style = YapNPlayTheme.typography.caption,
                    )
                    BasicText(
                        text = Strings.homeHostLobby,
                        style = YapNPlayTheme.typography.label.copy(
                            fontFamily = HeadlineFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = colors.TextPrimary,
                        ),
                    )
                }
            }
        }
    }
}
