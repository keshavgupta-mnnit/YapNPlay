package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Home
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.navigation.AppNavTab

/**
 * Top bar tabs: Home, Games and Rooms, each its own glass pill side by
 * side. The selected tab glows green (fill and border).
 */
@Composable
fun AppNavTabs(
    selectedTab: AppNavTab,
    onSelectTab: (AppNavTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Row(
        modifier = modifier.height(dimens.topBarControlHeight),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        NavTabItem(
            label = Strings.homeNavHome,
            icon = Icons.Rounded.Home,
            selected = selectedTab == AppNavTab.HOME,
            onClick = { onSelectTab(AppNavTab.HOME) },
        )
        NavTabItem(
            label = Strings.homeNavGames,
            icon = Icons.Rounded.SportsEsports,
            selected = selectedTab == AppNavTab.GAMES,
            onClick = { onSelectTab(AppNavTab.GAMES) },
        )
        NavTabItem(
            label = Strings.homeNavRooms,
            icon = Icons.Rounded.Groups,
            selected = selectedTab == AppNavTab.ROOMS,
            onClick = { onSelectTab(AppNavTab.ROOMS) },
        )
    }
}

@Composable
private fun NavTabItem(
    label: String,
    icon: ImageVector,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val shape = YapNPlayTheme.shapes.pill
    val textColor = if (selected) colors.TextPrimary else colors.HomeTextSoft
    val iconColor = if (selected) colors.TextPrimary else colors.HomeTextSoft

    val selectionModifier = if (selected) {
        Modifier.glassPill(glowColor = colors.TopBarTabSelectedBorder, surface = colors.TopBarTabSelected)
    } else {
        Modifier.glassPill(glowColor = colors.TopBarIcon, surface = colors.TopBarTabFrost)
    }

    Row(
        modifier = modifier
            .fillMaxHeight()
            .clip(shape)
            .then(selectionModifier)
            .clickable(role = Role.Tab, onClick = onClick)
            .padding(horizontal = Spacing.Xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        AppIcon(
            imageVector = icon,
            tint = iconColor,
        )
        BasicText(
            text = label,
            style = YapNPlayTheme.typography.topBarTab.copy(
                fontWeight = if (selected) FontWeight.SemiBold else FontWeight.Medium,
                color = textColor,
            ),
        )
    }
}
