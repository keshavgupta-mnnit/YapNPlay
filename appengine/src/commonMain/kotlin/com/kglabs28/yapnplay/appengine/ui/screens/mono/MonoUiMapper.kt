package com.kglabs28.yapnplay.appengine.ui.screens.mono

import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatTable
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoContinueVote
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardEntryUi
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardUi
import kotlinx.datetime.Instant
import com.kglabs28.yapnplay.appengine.core.ui.MonoStrings
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoMatch
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoPlayer
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosMatch
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosPlayer
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants
import com.kglabs28.yapnplay.appengine.protocol.SeatPresence
import com.kglabs28.yapnplay.appengine.ui.components.table.AvatarRing
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoHue
import com.kglabs28.yapnplay.appengine.ui.components.table.MonoPlayerPodUi
import com.kglabs28.yapnplay.appengine.ui.components.table.TableAvatarUi
import com.kglabs28.yapnplay.appengine.ui.components.table.rim

/**
 * Turns a Standard MONO match into the cards the table draws. Shared by
 * both MONO games: they differ in the model, not in how a player reads.
 */
object MonoUiMapper {

    /** What the shared seat dialogs need from a MONO match. */
    fun seatTable(match: MonoMatch) = SeatTable(
        offer = match.seatOffer,
        viewerUserId = match.viewerUserId,
        seated = match.me != null,
        roomMatch = match.roomId != null,
        spectators = match.spectators.map { it.userId to it.name },
        canOfferSeats = match.canOfferSeats,
        botSeats = match.players.filter { !it.removed && (it.isCpu || it.presence == SeatPresence.BOT.name) }.associate { it.seat to it.name },
    )

    /** What the shared seat dialogs need from a Complete Chaos match. */
    fun seatTable(match: MonoChaosMatch) = SeatTable(
        offer = match.seatOffer,
        viewerUserId = match.viewerUserId,
        seated = match.me != null,
        roomMatch = match.roomId != null,
        spectators = match.spectators.map { it.userId to it.name },
        canOfferSeats = match.canOfferSeats,
        botSeats = match.players.filter { !it.removed && (it.isCpu || it.presence == SeatPresence.BOT.name) }.associate { it.seat to it.name },
    )

    /** The colour a seat wears: the four card colours, in seat order. */
    fun seatTint(seat: Int): Color = SEAT_HUES[(seat - 1).mod(SEAT_HUES.size)].rim

    /** [player] as the pod the table draws. */
    fun card(
        player: MonoPlayer,
        currentSeat: Int,
        timerFraction: Float,
        places: Map<Int, String>,
        calledSeat: Int? = null,
        talking: Set<String> = emptySet(),
        mutedForMe: Set<String> = emptySet(),
        mutedTalkers: Set<String> = emptySet(),
    ) = MonoPlayerPodUi(
        seat = player.seat,
        name = player.name,
        avatarId = player.avatarId.orEmpty(),
        level = if (player.isCpu) "" else player.level.toString(),
        tint = seatTint(player.seat),
        isMe = player.isMe,
        isTurn = player.seat == currentSeat,
        talking = player.userId != null && player.userId in talking && player.userId !in mutedForMe,
        talkingMuted = player.userId != null && player.userId in mutedTalkers && !player.micOn,
        micOn = player.micOn,
        cards = player.cardCount,
        timerFraction = timerFraction,
        placeLabel = places[player.seat],
        mutedForMe = player.userId != null && player.userId in mutedForMe,
        // The user's own lit Autoplay switch says it already; others get the tag.
        statusLabel = if (player.isMe) null else statusLabel(player.presence, player.autoplay, player.isCpu),
        canSpeak = !player.isCpu,
        calledMono = player.seat == calledSeat && player.cardCount == 1,
    )

    /** [player] as the pod the table draws (Complete Chaos). */
    fun chaosCard(
        player: MonoChaosPlayer,
        currentSeat: Int,
        timerFraction: Float,
        places: Map<Int, String>,
        eliminated: Set<Int>,
        calledSeat: Int? = null,
        talking: Set<String> = emptySet(),
        mutedForMe: Set<String> = emptySet(),
        mutedTalkers: Set<String> = emptySet(),
    ) = MonoPlayerPodUi(
        seat = player.seat,
        name = player.name,
        avatarId = player.avatarId.orEmpty(),
        level = if (player.isCpu) "" else player.level.toString(),
        tint = seatTint(player.seat),
        isMe = player.isMe,
        isTurn = player.seat == currentSeat,
        talking = player.userId != null && player.userId in talking && player.userId !in mutedForMe,
        talkingMuted = player.userId != null && player.userId in mutedTalkers && !player.micOn,
        micOn = player.micOn,
        cards = player.cardCount,
        timerFraction = timerFraction,
        placeLabel = places[player.seat],
        mutedForMe = player.userId != null && player.userId in mutedForMe,
        // Knocked out at 25 cards: their name stays, their count does not.
        statusLabel = if (player.seat in eliminated) {
            MonoStrings.monoEliminated
        } else {
            statusLabel(player.presence, player.autoplay, player.isCpu).takeUnless { player.isMe }
        },
        canSpeak = !player.isCpu,
        eliminated = player.seat in eliminated,
        calledMono = player.seat == calledSeat && player.cardCount == 1,
    )

    /**
     * The opponents in turn order, starting with the seat after the user, so
     * the table reads clockwise from the user's seat (bottom left): up the
     * left column, along the top, down the right. A spectator sees seat
     * order from seat 1.
     */
    fun <T> clockwise(players: List<T>, mySeat: Int?, seatOf: (T) -> Int): List<T> {
        val sorted = players.sortedBy(seatOf)
        if (mySeat == null) return sorted
        val after = sorted.filter { seatOf(it) > mySeat }
        val before = sorted.filter { seatOf(it) < mySeat }
        return after + before
    }

