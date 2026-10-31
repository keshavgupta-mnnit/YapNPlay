package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.layout.widthIn
import kotlin.math.abs
import androidx.compose.ui.input.pointer.changedToDown
import androidx.compose.ui.input.pointer.PointerEventPass
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.layout.layout
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
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
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.MonoStrings

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
    /** The colour just named: the note shows in it; null: the table's usual dark and gold. */
    noteColour: Color? = null,
    /** A word of the note in its colour (the Roulette's colour); null: none. */
    noteWord: Pair<String, Color>? = null,
    /** The colour in play, shown with a running stack ("STACK +12 · RED"); null: none. */
    stackColour: String? = null,
    /** A penalty that concerns the user, as a snackbar under the note; null shows nothing. */
    snack: TableFeedLineUi? = null,
    /** Small buttons hanging under the deck (Draw / Take, Pass) on the user's turn; null shows none. */
    deckButtons: (@Composable () -> Unit)? = null,
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
        // The ring's measured size (already dp values), back into a Dp.
        val byRing = if (ring.width > 0f) Dp(minOf(ring.height * RING_FILL_H * CARD_W_PER_H, (ring.width * RING_FILL_W - Spacing.Xl.value) / 2)) else cardWidth
        val dimens = YapNPlayTheme.dimens
        val cardWidth = minOf(cardWidth, byRing, maxHeight * PILE_OF_ARENA * CARD_W_PER_H).coerceAtLeast(dimens.monoMinPileWidth)
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
                // Lit like the discard pile, so the deck reads as clearly on the felt.
                glow = hue.ring,
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
        // One slot under the piles, a set gap below them and centred: the Draw / Pass buttons on the user's turn,
        // else one message. Only one ever shows, always there and as tall, so nothing moves.
        // One message: a penalty for the user first; else the running stack ("STACK +12 · RED"); else what just happened.
        val pileHalf = cardWidth / CARD_W_PER_H * LYING_HEIGHT / 2
        val messageTop = maxHeight / 2 + pileHalf + dimens.monoPileGap
        val shownNote = note?.takeIf { it.isNotBlank() }
        // A running stack stays short: its total and the colour in play.
        val stack = activeStackPenalty.takeIf { it > 0 }?.let { MonoStrings.monoStack(it, stackColour) }
        val message = when {
            snack != null -> snack.text to (snack.tone == TableFeedTone.LOSS)
            stack != null -> stack to true
            shownNote != null -> shownNote to false
            else -> null
        }
        val slot = Modifier.align(Alignment.TopCenter).offset(y = messageTop).widthIn(max = maxWidth).zIndex(1f)
        val colourNote = snack == null && stack == null && shownNote != null
        when {
            deckButtons != null -> Row(modifier = slot, horizontalArrangement = Arrangement.spacedBy(Spacing.Xs)) { deckButtons() }
            message != null -> MonoTableMessage(
                text = message.first,
                alert = message.second,
                // A colour named shows on it; a running stack wears the colour in play.
                fill = when {
                    colourNote -> noteColour
                    snack == null && stack != null && stackColour != null -> hue?.fill
                    else -> null
                },
                word = if (colourNote) noteWord else null,
                modifier = slot,
            )
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
 * The local player's hand, fanned like a held hand, always fitting the width.
 * Cards overlap as the hand grows; resting or sliding a thumb on the hand
 * (sliding it along) magnifies the cards under it ([HandLens]): they spread out and come
 * forward so their corners read clearly, while the rest close up. A tap
 * still plays the card under the finger. A playable card rises above the
 * row (room is kept for it). Exactly one card is drawn per hand index, so
 * the screen decides selection and playability.
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
    val liftRoom = YapNPlayTheme.dimens.monoCardLift
    // Where the thumb rests on the hand (px), or null. Read only while laying out: moving the thumb
    // re-places the cards without recomposing them.
    val thumb = remember { mutableStateOf<Float?>(null) }

    BoxWithConstraints(modifier = modifier.fillMaxWidth()) {
        val margin = cardWidth * FAN_MARGIN
        val step = handStep(handSize = handSize, cardWidth = cardWidth, available = maxWidth - margin * 2)
        Layout(
            content = { repeat(handSize) { index -> card(index) } },
            modifier = Modifier.pointerInput(Unit) {
                // Watches the thumb without taking its touches. The lens opens only once the thumb slides
                // along the hand (past the touch slop): a plain tap plays the card under it with nothing moving.
                awaitPointerEventScope {
                    var downX = 0f
                    var sliding = false
                    while (true) {
                        val change = awaitPointerEvent(PointerEventPass.Initial).changes.firstOrNull() ?: continue
                        when {
                            !change.pressed -> sliding = false
                            change.changedToDown() -> {
                                downX = change.position.x
                                sliding = false
                            }
                            abs(change.position.x - downX) > viewConfiguration.touchSlop -> sliding = true
                        }
                        thumb.value = if (sliding) change.position.x else null
                    }
                }
            },
        ) { measurables, constraints ->
            val widthPx = with(density) { cardWidth.roundToPx() }
            val stepPx = with(density) { step.toPx() }
            val placeables = measurables.map { measurable ->
                measurable.measure(constraints.copy(minWidth = widthPx, maxWidth = widthPx))
            }
            val marginPx = with(density) { margin.roundToPx() }
            val handWidth = (stepPx * (placeables.size - 1).coerceAtLeast(0)).toInt() + widthPx + marginPx * 2
            // The hand sits in the middle of the row.
            val rowWidth = maxOf(handWidth, this@BoxWithConstraints.constraints.maxWidth)
            val start = (rowWidth - handWidth) / 2 + marginPx
            val cardHeight = placeables.maxOfOrNull { it.height } ?: 0
            // Room above for a playable card standing up (a magnified card grows into it too, only while the thumb rests).
            val liftPx = with(density) { liftRoom.roundToPx() }
            val spots = HandLens.spots(placeables.size, start.toFloat(), stepPx, widthPx.toFloat(), thumb.value)
            // Fanned like a held hand: each card tilts out from the middle and the ends dip.
            val middle = (placeables.size - 1) / 2f
            val spread = (FAN_DEGREES / placeables.size.coerceAtLeast(1)).coerceAtMost(FAN_STEP_MAX)
            val dipPx = (cardHeight * FAN_DIP).toInt()
            layout(rowWidth, liftPx + cardHeight + dipPx) {
                placeables.forEachIndexed { index, placeable ->
                    val spot = spots[index]
                    val offCentre = (index - middle) / middle.coerceAtLeast(1f)
                    // In focus: upright, a little bigger and on top, so its corner reads clearly.
                    val dip = (dipPx * offCentre * offCentre * (1f - spot.focus)).toInt()
                    placeable.placeWithLayer(spot.x.toInt(), liftPx + dip, zIndex = spot.focus) {
                        rotationZ = (index - middle) * spread * (1f - spot.focus)
                        scaleX = 1f + LENS_GROW * spot.focus
                        scaleY = 1f + LENS_GROW * spot.focus
                        transformOrigin = TransformOrigin(0.5f, 1f)
                    }
                }
            }
        }
    }
}

