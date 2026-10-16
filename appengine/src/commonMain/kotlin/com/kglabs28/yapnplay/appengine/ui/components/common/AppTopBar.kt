package com.kglabs28.yapnplay.appengine.ui.components.common

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
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.navigation.AppNavTab
import com.kglabs28.yapnplay.appengine.ui.components.home.HomeCenterLogo

/**
 * Main global top bar matching topbar.png across Home, Games, and Rooms tabs.
 */
@Composable
fun AppTopBar(
    userName: String,
    level: String,
    coinBalance: String,
    diamondBalance: String,
    selectedTab: AppNavTab,
    onSelectTab: (AppNavTab) -> Unit,
    onOpenSettings: () -> Unit,
    onOpenNotifications: () -> Unit,
    modifier: Modifier = Modifier,
    bellIcon: String = Strings.iconBell,
    settingsIcon: String = Strings.iconSettings,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        // Far Left: Logo & Tagline
        HomeCenterLogo()

        // Center: App Navigation Tabs (Home, Games, Rooms)
        AppNavTabs(
            selectedTab = selectedTab,
            onSelectTab = onSelectTab,
        )

        // Right Side Actions & User Profile
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
        ) {
            // Currency & Level Glass Pill (Coins, Diamonds, Level)
            AppCurrenciesPill(
                coinBalance = coinBalance,
                diamondBalance = diamondBalance,
                level = level,
            )

            // Bell Button
            Box(
                modifier = Modifier
                    .size(dimens.badgeMedium)
                    .clip(CircleShape)
                    .background(colors.GlassSurface)
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
                    .background(colors.GlassSurface)
                    .border(dimens.hairline, colors.TextDisabled.copy(alpha = 0.3f), CircleShape)
                    .clickable(onClick = onOpenSettings),
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = settingsIcon,
                    style = YapNPlayTheme.typography.caption,
                )
            }

            // Far Right User Profile (Avatar + Name below)
            AppUserProfile(userName = userName)
        }
    }
}
