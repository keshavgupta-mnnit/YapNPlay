package com.kglabs28.yapnplay.appengine.ui.components.seat

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatHandoverAction
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatHandoverModal

/** Any game's seat dialog; every answer goes to [onAction]. */
@Composable
fun SeatHandoverDialog(modal: SeatHandoverModal, onAction: (SeatHandoverAction) -> Unit, modifier: Modifier = Modifier) {
    when (modal) {
        is SeatHandoverModal.SeatInvite -> SeatOfferPromptCard(
            title = modal.title,
            body = modal.body,
            acceptLabel = modal.takeLabel,
            onAccept = { onAction(SeatHandoverAction.Take(modal.seat)) },
            onDecline = { onAction(SeatHandoverAction.Decline(modal.seat)) },
            modifier = modifier,
        )
        is SeatHandoverModal.AbsenceCheck -> SeatPickerDialog(
            title = modal.title,
            message = modal.body,
            options = modal.options,
            selectedIndex = modal.selectedIndex,
            onSelect = { onAction(SeatHandoverAction.Pick(it)) },
            countdown = modal.countdown,
            secondaryLabel = TableStrings.seatImHere,
            onSecondary = { onAction(SeatHandoverAction.StillHere) },
            primaryLabel = TableStrings.seatSpectate,
            onPrimary = { onAction(SeatHandoverAction.Spectate) },
            modifier = modifier,
        )
        is SeatHandoverModal.SeatOfferPrompt -> SeatOfferPromptCard(
            title = modal.title,
            body = modal.body,
            acceptLabel = modal.acceptLabel,
            onAccept = { onAction(SeatHandoverAction.Answer(true)) },
            onDecline = { onAction(SeatHandoverAction.Answer(false)) },
            modifier = modifier,
        )
        is SeatHandoverModal.LeaderSeatChoice -> SeatPickerDialog(
            title = modal.title,
            message = modal.body,
            options = modal.options,
            selectedIndex = modal.selectedIndex,
            onSelect = { onAction(SeatHandoverAction.Pick(it)) },
            countdown = modal.countdown,
            secondaryLabel = TableStrings.seatOfferToBot,
            onSecondary = { onAction(SeatHandoverAction.OfferToBot) },
            primaryLabel = TableStrings.seatInviteToSeat,
            onPrimary = { onAction(SeatHandoverAction.InviteToSeat) },
            primaryEnabled = modal.selectedIndex != null,
            modifier = modifier,
        )
        is SeatHandoverModal.OfferBotSeat -> SeatPickerDialog(
            title = modal.title,
            message = modal.body,
            options = modal.options,
            selectedIndex = modal.selectedIndex,
            onSelect = { onAction(SeatHandoverAction.Pick(it)) },
            secondaryLabel = TableStrings.seatCancel,
            onSecondary = { onAction(SeatHandoverAction.CancelBotSeatOffer) },
            primaryLabel = TableStrings.seatOfferSeat,
            onPrimary = { onAction(SeatHandoverAction.SendBotSeatOffer) },
            primaryEnabled = modal.selectedIndex != null,
            modifier = modifier,
        )
    }
}
