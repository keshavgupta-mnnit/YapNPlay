package com.kglabs28.yapnplay.appengine.games.doodleking.domain

import com.kglabs28.yapnplay.appengine.games.base.match.MatchKeepPlayingVote
import com.kglabs28.yapnplay.appengine.games.base.match.MatchEndVote
import com.kglabs28.yapnplay.appengine.features.seathandover.domain.SeatOffer
import kotlinx.datetime.Instant

/**
 * A Doodle King match as this user sees it. [word] is theirs only while they
 * draw (everyone's at the reveal); guessers see [hint]. Times are on this
 * device's clock.
 */
data class DoodleMatch(
    val matchId: String,
    val title: String,
    val roomId: String?,
    val players: List<DoodlePlayer>,
    val spectators: List<DoodleSpectator>,
    val phase: DoodlePhase,
    val round: Int,
    val rounds: Int,
    /** EASY, MEDIUM or HARD. */
    val level: String,
    val drawerSeat: Int,
    val phaseEndsAt: Instant,
    val drawSeconds: Int,
    val word: String?,
    val options: List<String>,
    val hint: String,
    val feed: List<DoodleFeedLine>,
    val drawingId: Int,
    val strokeCount: Int,
    val startedAt: Instant,
    val viewerUserId: String,
    /** A seat changing hands (every table's handover), and whether the user may offer a bot's seat. */
    val seatOffer: SeatOffer? = null,
    val canOfferSeats: Boolean = false,
    /** Someone asked to end the match early (every table's vote). */
    val endVote: MatchEndVote? = null,
    /** The rounds are over: keep playing? (every table's vote). */
    val keepPlaying: MatchKeepPlayingVote? = null,
) {
    val me: DoodlePlayer? get() = players.firstOrNull { it.isMe }
    val drawer: DoodlePlayer? get() = player(drawerSeat)
    val iAmDrawing: Boolean get() = me?.seat == drawerSeat && phase != DoodlePhase.FINISHED
    val spectating: Boolean get() = me == null

    fun player(seat: Int): DoodlePlayer? = players.firstOrNull { it.seat == seat }

    /** Everyone by points, players who left last. */
    val ranking: List<DoodlePlayer> get() = players.sortedWith(compareBy({ it.removed }, { -it.score }, { it.seat }))
}
