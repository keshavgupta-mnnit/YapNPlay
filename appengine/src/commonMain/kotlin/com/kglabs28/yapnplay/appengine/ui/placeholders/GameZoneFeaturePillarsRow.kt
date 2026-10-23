package com.kglabs28.yapnplay.appengine.ui.placeholders

import com.kglabs28.yapnplay.appengine.ui.components.common.IconCircle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.People
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * Three feature pillars in the "ENTER GAME ZONE" card, split by thin
 * vertical dividers.
 */
@Composable
fun GameZoneFeaturePillarsRow(
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors

    Row(
        modifier = modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        FeaturePillarItem(
            icon = Icons.Rounded.Groups,
            iconTint = colors.FeatureIconPurple,
            title = Strings.homeFeaturePopularGames,
            subtitle = Strings.homeFeaturePopularGamesSub,
            modifier = Modifier.weight(1f),
        )
        PillarDivider()
        FeaturePillarItem(
            icon = Icons.Rounded.Settings,
            iconTint = colors.FeatureIconGreen,
            title = Strings.homeFeatureHouseRules,
            subtitle = Strings.homeFeatureHouseRulesSub,
            modifier = Modifier.weight(1f),
        )
        PillarDivider()
        FeaturePillarItem(
            icon = Icons.Rounded.People,
            iconTint = colors.FeatureIconGreen,
            title = Strings.homeFeatureRealPeople,
            subtitle = Strings.homeFeatureRealPeopleSub,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun PillarDivider(modifier: Modifier = Modifier) {
    val dimens = YapNPlayTheme.dimens
    Box(
        modifier = modifier
            .width(dimens.hairline)
            .height(dimens.iconMedium)
            .background(YapNPlayTheme.colors.TextDisabled.copy(alpha = 0.4f)),
    )
}

@Composable
private fun FeaturePillarItem(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        IconCircle(imageVector = icon, fill = iconTint.copy(alpha = PILLAR_ICON_FILL), tint = iconTint, size = YapNPlayTheme.dimens.pillarIconCircle)
        Column {
            BasicText(
                text = title,
                style = typography.micro.copy(
                    fontWeight = typography.sectionTitle.fontWeight,
                    color = colors.TextPrimary,
                ),
                maxLines = 1,
            )
            BasicText(
                text = subtitle,
                style = typography.micro.copy(color = colors.HomeTextSoft),
                maxLines = 1,
            )
        }
    }
}

/** How strongly a pillar's icon colour fills its circle. */
private const val PILLAR_ICON_FILL = 0.22f
