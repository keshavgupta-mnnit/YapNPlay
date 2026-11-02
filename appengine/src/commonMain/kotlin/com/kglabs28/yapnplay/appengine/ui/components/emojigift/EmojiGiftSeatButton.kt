package com.kglabs28.yapnplay.appengine.ui.components.emojigift

import androidx.compose.ui.platform.LocalWindowInfo
import androidx.compose.runtime.LaunchedEffect
import com.kglabs28.yapnplay.appengine.ui.components.common.LocalBlockingOverlay
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
 * The emoji smiley on [seat]'s avatar ([name]) and, while it is open, its
 * picker next to it: below the seat, or above or beside it where there is
 * no room. A tap anywhere else closes it, and so does any dialog opening
 * over the table (the picker never sits on top of one).
 */
@Composable
fun EmojiGiftSeatButton(table: EmojiGiftTable, seat: Int, name: String, size: Dp, modifier: Modifier = Modifier) {
    val picker = table.ui.value.picker?.takeIf { it.seat == seat }
    val dialogUp = LocalBlockingOverlay.current.content != null
    LaunchedEffect(dialogUp, picker != null) { if (dialogUp && picker != null) table.onClose() }
    EmojiGiftButton(
        description = TableStrings.emojiGiftOpen(name),
        onClick = { if (picker == null) table.onOpen(seat) else table.onClose() },
        size = size,
        open = picker != null,
        modifier = modifier,
    )
    if (picker != null && !dialogUp) {
        val density = LocalDensity.current
        val gap = with(density) { YapNPlayTheme.dimens.emojiGiftPickerGap.roundToPx() }
        // Read here, on the table's window (inside the popup the window is the popup's own).
        val windowHeight = with(density) { LocalWindowInfo.current.containerSize.height.toDp() }
        val tile = EmojiGiftSizing.tile(windowHeight, YapNPlayTheme.dimens.emojiGiftTile)
        val position = remember(gap) { EmojiGiftPopupPosition(gap) }
        Popup(popupPositionProvider = position, onDismissRequest = table.onClose, properties = PopupProperties(focusable = true)) {
            EmojiGiftPicker(picker = picker, onPick = table.onPick, onClose = table.onClose, tile = tile)
        }
    }
}
