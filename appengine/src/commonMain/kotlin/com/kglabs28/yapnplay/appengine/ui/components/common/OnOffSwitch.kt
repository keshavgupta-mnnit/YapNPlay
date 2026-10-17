package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.neonGlow
import com.kglabs28.yapnplay.appengine.core.ui.Strings

/**
 * Pill ON/OFF switch (house rules, mic, speaker): neon green with the knob on
 * the right when on, dark with the knob on the left when off. Read-only
 * (dimmed, ignores taps) when not [enabled].
 */
@Composable
fun OnOffSwitch(
    on: Boolean,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = YapNPlayTheme.shapes.pill

    Box(
        modifier = modifier
            .graphicsLayer { alpha = if (enabled) 1f else DISABLED_ALPHA }
            .size(width = dimens.switchWidth, height = dimens.switchHeight)
            .then(
                if (on) {
                    Modifier.neonGlow(color = colors.ReadyGreen, shape = shape, fillAlpha = 0.35f)
                } else {
                    Modifier
                        .clip(shape)
                        .background(colors.RoomsSmallButton)
                        .border(dimens.hairline, colors.GlassShine.copy(alpha = 0.25f), shape)
                },
            )
            .clickable(enabled = enabled, role = Role.Switch, onClick = onToggle)
            .padding(horizontal = Spacing.Xxs),
    ) {
        BasicText(
            text = if (on) Strings.lobbyOn else Strings.lobbyOff,
            style = YapNPlayTheme.typography.micro.copy(
                color = if (on) colors.ReadyGreen else colors.TextSecondary,
            ),
            modifier = Modifier.align(if (on) Alignment.CenterStart else Alignment.CenterEnd),
        )
        Box(
            modifier = Modifier
                .align(if (on) Alignment.CenterEnd else Alignment.CenterStart)
                .size(dimens.switchKnob)
                .clip(shape)
                .background(if (on) colors.ReadyGreen else colors.TextPrimary),
        )
    }
}

private const val DISABLED_ALPHA = 0.5f
