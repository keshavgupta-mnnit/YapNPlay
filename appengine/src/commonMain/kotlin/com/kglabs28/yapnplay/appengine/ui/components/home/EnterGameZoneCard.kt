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
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.HeadlineFontFamily
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.placeholders.GameZoneFeaturePillarsRow
import com.kglabs28.yapnplay.appengine.ui.placeholders.GameZoneGraphic

/**
 * Left Card ("ENTER GAME ZONE").
 * Clicking anywhere on the card triggers [onEnterGameZone].
 */
@Composable
fun EnterGameZoneCard(
    onEnterGameZone: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Box(
        modifier = modifier
            .fillMaxSize()
            .glassPanel(colors.GameZoneAccent)
            .clickable(onClick = onEnterGameZone)
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
                // Gamepad Icon Badge
                Box(
                    modifier = Modifier
                        .size(dimens.cardHeaderIconSize)
                        .clip(CircleShape)
                        .background(colors.Panel)
                        .border(dimens.borderThin, colors.GameZoneAccent, CircleShape),
                    contentAlignment = Alignment.Center,
                ) {
                    BasicText(
                        text = "🎮",
                        style = YapNPlayTheme.typography.body,
                    )
                }

                Column {
                    Row {
                        BasicText(
                            text = Strings.homeEnterGameZoneTitlePart1,
                            style = YapNPlayTheme.typography.body.copy(
                                fontFamily = HeadlineFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = colors.TextPrimary,
                            ),
                        )
                        BasicText(
                            text = Strings.homeEnterGameZoneTitlePart2,
                            style = YapNPlayTheme.typography.body.copy(
                                fontFamily = HeadlineFontFamily,
                                fontWeight = FontWeight.Bold,
                                color = colors.GameZoneAccent,
                            ),
                        )
                    }
                    BasicText(
                        text = Strings.homeEnterGameZoneSubtitle,
                        style = YapNPlayTheme.typography.caption.copy(
                            color = colors.TextSecondary,
                        ),
                    )
                }
            }

            // Central Graphic — clipped so it stays between the header
            // and the bottom UI, never painting over its neighbors.
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    .clip(RoundedCornerShape(dimens.panelRadius))
                    .padding(vertical = Spacing.Xxs),
                contentAlignment = Alignment.Center,
            ) {
                GameZoneGraphic()
            }

            // 3 Feature Pillars Row
            GameZoneFeaturePillarsRow(
                modifier = Modifier.padding(vertical = Spacing.Xxs),
            )

            // Bottom Glowing Quick Play Button
            Box(
                modifier = Modifier
                    .align(Alignment.CenterHorizontally)
                    .fillMaxWidth(0.65f)
                    .clip(CircleShape)
                    .background(
                        Brush.horizontalGradient(
                            colors = listOf(colors.GameZoneAccent, colors.ButtonFace),
                        ),
                    )
                    .padding(top = Spacing.Md, bottom = Spacing.Sm),
                contentAlignment = Alignment.Center,
            ) {
                Row(
                    verticalAlignment = Alignment.CenterVertically,
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
                ) {
                    BasicText(
                        text = "⚡",
                        style = YapNPlayTheme.typography.title,
                    )
                    BasicText(
                        text = Strings.homeQuickPlay,
                        style = YapNPlayTheme.typography.title.copy(
                            fontFamily = HeadlineFontFamily,
                            fontWeight = FontWeight.Bold,
                            color = colors.Background,
                        ),
                    )
                    BasicText(
                        text = "❯",
                        style = YapNPlayTheme.typography.title.copy(
                            fontWeight = FontWeight.Bold,
                            color = colors.Background,
                        ),
                    )
                }
            }
        }
    }
}
