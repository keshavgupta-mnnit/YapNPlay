package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.mapper

import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatTable
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import com.kglabs28.yapnplay.appengine.core.ui.CityBazaarStrings
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbNoticeArt
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.MoneyReason
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarEvent
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarExtensionVoteInfo
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardEntryUi
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbTransactionUi
import com.kglabs28.yapnplay.appengine.ui.components.profile.AvatarPresets
import com.kglabs28.yapnplay.appengine.ui.components.table.AvatarRing
import com.kglabs28.yapnplay.appengine.ui.components.table.TableAvatarUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarAuctionInfo
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarColour
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarMatch
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarPlayer
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarPresence
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarTurnPhase
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CardDeck
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CardEffect
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CityBazaarBoard
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CityBazaarCards
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CityGroup
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.HOTEL_LEVEL
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.JAIL_FINE
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.RESALE_DIVISOR
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.RupeeFormat
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.SET_FOR_DOUBLE
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.TileKind
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbAssetUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbDeckRowUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbModal
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbOwnedRowUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbPanel
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbPlayerCardUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbPropertyCardUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbRateRowUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbResultRowUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbRowKind
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbSheetRowUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbStackUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbTileUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbToastUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match.CbTokenUi
import kotlin.time.Duration

/** The table: tiles, tokens, player cards and their sides, the audience, the user's colour stacks. */
object CbTableMapper {
    fun tiles(owners: Map<Int, Int>, levels: Map<Int, Int>, colourOf: (Int) -> CityBazaarColour?): List<CbTileUi> =
        CityBazaarBoard.tiles.map { CbTileUi(it.index, owners[it.index]?.let(colourOf), levels[it.index] ?: 0) }

    fun tokens(match: CityBazaarMatch, positions: Map<Int, Int>): List<CbTokenUi> =
        match.players.filter { !it.bankrupt }.map { CbTokenUi(it.seat, it.colour, positions[it.seat] ?: it.position) }

    /** What every card needs: [turnEndsAtMillis] null while a move animates; [cash] as shown so far. */
    data class CardInputs(
        val speaking: Set<String>,
        val mutedForMe: Set<String>,
        /** When the current turn ends (this device's clock); null while a move animates (the ring stays full). */
        val turnEndsAtMillis: Long?,
        val turnSeconds: Long,
        val cash: Map<Int, Int>,
        /** Who is talking with their mic off (the user included): a grey glow. */
        val mutedTalkers: Set<String> = emptySet(),
    )

    /** What the shared seat dialogs need from this match. */
    fun seatTable(match: CityBazaarMatch) = SeatTable(
        offer = match.seatOffer,
        viewerUserId = match.viewerUserId,
        seated = match.me != null,
        roomMatch = match.isRoomMatch,
        spectators = match.spectators.map { it.userId to it.name },
        canOfferSeats = match.canOfferSeats,
        botSeats = match.players.filter { !it.removed && !it.bankrupt && (it.isCpu || it.presence == CityBazaarPresence.BOT) }.associate { it.seat to it.name },
    )

    fun cards(match: CityBazaarMatch, inputs: CardInputs): List<CbPlayerCardUi> {
        val totalMs = inputs.turnSeconds * MILLIS
        val finished = match.phase == CityBazaarTurnPhase.FINISHED
        return match.players.map { p ->
            val isTurn = p.seat == match.currentSeat && !finished && !p.bankrupt
            val muted = p.userId in inputs.mutedForMe
            val place = match.ranking.indexOf(p.seat)
            // A seat the CPU plays for someone who left looks just like a CPU seat.
            val cpuPlays = p.isCpu || p.presence == CityBazaarPresence.BOT
            CbPlayerCardUi(
                seat = p.seat,
                name = if (cpuPlays && !p.isCpu && !p.isMe) TableStrings.seatBot else p.name,
                avatarId = if (cpuPlays && !p.isCpu && !p.isMe) AvatarPresets.CPU else p.avatarId,
                // CPUs have no level.
                level = if (cpuPlays) "" else p.level.toString(),
                colour = p.colour,
                cash = inputs.cash[p.seat] ?: p.cash,
                rentCollected = p.rentCollected,
                isMe = p.isMe,
                isTurn = isTurn,
                talking = p.userId in inputs.speaking && p.micOn && !muted,
                talkingMuted = p.userId in inputs.mutedTalkers && !p.micOn,
                turnEndsAtMillis = if (isTurn) inputs.turnEndsAtMillis else null,
                turnTotalMs = totalMs,
                status = when {
                    finished && place >= 0 -> LudoStrings.ludoPlace(place + 1)
                    p.bankrupt -> CityBazaarStrings.cbStatusBankrupt
                    p.inJail && p.halfRent -> CityBazaarStrings.cbStatusHalf
                    p.inJail -> CityBazaarStrings.cbStatusJail
                    p.presence == CityBazaarPresence.AWAY -> CityBazaarStrings.cbStatusAway
                    else -> null
                },
                mutedForMe = muted,
                canSpeak = !cpuPlays,
                micOn = p.micOn,
                out = p.bankrupt,
            )
        }
    }

    /** Card columns: every other player on the left in turn order, the user on the right. Watching: half each side. */
    fun sides(match: CityBazaarMatch, cards: List<CbPlayerCardUi>): Triple<CbPlayerCardUi?, List<CbPlayerCardUi>, List<CbPlayerCardUi>> {
        val mine = cards.firstOrNull { it.isMe }
        val start = match.me?.seat ?: 0
        // A player who left takes no seat; everyone else spreads out round the table again.
        val others = cards.filterNot { it.isMe || match.player(it.seat)?.removed == true }.sortedBy { (it.seat - start + match.players.size) % match.players.size }
        if (mine != null) return Triple(mine, others, emptyList())
        val rightCount = (others.size + 1) / 2
        return Triple(null, others.drop(rightCount), others.take(rightCount))
    }

    fun audience(match: CityBazaarMatch, speaking: Set<String>, mutedForMe: Set<String>, mutedTalkers: Set<String> = emptySet()): List<TableAvatarUi> =
        match.spectators.map { TableAvatarUi(it.userId, it.name, AvatarRing.of(it.userId in speaking && it.userId !in mutedForMe, it.micOn, it.userId in mutedTalkers), it.userId in mutedForMe, it.avatarId) }

    /** The user's cities stacked by colour, in board colour order. */
    fun stacks(match: CityBazaarMatch): List<CbStackUi> {
        val me = match.me ?: return emptyList()
        val mine = match.owners.filter { it.value == me.seat }.keys
        return CityGroup.entries.mapNotNull { group ->
            val count = CityBazaarBoard.groupTiles(group).count { it in mine }
            if (count == 0) null else CbStackUi(group, count, CbFormat.groupName(group))
        }
    }

    private const val MILLIS = 1_000L
}
