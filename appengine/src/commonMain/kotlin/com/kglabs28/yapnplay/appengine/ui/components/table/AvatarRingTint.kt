package com.kglabs28.yapnplay.appengine.ui.components.table

import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayColors
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/** Ring colour for a top-bar avatar: purple while talking, red with the mic off. */
@Composable
fun AvatarRing.tint(): Color {
    val colors = YapNPlayTheme.colors
    return when (this) {
        AvatarRing.TALKING -> colors.Purple
        AvatarRing.TALKING_MUTED -> YapNPlayColors.ArenaTalkMutedLight
        AvatarRing.MUTED -> colors.Error
        AvatarRing.IDLE -> colors.AvatarRingSilver
    }
}
