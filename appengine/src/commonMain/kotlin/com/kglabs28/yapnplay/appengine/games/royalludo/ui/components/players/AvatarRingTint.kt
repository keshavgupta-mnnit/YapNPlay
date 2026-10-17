package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.AvatarRing

/** Ring colour for a top-bar avatar: green while talking, red with the mic off. */
@Composable
fun AvatarRing.tint(): Color {
    val colors = YapNPlayTheme.colors
    return when (this) {
        AvatarRing.TALKING -> colors.LiveGreen
        AvatarRing.MUTED -> colors.Error
        AvatarRing.IDLE -> colors.AvatarRingSilver
    }
}
