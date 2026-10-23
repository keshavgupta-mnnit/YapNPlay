package com.kglabs28.yapnplay.appengine.ui.components.common

import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.ui.components.home.TopBarBrand
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.navigation.AppNavTab
import com.kglabs28.yapnplay.appengine.ui.screens.main.NotificationUi

/**
 * Global top bar for the Home, Games and Rooms tabs (design): one floating
 * glass bar holding the brand (logo, name, tagline), the tab pills, then
 * the coin and XP pills, the bell, settings and the user's avatar. The bar
 * has one fixed height; every child sizes itself inside it.
 */
@Composable
fun AppTopBar(
    online: Boolean,
    profileLoaded: Boolean,
    coinBalance: String,
    level: String,
    levelProgress: Float,
    xpLabel: String,
    selectedTab: AppNavTab,
    onSelectTab: (AppNavTab) -> Unit,
    onOpenSettings: () -> Unit,
    onToggleNotifications: () -> Unit,
    modifier: Modifier = Modifier,
    notifications: List<NotificationUi> = emptyList(),
    unreadNotifications: Int = 0,
    notificationsOpen: Boolean = false,
    onDismissNotifications: () -> Unit = {},
    onNotificationClick: (roomId: String) -> Unit = {},
    avatarId: String = "",
    onOpenProfile: () -> Unit = {},
    /** The coins "+": free coins for an ad. */
    onAddCoins: () -> Unit = {},
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Row(
        modifier = modifier
            .fillMaxWidth()
            .padding(start = Spacing.Md, end = Spacing.Md, top = Spacing.Xs)
            .height(dimens.topBarHeight)
            .glassPill(glowColor = colors.TopBarIcon, surface = colors.TopBarPill)
            .padding(start = Spacing.Sm, end = Spacing.Xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        TopBarBrand()
        TopBarDivider()
        AppNavTabs(selectedTab = selectedTab, onSelectTab = onSelectTab)
        Spacer(Modifier.weight(1f))
        AppCurrenciesPill(
            coinBalance = coinBalance,
            level = level,
            levelProgress = levelProgress,
            xpLabel = xpLabel,
            balancesVisible = profileLoaded,
            onAddCoins = onAddCoins,
        )
        TopBarDivider()
        NotificationsBell(
            notifications = notifications,
            unreadCount = unreadNotifications,
            expanded = notificationsOpen,
            onToggle = onToggleNotifications,
            onDismiss = onDismissNotifications,
            onNotificationClick = onNotificationClick,
        )
        GlassIconButton(
            imageVector = Icons.Rounded.Settings,
            contentDescription = Strings.topBarSettings,
            onClick = onOpenSettings,
            surface = colors.TopBarButtonFrost,
            tint = colors.HomeTextSoft,
        )
        AppUserProfile(
            online = online,
            statusVisible = profileLoaded,
            avatarId = avatarId,
            onClick = onOpenProfile,
        )
    }
}
