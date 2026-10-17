package com.kglabs28.yapnplay.appengine.ui.components.lobby

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.semantics.Role
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon

/**
 * Mic / Speaker control from the lobby design: a glass tile with a neon
 * rim, a small switch (icon on the left, knob on the right) and the label
 * under it. On = [accent] (pink mic, cyan speaker) with a glow; off =
 * grey, knob back on the left.
 */
@Composable
fun LobbyVoiceToggle(
    icon: ImageVector,
    label: String,
    on: Boolean,
    accent: Color,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = RoundedCornerShape(dimens.rowRadius)
    val tone = if (on) accent else colors.TextDisabled
    val pill = YapNPlayTheme.shapes.pill

    Column(
        modifier = modifier
            .size(width = dimens.voiceToggleWidth, height = dimens.voiceToggleHeight)
            .then(if (on) Modifier.shadow(dimens.neonGlow, shape, ambientColor = accent, spotColor = accent) else Modifier)
            .clip(shape)
            .background(
                Brush.verticalGradient(
                    listOf(tone.copy(alpha = if (on) 0.28f else 0.16f), colors.Background.copy(alpha = 0.85f)),
                ),
            )
            .border(dimens.borderThin, tone, shape)
            .clickable(role = Role.Switch, onClick = onToggle)
            .padding(Spacing.Xxs),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceEvenly,
    ) {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(dimens.voiceToggleSwitch)
                .clip(pill)
                .background(tone.copy(alpha = 0.22f))
                .border(dimens.hairline, tone.copy(alpha = 0.8f), pill)
                .padding(horizontal = Spacing.Xxs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            if (on) {
                AppIcon(imageVector = icon, tint = accent, size = dimens.iconSmall)
                Box(Modifier.weight(1f))
                Knob(color = accent)
            } else {
                Knob(color = colors.TextDisabled)
                Box(Modifier.weight(1f))
                AppIcon(imageVector = icon, tint = colors.TextSecondary, size = dimens.iconSmall)
            }
        }
        BasicText(
            text = label,
            style = YapNPlayTheme.typography.label.copy(color = if (on) colors.TextPrimary else colors.TextSecondary),
            maxLines = 1,
        )
    }
}

@Composable
private fun Knob(color: Color) {
    val dimens = YapNPlayTheme.dimens
    Box(
        Modifier
            .size(dimens.voiceToggleKnob)
            .clip(CircleShape)
            .background(color)
            .border(dimens.hairline, YapNPlayTheme.colors.GlassShine.copy(alpha = 0.4f), CircleShape),
    )
}
