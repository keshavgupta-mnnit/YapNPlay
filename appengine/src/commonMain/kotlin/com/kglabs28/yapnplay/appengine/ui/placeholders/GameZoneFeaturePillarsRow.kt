package com.kglabs28.yapnplay.appengine.ui.placeholders

import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import androidx.compose.material.icons.rounded.Language
import androidx.compose.material.icons.rounded.WifiTethering
import com.kglabs28.yapnplay.appengine.ui.components.common.IconCircle
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Groups
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
 * The three ways to play, as pillars in the "ENTER GAME ZONE" card (the
 * Quick Play hub's cards, in their colours), split by thin vertical dividers.
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
            icon = Icons.Rounded.Language,
            iconTint = colors.PlayRandomsAccent,
            title = Strings.homeFeatureRandoms,
            subtitle = Strings.homeFeatureRandomsSub,
            // Only the width it needs (an equal third pushed the last one into the card's leaning edge).
            modifier = Modifier.weight(1f, fill = false),
        )
        PillarDivider()
        FeaturePillarItem(
            icon = Icons.Rounded.Groups,
            iconTint = colors.PlayFriendsAccent,
            title = Strings.homeFeatureFriends,
            subtitle = Strings.homeFeatureFriendsSub,
            // Only the width it needs (an equal third pushed the last one into the card's leaning edge).
            modifier = Modifier.weight(1f, fill = false),
        )
        PillarDivider()
        FeaturePillarItem(
            icon = Icons.Rounded.WifiTethering,
            iconTint = colors.PlayNearbyAccent,
            title = Strings.homeFeatureNearby,
            subtitle = Strings.homeFeatureNearbySub,
            // Only the width it needs (an equal third pushed the last one into the card's leaning edge).
            modifier = Modifier.weight(1f, fill = false),
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
        IconCircle(imageVector = icon, fill = iconTint.copy(alpha = PILLAR_ICON_FILL), tint = iconTint, size = YapNPlayTheme.dimens.homePillarIcon, iconSize = YapNPlayTheme.dimens.homePillarIcon * PILLAR_ICON_SHARE)
        // Shrinks to fit a narrow card instead of being cut off.
        Column(modifier = Modifier.weight(1f, fill = false)) {
            ShrinkText(text = title, style = typography.homePillarTitle.copy(color = iconTint))
            ShrinkText(text = subtitle, style = typography.homePillarText.copy(color = colors.HomeTextSoft))
        }
    }
}

/** How strongly a pillar's icon colour fills its circle, and the icon's share of the circle. */
private const val PILLAR_ICON_FILL = 0.22f
private const val PILLAR_ICON_SHARE = 0.6f
