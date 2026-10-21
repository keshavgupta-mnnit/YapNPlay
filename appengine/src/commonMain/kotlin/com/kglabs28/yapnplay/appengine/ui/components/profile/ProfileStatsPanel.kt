package com.kglabs28.yapnplay.appengine.ui.components.profile

import com.kglabs28.yapnplay.appengine.core.ui.ProfileStrings
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.Groups
import androidx.compose.material.icons.rounded.Insights
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileStatKind
import com.kglabs28.yapnplay.appengine.ui.screens.profile.ProfileStatUi

/**
 * "Player Stats": a thin outlined panel with its title in a pill sitting on
 * the top border (like the status pills on player cards); inside, games
 * played, games won, friends and level side by side, split by thin lines.
 */
@Composable
fun ProfileStatsPanel(stats: List<ProfileStatUi>, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    val rim = colors.IconMuted.copy(alpha = RIM_ALPHA)
    Box(modifier = modifier) {
        Row(
            modifier = Modifier
                .padding(top = dimens.profileTabHeight / 2)
                .border(dimens.hairline, rim, RoundedCornerShape(dimens.cbRowRadius))
                .height(IntrinsicSize.Min)
                .padding(start = Spacing.Sm, end = Spacing.Sm, top = dimens.profileTabHeight / 2, bottom = Spacing.Xxs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            stats.forEachIndexed { index, stat ->
                if (index > 0) Box(Modifier.padding(horizontal = Spacing.Md).width(dimens.hairline).fillMaxHeight().background(rim))
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
                    val (icon, tint) = when (stat.kind) {
                        ProfileStatKind.GAMES -> Icons.Rounded.SportsEsports to colors.SocialZoneAccent
                        ProfileStatKind.WINS -> Icons.Rounded.EmojiEvents to colors.Gold
                        ProfileStatKind.FRIENDS -> Icons.Rounded.Groups to colors.Cyan
                        ProfileStatKind.LEVEL -> Icons.Rounded.WorkspacePremium to colors.Gold
                    }
                    AppIcon(imageVector = icon, tint = tint, size = dimens.iconMedium)
                    Column {
                        BasicText(text = stat.label, style = typography.micro.copy(color = colors.TextSecondary), maxLines = 1)
                        BasicText(text = stat.value, style = typography.sectionTitle.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold))
                    }
                }
            }
        }
        // The title pill, sitting on the panel's top border.
        Row(
            modifier = Modifier
                .padding(start = Spacing.Md)
                .height(dimens.profileTabHeight)
                .clip(YapNPlayTheme.shapes.pill)
                .background(colors.RoomsPanel)
                .border(dimens.hairline, rim, YapNPlayTheme.shapes.pill)
                .padding(horizontal = Spacing.Sm),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            AppIcon(imageVector = Icons.Rounded.Insights, tint = colors.SocialZoneAccent, size = dimens.iconSmall)
            BasicText(text = ProfileStrings.profileStatsTitle, style = typography.micro.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold))
        }
    }
}

private const val RIM_ALPHA = 0.5f
