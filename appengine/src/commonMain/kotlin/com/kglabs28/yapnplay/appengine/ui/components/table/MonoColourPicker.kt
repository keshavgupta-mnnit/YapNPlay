package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * The Prism colour picker: one chip per colour, labelled with its name.
 * Shown when a Prism card leaves the colour of play open for the player
 * to name. [options] is (label, chip colour) in pick order.
 */
@Composable
fun MonoColourPicker(
    options: List<Pair<String, Color>>,
    onPick: (Int) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    Row(
        modifier = modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm, Alignment.CenterHorizontally),
    ) {
        options.forEachIndexed { index, (name, colour) ->
            val shape = YapNPlayTheme.shapes.button
            Box(
                modifier = Modifier
                    .size(dimens.monoColourChip)
                    .clip(shape)
                    .background(colour)
                    .border(dimens.borderThin, colors.TextDisabled.copy(alpha = RIM_ALPHA), shape)
                    .clickable { onPick(index) },
                contentAlignment = Alignment.Center,
            ) {
                BasicText(
                    text = name,
                    style = YapNPlayTheme.typography.micro.copy(
                        color = colors.TextPrimary,
                        fontWeight = FontWeight.Bold,
                        textAlign = TextAlign.Center,
                    ),
                    maxLines = 1,
                )
            }
        }
    }
}

private const val RIM_ALPHA = 0.4f