package com.kglabs28.yapnplay.appengine.ui.components.seat

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings

/** "END THE GAME?" asked first, in the same dialog as Leave the match: the vote starts only on End game. */
@Composable
fun EndGameConfirmDialog(onCancel: () -> Unit, onEnd: () -> Unit, modifier: Modifier = Modifier) {
    SeatPickerDialog(
        title = TableStrings.endVoteTitle,
        message = TableStrings.endConfirmBody,
        options = emptyList(),
        selectedIndex = null,
        onSelect = {},
        secondaryLabel = TableStrings.seatCancel,
        onSecondary = onCancel,
        primaryLabel = TableStrings.endVoteEnd,
        onPrimary = onEnd,
        modifier = modifier,
    )
}
