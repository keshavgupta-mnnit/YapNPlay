package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.background
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
import androidx.compose.ui.graphics.Brush
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.navigation.AppNavTab
import com.kglabs28.yapnplay.appengine.ui.screens.main.NotificationUi
import com.kglabs28.yapnplay.appengine.ui.components.home.HomeCenterLogo

/**
 * Global top bar for the Home, Games and Rooms tabs: logo left, tab
 * strip centre, balances / bell / settings / avatar right. The bar has
 * one fixed height; every child sizes itself inside it.
 */
@Composable
fun AppTopBar(
    online: Boolean,
    profileLoaded: Boolean,
    coinBalance: String,
    diamondBalance: String,
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
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Row(
        modifier = modifier
            .fillMaxWidth()
            .height(dimens.topBarHeight)
            // Frosted strip that is densest at the top edge and fades to
            // nothing at the bottom, so the bar melts into the art.
            .background(
                Brush.verticalGradient(
                    0f to colors.TopBarBackground.copy(alpha = 0.97f),
                    0.6f to colors.TopBarBackground.copy(alpha = 0.8f),
                    1f to colors.Transparent,
                ),
            )
            .padding(start = Spacing.Xs, end = Spacing.Md, top = Spacing.Xxs, bottom = Spacing.Xxs),
        verticalAlignment = Alignment.CenterVertically,
    ) {
        HomeCenterLogo()

        // Uneven gaps pull the tab strip toward the centre and leave the
        // right group hugging the edge, as in the design.
        Spacer(Modifier.weight(AppConstants.TOP_BAR_LEADING_GAP_WEIGHT))

        AppNavTabs(
            selectedTab = selectedTab,
            onSelectTab = onSelectTab,
        )

        Spacer(Modifier.weight(AppConstants.TOP_BAR_TRAILING_GAP_WEIGHT))

        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            AppCurrenciesPill(
                coinBalance = coinBalance,
                diamondBalance = diamondBalance,
                balancesVisible = profileLoaded,
                modifier = Modifier.padding(end = Spacing.Xxs),
            )
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
            )
            AppUserProfile(
                online = online,
                statusVisible = profileLoaded,
                modifier = Modifier.padding(start = Spacing.Xxs),
            )
        }
    }
}
