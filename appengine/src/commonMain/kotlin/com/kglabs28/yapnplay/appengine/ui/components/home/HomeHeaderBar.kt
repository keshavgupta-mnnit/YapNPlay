package com.kglabs28.yapnplay.appengine.ui.components.home

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Top floating header bar assembling user profile, center logo, currency pills, and action buttons.
 */
@Composable
fun HomeHeaderBar(
    userName: String,
    level: String,
    levelProgressText: String,
    coinBalance: String,
    diamondBalance: String,
    onOpenSettings: () -> Unit,
    onOpenNotifications: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Left User Profile & Level Pill
        HomeUserProfilePill(
            userName = userName,
            level = level,
            levelProgressText = levelProgressText,
        )

        // Center Logo
        HomeCenterLogo()

        // Right Actions: Gold Coin, Diamond, Bell, Gear
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
        ) {
            HomeHeaderCurrencyPill(
                iconGlyph = "🪙",
                valueText = coinBalance,
                onClick = {},
            )

            HomeHeaderCurrencyPill(
                iconGlyph = "💎",
                valueText = diamondBalance,
                onClick = {},
            )

            // Notification Bell Button
            Box(
                modifier = Modifier
                    .size(dimens.avatarList)
                    .clip(CircleShape)
                    .background(colors.GlassSurface)
                    .border(dimens.hairline, colors.TextDisabled.copy(alpha = 0.3f), CircleShape)
                    .clickable(onClick = onOpenNotifications),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = "🔔",
                    style = YapNPlayTheme.typography.body,
                )
            }

            // Settings Gear Button
            Box(
                modifier = Modifier
                    .size(dimens.avatarList)
                    .clip(CircleShape)
                    .background(colors.GlassSurface)
                    .border(dimens.hairline, colors.TextDisabled.copy(alpha = 0.3f), CircleShape)
                    .clickable(onClick = onOpenSettings),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = "⚙️",
                    style = YapNPlayTheme.typography.body,
                )
            }
        }
    }
}
