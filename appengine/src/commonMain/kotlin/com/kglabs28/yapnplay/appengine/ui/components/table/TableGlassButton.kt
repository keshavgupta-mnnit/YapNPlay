package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.RowScope
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPill
import androidx.compose.ui.graphics.luminance
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * The table's see-through glass button, like every other button in the
 * app: dark glass with a sheen and a rim glowing in the brightest colour
 * of [fill] (white when it's all dark). A dark [textColor] (meant for a
 * bright fill) reads in the rim's colour instead. [trailing] sits at the
 * end; [arrangement] places the text (centred by default).
 */
@Composable
fun TableGlassButton(
    text: String,
    onClick: () -> Unit,
    fill: List<Color>,
    textColor: Color,
    modifier: Modifier = Modifier,
    enabled: Boolean = true,
    arrangement: Arrangement.Horizontal = Arrangement.Center,
    trailing: @Composable RowScope.() -> Unit = {},
) {
    val dimens = YapNPlayTheme.dimens
    val shape = RoundedCornerShape(dimens.ludoGlossRadius)
    val brightest = fill.maxByOrNull { it.luminance() } ?: Color.White
    val glow = if (brightest.luminance() < DARK_GLOW) Color.White.copy(alpha = RIM_ALPHA) else brightest
    val ink = if (textColor.luminance() < DARK_TEXT) glow else textColor
    Row(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = if (enabled) 1f else DISABLED_ALPHA }
            .height(dimens.ludoGlossHeight)
            .glassPill(glowColor = glow, surface = YapNPlayTheme.colors.RoomsPanel, shape = shape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = arrangement,
    ) {
        BasicText(
            text = text,
            style = YapNPlayTheme.typography.sectionTitle.copy(color = ink),
            maxLines = 1,
            modifier = Modifier.weight(1f, fill = false),
        )
        trailing()
    }
}

private const val DISABLED_ALPHA = 0.45f
private const val RIM_ALPHA = 0.6f
/** Below this luminance a fill colour is too dark to glow as a rim. */
private const val DARK_GLOW = 0.15f
/** Below this luminance text was meant for a bright fill and is lost on dark glass. */
private const val DARK_TEXT = 0.3f