    /** The keep-playing vote as the prompt shows it; null when none is open. */
    fun vote(vote: MonoContinueVote?, mySeat: Int?, now: Instant): MonoVoteUi? = vote?.let {
        MonoVoteUi(
            secondsLeft = (it.endsAt - now).inWholeSeconds.coerceAtLeast(0),
            yes = it.yes.size,
            no = it.no.size,
            voters = it.voters.size,
            canVote = mySeat != null && mySeat in it.voters && mySeat !in it.yes && mySeat !in it.no,
        )
    }

    /** One player on the final board: who, how many cards they still hold, whether they finished or were knocked out. */
    data class Standing(
        val seat: Int,
        val name: String,
        val avatarId: String?,
        val micOn: Boolean,
        val isMe: Boolean,
        val cards: Int,
        val finished: Boolean,
        val knockedOut: Boolean = false,
    )

    /**
     * The end-of-match leaderboard. The server's ranking decides the order;
     * anyone it leaves out (an older server ranks finishers only) follows by
     * fewest cards, knocked-out players last.
     */
    fun leaderboard(title: String, ranking: List<Int>, standings: List<Standing>, duration: String): LeaderboardUi {
        val bySeat = standings.associateBy { it.seat }
        val rest = standings
            .filter { it.seat !in ranking }
            .sortedWith(compareBy<Standing>({ it.knockedOut }, { it.cards }, { it.seat }))
        val ordered = ranking.mapNotNull { bySeat[it] } + rest
        val entries = ordered.mapIndexed { index, s ->
            LeaderboardEntryUi(
                place = index + 1,
                placeLabel = placeLabel(index + 1),
                name = s.name,
                avatarId = s.avatarId.orEmpty(),
                title = MonoStrings.monoPlaceTitles[index.coerceAtMost(MonoStrings.monoPlaceTitles.lastIndex)],
                score = when {
                    s.finished -> MonoStrings.monoFinished
                    s.knockedOut -> MonoStrings.monoKnockedOutScore
                    else -> MonoStrings.monoCardsLeft(s.cards)
                },
                // The server's XP, filled in by LeaderboardResult once it pays it.
                exp = "",
                tint = Color.Unspecified,
                isMe = s.isMe,
                micOn = s.micOn,
                seat = s.seat,
            )
        }
        val mine = entries.firstOrNull { it.isMe }
        return LeaderboardUi(
            game = title,
            duration = duration,
            entries = entries,
            myPlace = mine?.placeLabel.orEmpty(),
        )
    }

    /** Finishing places from the match's ranking; empty while it plays. */
    fun places(match: MonoMatch): Map<Int, String> =
        match.ranking.mapIndexed { index, entry -> entry.seat to placeLabel(index + 1) }.toMap()

    /** Finishing places from the match's ranking (Complete Chaos). */
    fun chaosPlaces(match: MonoChaosMatch): Map<Int, String> =
        match.ranking.mapIndexed { index, entry -> entry.seat to placeLabel(index + 1) }.toMap()

    /** The seat that emptied their hand first, if anyone has. */
    fun winner(match: MonoMatch): Int? = match.finishedSeats.minOrNull()?.takeIf { match.phase == FINISHED }

    /** The seat that emptied their hand first (Complete Chaos). */
    fun chaosWinner(match: MonoChaosMatch): Int? =
        match.finishedSeats.minOrNull()?.takeIf { match.phase == FINISHED }

    /** "1st" … "8th" for a finishing place. */
    fun placeLabel(place: Int): String = MonoStrings.monoPlace(place)

    /** Spectators and absent players, for the top bar strip. */
    /** The spectators in the top bar: their voice ring (talking only when the user hears them) and whether the user muted them. */
    fun audience(match: MonoMatch, talking: Set<String>, mutedForMe: Set<String>, mutedTalkers: Set<String> = emptySet()) = match.spectators.map {
        TableAvatarUi(it.userId, it.name, AvatarRing.of(it.userId in talking && it.userId !in mutedForMe, it.micOn, it.userId in mutedTalkers), it.userId in mutedForMe, it.avatarId)
    }

    /** Spectators and absent players (Complete Chaos). */
    fun chaosAudience(match: MonoChaosMatch, talking: Set<String>, mutedForMe: Set<String>, mutedTalkers: Set<String> = emptySet()) = match.spectators.map {
        TableAvatarUi(it.userId, it.name, AvatarRing.of(it.userId in talking && it.userId !in mutedForMe, it.micOn, it.userId in mutedTalkers), it.userId in mutedForMe, it.avatarId)
    }

    /** "Away" / "Autoplay" while the server plays for someone (a CPU seat needs no tag). */
    private fun statusLabel(presence: String, autoplay: Boolean, isCpu: Boolean): String? = when {
        isCpu || presence == SeatPresence.BOT.name -> null
        presence == SeatPresence.AWAY.name -> MonoStrings.monoAway
        autoplay -> MonoStrings.monoAutoplay
        else -> null
    }

    private val SEAT_HUES = listOf(
        MonoHue.RED,
        MonoHue.GREEN,
        MonoHue.BLUE,
        MonoHue.GOLD,
    )

    private const val FINISHED = "FINISHED"

    /** Standard MONO's colour set, so callers need no extra import. */
    val COLOURS: List<MonoConstants.Color> = MonoConstants.Color.entries

    /** Complete Chaos's colour set. */
    val CHAOS_COLOURS: List<MonoChaosConstants.Color> = MonoChaosConstants.Color.entries
}