/** How far apart the hand's cards sit: no overlap until it will not fit, then as close as it takes. */
private fun handStep(handSize: Int, cardWidth: Dp, available: Dp): Dp {
    if (handSize <= 1) return cardWidth
    val fit = (available - cardWidth) / (handSize - 1).toFloat()
    return fit.coerceIn(cardWidth * MIN_STEP, cardWidth)
}

/** Same as a card: width ÷ height. */
private const val EMPTY_PILE_RATIO = 0.68f
/** The whole hand's tilt, the most one card may tilt from its neighbour, and how far the ends dip (× card height). */
/** The whole hand's tilt, the most one card may tilt from its neighbour, and how far the ends dip (× card height). */
private const val FAN_DEGREES = 40f
private const val FAN_STEP_MAX = 5f
private const val FAN_DIP = 0.08f
private const val FAN_MARGIN = 0.6f
/** A pile may take this much of the arena's height (tilted, it looks shorter); a card is 0.68 as wide as tall. */
private const val PILE_OF_ARENA = 1f
/** The smallest a pile's card may get, however short the arena. */
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
/** The closest the cards may sit (× a card's width): the thumb magnifier opens them up to read. */
private const val MIN_STEP = 0.12f
/** How much bigger a card under the thumb grows. */
private const val LENS_GROW = 0.12f


