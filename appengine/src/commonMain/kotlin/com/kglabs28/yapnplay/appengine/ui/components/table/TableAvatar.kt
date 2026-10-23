package com.kglabs28.yapnplay.appengine.ui.components.table

import com.kglabs28.yapnplay.appengine.ui.components.profile.AvatarPresets
import com.kglabs28.yapnplay.appengine.ui.placeholders.PresetAvatarArtwork
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.graphics.Brush
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Person
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.Dp
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/** Round avatar for the top bar strip (their preset picture, or a person icon): its [ring] colour shows the voice state, and it glows in that colour while they talk ([glow]). */
@Composable
fun TableAvatar(
    ring: Color,
    modifier: Modifier = Modifier,
    size: Dp = YapNPlayTheme.dimens.ludoAvatar,
    ringWidth: Dp = YapNPlayTheme.dimens.ludoAvatarRing,
    glow: Boolean = false,
    /** Their avatar preset; unknown or "" shows a person icon. */
    avatarId: String = "",
) {
    val colors = YapNPlayTheme.colors
    Box(
        modifier = modifier
            .size(size)
            // A soft halo of the ring's colour round the avatar, past its edge.
            .drawBehind { if (glow) drawCircle(Brush.radialGradient(listOf(ring.copy(alpha = GLOW_ALPHA), Color.Transparent), radius = this.size.minDimension * GLOW_REACH), radius = this.size.minDimension * GLOW_REACH) }
            .clip(CircleShape)
            .background(colors.RoomsSmallButton)
            .border(ringWidth, ring, CircleShape),
        contentAlignment = Alignment.Center,
    ) {
        val preset = AvatarPresets.index(avatarId)
        if (preset >= 0) {
            PresetAvatarArtwork(index = preset, modifier = Modifier.fillMaxSize().padding(ringWidth).clip(CircleShape))
        } else {
            AppIcon(imageVector = Icons.Rounded.Person, tint = colors.IconMuted, size = size / 2)
        }
    }
}

/** The talking halo: how strong at the avatar, and how far out (× the avatar's size). */
private const val GLOW_ALPHA = 0.85f
private const val GLOW_REACH = 0.85f
