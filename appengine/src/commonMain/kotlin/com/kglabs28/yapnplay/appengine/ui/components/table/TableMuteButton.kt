package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
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
    /** Their mic is on. Off (and not muted) there is nothing to mute: the button is greyed out. */
    micOn: Boolean = true,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    // Three looks: muted (red, tap to hear them again), speaking (lit, tap to mute), mic off (grey, nothing to do).
    val enabled = muted || micOn
    val fill = when {
        muted -> colors.Error
        micOn -> colors.Cyan.copy(alpha = LIVE_FILL_ALPHA)
        else -> colors.RoomsSmallButton.copy(alpha = OFF_ALPHA)
    }
    val rim = when {
        muted -> colors.Error
        micOn -> colors.Cyan
        else -> colors.IconMuted.copy(alpha = OFF_ALPHA)
    }
    Box(
        modifier = modifier
            .size(dimens.ludoMuteButton)
            .clip(CircleShape)
            .background(fill)
            .border(dimens.hairline, rim, CircleShape)
            .clickable(enabled = enabled, role = Role.Switch, onClick = onToggle)
            .semantics { contentDescription = description },
        contentAlignment = Alignment.Center,
    ) {
        AppIcon(
            imageVector = if (muted || !micOn) Icons.AutoMirrored.Rounded.VolumeOff else Icons.AutoMirrored.Rounded.VolumeUp,
            tint = when {
                muted -> colors.TextPrimary
                micOn -> colors.Cyan
                else -> colors.IconMuted.copy(alpha = OFF_ALPHA)
            },
            size = dimens.ludoMuteIcon,
        )
    }
}

private const val LIVE_FILL_ALPHA = 0.18f
private const val OFF_ALPHA = 0.5f
