package com.kglabs28.yapnplay.appengine.ui.placeholders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.SportsEsports
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * Rooms hero art. Stand-in: a glowing gamepad until the arcade-machine
 * illustration lands (docs/placeholder-assets.md) — swap the body for
 * the drawable, callers stay the same.
 */
@Composable
fun CreateRoomGraphic(
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens

    Box(
        modifier = modifier
            .size(dimens.roomsHeroArt)
            .background(
                Brush.radialGradient(
                    listOf(colors.SocialZoneAccent.copy(alpha = 0.45f), colors.Transparent),
                ),
            ),
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(
            imageVector = Icons.Rounded.SportsEsports,
            tint = colors.Purple,
            size = dimens.roomsHeroArt / 2,
        )
    }
}
