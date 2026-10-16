package com.kglabs28.yapnplay.appengine.ui.placeholders

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
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.core.ui.Strings

/**
 * Bottom sub-banner inside "PLAY WITH FRIENDS" card.
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
            .glassPanel(colors.SocialZoneAccent, dimens.panelRadius)
            .padding(Spacing.Sm),
    ) {
        Row(
            modifier = Modifier.fillMaxWidth(),
            horizontalArrangement = Arrangement.SpaceBetween,
            verticalAlignment = Alignment.CenterVertically,
        ) {
            Row(
                modifier = Modifier.weight(1f),
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
            ) {
                Box(
                    modifier = Modifier
                        .size(dimens.chipHeight)
                        .clip(CircleShape)
                        .background(colors.SocialZoneAccent.copy(alpha = 0.3f)),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(
                        text = "👥",
                        style = YapNPlayTheme.typography.caption,
                    )
                }

                BasicText(
                    text = Strings.homeCreateRoomBannerText,
                    style = YapNPlayTheme.typography.micro.copy(
                        color = colors.TextSecondary,
                    ),
                    modifier = Modifier.padding(end = Spacing.Xs),
                )
            }

            Box(
                modifier = Modifier
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(colors.SocialZoneAccent, colors.Purple),
                        ),
                    )
                    .padding(horizontal = Spacing.Sm, vertical = Spacing.Xxs),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
                ) {
                    BasicText(
                        text = "➕",
                        style = YapNPlayTheme.typography.micro,
                    )
                    BasicText(
                        text = Strings.homeCreateRoom,
                        style = YapNPlayTheme.typography.caption.copy(
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
