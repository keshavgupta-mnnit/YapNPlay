package com.kglabs28.yapnplay.appengine.ui.components.table

import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.Stable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.key
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Rect
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned
import androidx.compose.ui.layout.positionInRoot
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.IntOffset
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import kotlinx.coroutines.delay
import kotlin.math.PI
import kotlin.math.roundToInt
import kotlin.math.sin

/** A place on the card table a card can fly from or to. */
sealed interface MonoSpot {
    data object Deck : MonoSpot
    data object Discard : MonoSpot
    /** The local player's hand along the bottom. */
    data object Hand : MonoSpot
    /** The card the user just tapped to play, where it sat in the hand. */
    data object PlayedCard : MonoSpot
    /** Another player's seat (their fan). */
    data class Seat(val seat: Int) : MonoSpot
    /** The sorted place in the user's hand where the card of flight [flightId] goes. */
    data class HandSlot(val flightId: Long) : MonoSpot
}

/**
 * One card moving across the table. [look] is the face shown in flight
 * (null: face down). A card flying onto the discard pile carries the
 * pile's previous top as [cover], which the pile keeps showing until
 * the card lands on it.
 */
data class MonoFlight(
    val id: Long,
    val from: MonoSpot,
    val to: MonoSpot,
    val look: MonoCardLook?,
    val delayMs: Int = 0,
    val cover: MonoCardLook? = null,
    val chaos: Boolean = false,
)

/** Where each [MonoSpot] is on screen, filled in as the table lays out. */
@Stable
class MonoAnchors {
    internal val rects = mutableStateMapOf<MonoSpot, Rect>()
}

@Composable
fun rememberMonoAnchors(): MonoAnchors = remember { MonoAnchors() }

/** Notes where [spot] is right now (a tapped card, just before it's played). */
fun MonoAnchors.mark(spot: MonoSpot, rect: Rect) {
    rects[spot] = rect
}

/** The faces of the cards still flying into the user's hand (the hand keeps their places empty until they land). */
/** The cards flying into the user's hand whose faces are known, in flight order. */
val MonoFlightState.flightsIntoHand: List<MonoFlight>
    get() = flights.filter { it.to == MonoSpot.Hand && it.look != null }

/** Marks this element as [spot], so cards can fly to and from it. */
fun Modifier.monoAnchor(anchors: MonoAnchors?, spot: MonoSpot): Modifier =
    if (anchors == null) this else onGloballyPositioned { anchors.rects[spot] = it.boundsInRoot() }

/** The cards in the air. */
@Stable
class MonoFlightState {
    internal val flights = mutableStateListOf<MonoFlight>()

    fun launch(more: List<MonoFlight>) {
        flights += more
    }

    /** True while a card is on its way to the discard pile. */
    val coveringDiscard: Boolean get() = flights.any { it.to == MonoSpot.Discard }

    /** What the discard pile shows until the first incoming card lands. */
    val discardCover: MonoCardLook? get() = flights.firstOrNull { it.to == MonoSpot.Discard }?.cover

    internal fun done(id: Long) {
        flights.removeAll { it.id == id }
    }
}

@Composable
fun rememberMonoFlightState(): MonoFlightState = remember { MonoFlightState() }

/**
 * Draws every card in the air above the table. It covers the whole
 * screen and never takes a touch; each card arcs from its start to its
 * end, turning and growing or shrinking to fit, then is gone.
 */
@Composable
fun MonoFlightLayer(
    state: MonoFlightState,
    anchors: MonoAnchors,
    modifier: Modifier = Modifier,
) {
    var origin by remember { mutableStateOf(Offset.Zero) }
    Box(
        modifier = modifier
            .fillMaxSize()
            .onGloballyPositioned { origin = it.positionInRoot() },
    ) {
        state.flights.forEach { flight ->
            key(flight.id) {
                // A played card with no tap on record (autoplay) leaves from the hand.
                val from = anchors.rects[flight.from] ?: if (flight.from == MonoSpot.PlayedCard) anchors.rects[MonoSpot.Hand] else null
                // A card for the user's hand lands straight in its sorted place (once the hand has laid it out).
                val to = (if (flight.to == MonoSpot.Hand) anchors.rects[MonoSpot.HandSlot(flight.id)] else null) ?: anchors.rects[flight.to]
                if (from == null || to == null) {
                    LaunchedEffect(Unit) { state.done(flight.id) }
                } else {
                    FlyingCard(flight, from.translate(-origin), to.translate(-origin)) {
                        anchors.rects.remove(MonoSpot.HandSlot(flight.id))
                        state.done(flight.id)
                    }
                }
            }
        }
    }
}

@Composable
private fun FlyingCard(flight: MonoFlight, from: Rect, to: Rect, onLanded: () -> Unit) {
    val density = LocalDensity.current
    val cardWidth = YapNPlayTheme.dimens.monoFlightCard
    val cardPx = with(density) { cardWidth.toPx() }
    val arcPx = with(density) { YapNPlayTheme.dimens.monoFlightCard.toPx() } * ARC
    val progress = remember { Animatable(0f) }
    LaunchedEffect(Unit) {
        if (flight.delayMs > 0) delay(flight.delayMs.toLong())
        progress.animateTo(1f, tween(FLIGHT_MS, easing = FastOutSlowInEasing))
        onLanded()
    }
    val t = progress.value
    // Hidden until its delay is over, so a burst of draws leaves the deck one by one.
    if (t <= 0f && flight.delayMs > 0) return
    val start = from.center
    val end = to.center
    val x = start.x + (end.x - start.x) * t
    val y = start.y + (end.y - start.y) * t - sin(PI * t).toFloat() * arcPx
    val scaleFrom = scaleAt(flight.from, from, cardPx)
    val scaleTo = scaleAt(flight.to, to, cardPx)
    val heightPx = cardPx / CARD_RATIO
    MonoCardFace(
        look = flight.look ?: MonoCardLook(fill = YapNPlayTheme.colors.MonoBackBase, rim = YapNPlayTheme.colors.MonoBackRim),
        faceDown = flight.look == null,
        chaos = flight.chaos,
        modifier = Modifier
            .offset { IntOffset((x - cardPx / 2).roundToInt(), (y - heightPx / 2).roundToInt()) }
            .width(cardWidth)
            .graphicsLayer {
                val scale = scaleFrom + (scaleTo - scaleFrom) * t
                scaleX = scale
                scaleY = scale
                rotationZ = SPIN * (1f - t)
            },
    )
}

/** How big a flying card is at [spot]: a pile's own card size; fixed sizes at a hand or a seat (their boxes are wider than a card). */
private fun scaleAt(spot: MonoSpot, rect: Rect, cardPx: Float): Float = when (spot) {
    MonoSpot.Deck, MonoSpot.Discard -> (rect.width / cardPx).coerceIn(MIN_SCALE, MAX_SCALE)
    MonoSpot.Hand -> HAND_SCALE
    // A hand slot is one card of the hand: land at its own size.
    MonoSpot.PlayedCard, is MonoSpot.HandSlot -> (rect.width / cardPx).coerceIn(MIN_SCALE, MAX_SCALE)
    is MonoSpot.Seat -> SEAT_SCALE
}

private const val FLIGHT_MS = 420
private const val HAND_SCALE = 1.1f
private const val SEAT_SCALE = 0.45f
private const val ARC = 0.9f
private const val SPIN = -18f
private const val MIN_SCALE = 0.4f
private const val MAX_SCALE = 1.4f
/** Same as a card: width ÷ height. */
private const val CARD_RATIO = 0.68f
