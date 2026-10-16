package com.kglabs28.yapnplay.appengine.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
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

/**
 * Right actions pill in top header bar encapsulating currency balances, notifications, and settings
 * in one glass pill matching the left user profile pill.
 */
@Composable
fun HomeActionsPill(
    coinBalance: String,
    diamondBalance: String,
    onOpenNotifications: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
    coinIcon: String = Strings.iconCoin,
    diamondIcon: String = Strings.iconDiamond,
    bellIcon: String = Strings.iconBell,
    settingsIcon: String = Strings.iconSettings,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Box(
        modifier = modifier
            .height(dimens.headerPillHeight)
            .glassPanel(colors.SocialZoneAccent)
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
        ) {
            // Gold Coin Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colors.Panel)
                    .padding(horizontal = Spacing.Sm, vertical = Spacing.Xxs),
            ) {
                BasicText(
                    text = coinIcon,
                    style = YapNPlayTheme.typography.caption,
                )
                BasicText(
                    text = coinBalance,
                    style = YapNPlayTheme.typography.caption.copy(
                        fontFamily = HeadlineFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = colors.TextPrimary,
                    ),
                )
            }

            // Diamond Pill
            Row(
                verticalAlignment = Alignment.CenterVertically,
                horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
                modifier = Modifier
                    .clip(CircleShape)
                    .background(colors.Panel)
                    .padding(horizontal = Spacing.Sm, vertical = Spacing.Xxs),
            ) {
                BasicText(
                    text = diamondIcon,
                    style = YapNPlayTheme.typography.caption,
                )
                BasicText(
                    text = diamondBalance,
                    style = YapNPlayTheme.typography.caption.copy(
                        fontFamily = HeadlineFontFamily,
                        fontWeight = FontWeight.Bold,
                        color = colors.TextPrimary,
                    ),
                )
            }

            // Bell Button
            Box(
                modifier = Modifier
                    .size(dimens.badgeMedium)
                    .clip(CircleShape)
                    .background(colors.Panel)
                    .border(dimens.hairline, colors.TextDisabled.copy(alpha = 0.3f), CircleShape)
                    .clickable(onClick = onOpenNotifications),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = bellIcon,
                    style = YapNPlayTheme.typography.caption,
                )
            }

            // Settings Button
            Box(
                modifier = Modifier
                    .size(dimens.badgeMedium)
                    .clip(CircleShape)
                    .background(colors.Panel)
                    .border(dimens.hairline, colors.TextDisabled.copy(alpha = 0.3f), CircleShape)
                    .clickable(onClick = onOpenSettings),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = settingsIcon,
                    style = YapNPlayTheme.typography.caption,
                )
            }
        }
    }
}
