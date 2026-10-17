package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * The player-card avatar from the design: a rounded square framed in the
 * player's [tint] with a glow, and their [level] in a small badge across
 * the bottom edge. While [talking] a glow of [tint] blinks on and off
 * behind it.
 */
@Composable
fun TableCardAvatar(
    tint: Color,
    talking: Boolean,
    level: String,
    modifier: Modifier = Modifier,
    size: Dp = YapNPlayTheme.dimens.ludoCardAvatar,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = RoundedCornerShape(dimens.ludoAvatarRadius)
    val blink by rememberInfiniteTransition().animateFloat(
        initialValue = 0f,
        targetValue = 1f,
        animationSpec = infiniteRepeatable(tween(AppConstants.LUDO_TALK_BLINK_MS), RepeatMode.Reverse),
    )
    Box(modifier = modifier, contentAlignment = Alignment.BottomCenter) {
        Box(
            modifier = Modifier
                .size(size)
                .drawBehind {
                    val spread = dimens.ludoTalkGlow.toPx()
                    val glow = if (talking) blink else IDLE_GLOW
                    drawRoundRect(
                        brush = Brush.radialGradient(listOf(tint.copy(alpha = glow), Color.Transparent), center, this.size.maxDimension / 2 + spread),
                        topLeft = Offset(-spread, -spread),
                        size = Size(this.size.width + spread * 2, this.size.height + spread * 2),
                        cornerRadius = CornerRadius(dimens.ludoAvatarRadius.toPx() + spread),
                    )
                }
                .clip(shape)
                .background(Brush.verticalGradient(listOf(colors.RoomsSmallButton, colors.Background)))
                .border(dimens.ludoAvatarRing, tint, shape),
            contentAlignment = Alignment.Center,
        ) {
            AppIcon(imageVector = Icons.Rounded.Person, tint = colors.IconMuted, size = size * ICON_SHARE)
        }
        BasicText(
            text = level,
            style = YapNPlayTheme.typography.micro.copy(color = colors.TextPrimary, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
            maxLines = 1,
            modifier = Modifier
                .offset(y = dimens.ludoLevelBadgeHeight / 2)
                .widthIn(min = size * BADGE_MIN_SHARE)
                .clip(shape)
                .background(colors.Background)
                .border(dimens.hairline, tint, shape)
                .padding(horizontal = Spacing.Xs),
        )
    }
}

private const val ICON_SHARE = 0.55f
private const val BADGE_MIN_SHARE = 0.6f
private const val IDLE_GLOW = 0.25f
