package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.IntSize
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.runtime.remember
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.zIndex
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.Layout
import androidx.compose.ui.layout.findRootCoordinates
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
 * The centre play arena, on the table of the room behind it: the
 * face-down draw deck on the left and the face-up discard on the right,
 * both lying tilted on the table. Each pile is as thick as the cards in
 * it (no numbers: the height tells), the discard glows in the colour in
 * force, and the direction arrows run round them. When draw cards are
 * stacked, the penalty counter floats above the discard.
 *
 * [faceCard] draws the top of the discard pile.
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
    anchors: MonoAnchors? = null,
    chaos: Boolean = false,
    /** A tap on the draw pile (draws, like the Draw button); null leaves it untappable. */
    onDeckClick: (() -> Unit)? = null,
    /** What just happened, shown under the piles; null shows nothing. */
    note: String? = null,
    faceCard: @Composable () -> Unit,
) {
    val colors = YapNPlayTheme.colors
    val density = LocalDensity.current
    // The screen's size: the table picture covers it, so it fixes where the picture's rings land.
    var screen by remember { mutableStateOf(IntSize.Zero) }
    BoxWithConstraints(
        modifier = modifier.onGloballyPositioned { screen = it.findRootCoordinates().size },
        contentAlignment = Alignment.Center,
    ) {
        // The piles stay inside the table's innermost ring, and inside the arena, however short it is.
        val ring = with(density) { innerRingSize(screen).let { Size(it.width.toDp().value, it.height.toDp().value) } }
        val byRing = if (ring.width > 0f) minOf(ring.height * RING_FILL_H * CARD_W_PER_H, (ring.width * RING_FILL_W - Spacing.Xl.value) / 2).dp else cardWidth
        val cardWidth = minOf(cardWidth, byRing, maxHeight * PILE_OF_ARENA * CARD_W_PER_H).coerceAtLeast(MIN_PILE_WIDTH)
        MonoTurnRing(
            hue = hue,
            clockwise = clockwise,
            modifier = Modifier.fillMaxSize(),
        )
        Row(
            horizontalArrangement = Arrangement.spacedBy(Spacing.Xl),
            verticalAlignment = Alignment.CenterVertically,
        ) {
            MonoTiltedPile(
                count = drawPileCount,
                cardWidth = cardWidth,
                edge = colors.MonoDeckEdge,
                edgeShade = colors.MonoDeckEdgeShade,
                tiltZ = DECK_TURN,
                modifier = Modifier
                    .monoAnchor(anchors, MonoSpot.Deck)
                    .then(if (onDeckClick != null) Modifier.clickable(interactionSource = null, indication = null, onClick = onDeckClick) else Modifier),
            ) { tilt ->
                if (drawPileCount > 0) {
                    MonoCardFace(
                        look = MonoCardLook(fill = colors.MonoBackBase, rim = hue.ring),
                        faceDown = true,
                        chaos = chaos,
                        modifier = tilt,
                    )
                } else {
                    MonoEmptyPile(modifier = tilt)
                }
            }
            MonoTiltedPile(
                count = discardPileCount,
                cardWidth = cardWidth,
                edge = colors.MonoDiscardEdge,
                edgeShade = colors.MonoTableRim,
                tiltZ = DISCARD_TURN,
                glow = hue.ring,
                modifier = Modifier.monoAnchor(anchors, MonoSpot.Discard),
            ) { tilt ->
                Box(modifier = tilt) { faceCard() }
            }
        }
        // Under the piles, between the table's rings and centred on it: the stack's total, then
        // what just happened. They float over the table and take no room, so the piles never move.
        val pileHalf = cardWidth / CARD_W_PER_H * LYING_HEIGHT / 2
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(top = maxHeight / 2 + pileHalf + Spacing.Xs)
                .zIndex(1f),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.Xxs),
        ) {
            if (activeStackPenalty > 0) MonoStackCounter(penalty = activeStackPenalty)
            note?.let { MonoTableNote(it) }
        }
    }
}

/**
 * A pile lying on the table: its top card tilted back, and under it one
 * card edge per few cards it holds, so a big pile stands visibly taller
 * than a small one. [glow] lights the felt under it.
 */
@Composable
private fun MonoTiltedPile(
    count: Int,
    cardWidth: Dp,
    edge: Color,
    edgeShade: Color,
    tiltZ: Float,
    modifier: Modifier = Modifier,
    glow: Color? = null,
    top: @Composable (Modifier) -> Unit,
) {
    val step = YapNPlayTheme.dimens.monoPileDepthStep
    val layers = ((count + CARDS_PER_LAYER - 1) / CARDS_PER_LAYER).coerceIn(0, MAX_LAYERS)
    val edgeShape = RoundedCornerShape(percent = EDGE_CORNER_PERCENT)
    Box(
        modifier = modifier
            .padding(bottom = step * MAX_LAYERS)
            .drawBehind {
                if (glow == null) return@drawBehind
                drawOval(
                    Brush.radialGradient(listOf(glow.copy(alpha = GLOW_ALPHA), Color.Transparent), center, size.width * GLOW_REACH),
                    topLeft = Offset(-size.width * GLOW_SPREAD, -size.height * GLOW_SPREAD),
                    size = Size(size.width * (1 + GLOW_SPREAD * 2), size.height * (1 + GLOW_SPREAD * 2)),
                )
            },
        contentAlignment = Alignment.TopCenter,
    ) {
        // Deepest edge first, so the top card covers the rest.
        for (layer in layers downTo 1) {
            Box(
                modifier = Modifier
                    .offset(y = step * layer)
                    .width(cardWidth)
                    .aspectRatio(CARD_W_PER_H)
                    .tableTilt(tiltZ)
                    .clip(edgeShape)
                    .background(if (layer % 2 == 0) edgeShade else edge),
            )
        }
        top(
            Modifier
                .width(cardWidth)
                .tableTilt(tiltZ),
        )
    }
}

