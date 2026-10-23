package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.VolumeOff
import androidx.compose.material.icons.automirrored.rounded.VolumeUp
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassIconButton

/**
 * A spectator's voice switches, side by side: their mic (they talk in the
 * room's voice too) and their speaker (off, they hear nobody; only on
 * their device). Each is green while on, red while off.
 */
@Composable
fun TableSpectatorVoiceButtons(
    micOn: Boolean,
    speakerOn: Boolean,
    onToggleMic: () -> Unit,
    onToggleSpeaker: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    Row(modifier = modifier, horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
        val micTint = if (micOn) colors.ReadyGreen else colors.Error
        GlassIconButton(
            imageVector = if (micOn) Icons.Rounded.Mic else Icons.Rounded.MicOff,
            contentDescription = RoomStrings.lobbyMic,
            onClick = onToggleMic,
            size = YapNPlayTheme.dimens.ludoSpectatorMic,
            tint = micTint,
            glowColor = micTint,
        )
        val speakerTint = if (speakerOn) colors.ReadyGreen else colors.Error
        GlassIconButton(
            imageVector = if (speakerOn) Icons.AutoMirrored.Rounded.VolumeUp else Icons.AutoMirrored.Rounded.VolumeOff,
            contentDescription = RoomStrings.lobbySpeaker,
            onClick = onToggleSpeaker,
            size = YapNPlayTheme.dimens.ludoSpectatorMic,
            tint = speakerTint,
            glowColor = speakerTint,
        )
    }
}
