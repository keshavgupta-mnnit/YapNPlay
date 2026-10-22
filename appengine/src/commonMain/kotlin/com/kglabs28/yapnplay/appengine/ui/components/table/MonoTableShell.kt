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
 *      between the U and the hand (the hand overlaps the table's near edge,
 *      as in the design);
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
    // The bands the layers keep clear of each other.
    val side = dimens.monoPodWidthCompact + Spacing.Sm
    val topBand = if (topPods.isEmpty()) Spacing.Xs else dimens.monoTopBandHeight
    val handBand = dimens.monoHandCardWidth / CARD_W_PER_H * HAND_OVER_TABLE

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
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Lg, Alignment.CenterHorizontally),
                ) {
                    // Their fans sit beside them, so nothing of theirs lies on the table.
                    topPods.forEach { pod ->
                        MonoPlayerPod(
                            card = pod,
                            fanBeside = true,
                            modifier = Modifier.width(dimens.monoPodWidth + dimens.monoSideFanWidth).monoAnchor(anchors, MonoSpot.Seat(pod.seat)),
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
                            modifier = Modifier.fillMaxWidth().monoAnchor(anchors, MonoSpot.Seat(pod.seat)),
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
                ) {
                    rightPods.forEach { pod ->
                        MonoPlayerPod(
                            card = pod,
                            compact = true,
                            modifier = Modifier.fillMaxWidth().monoAnchor(anchors, MonoSpot.Seat(pod.seat)),
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
 * so the table reads clockwise. Seven opponents fill it exactly: three
 * left, two top, two right.
 */
fun <T> monoTableZones(opponents: List<T>): Triple<List<T>, List<T>, List<T>> {
    val leftCount = (opponents.size + 2) / 3
    val topCount = (opponents.size + 1) / 3
    val rightCount = opponents.size - leftCount - topCount
    val left = opponents.take(leftCount)
    val top = opponents.drop(leftCount).take(topCount)
    val right = opponents.drop(leftCount + topCount).take(rightCount)
    return Triple(left, top, right)
}

/** Same as a card: width ÷ height. */
private const val CARD_W_PER_H = 0.68f
/** How much of the hand's height the arena keeps clear of (the rest overlaps the table's near edge). */
private const val HAND_OVER_TABLE = 0.55f
