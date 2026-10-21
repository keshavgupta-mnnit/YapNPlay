package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.horizontalScroll
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.aspectRatio
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.MonoStrings
import kotlin.math.roundToInt

/**
 * The centre play arena. It holds exactly two large things — the
 * face-down draw deck on the left and the face-up discard on the right —
 * inside the directional light ring. When draw cards are stacked, the
 * penalty counter floats above the discard.
 *
 * [faceCard] draws the top of the discard pile; [hue] is the colour in
 * force and lights the ring.
 */
@Composable
fun MonoTableArena(
    hue: MonoHue?,
    clockwise: Boolean,
    drawPileCount: Int,
    discardPileCount: Int,
    activeStackPenalty: Int,
    cardWidth: Dp,
    modifier: Modifier = Modifier,
    faceCard: @Composable () -> Unit,
) {
    val colors = YapNPlayTheme.colors

    MonoTableSurface(modifier = modifier) {
      // The piles (card and label) always fit inside the oval, however short the arena is.
      BoxWithConstraints(contentAlignment = Alignment.Center) {
        val cardWidth = minOf(cardWidth, maxHeight * PILE_OF_ARENA * CARD_W_PER_H)
        MonoTurnRing(
            hue = hue,
            clockwise = clockwise,
            modifier = Modifier.fillMaxSize(),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.Lg),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MonoPile(
                label = MonoStrings.monoDrawPile,
                count = drawPileCount,
                cardWidth = cardWidth,
            ) {
                MonoCardFace(
                    look = deckLook(hue),
                    faceDown = true,
                    modifier = Modifier.width(cardWidth),
                )
            }
            Box {
                MonoPile(
                    label = MonoStrings.monoDiscardPile,
                    count = discardPileCount,
                    cardWidth = cardWidth,
                ) {
                    Box(modifier = Modifier.width(cardWidth)) { faceCard() }
                }
                if (activeStackPenalty > 0) {
                    MonoStackCounter(
                        penalty = activeStackPenalty,
                        modifier = Modifier
                            .align(Alignment.TopCenter)
                            .offset(y = -Spacing.Lg),
                    )
                }
            }
        }
      }
    }
}

/** The face-down look the draw deck wears. */
@Composable
private fun deckLook(hue: MonoHue?): MonoCardLook {
    val colors = YapNPlayTheme.colors
    return MonoCardLook(
        fill = colors.MonoBackBase,
        rim = hue.ring,
    )
}

/** One pile: its card, then its count and name underneath. */
@Composable
private fun MonoPile(
    label: String,
    count: Int,
    cardWidth: Dp,
    modifier: Modifier = Modifier,
    content: @Composable () -> Unit,
) {
    val colors = YapNPlayTheme.colors
    Column(
        modifier = modifier,
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
    ) {
        content()
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.Xxs),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            BasicText(
                text = label,
                style = YapNPlayTheme.typography.micro.copy(
                    color = colors.TextSecondary,
                    textAlign = TextAlign.Center,
                ),
                maxLines = 1,
            )
            BasicText(
                text = count.toString(),
                style = YapNPlayTheme.typography.label.copy(
                    color = colors.TextPrimary,
                    fontWeight = FontWeight.Black,
                ),
                maxLines = 1,
                modifier = Modifier
                    .clip(YapNPlayTheme.shapes.pill)
                    .background(colors.Panel)
                    .padding(horizontal = Spacing.Xs, vertical = Spacing.Xxxs),
            )
        }
    }
}

/** The outline an empty discard pile shows before the first card lands. */
@Composable
fun MonoEmptyPile(modifier: Modifier = Modifier) {
    val colors = YapNPlayTheme.colors
    val shape = YapNPlayTheme.shapes.button
    Box(
        modifier = modifier
            .aspectRatio(EMPTY_PILE_RATIO)
            .clip(shape)
            .background(colors.MonoTableFeltEdge)
            .border(YapNPlayTheme.dimens.borderThin, colors.MonoTableRim, shape),
    )
}

