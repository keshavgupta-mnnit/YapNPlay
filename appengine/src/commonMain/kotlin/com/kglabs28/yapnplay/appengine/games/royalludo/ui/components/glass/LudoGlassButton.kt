package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.glass

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
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.semantics.Role
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * A glossy 3D glass button from the Royal Ludo design: a [fill] gradient
 * body, a bright reflection across its upper half and a pale rim. The silver "Roll Dice" and the smoked "Autoplay"
 * are this button with different colours. [trailing] sits at the end;
 * [arrangement] places the text (centred by default).
 */
@Composable
fun LudoGlassButton(
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
    Row(
        modifier = modifier
            .fillMaxWidth()
            .graphicsLayer { alpha = if (enabled) 1f else DISABLED_ALPHA }
            .height(dimens.ludoGlossHeight)
            .clip(shape)
            .background(Brush.verticalGradient(fill))
            .drawWithContent {
                drawRect(
                    brush = Brush.verticalGradient(listOf(Color.White.copy(alpha = GLOSS_ALPHA), Color.Transparent), 0f, size.height / 2),
                )
                drawContent()
            }
            .border(dimens.hairline, Color.White.copy(alpha = RIM_ALPHA), shape)
            .clickable(enabled = enabled, role = Role.Button, onClick = onClick)
            .padding(horizontal = Spacing.Sm),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = arrangement,
    ) {
        BasicText(
            text = text,
            style = YapNPlayTheme.typography.sectionTitle.copy(color = textColor),
            maxLines = 1,
            modifier = Modifier.weight(1f, fill = false),
        )
        trailing()
    }
}

private const val DISABLED_ALPHA = 0.45f
private const val GLOSS_ALPHA = 0.4f
private const val RIM_ALPHA = 0.6f
