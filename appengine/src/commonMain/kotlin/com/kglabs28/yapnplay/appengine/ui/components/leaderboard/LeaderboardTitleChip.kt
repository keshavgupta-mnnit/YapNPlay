package com.kglabs28.yapnplay.appengine.ui.components.leaderboard

import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.ui.components.common.ShrinkText
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/** A fun title ("🔥 Chaos King") in a glowing pill, coloured by place. */
@Composable
fun LeaderboardTitleChip(title: String, place: Int, modifier: Modifier = Modifier, textStyle: TextStyle = YapNPlayTheme.typography.micro) {
    val colors = YapNPlayTheme.colors
    val tint = when (place) {
        1 -> colors.Error
        2 -> colors.IconMuted
        3 -> colors.Gold
        4 -> colors.Gold
        5 -> colors.SocialZoneAccent
        else -> colors.LiveGreen
    }
    // Centred under the name; a long title shrinks to fit instead of being cut.
    ShrinkText(
        text = title,
        style = textStyle.copy(color = Color.White, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
        minScale = CHIP_MIN_SCALE,
        modifier = modifier
            .background(Brush.verticalGradient(listOf(tint.copy(alpha = TOP), tint.copy(alpha = BOTTOM))), CircleShape)
            .border(YapNPlayTheme.dimens.borderThin, tint, CircleShape)
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xxxs),
    )
}

private const val TOP = 0.55f
private const val CHIP_MIN_SCALE = 0.5f
private const val BOTTOM = 0.25f
