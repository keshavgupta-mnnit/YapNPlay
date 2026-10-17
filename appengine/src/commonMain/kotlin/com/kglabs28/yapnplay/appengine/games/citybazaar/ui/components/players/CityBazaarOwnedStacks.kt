package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.players

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ExperimentalLayoutApi
import androidx.compose.foundation.layout.FlowRow
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.tint
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbStackUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CityGroup

/** The user's cities as little card stacks, one per colour (wrapping), with the count and no name; a tap opens that colour's cards. */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CityBazaarOwnedStacks(
    stacks: List<CbStackUi>,
    onOpen: (CityGroup) -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = RoundedCornerShape(dimens.cbRowRadius / 2)
    FlowRow(
        modifier = modifier,
        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
        verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
    ) {
        stacks.forEach { stack ->
            val tint = stack.group.tint()
            Column(
                // No name under the square; it's what a screen reader says.
                modifier = Modifier.clickable { onOpen(stack.group) }.semantics { contentDescription = stack.label },
                horizontalAlignment = Alignment.CenterHorizontally,
                verticalArrangement = Arrangement.spacedBy(Spacing.Xxxs),
            ) {
                Box(contentAlignment = Alignment.Center) {
                    for (layer in stack.count - 1 downTo 0) {
                        Box(
                            Modifier
                                .offset(x = dimens.cbStackOffset * layer, y = -dimens.cbStackOffset * layer)
                                .size(dimens.cbStackCard)
                                .background(lerp(tint, colors.Background, if (layer == 0) 0f else LAYER_DARKEN), shape)
                                .border(dimens.hairline, colors.TextPrimary.copy(alpha = RIM_ALPHA), shape),
                        )
                    }
                    BasicText(text = stack.count.toString(), style = YapNPlayTheme.typography.label.copy(color = colors.Background, fontWeight = FontWeight.Bold))
                }
            }
        }
    }
}

private const val LAYER_DARKEN = 0.3f
private const val RIM_ALPHA = 0.5f
