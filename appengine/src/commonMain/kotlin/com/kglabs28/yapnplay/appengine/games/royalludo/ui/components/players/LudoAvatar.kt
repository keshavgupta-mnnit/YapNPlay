package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * Round player avatar with a [ring] colour. While [talking], a glow of
 * [glow] blinks on and off behind it (the speaker's piece colour).
 */
@Composable
fun LudoAvatar(
    ring: Color,
    talking: Boolean,
    modifier: Modifier = Modifier,
    glow: Color = ring,
    size: Dp = YapNPlayTheme.dimens.ludoAvatar,
    ringWidth: Dp = YapNPlayTheme.dimens.ludoAvatarRing,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val blink by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(AppConstants.LUDO_TALK_BLINK_MS), RepeatMode.Reverse),
    )
    Box(
        modifier = modifier
            .size(size)
            .drawBehind {
                if (talking) {
                    val reach = this.size.minDimension / 2 + dimens.ludoTalkGlow.toPx()
                    drawCircle(
                        brush = Brush.radialGradient(listOf(glow.copy(alpha = blink), Color.Transparent), center = center, radius = reach),
                        radius = reach,
                    )
                }
            }
            .clip(CircleShape)
            .background(colors.RoomsSmallButton)
            .border(ringWidth, ring, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(imageVector = Icons.Rounded.Person, tint = colors.IconMuted, size = size / 2)
    }
}
