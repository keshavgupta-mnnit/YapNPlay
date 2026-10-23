package com.kglabs28.yapnplay.appengine.ui.components.seat

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatPickOptionUi

/**
 * "Leave the match?" — the one exit dialog every game's table shows: the
 * same title, Cancel and Exit, over [message]. A room match may list who
 * takes the seat ([options], [selectedIndex], [onSelect]) and count down
 * to closing by itself ([countdown]).
 */
@Composable
fun LeaveMatchDialog(
    onStay: () -> Unit,
    onLeave: () -> Unit,
    modifier: Modifier = Modifier,
    message: String = TableStrings.leaveMatchBody,
    options: List<SeatPickOptionUi> = emptyList(),
    selectedIndex: Int? = null,
    onSelect: (Int) -> Unit = {},
    countdown: String? = null,
) {
    SeatPickerDialog(
        title = TableStrings.leaveMatchTitle,
        message = message,
        options = options,
        selectedIndex = selectedIndex,
        onSelect = onSelect,
        secondaryLabel = TableStrings.seatCancel,
        onSecondary = onStay,
        primaryLabel = TableStrings.seatExit,
        onPrimary = onLeave,
        modifier = modifier,
        countdown = countdown,
    )
}
