package com.kglabs28.yapnplay.appengine.ui.components.emojigift

import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.boundsInRoot
import androidx.compose.ui.layout.onGloballyPositioned

/** Reports this avatar's place on screen as [seat]'s (the user's own when [isMe]); no table: nothing. */
fun Modifier.emojiGiftAnchor(table: EmojiGiftTable?, seat: Int?, isMe: Boolean): Modifier =
    if (table == null || seat == null) this else onGloballyPositioned { table.anchors.place(seat, it.boundsInRoot(), isMe) }
