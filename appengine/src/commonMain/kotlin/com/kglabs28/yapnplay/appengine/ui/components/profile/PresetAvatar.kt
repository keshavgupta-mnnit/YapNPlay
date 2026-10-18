package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
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
import com.kglabs28.yapnplay.appengine.ui.placeholders.PresetAvatarArtwork

/** A preset avatar in a ringed circle on a dark fill; the person icon when [avatarId] is none. */
@Composable
fun PresetAvatar(
    avatarId: String,
    size: Dp,
    modifier: Modifier = Modifier,
    rimColor: Color = YapNPlayTheme.colors.GlassShine.copy(alpha = RIM_ALPHA),
) {
    val colors = YapNPlayTheme.colors
    val index = AvatarPresets.index(avatarId)
    Box(
        modifier = modifier
            .size(size)
            .clip(CircleShape)
            .background(colors.RoomsSmallButton)
            .border(YapNPlayTheme.dimens.borderThin, rimColor, CircleShape)
            // The art sits inside the ring, not over it.
            .padding(YapNPlayTheme.dimens.borderThin),
        contentAlignment = Alignment.Center,
    ) {
        if (index >= 0) {
            PresetAvatarArtwork(index = index, modifier = Modifier.fillMaxSize().clip(CircleShape))
        } else {
            AppIcon(imageVector = Icons.Rounded.Person, tint = colors.IconMuted, size = size * ICON_SHARE)
        }
    }
}

private const val ICON_SHARE = 0.55f
private const val RIM_ALPHA = 0.2f
