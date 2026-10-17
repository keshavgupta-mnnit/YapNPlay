package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.glass.LudoGlassButton
import com.kglabs28.yapnplay.appengine.ui.components.common.OnOffSwitch

/**
 * The user's own controls: smoked glass Mic and Autoplay buttons (label
 * at the start, switch at the end), then the glossy silver Roll Dice,
 * lit only on their turn and off while autoplay plays for them.
 */
@Composable
fun LudoMyControls(
    canRoll: Boolean,
    autoplay: Boolean,
    micOn: Boolean,
    onRoll: () -> Unit,
    onToggleAutoplay: () -> Unit,
    onToggleMic: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val smoke = listOf(colors.LudoSmokeTop, colors.LudoSmokeBottom)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
        LudoGlassButton(
            text = Strings.lobbyMic,
            onClick = onToggleMic,
            fill = smoke,
            textColor = if (micOn) colors.TextPrimary else colors.TextSecondary,
            arrangement = Arrangement.SpaceBetween,
        ) {
            OnOffSwitch(on = micOn, onToggle = onToggleMic)
        }
        LudoGlassButton(
            text = Strings.ludoAutoplay,
            onClick = onToggleAutoplay,
            fill = smoke,
            textColor = if (autoplay) colors.TextPrimary else colors.TextSecondary,
            arrangement = Arrangement.SpaceBetween,
        ) {
            OnOffSwitch(on = autoplay, onToggle = onToggleAutoplay)
        }
        LudoGlassButton(
            text = Strings.ludoRollDice,
            onClick = onRoll,
            fill = listOf(colors.LudoSilverTop, colors.LudoSilverMid, colors.LudoSilverBottom),
            textColor = colors.LudoSilverText,
            enabled = canRoll && !autoplay,
        )
    }
}
