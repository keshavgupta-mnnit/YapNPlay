package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoColour

/** The theme colour for a Ludo colour (pieces, cards, timers). */
@Composable
fun LudoColour.tint(): Color {
    val colors = YapNPlayTheme.colors
    return when (this) {
        LudoColour.RED -> colors.PlayerRed
        LudoColour.GREEN -> colors.PlayerGreen
        LudoColour.YELLOW -> colors.PlayerYellow
        LudoColour.BLUE -> colors.PlayerBlue
    }
}