/** A card lying on the table: leant back away from the viewer, turned a little. */
private fun Modifier.tableTilt(turn: Float): Modifier = graphicsLayer {
    rotationX = TABLE_TILT
    rotationZ = turn
    cameraDistance = CAMERA_DISTANCE * density
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

/** The penalty counter under the piles. */
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
 * The table picture's innermost ring on a screen of [screen] px: the
 * picture covers the screen (cropped), so the ring scales with it.
 */
private fun innerRingSize(screen: IntSize): Size {
    if (screen.width == 0 || screen.height == 0) return Size.Zero
    val scale = maxOf(screen.width / TABLE_IMAGE_W, screen.height / TABLE_IMAGE_H)
    return Size(TABLE_IMAGE_W * INNER_RING_W * scale, TABLE_IMAGE_H * INNER_RING_H * scale)
}

/**
 * The local player's hand, fanned. Cards overlap as the hand grows so a
 * big hand still shows every corner index; past the point where the
 * overlap stops being readable the row scrolls. A playable card rises
 * well past the fan's own dip, so it never reads as just a middle card. Exactly one card is
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
            val handWidth = stepPx * (placeables.size - 1).coerceAtLeast(0) + widthPx + marginPx * 2
            // A hand narrower than the row sits in its middle; a wider one fills it and scrolls.
            val rowWidth = maxOf(handWidth, this@BoxWithConstraints.constraints.maxWidth)
            val start = (rowWidth - handWidth) / 2
            val cardHeight = placeables.maxOfOrNull { it.height } ?: 0
            // Fanned like a held hand: each card tilts out from the middle and the ends dip.
            val middle = (placeables.size - 1) / 2f
            val spread = (FAN_DEGREES / placeables.size.coerceAtLeast(1)).coerceAtMost(FAN_STEP_MAX)
            val dipPx = (cardHeight * FAN_DIP).roundToInt()
            layout(rowWidth, cardHeight + dipPx) {
                placeables.forEachIndexed { index, placeable ->
                    val offCentre = (index - middle) / middle.coerceAtLeast(1f)
                    placeable.placeWithLayer(start + marginPx + stepPx * index, (dipPx * offCentre * offCentre).roundToInt()) {
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
private const val EMPTY_PILE_RATIO = 0.68f
/** The whole hand's tilt, the most one card may tilt from its neighbour, and how far the ends dip (× card height). */
private const val FAN_DEGREES = 40f
private const val FAN_STEP_MAX = 5f
private const val FAN_DIP = 0.08f
private const val FAN_MARGIN = 0.6f
/** A pile may take this much of the arena's height (tilted, it looks shorter); a card is 0.68 as wide as tall. */
private const val PILE_OF_ARENA = 1f
/** The smallest a pile's card may get, however short the arena. */
private val MIN_PILE_WIDTH = 34.dp
/** The table picture (bg_game_table_arena) and its innermost ring, as fractions of the picture. */
private const val TABLE_IMAGE_W = 2752f
private const val TABLE_IMAGE_H = 1536f
private const val INNER_RING_W = 0.274f
private const val INNER_RING_H = 0.177f
/** How much of the ring's height a pile's card may take (lying back it looks ~0.8 as tall), and of its width the two piles may. */
private const val RING_FILL_H = 0.85f
private const val RING_FILL_W = 0.8f
private const val CARD_W_PER_H = 0.68f
/** A pile card lying back on the table looks about this much of its height. */
private const val LYING_HEIGHT = 0.8f
/** The piles lie back on the table and turn slightly, as in the table design. */
private const val TABLE_TILT = 38f
private const val CAMERA_DISTANCE = 14f
private const val DECK_TURN = -6f
private const val DISCARD_TURN = 5f
/** One visible card edge per this many cards; the tallest pile stops growing at [MAX_LAYERS]. */
private const val CARDS_PER_LAYER = 4
private const val MAX_LAYERS = 12
private const val EDGE_CORNER_PERCENT = 8
private const val GLOW_ALPHA = 0.45f
private const val GLOW_REACH = 0.9f
private const val GLOW_SPREAD = 0.35f
/** The tightest a fanned hand may get before it scrolls instead. */
private const val MIN_STEP = 0.45f
