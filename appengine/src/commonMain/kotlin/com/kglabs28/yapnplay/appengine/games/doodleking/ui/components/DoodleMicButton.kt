package com.kglabs28.yapnplay.appengine.games.doodleking.ui.components

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.DoodleStrings
import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassIconButton

/** The user's mic beside their card: green on, red off; dim and closed for their turn to draw ([locked]). */
@Composable
fun DoodleMicButton(micOn: Boolean, locked: Boolean, onToggle: () -> Unit, modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val tint = when {
        locked -> colors.IconMuted
        micOn -> colors.ReadyGreen
        else -> colors.Error
    }
    GlassIconButton(
        imageVector = if (micOn) Icons.Rounded.Mic else Icons.Rounded.MicOff,
        contentDescription = if (locked) DoodleStrings.dkMicLocked else RoomStrings.lobbyMic,
        onClick = onToggle,
        size = YapNPlayTheme.dimens.doodleToolButton,
        tint = tint,
        glowColor = tint,
        modifier = modifier,
    )
}
