package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * The profile's bottom cards share one shape: an icon and title (a red
 * count badge when [badge] > 0), one line of description, then the
 * [action] row (a button at the end, or the ID field with its button).
 */
@Composable
fun ProfileActionCard(
    icon: ImageVector,
    iconTint: Color,
    title: String,
    description: String,
    modifier: Modifier = Modifier,
    descriptionColor: Color = YapNPlayTheme.colors.RoomsSubtitle,
    badge: Int = 0,
    action: @Composable RowScope.() -> Unit,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    Column(
        modifier = modifier
            .glassPanel(glowColor = colors.IconMuted, surface = colors.RoomsPanel.copy(alpha = PANEL_ALPHA))
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
            AppIcon(imageVector = icon, tint = iconTint, size = dimens.iconMedium)
            BasicText(text = title, style = typography.sectionTitle.copy(color = colors.TextPrimary), maxLines = 1)
            if (badge > 0) {
                Box(modifier = Modifier.size(dimens.profileBadge).background(colors.Error, CircleShape), contentAlignment = Alignment.Center) {
                    BasicText(text = badge.toString(), style = typography.micro.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold))
                }
            }
        }
        BasicText(text = description, style = typography.micro.copy(color = descriptionColor), maxLines = 1)
        Row(
            modifier = Modifier.fillMaxWidth(),
            verticalAlignment = Alignment.CenterVertically,
            horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
            content = action,
        )
    }
}

private const val PANEL_ALPHA = 0.9f
