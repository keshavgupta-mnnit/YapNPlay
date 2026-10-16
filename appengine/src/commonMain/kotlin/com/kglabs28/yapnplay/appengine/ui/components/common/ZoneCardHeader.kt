package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Big card heading: icon badge, two-tone title ("ENTER " + "GAME ZONE")
 * and a subtitle underneath. Home cards share it.
 */
@Composable
fun ZoneCardHeader(
    icon: ImageVector,
    iconFill: Color,
    title: String,
    titleAccent: String,
    accentColor: Color,
    subtitle: String,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.Top,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        IconCircle(
            imageVector = icon,
            fill = iconFill,
            iconSize = YapNPlayTheme.dimens.iconMedium,
        )
        Column(verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs)) {
            Row {
                BasicText(
                    text = title,
                    style = typography.cardTitle.copy(color = colors.TextPrimary),
                )
                BasicText(
                    text = titleAccent,
                    style = typography.cardTitle.copy(color = accentColor),
                )
            }
            BasicText(
                text = subtitle,
                style = typography.micro.copy(color = colors.RoomsSubtitle),
            )
        }
    }
}