/** The floating penalty counter over the discard pile. */
@Composable
fun MonoStackCounter(
    penalty: Int,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    BasicText(
        text = MonoStrings.monoStack(penalty),
        style = YapNPlayTheme.typography.label.copy(
            color = colors.TextPrimary,
            fontWeight = FontWeight.Black,
        ),
        maxLines = 1,
        modifier = modifier
            .clip(YapNPlayTheme.shapes.pill)
            .background(colors.Error)
            .border(dimens.hairline, colors.TextPrimary.copy(alpha = STACK_RIM_ALPHA), YapNPlayTheme.shapes.pill)
            .padding(horizontal = Spacing.Sm, vertical = Spacing.Xxxs),
    )
}

/**
 * The local player's hand, fanned. Cards overlap as the hand grows so a
 * big hand still shows every corner index; past the point where the
 * overlap stops being readable the row scrolls. Exactly one card is
 * drawn per hand index, so the screen decides selection and playability.
 */
@Composable
fun MonoHandRow(
    handSize: Int,
    cardWidth: Dp,
    modifier: Modifier = Modifier,
    card: @Composable (Int) -> Unit,
) {
    if (handSize == 0) return
    val density = LocalDensity.current

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        // Room inside the scroller at both ends, so the tilted outer cards are never clipped.
        val margin = cardWidth * FAN_MARGIN
        val step = handStep(handSize = handSize, cardWidth = cardWidth, available = maxWidth - margin * 2)
        Layout(
            content = { repeat(handSize) { index -> card(index) } },
            modifier = Modifier.horizontalScroll(rememberScrollState()),
        ) { measurables, constraints ->
            val widthPx = with(density) { cardWidth.roundToPx() }
            val stepPx = with(density) { step.roundToPx() }
            val placeables = measurables.map { measurable ->
                measurable.measure(constraints.copy(minWidth = widthPx, maxWidth = widthPx))
            }
            val marginPx = with(density) { margin.roundToPx() }
            val totalWidth = stepPx * (placeables.size - 1).coerceAtLeast(0) + widthPx + marginPx * 2
            val cardHeight = placeables.maxOfOrNull { it.height } ?: 0
            // Fanned like a held hand: each card tilts out from the middle and the ends dip.
            val middle = (placeables.size - 1) / 2f
            val spread = (FAN_DEGREES / placeables.size.coerceAtLeast(1)).coerceAtMost(FAN_STEP_MAX)
            val dipPx = (cardHeight * FAN_DIP).roundToInt()
            layout(totalWidth, cardHeight + dipPx) {
                placeables.forEachIndexed { index, placeable ->
                    val offCentre = (index - middle) / middle.coerceAtLeast(1f)
                    placeable.placeWithLayer(marginPx + stepPx * index, (dipPx * offCentre * offCentre).roundToInt()) {
                        rotationZ = (index - middle) * spread
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    }
                }
            }
        }
    }
}

/** How far apart the hand's cards sit: no overlap until it will not fit. */
private fun handStep(handSize: Int, cardWidth: Dp, available: Dp): Dp {
    if (handSize <= 1) return cardWidth
    val fit = (available - cardWidth) / (handSize - 1).toFloat()
    return fit.coerceIn(cardWidth * MIN_STEP, cardWidth)
}

private const val STACK_RIM_ALPHA = 0.6f
/** Same as a card: width ÷ height. */
private const val EMPTY_PILE_RATIO = 0.6f
/** The whole hand's tilt, the most one card may tilt from its neighbour, and how far the ends dip (× card height). */
private const val FAN_DEGREES = 40f
private const val FAN_STEP_MAX = 5f
private const val FAN_DIP = 0.08f
private const val FAN_MARGIN = 0.6f
/** A pile (card plus label) may take this much of the arena's height; a card is 0.6 as wide as tall. */
private const val PILE_OF_ARENA = 0.62f
private const val CARD_W_PER_H = 0.6f
/** The tightest a fanned hand may get before it scrolls instead. */
private const val MIN_STEP = 0.34f
