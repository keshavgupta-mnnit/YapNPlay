package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import kotlin.math.roundToInt
import androidx.compose.ui.unit.Constraints
import androidx.compose.ui.layout.layout
import androidx.compose.ui.graphics.TransformOrigin
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.ui.placeholders.GameTableArenaArtwork

/**
 * The MONO table: the shared skeleton both MONO games draw into, over
 * the card-table room (its lit oval table holds the piles).
 *
 *  - The top bar: logo, title, spectators, match clock, settings, exit.
 *  - Under it, everything is layered over the table rather than stacked,
 *    so nothing at the edges can squeeze the piles:
 *    - the opponents in a U, in turn order from the user: up the left
 *      column, along the top row (their fans beside them, off the table),
 *      down the right column (clockwise);
 *    - the arena with the two piles in the middle, always given the room
 *      between the U and the hand's own band (they never overlap);
 *    - bottom left, the user's seat over the MONO · CAUGHT button; bottom
 *      centre, the hand; bottom right, the Draw button.
 *  - A prompt the table waits on (colour, swap, vote) sits over the piles
 *    only, never over the hand or the seats.
 *  - Above everything, the cards in flight between piles, hands and seats.
 *
 * The shell owns the layout only; the game owns what goes in it.
 */
@Composable
fun MonoTableShell(
    title: String,
    subtitle: String,
    audience: List<TableAvatarUi>,
    showAudience: Boolean,
    spectatorLabel: String,
    elapsed: String,
    onSettings: () -> Unit,
    onExit: () -> Unit,
    leftPods: List<MonoPlayerPodUi>,
    topPods: List<MonoPlayerPodUi>,
    rightPods: List<MonoPlayerPodUi>,
    anchors: MonoAnchors,
    flights: MonoFlightState,
    modifier: Modifier = Modifier,
    /** The game's logo beside the title. */
    logo: (@Composable (Modifier) -> Unit)? = null,
    /** What the table is waiting on (a colour, a swap partner, the vote); null when nothing. */
    prompt: (@Composable () -> Unit)? = null,
    arena: @Composable () -> Unit,
    localPod: @Composable () -> Unit,
    callButton: @Composable () -> Unit,
    hand: @Composable () -> Unit,
    drawButton: @Composable () -> Unit,
) {
    val dimens = YapNPlayTheme.dimens
    // Every opponent's seat, shrunk together as the table fills.
    val seatScale = monoSeatScale(leftPods.size + topPods.size + rightPods.size)
    // The bands the layers keep clear of each other.
    val side = dimens.monoPodWidthCompact + Spacing.Sm
    val topBand = if (topPods.isEmpty()) Spacing.Xs else dimens.monoTopBandHeight
    // The hand's own band: a card's height plus its lift, so it never reaches the piles.
    val handBand = dimens.monoHandCardWidth / CARD_W_PER_H + dimens.monoCardLift + Spacing.Xs

    Box(modifier = modifier.fillMaxSize()) {
        GameTableArenaArtwork(modifier = Modifier.fillMaxSize())
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.Md, vertical = Spacing.Xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            ArenaTopBar(
                title = title,
                subtitle = subtitle,
                audience = audience,
                showAudience = showAudience,
                spectatorLabel = spectatorLabel,
                elapsed = elapsed,
                onSettings = onSettings,
                onExit = onExit,
                logo = logo,
            )
            Box(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth(),
            ) {
                // The arena: everything between the U and the hand.
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(start = side, end = side, top = topBand, bottom = handBand),
                    contentAlignment = Alignment.Center,
                ) {
                    arena()
                }

                // The top row of the U.
                Row(
                    modifier = Modifier
                        .align(Alignment.TopCenter)
                        .padding(horizontal = side),
                    horizontalArrangement = Arrangement.spacedBy(if (seatScale < 1f) Spacing.Sm else Spacing.Lg, Alignment.CenterHorizontally),
                ) {
                    // Their fans sit beside them, so nothing of theirs lies on the table.
                    topPods.forEach { pod ->
                        MonoPlayerPod(
                            card = pod,
                            fanBeside = true,
                            modifier = Modifier
                                .monoAnchor(anchors, MonoSpot.Seat(pod.seat))
                                .monoScaled(seatScale)
                                .width(dimens.monoPodWidth + dimens.monoSideFanWidth),
                        )
                    }
                }

                // The left column of the U (the next seat after the user at its foot), above the user's corner.
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .width(dimens.monoPodWidthCompact)
                        .fillMaxHeight()
                        .padding(bottom = dimens.monoLocalCornerHeight),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Sm, Alignment.CenterVertically),
                ) {
                    leftPods.asReversed().forEach { pod ->
                        MonoPlayerPod(
                            card = pod,
                            compact = true,
                            modifier = Modifier
                                .monoAnchor(anchors, MonoSpot.Seat(pod.seat))
                                .monoScaled(seatScale)
                                .width(dimens.monoPodWidthCompact),
                        )
                    }
                }

                // The right column of the U, above the Draw button.
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .width(dimens.monoPodWidthCompact)
                        .fillMaxHeight()
                        .padding(bottom = dimens.arenaActionHeight + Spacing.Sm),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Sm, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.End,
                ) {
                    rightPods.forEach { pod ->
                        MonoPlayerPod(
                            card = pod,
                            compact = true,
                            modifier = Modifier
                                .monoAnchor(anchors, MonoSpot.Seat(pod.seat))
                                .monoScaled(seatScale)
                                .width(dimens.monoPodWidthCompact),
                        )
                    }
                }

                // Bottom left: the user's seat over the callout.
                Column(
                    modifier = Modifier.align(Alignment.BottomStart),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
                ) {
                    localPod()
                    callButton()
                }

                // Bottom centre: the hand, over the table's near edge.
                Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(start = dimens.arenaCallWidth + Spacing.Sm, end = dimens.arenaDrawWidth + Spacing.Sm)
                        .monoAnchor(anchors, MonoSpot.Hand),
                    contentAlignment = Alignment.BottomCenter,
                ) { hand() }

                // Bottom right: Draw.
                Box(modifier = Modifier.align(Alignment.BottomEnd)) { drawButton() }

                // A choice the table waits on, centred over the whole middle so it always has room.
                prompt?.let { Box(modifier = Modifier.align(Alignment.Center)) { it() } }
            }
        }
        MonoFlightLayer(state = flights, anchors = anchors)
    }
}

