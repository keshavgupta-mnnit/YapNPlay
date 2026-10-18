package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.widthIn
import androidx.compose.foundation.text.BasicText
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.profile.AvatarPresets
import com.kglabs28.yapnplay.appengine.ui.placeholders.PresetAvatarArtwork

/**
 * The player-card avatar from the design: their preset avatar in a rounded
 * square framed in the player's [tint], and their [level] in a small badge across the bottom
 * edge. (Talking shows as the card's voice wave, not on the avatar.)
 */
@Composable
fun TableCardAvatar(
    tint: Color,
    level: String,
    modifier: Modifier = Modifier,
    size: Dp = YapNPlayTheme.dimens.ludoCardAvatar,
    /** The player's preset avatar ("" = person icon). */
    avatarId: String = "",
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = RoundedCornerShape(dimens.ludoAvatarRadius)
    Box(modifier = modifier, contentAlignment = Alignment.BottomCenter) {
        Box(
            modifier = Modifier
                .size(size)
                .clip(shape)
                .background(Brush.verticalGradient(listOf(colors.RoomsSmallButton, colors.Background)))
                .border(dimens.ludoAvatarRing, tint, shape)
                // The art sits inside the frame, not over it.
                .padding(dimens.ludoAvatarRing),
            contentAlignment = Alignment.Center,
        ) {
            val preset = AvatarPresets.index(avatarId)
            if (preset >= 0) {
                PresetAvatarArtwork(index = preset, modifier = Modifier.fillMaxSize().clip(shape))
            } else {
                AppIcon(imageVector = Icons.Rounded.Person, tint = colors.IconMuted, size = size * ICON_SHARE)
            }
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
