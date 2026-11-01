package com.kglabs28.yapnplay.appengine.ui.components.emojigift

import androidx.compose.runtime.Immutable

/** Everything the table draws for emoji gifts: the open picker, emojis in flight, emojis on avatars. */
@Immutable
data class EmojiGiftTableUi(
    val picker: EmojiGiftPickerUi? = null,
    val flights: List<EmojiGiftFlightUi> = emptyList(),
    /** Seat → the emoji covering its avatar right now. */
    val landed: Map<Int, EmojiGiftLandingUi> = emptyMap(),
)

/** The picker, opened on [seat]'s card. */
@Immutable
data class EmojiGiftPickerUi(
    val seat: Int,
    /** The user's coins, ready to show. */
    val coins: String,
    val offers: List<EmojiGiftOfferUi>,
    /** Prices still on their way (the first time it opens). */
    val loading: Boolean = false,
    /** A paid emoji is on its way: the tiles wait. */
    val sending: Boolean = false,
    /** Why the last pick didn't go (short of coins, no connection). */
    val message: String? = null,
)

/** One emoji tile: its [price] ready to show, faded when the user can't afford it. */
@Immutable
data class EmojiGiftOfferUi(val emojiId: String, val price: String, val affordable: Boolean)

/** An emoji flying from [fromSeat]'s avatar to [toSeat]'s; [id] tells repeats apart. */
@Immutable
data class EmojiGiftFlightUi(val id: Long, val fromSeat: Int, val toSeat: Int, val emojiId: String)

/** An emoji over a seat's avatar; [id] restarts its pop when another lands. */
@Immutable
data class EmojiGiftLandingUi(val id: Long, val emojiId: String)
