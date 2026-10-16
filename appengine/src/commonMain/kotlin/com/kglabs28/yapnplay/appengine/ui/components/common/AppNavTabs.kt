package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
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
import com.kglabs28.yapnplay.appengine.navigation.AppNavTab

/**
 * Top bar navigation tabs container (Home, Games, Rooms) matching topbar.png.
 */
@Composable
fun AppNavTabs(
    selectedTab: AppNavTab,
    onSelectTab: (AppNavTab) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Box(
        modifier = modifier
            .clip(RoundedCornerShape(dimens.cardRadius))
            .background(colors.GlassSurface)
            .border(dimens.hairline, colors.TextDisabled.copy(alpha = 0.3f), RoundedCornerShape(dimens.cardRadius))
            .padding(Spacing.Xxs),
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            NavTabItem(
                label = Strings.homeNavHome,
                iconGlyph = Strings.iconHomeTab,
                selected = selectedTab == AppNavTab.HOME,
                onClick = { onSelectTab(AppNavTab.HOME) },
            )
            NavTabItem(
                label = Strings.homeNavGames,
                iconGlyph = Strings.iconGamesTab,
                selected = selectedTab == AppNavTab.GAMES,
                onClick = { onSelectTab(AppNavTab.GAMES) },
            )
            NavTabItem(
                label = Strings.homeNavRooms,
                iconGlyph = Strings.iconRoomsTab,
                selected = selectedTab == AppNavTab.ROOMS,
                onClick = { onSelectTab(AppNavTab.ROOMS) },
            )
        }
    }
}

@Composable
private fun NavTabItem(
    label: String,
    iconGlyph: String,
    selected: Boolean,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors

    val backgroundModifier = if (selected) {
        Modifier
            .clip(CircleShape)
            .background(
                Brush.horizontalGradient(
                    colors = listOf(colors.GameZoneAccent, colors.ButtonFace),
                ),
            )
    } else {
        Modifier
            .clip(CircleShape)
    }

    val contentColor = if (selected) colors.Background else colors.TextSecondary

    Box(
        modifier = modifier
            .then(backgroundModifier)
            .clickable(onClick = onClick)
            .padding(horizontal = Spacing.Lg, vertical = Spacing.Xs),
        contentAlignment = Alignment.Center,
    ) {
        Row(
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            BasicText(
                text = iconGlyph,
                style = YapNPlayTheme.typography.caption,
            )
            BasicText(
                text = label,
                style = YapNPlayTheme.typography.caption.copy(
                    fontFamily = HeadlineFontFamily,
                    fontWeight = if (selected) FontWeight.Bold else FontWeight.Medium,
                    color = contentColor,
                ),
            )
        }
    }
}
