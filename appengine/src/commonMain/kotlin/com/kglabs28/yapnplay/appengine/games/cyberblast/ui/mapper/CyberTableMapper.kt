package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.mapper

import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.ui.CyberBlastStrings
import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatTable
import com.kglabs28.yapnplay.appengine.games.cyberblast.BlastPhase
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberFrameData
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberMatch
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberSeat
import com.kglabs28.yapnplay.appengine.games.cyberblast.ui.screens.match.CyberCardUi
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardEntryUi
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardUi
import com.kglabs28.yapnplay.appengine.ui.components.moderation.EndVoteUi
import com.kglabs28.yapnplay.appengine.ui.components.moderation.EndVoteUiMapper
import com.kglabs28.yapnplay.appengine.ui.components.seat.VoterUi
import com.kglabs28.yapnplay.appengine.ui.components.table.AvatarRing
import com.kglabs28.yapnplay.appengine.ui.components.table.TableAvatarUi
import kotlinx.datetime.Instant
import kotlin.time.Duration

/** Who is talking and who the user muted, for the cards' voice glow. */
data class CyberVoiceInputs(val speaking: Set<String>, val mutedForMe: Set<String>, val mutedTalkers: Set<String>)

/** A Cyber Blast match and its latest frame → what the table shows. */
object CyberTableMapper {

    /** Everyone's card: the user's first, then seat order; hearts and points from the latest frame. */
    fun cards(match: CyberMatch, frame: CyberFrameData?, voice: CyberVoiceInputs): List<CyberCardUi> =
        match.seats.sortedWith(compareBy({ !it.isMe }, { it.seat })).map { s -> card(match, frame, s, voice) }

    private fun card(match: CyberMatch, frame: CyberFrameData?, s: CyberSeat, voice: CyberVoiceInputs): CyberCardUi {
        val body = frame?.body(s.seat)
        val muted = s.userId in voice.mutedForMe
        val out = body?.out == true
        return CyberCardUi(
            seat = s.seat,
            name = if (s.isCpu) TableStrings.seatBot else s.name,
            avatarId = s.avatarId,
            level = if (s.isCpu) "" else s.level.toString(),
            isMe = s.isMe,
            isTurn = false,
            talking = s.userId in voice.speaking && s.micOn && !muted,
            talkingMuted = s.userId in voice.mutedTalkers && !s.micOn,
            turnEndsAtMillis = null,
            turnTotalMs = 0L,
            mutedForMe = muted,
            canSpeak = !s.isCpu,
            micOn = s.micOn,
            lives = body?.lives ?: match.lives,
            maxLives = match.lives,
            reach = CyberBlastStrings.blastReach(body?.radius ?: 1),
            kills = (body?.kills ?: 0).toString(),
            score = CyberBlastStrings.blastScore(body?.score ?: 0),
            tag = when {
                s.removed -> CyberBlastStrings.blastLeftTag
                out -> CyberBlastStrings.blastOutTag
                s.away -> LudoStrings.ludoAway
                else -> null
            },
            faded = s.removed || out,
        )
    }

    /** What changes a card (hearts, points, reach, kills, out): the table re-renders only when this does. */
    fun cardKey(frame: CyberFrameData): List<Long> = frame.bodies.flatMap { listOf(it.lives.toLong(), it.score.toLong(), it.radius.toLong(), it.kills.toLong(), if (it.out) 1L else 0L) }

    /** Over the board: the countdown, "GO!", the closing warning, or the user being out. */
    fun banner(match: CyberMatch, frame: CyberFrameData?, goUntil: Long, now: Long): String {
        frame ?: return ""
        val myBody = match.me?.let { frame.body(it.seat) }
        return when {
            frame.phase == BlastPhase.COUNTDOWN -> ((frame.countdownMs + MILLIS - 1) / MILLIS).coerceAtLeast(1).toString()
            now < goUntil -> CyberBlastStrings.blastGo
            frame.phase == BlastPhase.FINISHED -> ""
            myBody?.out == true -> CyberBlastStrings.blastYouAreOut
            frame.closingInMs != null && frame.closingInMs <= CLOSING_WARNING_MS -> CyberBlastStrings.blastClosingIn((frame.closingInMs + MILLIS - 1) / MILLIS)
            else -> ""
        }
    }

    /** Players still in first, then by points (the server's ranking replaces this once it arrives). */
    fun leaderboard(match: CyberMatch, frame: CyberFrameData?, duration: String): LeaderboardUi {
        val ranked = match.seats.sortedWith(
            compareBy<CyberSeat>({ it.removed }, { frame?.body(it.seat)?.out == true }, { -(frame?.body(it.seat)?.score ?: 0) }, { it.seat }),
        )
        val entries = ranked.mapIndexed { i, s ->
            LeaderboardEntryUi(
                place = i + 1,
                placeLabel = LudoStrings.ludoPlace(i + 1),
                name = s.name,
                avatarId = s.avatarId,
                title = CyberBlastStrings.blastPlaceTitles[i.coerceAtMost(CyberBlastStrings.blastPlaceTitles.lastIndex)],
                score = CyberBlastStrings.blastScore(frame?.body(s.seat)?.score ?: 0),
                exp = "",
                micOn = s.micOn,
                tint = Color.Unspecified,
                isMe = s.isMe,
                seat = s.seat,
            )
        }
        return LeaderboardUi(CyberBlastStrings.blastTitle, duration, entries, entries.firstOrNull { it.isMe }?.placeLabel.orEmpty())
    }

    fun audience(match: CyberMatch, voice: CyberVoiceInputs): List<TableAvatarUi> = match.spectators.map {
        val muted = it.userId in voice.mutedForMe
        TableAvatarUi(it.userId, it.name, AvatarRing.of(it.userId in voice.speaking && !muted, it.micOn, it.userId in voice.mutedTalkers), muted, it.avatarId)
    }

    fun seatTable(match: CyberMatch) = SeatTable(
        offer = match.seatOffer,
        viewerUserId = match.viewerUserId,
        seated = match.me != null,
        roomMatch = match.roomId != null,
        spectators = match.spectators.map { it.userId to it.name },
        canOfferSeats = match.canOfferSeats,
        botSeats = match.seats.filter { !it.removed && (it.isCpu || it.botHeld) }.associate { it.seat to it.name },
    )

    fun endVote(match: CyberMatch, now: Instant): EndVoteUi? = match.endVote?.let {
        EndVoteUiMapper.of(it, match.me?.seat, match.seat(it.proposerSeat)?.name.orEmpty(), CyberBlastStrings.blastEndRanking, now) { seat -> voter(match, seat) }
    }

    private fun voter(match: CyberMatch, seat: Int): VoterUi? = match.seat(seat)?.let { VoterUi(it.name, it.avatarId) }

    /** The match's running time ("12:05"). */
    fun elapsed(time: Duration): String = time.inWholeSeconds.coerceAtLeast(0).let { LudoStrings.ludoClock(it / SECONDS_PER_MINUTE, it % SECONDS_PER_MINUTE) }

    private const val MILLIS = 1_000L
    private const val SECONDS_PER_MINUTE = 60L
    /** The "arena closes in" warning shows this long before (ms). */
    private const val CLOSING_WARNING_MS = 10_000L
}
