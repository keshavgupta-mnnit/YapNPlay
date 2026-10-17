package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Mic
import androidx.compose.material.icons.rounded.MicOff
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassIconButton

/** A spectator's mic (they talk in the room's voice too): green while on, red while off. */
@Composable
fun LudoSpectatorMicButton(
    micOn: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val tint = if (micOn) colors.ReadyGreen else colors.Error
    GlassIconButton(
        imageVector = if (micOn) Icons.Rounded.Mic else Icons.Rounded.MicOff,
        contentDescription = Strings.lobbyMic,
        onClick = onToggle,
        modifier = modifier,
        size = YapNPlayTheme.dimens.ludoSpectatorMic,
        tint = tint,
        glowColor = tint,
    )
}
