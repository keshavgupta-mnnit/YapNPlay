package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.background
import com.kglabs28.yapnplay.appengine.core.designsystem.AppBrushes
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.draw.clip
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/** The latest game event, in a glassy black bar at the bottom of the table (rim and dot in its tone's colour). */
@Composable
fun TableFeedSnackbar(line: TableFeedLineUi, modifier: Modifier = Modifier, playerTints: List<Color> = emptyList()) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val accent = line.tone.accent()
    val radius = dimens.ludoGlossRadius
    Row(
        modifier = modifier
            .widthIn(max = dimens.feedSnackMaxWidth)
            .clip(RoundedCornerShape(radius))
            .background(Brush.verticalGradient(listOf(colors.SnackGlassTop, colors.SnackGlassBottom)))
            .background(AppBrushes.glassSheen)
            .border(dimens.hairline, accent.copy(alpha = RIM_ALPHA), RoundedCornerShape(radius))
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xs),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        Box(Modifier.size(dimens.statusDotSmall).background(accent, CircleShape))
        BasicText(text = feedText(line, playerTints), style = YapNPlayTheme.typography.label.copy(color = colors.TextPrimary), maxLines = 2)
    }
}

private const val RIM_ALPHA = 0.6f
