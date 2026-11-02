package com.kglabs28.yapnplay.appengine.ui.screens.emojigift

import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.ui.components.emojigift.EmojiGiftTableUi

/** Emoji gifts at one table (every game shares it). */
data class EmojiGiftState(val table: EmojiGiftTableUi = EmojiGiftTableUi()) : UiState

sealed interface EmojiGiftIntent : UiIntent {
    /** The table opened: show the emojis sent at [matchId]. */
    data class Start(val matchId: String) : EmojiGiftIntent
    /** The emoji button on [seat]'s card. */
    data class Open(val seat: Int) : EmojiGiftIntent
    data object Close : EmojiGiftIntent
    /** Buy [emojiId] for the seat the picker is open on. */
    data class Pick(val emojiId: String) : EmojiGiftIntent
}

/** Nothing one-off yet: everything shows from the state. */
sealed interface EmojiGiftEffect : UiEffect
