package com.kglabs28.yapnplay.appengine.ui.components.profile

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.Role
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill

/** A glass pill washed with [tint] (red for Remove / Log out, green for Accept). */
@Composable
fun TintedGlassButton(
    text: String,
    tint: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val colors = YapNPlayTheme.colors
    Row(
        modifier = modifier
            .graphicsLayer { alpha = if (enabled) 1f else DISABLED_ALPHA }
            .height(YapNPlayTheme.dimens.smallButtonHeight)
            .glassPill(glowColor = tint, surface = lerp(colors.RoomsPanel, tint, TINT))
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.Center,
    ) {
        BasicText(text = text, style = YapNPlayTheme.typography.sectionTitle.copy(color = colors.TextPrimary), maxLines = 1)
    }
}

private const val TINT = 0.45f
private const val DISABLED_ALPHA = 0.5f
