package com.kglabs28.yapnplay.appengine.ui.components.leaderboard

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
fun LeaderboardTitleChip(title: String, place: Int, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val tint = when (place) {
        1 -> colors.Error
        2 -> colors.IconMuted
        3 -> colors.Gold
        4 -> colors.Gold
        5 -> colors.SocialZoneAccent
        else -> colors.LiveGreen
    }
    BasicText(
        text = title,
        style = YapNPlayTheme.typography.micro.copy(color = Color.White, fontWeight = FontWeight.Bold),
        maxLines = 1,
        modifier = modifier
            .background(Brush.verticalGradient(listOf(tint.copy(alpha = TOP), tint.copy(alpha = BOTTOM))), CircleShape)
            .border(YapNPlayTheme.dimens.borderThin, tint, CircleShape)
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xxxs),
    )
}

private const val TOP = 0.55f
private const val BOTTOM = 0.25f
