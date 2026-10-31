package com.kglabs28.yapnplay.appengine.features.seathandover.presentation

import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.features.seathandover.domain.SeatCommand
import com.kglabs28.yapnplay.appengine.features.seathandover.domain.SeatHandoverRules
import com.kglabs28.yapnplay.appengine.features.seathandover.domain.SeatOfferStage
import com.kglabs28.yapnplay.appengine.features.seathandover.usecase.SendSeatCommandUseCase
import kotlinx.datetime.Instant

/**
 * The seat dialogs of one table, shared by every game: which one the user
 * owes (from the match's [SeatTable]), what they picked, and sending
 * their answer. One per view model.
 */
class SeatHandoverController(private val sendCommand: SendSeatCommandUseCase) {
    /** The open seat the user took or passed on: not offered to them again. */
    private var declinedSeat: Int? = null
    private var pick: Int? = null
    /** The bot's seat the leader is offering (their long press), while they pick a spectator. */
    private var botSeat: Int? = null

    /** A long press on [seat] may offer it: the user leads the room, the bot plays it, someone watches and no handover runs. */
    fun canOfferBotSeat(table: SeatTable, seat: Int): Boolean =
        table.roomMatch && table.canOfferSeats && table.offer == null && seat in table.botSeats && table.spectators.isNotEmpty()

    /** The dialog the user owes now, or null. */
    fun modal(table: SeatTable, now: Instant): SeatHandoverModal? {
        botSeat?.let { seat ->
            if (canOfferBotSeat(table, seat)) return offerBotSeat(table, seat)
            botSeat = null
        }
        val offer = table.offer ?: return null
        val seconds = SeatHandoverUiMapper.secondsLeft(offer, now)
        val countdown = SeatHandoverUiMapper.countdown(offer, now)
        return when (val stage = offer.stage) {
            is SeatOfferStage.ConfirmingAbsence -> if (stage.userId != table.viewerUserId) {
                null
            } else {
                SeatHandoverModal.AbsenceCheck(
                    title = TableStrings.seatInactiveTitle,
                    body = TableStrings.seatAbsenceBody(SeatHandoverRules.IDLE_TURNS_BEFORE_CHECK, table.roomMatch),
                    countdown = countdown,
                    // The bot first and picked by default; spectators only in a room match.
                    options = SeatHandoverUiMapper.options(if (table.roomMatch) table.spectatorNames else emptyList(), withBot = true),
                    selectedIndex = pick ?: 0,
                )
            }
            is SeatOfferStage.ToSpectator -> when {
                stage.userId != table.viewerUserId -> null
                // Their own seat, which the bot kept while they were away.
                stage.name == offer.leaverName -> SeatHandoverModal.SeatOfferPrompt(TableStrings.seatBackTitle, TableStrings.seatBackBody, TableStrings.seatTake(seconds))
                stage.round > 1 -> SeatHandoverModal.SeatOfferPrompt(TableStrings.seatPromptTitle(offer.leaverName), TableStrings.seatLeaderOfferBody(offer.leaverName), TableStrings.seatTake(seconds))
                else -> SeatHandoverModal.SeatOfferPrompt(TableStrings.seatPromptTitle(offer.leaverName), TableStrings.seatPromptBody(offer.leaverName), TableStrings.seatTake(seconds))
            }
            is SeatOfferStage.LeaderChoosing -> if (stage.leaderUserId != table.viewerUserId) {
                null
            } else {
                SeatHandoverModal.LeaderSeatChoice(
                    title = TableStrings.seatOpenTitle,
                    body = TableStrings.seatLeaderBody(offer.leaverName),
                    countdown = countdown,
                    // The bot is a button here, not a tile.
                    options = SeatHandoverUiMapper.options(table.spectatorNames, withBot = false),
                    selectedIndex = pick,
                )
            }
            SeatOfferStage.OpenToAll -> if (table.seated || offer.seat == declinedSeat) {
                null
            } else {
                SeatHandoverModal.SeatInvite(offer.seat, TableStrings.seatInviteTitle(offer.leaverName), TableStrings.seatInviteBody, TableStrings.seatTake(seconds))
            }
        }
    }

    /** Handles [action] for [matchId]; true when the dialogs changed and the table should redraw. */
    suspend fun handle(matchId: String, table: SeatTable, action: SeatHandoverAction): Boolean {
        when (action) {
            is SeatHandoverAction.OpenBotSeatOffer -> {
                if (!canOfferBotSeat(table, action.seat)) return false
                botSeat = action.seat
                pick = null
            }
            is SeatHandoverAction.Pick -> pick = action.index
            is SeatHandoverAction.Take -> {
                declinedSeat = action.seat
                sendCommand(matchId, SeatCommand.Take(action.seat))
            }
            is SeatHandoverAction.Decline -> declinedSeat = action.seat
            is SeatHandoverAction.Answer -> sendCommand(matchId, SeatCommand.Answer(action.accept))
            SeatHandoverAction.StillHere -> sendCommand(matchId, SeatCommand.StillHere)
            SeatHandoverAction.InviteToSeat -> {
                val giveTo = SeatHandoverUiMapper.giveTo(pick, table.spectatorIds, withBot = false) ?: return false
                pick = null
                sendCommand(matchId, SeatCommand.ChooseTaker(giveTo))
            }
            SeatHandoverAction.OfferToBot -> {
                pick = null
                sendCommand(matchId, SeatCommand.ChooseTaker(null))
            }
            SeatHandoverAction.Spectate -> {
                // The seat goes to the pick (the bot by default); the user stays and watches.
                val giveTo = SeatHandoverUiMapper.giveTo(pick ?: 0, table.spectatorIds, withBot = true)
                pick = null
                sendCommand(matchId, SeatCommand.Leave(giveTo))
            }
            SeatHandoverAction.SendBotSeatOffer -> {
                val seat = botSeat ?: return false
                val toUserId = SeatHandoverUiMapper.giveTo(pick, table.spectatorIds, withBot = false) ?: return false
                botSeat = null
                pick = null
                sendCommand(matchId, SeatCommand.OfferBotSeat(seat, toUserId))
            }
            SeatHandoverAction.CancelBotSeatOffer -> {
                botSeat = null
                pick = null
            }
        }
        return true
    }

    private fun offerBotSeat(table: SeatTable, seat: Int) = SeatHandoverModal.OfferBotSeat(
        title = TableStrings.seatOfferBotTitle,
        body = TableStrings.seatOfferBotBody(table.botSeats[seat].orEmpty()),
        options = SeatHandoverUiMapper.options(table.spectatorNames, withBot = false),
        selectedIndex = pick,
    )
}
