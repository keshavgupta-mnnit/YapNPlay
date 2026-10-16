package com.kglabs28.yapnplay.appengine.ui.components.home

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.HeadlineFontFamily
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings

/**
 * 3 feature pillars row in the "ENTER GAME ZONE" card matching mockup.
 */
@Composable
fun GameZoneFeaturePillarsRow(
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween,
        verticalAlignment = Alignment.CenterVertically,
    ) {
        FeaturePillarItem(
            iconGlyph = "👥",
            title = Strings.homeFeaturePopularGames,
            subtitle = Strings.homeFeaturePopularGamesSub,
            modifier = Modifier.weight(1f),
        )
        FeaturePillarItem(
            iconGlyph = "⚙️",
            title = Strings.homeFeatureHouseRules,
            subtitle = Strings.homeFeatureHouseRulesSub,
            modifier = Modifier.weight(1f),
        )
        FeaturePillarItem(
            iconGlyph = "🗣️",
            title = Strings.homeFeatureRealPeople,
            subtitle = Strings.homeFeatureRealPeopleSub,
            modifier = Modifier.weight(1f),
        )
    }
}

@Composable
private fun FeaturePillarItem(
    iconGlyph: String,
    title: String,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        BasicText(
            text = iconGlyph,
            style = YapNPlayTheme.typography.body,
        )
        Column {
            BasicText(
                text = title,
                style = YapNPlayTheme.typography.caption.copy(
                    fontFamily = HeadlineFontFamily,
                    fontWeight = FontWeight.Bold,
                    color = colors.TextPrimary,
                ),
            )
            BasicText(
                text = subtitle,
                style = YapNPlayTheme.typography.caption.copy(
                    color = colors.TextSecondary,
                ),
            )
        }
    }
}
