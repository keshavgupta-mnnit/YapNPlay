package com.kglabs28.yapnplay.appengine.ui.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier

/**
 * Top floating header bar assembling user profile pill, center logo, and actions pill.
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

        // Right Actions Pill (Coins, Diamonds, Bell, Settings in one glass container)
        HomeActionsPill(
            coinBalance = coinBalance,
            diamondBalance = diamondBalance,
            onOpenNotifications = onOpenNotifications,
            onOpenSettings = onOpenSettings,
        )
    }
}
