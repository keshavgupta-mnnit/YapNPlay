package com.kglabs28.yapnplay.appengine.ui.components.lobby

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import com.kglabs28.yapnplay.appengine.ui.components.common.AppIcon
import com.kglabs28.yapnplay.appengine.ui.components.common.OnOffSwitch

/**
 * Glass "🎤 Mic [ON]" / "🔊 Speaker [OFF]" card for the lobby bottom bar.
 */
@Composable
fun VoiceToggleCard(
    icon: ImageVector,
    label: String,
    on: Boolean,
    accent: Color,
    onToggle: () -> Unit,
    modifier: Modifier = Modifier,
) {
    Row(
        modifier = modifier
            .fillMaxHeight()
            .glassPill(glowColor = accent, surface = YapNPlayTheme.colors.RoomsPanel)
            .padding(horizontal = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        AppIcon(imageVector = icon, tint = accent)
        BasicText(
            text = label,
            style = YapNPlayTheme.typography.sectionTitle.copy(color = YapNPlayTheme.colors.TextPrimary),
        )
        OnOffSwitch(on = on, onToggle = onToggle)
    }
}