/**
 * Splits the opponents (in turn order from the user) across the three
 * rails of the U — left column (drawn bottom-up), top row, right column —
 * so the table reads clockwise. Up to five opponents: one left, up to two
 * right, the rest along the top; six or seven (seats shrunk, see
 * [monoSeatScale]): two left, two right, the rest along the top.
 */
fun <T> monoTableZones(opponents: List<T>): Triple<List<T>, List<T>, List<T>> {
    val leftCount = minOf(if (opponents.size >= CROWDED) 2 else 1, opponents.size)
    val rest = opponents.size - leftCount
    val rightCount = minOf(2, rest / 2)
    val topCount = rest - rightCount
    val left = opponents.take(leftCount)
    val top = opponents.drop(leftCount).take(topCount)
    val right = opponents.drop(leftCount + topCount).take(rightCount)
    return Triple(left, top, right)
}

/**
 * How big each opponent's seat is drawn for [opponents] of them: the
 * whole seat (avatar, pill, name, speaker, fan) shrinks together as the
 * table fills, so every seat fits whole and the table doesn't crowd.
 */
fun monoSeatScale(opponents: Int): Float = when {
    opponents >= CROWDED -> SCALE_CROWDED
    opponents >= BUSY -> SCALE_BUSY
    else -> 1f
}

/** Draws the content at [scale] of its size, taking only that much room (anchored top-left). */
fun Modifier.monoScaled(scale: Float): Modifier = if (scale == 1f) this else layout { measurable, constraints ->
    val placeable = measurable.measure(
        Constraints(
            maxWidth = if (constraints.hasBoundedWidth) (constraints.maxWidth / scale).toInt() else Constraints.Infinity,
            maxHeight = if (constraints.hasBoundedHeight) (constraints.maxHeight / scale).toInt() else Constraints.Infinity,
        ),
    )
    layout((placeable.width * scale).roundToInt(), (placeable.height * scale).roundToInt()) {
        placeable.placeWithLayer(0, 0) {
            scaleX = scale
            scaleY = scale
            transformOrigin = TransformOrigin(0f, 0f)
        }
    }
}

/** Opponent counts from which seats shrink, and how much. */
private const val BUSY = 4
private const val CROWDED = 6
private const val SCALE_BUSY = 0.85f
private const val SCALE_CROWDED = 0.72f

/** Same as a card: width ÷ height. */
private const val CARD_W_PER_H = 0.68f
