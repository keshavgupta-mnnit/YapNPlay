package com.kglabs28.yapnplay.appengine.games.citybazaar.data.remote

import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.games.citybazaar.data.remote.wire.CityBazaarPublicState
import com.kglabs28.yapnplay.appengine.games.citybazaar.data.remote.wire.CityBazaarWireEvent
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarAuctionInfo
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarColour
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarDebtInfo
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarEvent
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarMatch
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarPlayer
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarPresence
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarSpectator
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarTurnPhase
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarUpdate
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.MoneyReason
import com.kglabs28.yapnplay.appengine.protocol.MatchSnapshot
import com.kglabs28.yapnplay.appengine.protocol.SeatPresence
import kotlinx.datetime.Instant
import kotlinx.serialization.json.Json
import kotlin.time.Duration.Companion.milliseconds

/**
 * `MatchSnapshot` (City Bazaar public state and events) → [CityBazaarUpdate].
 * Server times are moved onto this device's clock (now − serverTime).
 */
class CityBazaarSnapshotMapper(private val json: Json) {

    fun map(snapshot: MatchSnapshot, myUserId: String, now: Instant): CityBazaarUpdate {
        val state = json.decodeFromJsonElement(CityBazaarPublicState.serializer(), snapshot.publicState)
        val toLocal = { serverMillis: Long -> now + (serverMillis - snapshot.serverTime).milliseconds }
        val money = state.players.associateBy { it.seat }
        val match = CityBazaarMatch(
            matchId = snapshot.matchId,
            title = snapshot.title,
            players = snapshot.seats.map { seat ->
                val wallet = money[seat.seat]
                CityBazaarPlayer(
                    seat = seat.seat,
                    userId = seat.controllerUserId,
                    name = seat.displayName,
                    level = seat.level,
                    colour = CityBazaarColour.entries.firstOrNull { it.name == seat.colour.name } ?: CityBazaarColour.RED,
                    isMe = seat.seat == snapshot.viewer.seat,
                    micOn = seat.micOn,
                    autoplay = seat.cpu || seat.autoPlay,
                    presence = when (seat.presence) {
                        SeatPresence.PLAYING -> CityBazaarPresence.PLAYING
                        SeatPresence.AWAY -> CityBazaarPresence.AWAY
                        SeatPresence.BOT -> CityBazaarPresence.BOT
                    },
                    cash = wallet?.cash ?: 0,
                    position = wallet?.position ?: 0,
                    inJail = wallet?.inJail == true,
                    halfRent = wallet?.halfRent == true,
                    bankrupt = wallet?.bankrupt == true,
                    isCpu = seat.cpu,
                    avatarId = seat.avatarId,
                )
            },
            spectators = snapshot.spectators.map { CityBazaarSpectator(it.userId, it.displayName, it.micOn) },
            spectatorCount = snapshot.spectators.size,
            owners = state.owners,
            levels = state.levels,
            currentSeat = snapshot.turn.seat,
            phase = CityBazaarTurnPhase.entries.firstOrNull { it.name == state.phase } ?: CityBazaarTurnPhase.ROLL,
            die1 = state.die1,
            die2 = state.die2,
            pendingTile = state.pendingTile,
            debt = state.debt?.let { CityBazaarDebtInfo(it.amount, reason(it.reason), it.tile) },
            jailTriesLeft = state.jailTriesLeft,
            auction = state.auction?.let {
                CityBazaarAuctionInfo(it.tile, it.sellerSeat, it.bid, it.leaderSeat, it.nextBid, toLocal(it.endsAt))
            },
            turnEndsAt = snapshot.turn.deadline?.let(toLocal),
            startedAt = toLocal(snapshot.startedAt),
            timeEndsAt = state.timeEndsAt?.let(toLocal),
            round = state.round,
            roundLimit = state.roundLimit,
            netWorth = state.netWorth,
            ranking = state.ranking,
            roomId = snapshot.roomId,
            viewerUserId = myUserId,
        )
        val events = snapshot.events.mapNotNull { event ->
            runCatching { json.decodeFromJsonElement(CityBazaarWireEvent.serializer(), event).toDomain() }
                .onFailure { CrashReporter.record(it, "CityBazaarSnapshotMapper.event") }
                .getOrNull()
        }
        return CityBazaarUpdate(match, events)
    }

    private fun reason(name: String): MoneyReason = MoneyReason.entries.firstOrNull { it.name == name } ?: MoneyReason.CARD

    private fun CityBazaarWireEvent.toDomain(): CityBazaarEvent = when (this) {
        is CityBazaarWireEvent.Rolled -> CityBazaarEvent.Rolled(seat, die1, die2)
        is CityBazaarWireEvent.Moved -> CityBazaarEvent.Moved(seat, from, to, steps, jump)
        is CityBazaarWireEvent.Money -> CityBazaarEvent.Money(from, to, amount, reason(reason), tile)
        is CityBazaarWireEvent.Card -> CityBazaarEvent.Card(seat, cardId)
        is CityBazaarWireEvent.Bought -> CityBazaarEvent.Bought(seat, tile)
        is CityBazaarWireEvent.CantAfford -> CityBazaarEvent.CantAfford(seat, tile)
        is CityBazaarWireEvent.Built -> CityBazaarEvent.Built(seat, tile, level)
        is CityBazaarWireEvent.Sold -> CityBazaarEvent.Sold(seat, tile)
        is CityBazaarWireEvent.Jailed -> CityBazaarEvent.Jailed(seat)
        is CityBazaarWireEvent.Freed -> CityBazaarEvent.Freed(seat)
        is CityBazaarWireEvent.HalfRent -> CityBazaarEvent.HalfRent(seat)
        is CityBazaarWireEvent.AuctionStarted -> CityBazaarEvent.AuctionStarted(seller, tile, opening)
        is CityBazaarWireEvent.BidPlaced -> CityBazaarEvent.BidPlaced(seat, tile, amount)
        is CityBazaarWireEvent.AuctionEnded -> CityBazaarEvent.AuctionEnded(seller, tile, winner, amount)
        is CityBazaarWireEvent.Bankrupt -> CityBazaarEvent.Bankrupt(seat)
        is CityBazaarWireEvent.Skipped -> CityBazaarEvent.Skipped(seat)
        is CityBazaarWireEvent.TurnPassed -> CityBazaarEvent.TurnPassed(seat)
    }
}
