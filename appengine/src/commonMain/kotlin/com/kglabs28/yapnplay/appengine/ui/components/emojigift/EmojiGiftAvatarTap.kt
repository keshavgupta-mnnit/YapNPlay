package com.kglabs28.yapnplay.appengine.ui.components.emojigift

import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.gestures.waitForUpOrCancellation
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import kotlinx.coroutines.withTimeoutOrNull

/**
 * A quick tap on another player's avatar opens the emoji picker for
 * [seat]; only the tap is taken, so a long press still reaches the card
 * (add / remove friend) and a tap anywhere else on the card still opens
 * the player's details. Nothing when [enabled] is false (the user's own
 * seat, or no seat to send from).
 */
fun Modifier.emojiGiftAvatarTap(table: EmojiGiftTable?, seat: Int?, enabled: Boolean): Modifier =
    if (table == null || seat == null || !enabled) {
        this
    } else {
        pointerInput(table, seat) {
            awaitEachGesture {
                awaitFirstDown(requireUnconsumed = false)
                val up = withTimeoutOrNull(viewConfiguration.longPressTimeoutMillis) { waitForUpOrCancellation() }
                if (up != null) {
                    // Taken here, so the card's own tap (the details) doesn't fire as well.
                    up.consume()
                    table.onOpen(seat)
                }
            }
        }
    }
