package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * Round mute button on another player's card: red while the user has
 * muted them (for the user only), grey otherwise. A tap toggles it.
 */
@Composable
fun TableMuteButton(
    muted: Boolean,
    description: String,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Box(
        modifier = modifier
            .size(dimens.ludoMuteButton)
            .clip(CircleShape)
            .background(if (muted) colors.Error else colors.RoomsSmallButton)
            .border(dimens.hairline, if (muted) colors.Error else colors.IconMuted, CircleShape)
            .clickable(role = Role.Switch, onClick = onToggle)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(
            imageVector = Icons.AutoMirrored.Rounded.VolumeOff,
            tint = if (muted) colors.TextPrimary else colors.IconMuted,
            size = dimens.ludoMuteIcon,
        )
    }
}
