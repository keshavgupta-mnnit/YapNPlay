package com.kglabs28.yapnplay.appengine.features.seathandover.presentation

import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.seathandover.domain.SeatOffer
import com.kglabs28.yapnplay.appengine.features.seathandover.domain.SeatOfferStage
import kotlinx.datetime.Instant

/** Texts for a seat handover, shared by every game's table. Pure functions. */
object SeatHandoverUiMapper {

    /** Picker tiles: each spectator, after the bot when [withBot] (leaving: the bot is the default). */
    fun options(spectatorNames: List<String>, withBot: Boolean): List<SeatPickOptionUi> =
        (if (withBot) listOf(SeatPickOptionUi(Strings.seatBot, isBot = true)) else emptyList()) +
            spectatorNames.map { SeatPickOptionUi(it, isBot = false) }

    /** The spectator picked at [index] of [options] (same [withBot]); null for the bot or no pick. */
    fun giveTo(index: Int?, spectatorIds: List<String>, withBot: Boolean): String? =
        index?.let { spectatorIds.getOrNull(if (withBot) it - 1 else it) }

    /** "0:45" for the dialog's timer chip. */
    fun countdown(offer: SeatOffer, now: Instant): String = Strings.seatCountdown(secondsLeft(offer, now))

    /** The strip everyone sees while the seat changes hands. */
    fun banner(offer: SeatOffer, now: Instant): String {
        val seconds = secondsLeft(offer, now)
        return when (val stage = offer.stage) {
            is SeatOfferStage.ConfirmingAbsence -> Strings.seatCheckingAbsence(stage.name, seconds)
            is SeatOfferStage.ToSpectator -> Strings.seatOfferedTo(offer.leaverName, stage.name, seconds)
            is SeatOfferStage.LeaderChoosing -> Strings.seatLeaderChoosing(stage.leaderName, offer.leaverName, seconds)
            SeatOfferStage.OpenToAll -> Strings.seatOpen(offer.leaverName, seconds)
        }
    }

    fun secondsLeft(offer: SeatOffer, now: Instant): Long = (offer.endsAt - now).inWholeSeconds.coerceAtLeast(0)
}
