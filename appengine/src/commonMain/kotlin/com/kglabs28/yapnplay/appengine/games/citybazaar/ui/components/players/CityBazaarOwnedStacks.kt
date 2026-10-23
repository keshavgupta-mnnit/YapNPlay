package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.players

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
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
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.graphics.lerp
import androidx.compose.ui.semantics.contentDescription
import androidx.compose.ui.semantics.semantics
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.components.board.tint
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbStackUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CityGroup

/**
 * The user's cities as little card stacks, one per colour, with the count
 * and no name; a tap opens that colour's cards. Sized so five stacks (and
 * the gaps between them) fill exactly the width given, so every colour
 * stays in sight; more wrap to the next row.
 */
@OptIn(ExperimentalLayoutApi::class)
@Composable
fun CityBazaarOwnedStacks(
    stacks: List<CbStackUi>,
    onOpen: (CityGroup) -> Unit,
    modifier: Modifier = Modifier,
    /** A fixed square size (the row then takes only what its stacks need); null fits five to the width. */
    cardSize: Dp? = null,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val shape = RoundedCornerShape(dimens.cbRowRadius / 2)
    BoxWithConstraints(modifier = modifier) {
    val gap = Spacing.Xs
    // Room for the deepest stack's offset layers inside each slot.
    val card = cardSize ?: (((maxWidth - gap * (PER_ROW - 1)) / PER_ROW) - dimens.cbStackOffset * (MAX_LAYERS - 1))
    FlowRow(
        maxItemsInEachRow = PER_ROW,
        horizontalArrangement = Arrangement.spacedBy(gap),
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
                                .size(card)
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
}

private const val PER_ROW = 5
/** A colour group holds at most 4 cities. */
private const val MAX_LAYERS = 4

private const val LAYER_DARKEN = 0.3f
private const val RIM_ALPHA = 0.5f
