package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.components.actions

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.components.table.tableFrostedGlass

/** A tactile action tile from the design (Build House/Hotel, Trade): frosted glass with a neon [accent] rim and label. */
@Composable
fun CityBaazarActionButton(
    text: String,
    accent: Color,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
) {
    val dimens = YapNPlayTheme.dimens
    val shape = RoundedCornerShape(dimens.ludoGlossRadius)
    Box(
        modifier = modifier
            .fillMaxWidth()
            .height(dimens.cbActionButtonHeight)
            .graphicsLayer { alpha = if (enabled) 1f else DISABLED_ALPHA }
            .tableFrostedGlass(tint = accent, shape = shape, radius = dimens.ludoGlossRadius, glow = dimens.zero)
            .border(dimens.borderThin, accent, shape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.Sm),
        contentAlignment = Alignment.Center,
    ) {
        BasicText(
            text = text,
            style = YapNPlayTheme.typography.sectionTitle.copy(color = accent, fontWeight = FontWeight.Bold, textAlign = TextAlign.Center),
            maxLines = 2,
        )
    }
}

private const val DISABLED_ALPHA = 0.45f
