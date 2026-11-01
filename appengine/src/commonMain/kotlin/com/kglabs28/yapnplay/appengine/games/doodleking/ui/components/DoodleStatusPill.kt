package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Brush
import androidx.compose.material.icons.rounded.Schedule
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.withStyle
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleStatusUi
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.table.arenaGlass

/**
 * Over the canvas, one pill: the drawer's name in their seat's colour, what
 * they're doing in white (a brush while they draw), then the clock and the
 * time left (red in its last seconds).
 */
@Composable
fun DoodleStatusPill(status: DoodleStatusUi, timeLeft: String, urgent: Boolean, seatTint: (Int) -> Color, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val style = YapNPlayTheme.typography.label
    val icon = YapNPlayTheme.dimens.iconSmall
    Row(
        modifier = modifier
            .height(YapNPlayTheme.dimens.doodleStatusHeight)
            .arenaGlass(RoundedCornerShape(percent = PILL))
            .padding(horizontal = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        BasicText(
            text = buildAnnotatedString {
                if (status.name.isNotEmpty()) withStyle(SpanStyle(color = seatTint(status.seat))) { append(status.name) }
                withStyle(SpanStyle(color = colors.TextPrimary)) { append(status.text) }
            },
            style = style,
            maxLines = 1,
        )
        if (status.brush) AppIcon(imageVector = Icons.Rounded.Brush, tint = colors.TextPrimary, size = icon)
        if (timeLeft.isNotEmpty()) {
            Spacer(Modifier.width(Spacing.Md))
            AppIcon(imageVector = Icons.Rounded.Schedule, tint = if (urgent) colors.Error else colors.TextPrimary, size = icon)
            BasicText(text = timeLeft, style = style.copy(color = if (urgent) colors.Error else colors.TextPrimary))
        }
    }
}

private const val PILL = 50
