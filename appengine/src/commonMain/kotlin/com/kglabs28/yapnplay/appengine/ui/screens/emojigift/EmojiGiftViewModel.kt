package com.kglabs28.yapnplay.appengine.ui.screens.emojigift

import com.kglabs28.yapnplay.appengine.core.audio.GameAudio
import com.kglabs28.yapnplay.appengine.core.audio.GameSound
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.TextUtils
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.features.emojigift.domain.EmojiGiftFlight
import com.kglabs28.yapnplay.appengine.features.emojigift.domain.EmojiGiftOffer
import com.kglabs28.yapnplay.appengine.features.emojigift.usecase.GetEmojiGiftMenuUseCase
import com.kglabs28.yapnplay.appengine.features.emojigift.usecase.ObserveEmojiGiftsUseCase
import com.kglabs28.yapnplay.appengine.features.emojigift.usecase.SendEmojiGiftUseCase
import com.kglabs28.yapnplay.appengine.ui.components.emojigift.EmojiGiftFlightUi
import com.kglabs28.yapnplay.appengine.ui.components.emojigift.EmojiGiftLandingUi
import com.kglabs28.yapnplay.appengine.ui.components.emojigift.EmojiGiftOfferUi
import com.kglabs28.yapnplay.appengine.ui.components.emojigift.EmojiGiftPickerUi
import com.kglabs28.yapnplay.appengine.ui.components.emojigift.EmojiGiftTiming
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Emoji gifts at a table: opens the picker on a seat (prices and coins from
 * the server), buys the picked emoji, and flies every emoji the server
 * passes on (the user's own too) from seat to seat, with its sounds, then
 * leaves it on the receiver's avatar for a few seconds.
 */
class EmojiGiftViewModel(
    private val getMenu: GetEmojiGiftMenuUseCase,
    private val sendGift: SendEmojiGiftUseCase,
    private val observeGifts: ObserveEmojiGiftsUseCase,
    private val audio: GameAudio,
    dispatchers: DispatcherProvider,
) : MviViewModel<EmojiGiftState, EmojiGiftIntent, EmojiGiftEffect>(EmojiGiftState(), dispatchers) {

    private var matchId: String? = null
    private var listening: Job? = null
    /** The last prices and coins seen, so the picker opens at once the next time. */
    private var offers: List<EmojiGiftOffer> = emptyList()
    private var coins = 0
    private var nextId = 0L

    override suspend fun handleIntent(intent: EmojiGiftIntent) {
        when (intent) {
            is EmojiGiftIntent.Start -> start(intent.matchId)
            is EmojiGiftIntent.Open -> open(intent.seat)
            EmojiGiftIntent.Close -> setPicker { null }
            is EmojiGiftIntent.Pick -> pick(intent.emojiId)
        }
    }

    private fun start(id: String) {
        // The table coming back (another screen on top, then Back) keeps listening as it was.
        if (id == matchId && listening?.isActive == true) return
        matchId = id
        listening?.cancel()
        listening = scope.launch {
            observeGifts(id).collect { flight -> scope.launch { fly(flight) } }
        }
    }

    private suspend fun open(seat: Int) {
        setPicker { EmojiGiftPickerUi(seat, TextUtils.groupedThousands(coins), offerUis(), loading = true) }
        when (val result = getMenu()) {
            is AppResult.Success -> {
                offers = result.data.offers
                coins = result.data.coins
                setPicker { it?.copy(coins = TextUtils.groupedThousands(coins), offers = offerUis(), loading = false) }
            }
            is AppResult.Failure -> setPicker { it?.copy(loading = false, message = if (offers.isEmpty()) TableStrings.emojiGiftLoadFailed else null) }
        }
    }

    private suspend fun pick(emojiId: String) {
        val picker = state.value.table.picker ?: return
        val match = matchId ?: return
        val offer = offers.firstOrNull { it.id == emojiId } ?: return
        if (picker.sending) return
        if (offer.coins > coins) return setPicker { it?.copy(message = TableStrings.emojiGiftNotEnough(offer.coins)) }
        setPicker { it?.copy(sending = true, message = null) }
        when (val result = sendGift(match, offer, picker.seat)) {
            // Sent: the picker closes; the emoji flies when the server passes it back to the table.
            is AppResult.Success -> {
                coins = result.data.coins
                setPicker { null }
            }
            is AppResult.Failure -> setPicker {
                it?.copy(
                    sending = false,
                    message = if (result.error is YapNPlayError.LimitReached) TableStrings.emojiGiftNotEnough(offer.coins) else TableStrings.emojiGiftFailed,
                )
            }
        }
    }

    /** Tosses [flight] across the table, then leaves it on the receiver's avatar for a while. */
    private suspend fun fly(flight: EmojiGiftFlight) {
        val id = nextId++
        val ui = EmojiGiftFlightUi(id, flight.fromSeat, flight.toSeat, flight.emojiId)
        setState { copy(table = table.copy(flights = table.flights + ui)) }
        audio.play(GameSound.EMOJI_TOSS)
        delay(EmojiGiftTiming.FLIGHT_MS.toLong())
        setState { copy(table = table.copy(flights = table.flights - ui, landed = table.landed + (flight.toSeat to EmojiGiftLandingUi(id, flight.emojiId)))) }
        audio.play(GameSound.EMOJI_POP)
        delay(EmojiGiftTiming.SHOW_MS)
        // A later emoji on the same seat keeps its own time.
        setState { copy(table = table.copy(landed = if (table.landed[flight.toSeat]?.id == id) table.landed - flight.toSeat else table.landed)) }
    }

    private fun offerUis(): List<EmojiGiftOfferUi> =
        offers.map { EmojiGiftOfferUi(it.id, TextUtils.groupedThousands(it.coins), affordable = it.coins <= coins) }

    private fun setPicker(transform: (EmojiGiftPickerUi?) -> EmojiGiftPickerUi?) =
        setState { copy(table = table.copy(picker = transform(table.picker))) }
}
