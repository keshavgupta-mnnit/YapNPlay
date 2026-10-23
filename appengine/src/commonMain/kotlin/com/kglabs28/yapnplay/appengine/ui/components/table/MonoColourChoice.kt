package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.Role
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.AppBrushes
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * Choosing a colour (a wild card, or the Roulette): one glossy button per
 * colour, filled with that colour (lighter at the top, a glass sheen, a
 * pale rim) and its name in white. [options] is (name, colour) in pick
 * order.
 */
@Composable
fun MonoColourChoice(
    options: List<Pair<String, Color>>,
    onPick: (index: Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = YapNPlayTheme.shapes.pill
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm, Alignment.CenterHorizontally),
    ) {
        options.forEachIndexed { index, (name, colour) ->
            Box(
                modifier = Modifier
                    .weight(1f)
                    .height(dimens.ctaHeight)
                    .clip(shape)
                    .background(Brush.verticalGradient(listOf(lerp(colour, Color.White, LIGHT_TOP), colour)))
                    .background(AppBrushes.glassSheen)
                    .border(dimens.borderThin, Color.White.copy(alpha = RIM_ALPHA), shape)
                    .clickable(role = Role.Button) { onPick(index) },
                contentAlignment = Alignment.Center,
            ) {
                BasicText(text = name, style = YapNPlayTheme.typography.sectionTitle.copy(color = colors.TextPrimary, fontWeight = FontWeight.Black), maxLines = 1)
            }
        }
    }
}

/** How much lighter the top of each colour is, and how bright the rim. */
private const val LIGHT_TOP = 0.25f
private const val RIM_ALPHA = 0.5f
