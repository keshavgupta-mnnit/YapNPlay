package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.topbar

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.PingQuality

/** Ping text colour: green when good, yellow when fair, red when high. */
@Composable
fun PingQuality.tint(): Color {
    val colors = YapNPlayTheme.colors
    return when (this) {
        PingQuality.GOOD -> colors.LiveGreen
        PingQuality.FAIR -> colors.PlayerYellow
        PingQuality.POOR -> colors.Error
    }
}
