package com.kglabs28.yapnplay.appengine.ui.components.emojigift

import androidx.compose.runtime.Stable
import androidx.compose.runtime.State
import androidx.compose.runtime.staticCompositionLocalOf

/**
 * Emoji gifts at the table being drawn, handed to every seat through
 * [LocalEmojiGiftTable]: the seats report where their avatars are, show the
 * emoji button and picker, and swap the avatar for an emoji that landed.
 * Outside a table (null) seats show none of it.
 */
@Stable
class EmojiGiftTable(
    val anchors: EmojiGiftAnchors,
    val ui: State<EmojiGiftTableUi>,
    val onOpen: (seat: Int) -> Unit,
    val onClose: () -> Unit,
    val onPick: (emojiId: String) -> Unit,
)

val LocalEmojiGiftTable = staticCompositionLocalOf<EmojiGiftTable?> { null }
