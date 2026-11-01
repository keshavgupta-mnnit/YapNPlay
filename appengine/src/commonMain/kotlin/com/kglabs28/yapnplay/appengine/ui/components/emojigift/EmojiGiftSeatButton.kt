package com.kglabs28.yapnplay.appengine.ui.components.emojigift

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.window.Popup
import androidx.compose.ui.window.PopupProperties
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings

/**
 * The emoji button on [seat]'s card ([name]) and, while it is open, its
 * picker next to it: below the seat, or above or beside it where there is
 * no room. A tap anywhere else closes it.
 */
@Composable
fun EmojiGiftSeatButton(table: EmojiGiftTable, seat: Int, name: String, size: Dp, modifier: Modifier = Modifier) {
    val picker = table.ui.value.picker?.takeIf { it.seat == seat }
    EmojiGiftButton(
        description = TableStrings.emojiGiftOpen(name),
        onClick = { if (picker == null) table.onOpen(seat) else table.onClose() },
        size = size,
        open = picker != null,
        modifier = modifier,
    )
    if (picker != null) {
        val gap = with(LocalDensity.current) { YapNPlayTheme.dimens.emojiGiftPickerGap.roundToPx() }
        val position = remember(gap) { EmojiGiftPopupPosition(gap) }
        Popup(popupPositionProvider = position, onDismissRequest = table.onClose, properties = PopupProperties(focusable = true)) {
            EmojiGiftPicker(picker = picker, onPick = table.onPick, onClose = table.onClose)
        }
    }
}
