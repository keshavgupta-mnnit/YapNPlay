package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.ui.graphics.TransformOrigin
import androidx.compose.ui.graphics.graphicsLayer
import com.kglabs28.yapnplay.appengine.core.designsystem.AdaptiveScale
import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import androidx.compose.foundation.layout.RowScope
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
 *      between the U and the hand's own band (they never overlap);
 *    - bottom left, the user's seat over the MONO · CAUGHT button; bottom
 *      centre, the hand; bottom right, the Draw button. A watcher
 *      ([watching]) has none of these: the U runs down to the bottom.
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
    watching: Boolean = false,
    /** A watcher's mic and speaker, bottom right; shown only while [watching]. */
    watcherControls: (@Composable () -> Unit)? = null,
    /** Mutes or unmutes a player (by seat) for the user only, from the button on their pill; null: no button. */
    onToggleHear: ((Int) -> Unit)? = null,
    /** Mutes or unmutes a spectator (by user id) for the user only, from a long press in the top bar. */
    onToggleHearSpectator: ((String) -> Unit)? = null,
) {
    val dimens = YapNPlayTheme.dimens
    // Every opponent's seat, shrunk together as the table fills.
    val seatScale = TableSeatPlan.scale(leftPods.size + topPods.size + rightPods.size)
    // The bands the layers keep clear of each other.
    // A seat with a mute button is that much wider: the button sits outside its name pill.
    val muteRoom = dimens.ludoMuteButton + Spacing.Xxs
    fun roomFor(pod: MonoPlayerPodUi) = if (onToggleHear != null && pod.canSpeak) muteRoom else dimens.zero
    val sideColumn = dimens.monoPodWidthCompact + ((leftPods + rightPods).maxOfOrNull { roomFor(it) } ?: dimens.zero)
    val side = sideColumn + Spacing.Sm
    val topBand = if (topPods.isEmpty()) Spacing.Xs else dimens.monoTopBandHeight
    // The hand's own band: a card's height plus its lift, so it never reaches the piles.
    val handBand = dimens.monoHandCardWidth / CARD_W_PER_H + dimens.monoCardLift + Spacing.Xs

    Box(modifier = modifier.fillMaxSize()) {
        // On a tablet the seats keep their phone places while the picture grows with the height:
        // zoomed a little from the top (its bottom stays put), the table sits up among them as on a phone.
        val lift = AdaptiveScale.forTablet(phone = 0f, tablet = TABLET_TABLE_LIFT)
        GameTableArenaArtwork(
            modifier = Modifier.fillMaxSize().graphicsLayer {
                transformOrigin = TransformOrigin(0.5f, 0f)
                scaleX = 1f + lift
                scaleY = 1f + lift
                translationY = -size.height * lift
            },
        )
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
                onToggleHearSpectator = onToggleHearSpectator,
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
                            trailing = muteButtonFor(pod, onToggleHear),
                            modifier = Modifier
                                .monoAnchor(anchors, MonoSpot.Seat(pod.seat))
                                .monoScaled(seatScale)
                                .width(dimens.monoPodWidth + dimens.monoSideFanWidth + roomFor(pod)),
                        )
                    }
                }

                // The left column of the U (the next seat after the user at its foot), above the user's corner.
                Column(
                    modifier = Modifier
                        .align(Alignment.TopStart)
                        .width(sideColumn)
                        .fillMaxHeight()
                        .padding(bottom = if (watching) Spacing.Sm else dimens.monoLocalCornerHeight),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Sm, Alignment.CenterVertically),
                ) {
                    leftPods.asReversed().forEach { pod ->
                        MonoPlayerPod(
                            card = pod,
                            compact = true,
                            trailing = muteButtonFor(pod, onToggleHear),
                            modifier = Modifier
                                .monoAnchor(anchors, MonoSpot.Seat(pod.seat))
                                .monoScaled(seatScale)
                                .width(dimens.monoPodWidthCompact + roomFor(pod)),
                        )
                    }
                }

                // The right column of the U, above the Draw button.
                Column(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .width(sideColumn)
                        .fillMaxHeight()
                        .padding(bottom = if (watching) Spacing.Sm else dimens.arenaActionHeight + Spacing.Sm),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Sm, Alignment.CenterVertically),
                    horizontalAlignment = Alignment.End,
                ) {
                    rightPods.forEach { pod ->
                        MonoPlayerPod(
                            card = pod,
                            compact = true,
                            trailing = muteButtonFor(pod, onToggleHear),
                            modifier = Modifier
                                .monoAnchor(anchors, MonoSpot.Seat(pod.seat))
                                .monoScaled(seatScale)
                                .width(dimens.monoPodWidthCompact + roomFor(pod)),
                        )
                    }
                }

                // Bottom left: the user's seat over the callout (a watcher has no seat, hand or buttons).
                if (!watching) Column(
                    modifier = Modifier.align(Alignment.BottomStart),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
                ) {
                    // The user's own seat grows and shrinks with the table like everyone's.
                    Box(modifier = Modifier.monoScaled(seatScale)) { localPod() }
                    callButton()
                }

                // Bottom centre: the hand, over the table's near edge.
                if (!watching) Box(
                    modifier = Modifier
                        .align(Alignment.BottomCenter)
                        .fillMaxWidth()
                        .padding(start = dimens.arenaCallWidth + Spacing.Sm, end = dimens.arenaDrawWidth + Spacing.Sm)
                        .monoAnchor(anchors, MonoSpot.Hand),
                    contentAlignment = Alignment.BottomCenter,
                ) { hand() }

                // Bottom right: Draw.
                if (!watching) Box(modifier = Modifier.align(Alignment.BottomEnd)) { drawButton() }
                if (watching) watcherControls?.let { Box(modifier = Modifier.align(Alignment.BottomEnd)) { it() } }

                // A choice the table waits on, centred over the whole middle so it always has room.
                prompt?.let { Box(modifier = Modifier.align(Alignment.Center)) { it() } }
            }
        }
        MonoFlightLayer(state = flights, anchors = anchors)
    }
}

/** The "mute for me" button on a player's pill (a person, not a CPU); null keeps the pod's own speaker. */
private fun muteButtonFor(pod: MonoPlayerPodUi, onToggleHear: ((Int) -> Unit)?): (@Composable RowScope.() -> Unit)? =
    if (onToggleHear == null || !pod.canSpeak) {
        null
    } else {
        {
            TableMuteButton(
                muted = pod.mutedForMe,
                description = LudoStrings.ludoMuteFor(pod.name),
                onToggle = { onToggleHear(pod.seat) },
                micOn = pod.micOn,
            )
        }
    }

/** Same as a card: width ÷ height. */
private const val CARD_W_PER_H = 0.68f

/** On a tablet, how much the table picture is zoomed from its top (it rises by the same share). */
private const val TABLET_TABLE_LIFT = 0.1f
