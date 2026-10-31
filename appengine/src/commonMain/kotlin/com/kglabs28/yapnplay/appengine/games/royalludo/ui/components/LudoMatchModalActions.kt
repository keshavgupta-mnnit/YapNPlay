package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components

import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatHandoverAction
/** Everything the table's dialogs can ask for, in one place. */
class LudoMatchModalActions(
    val onDismiss: () -> Unit,
    val onConfirmLeave: () -> Unit,
    val onDone: () -> Unit,
    val onOpenAppSettings: () -> Unit,
    val onToggleEffects: () -> Unit,
    val onToggleMusic: () -> Unit,
    val onOpenReport: () -> Unit,
    val onSelectReportPerson: (userId: String) -> Unit,
    val onSubmitReport: () -> Unit,
    val onVote: (agree: Boolean) -> Unit,
    val onWatchAd: () -> Unit,
    val onPickSeatTaker: (index: Int) -> Unit,
    val onSeat: (SeatHandoverAction) -> Unit,
    val onProposeEnd: () -> Unit,
    val onConfirmEnd: () -> Unit,
    val onEndVote: (agree: Boolean) -> Unit,
)
