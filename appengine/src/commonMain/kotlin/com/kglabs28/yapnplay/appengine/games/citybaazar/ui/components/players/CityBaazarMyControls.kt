package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.players

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.height
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.OnOffSwitch
import com.kglabs28.yapnplay.appengine.ui.components.table.TableGlassButton

/** The user's pod controls: Mic and Autoplay rows (label, switch), then the silver Roll Dice. */
@Composable
fun CityBaazarMyControls(
    canRoll: Boolean,
    rollLabel: String,
    autoplay: Boolean,
    micOn: Boolean,
    onRoll: () -> Unit,
    onToggleAutoplay: () -> Unit,
    onToggleMic: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val height = YapNPlayTheme.dimens.cbControlHeight
    val smoke = listOf(colors.LudoSmokeTop, colors.LudoSmokeBottom)
    Column(modifier = modifier, verticalArrangement = Arrangement.spacedBy(Spacing.Xxs)) {
        TableGlassButton(
            text = Strings.lobbyMic,
            onClick = onToggleMic,
            fill = smoke,
            textColor = if (micOn) colors.TextPrimary else colors.TextSecondary,
            arrangement = Arrangement.SpaceBetween,
            modifier = Modifier.height(height),
        ) {
            OnOffSwitch(on = micOn, onToggle = onToggleMic)
        }
        TableGlassButton(
            text = Strings.ludoAutoplay,
            onClick = onToggleAutoplay,
            fill = smoke,
            textColor = if (autoplay) colors.TextPrimary else colors.TextSecondary,
            arrangement = Arrangement.SpaceBetween,
            modifier = Modifier.height(height),
        ) {
            OnOffSwitch(on = autoplay, onToggle = onToggleAutoplay)
        }
        TableGlassButton(
            text = rollLabel,
            onClick = onRoll,
            fill = listOf(colors.LudoSilverTop, colors.LudoSilverMid, colors.LudoSilverBottom),
            textColor = colors.LudoSilverText,
            enabled = canRoll,
            modifier = Modifier.height(height),
        )
    }
}
