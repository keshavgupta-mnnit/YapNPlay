package com.kglabs28.yapnplay.appengine.games.doodleking.ui.mapper

import com.kglabs28.yapnplay.appengine.ui.components.seat.KeepPlayingUiMapper
import com.kglabs28.yapnplay.appengine.ui.components.seat.KeepPlayingUi
import com.kglabs28.yapnplay.appengine.ui.components.moderation.EndVoteUiMapper
import com.kglabs28.yapnplay.appengine.ui.components.moderation.EndVoteUi
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatTable
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.ui.DoodleStrings
import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleFeedKind
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodleMatch
import com.kglabs28.yapnplay.appengine.games.doodleking.domain.DoodlePhase
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleCardUi
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleFeedRowUi
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleFeedTone
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodlePickUi
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleRankRowUi
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleRevealRowUi
import com.kglabs28.yapnplay.appengine.games.doodleking.ui.screens.match.DoodleRevealUi
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardEntryUi
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardUi
import com.kglabs28.yapnplay.appengine.ui.components.table.AvatarRing
import com.kglabs28.yapnplay.appengine.ui.components.table.TableAvatarUi
import kotlinx.datetime.Instant
import kotlin.time.Duration

/** Who is talking and who the user muted, for the cards' voice rings. */
data class DoodleVoiceInputs(val speaking: Set<String>, val mutedForMe: Set<String>, val mutedTalkers: Set<String>)

/** A Doodle King match → what the table shows. */
object DoodleTableMapper {

    /** Every player's card in seat order (the user's too), with their points and tag. */
    fun cards(match: DoodleMatch, voice: DoodleVoiceInputs): List<DoodleCardUi> = match.players.map { p ->
        val drawing = p.seat == match.drawerSeat && match.phase != DoodlePhase.FINISHED
        val muted = p.userId in voice.mutedForMe
        DoodleCardUi(
            seat = p.seat,
            name = if (p.isCpu) TableStrings.seatBot else p.name,
            avatarId = p.avatarId,
            level = if (p.isCpu) "" else p.level.toString(),
            isMe = p.isMe,
            isTurn = drawing,
            talking = p.userId in voice.speaking && p.micOn && !muted,
            talkingMuted = p.userId in voice.mutedTalkers && !p.micOn,
            // The drawer's ring runs with the drawing time only.
            turnEndsAtMillis = if (drawing && match.phase == DoodlePhase.DRAW) match.phaseEndsAt.toEpochMilliseconds() else null,
            turnTotalMs = match.drawSeconds * MILLIS_PER_SECOND,
            mutedForMe = muted,
            canSpeak = !p.isCpu,
            micOn = p.micOn,
            score = DoodleStrings.dkScore(p.score),
            tag = when {
                p.removed -> DoodleStrings.dkLeftTag
                drawing -> DoodleStrings.dkDrawingTag
                p.guessed && match.phase == DoodlePhase.DRAW -> DoodleStrings.dkGuessedTag
                p.away -> LudoStrings.ludoAway
                else -> null
            },
            left = p.removed,
        )
    }

    /** The turn's feed, the newest last; names in their seat's colour. */
    fun feed(match: DoodleMatch): List<DoodleFeedRowUi> = match.feed.mapIndexed { i, line ->
        val name = match.player(line.seat)?.let { if (it.isMe) DoodleStrings.dkYou else it.name }.orEmpty()
        val (text, tone) = when (line.kind) {
            DoodleFeedKind.GUESS -> line.text.orEmpty() to DoodleFeedTone.GUESS
            DoodleFeedKind.CHAT -> line.text.orEmpty() to DoodleFeedTone.CHAT
            DoodleFeedKind.CLOSE -> DoodleStrings.dkVeryClose(line.text.orEmpty()) to DoodleFeedTone.CLOSE
            DoodleFeedKind.CORRECT -> "${DoodleStrings.dkGuessedIt} ${DoodleStrings.dkPoints(line.points)}" to DoodleFeedTone.CORRECT
            DoodleFeedKind.PASSED -> DoodleStrings.dkPassed(line.points) to DoodleFeedTone.NOTE
            DoodleFeedKind.NO_DRAWING -> DoodleStrings.dkDrewNothing(line.points) to DoodleFeedTone.NOTE
            DoodleFeedKind.SKIPPED -> DoodleStrings.dkSkipped to DoodleFeedTone.NOTE
            DoodleFeedKind.TIME_UP -> DoodleStrings.dkNobodyGot to DoodleFeedTone.NOTE
        }
        // "Nobody got it" is the table's line, not someone's.
        DoodleFeedRowUi(i, line.seat, if (line.kind == DoodleFeedKind.TIME_UP) "" else name, text, tone)
    }

    /** Everyone by points (the live leaderboard). */
    fun ranks(match: DoodleMatch): List<DoodleRankRowUi> = match.ranking.mapIndexed { i, p ->
        DoodleRankRowUi(p.seat, "${i + 1}", if (p.isMe) DoodleStrings.dkYou else p.name, p.score.toString(), p.isMe)
    }

    fun status(match: DoodleMatch): String {
        val name = match.drawer?.name.orEmpty()
        val mine = match.iAmDrawing
        return when (match.phase) {
            DoodlePhase.INTRO -> if (mine) DoodleStrings.dkYourTurn else DoodleStrings.dkTurnOf(name)
            DoodlePhase.PICK -> if (mine) DoodleStrings.dkYouPick else DoodleStrings.dkPicking(name)
            DoodlePhase.DRAW -> if (mine) DoodleStrings.dkYouDraw else DoodleStrings.dkDrawing(name)
            DoodlePhase.REVEAL -> DoodleStrings.dkWordWas
            DoodlePhase.LIMIT -> TableStrings.extendTitle
            DoodlePhase.FINISHED -> DoodleStrings.dkGameOver
        }
    }

    fun pick(match: DoodleMatch, now: Instant): DoodlePickUi? =
        if (match.iAmDrawing && match.phase == DoodlePhase.PICK && match.options.isNotEmpty()) {
            DoodlePickUi(match.options, DoodleStrings.dkPickSeconds(secondsLeft(match.phaseEndsAt, now)))
        } else {
            null
        }

    /** Between turns: the word and what each player scored this turn (best first). */
    fun reveal(match: DoodleMatch): DoodleRevealUi? {
        if (match.phase != DoodlePhase.REVEAL) return null
        val rows = match.players.filter { !it.removed }.sortedByDescending { it.turnPoints }.map {
            DoodleRevealRowUi(if (it.isMe) DoodleStrings.dkYou else it.name, DoodleStrings.dkPoints(it.turnPoints), it.turnPoints > 0)
        }
        return DoodleRevealUi(match.word.orEmpty().uppercase(), rows)
    }

    /** The end-of-match board: by points, a title for each place. */
    fun leaderboard(match: DoodleMatch, duration: String): LeaderboardUi {
        val entries = match.ranking.mapIndexed { i, p ->
            LeaderboardEntryUi(
                place = i + 1,
                placeLabel = LudoStrings.ludoPlace(i + 1),
                name = p.name,
                avatarId = p.avatarId,
                title = DoodleStrings.dkPlaceTitles[i.coerceAtMost(DoodleStrings.dkPlaceTitles.lastIndex)],
                score = DoodleStrings.dkScore(p.score),
                exp = "",
                micOn = p.micOn,
                tint = Color.Unspecified,
                isMe = p.isMe,
                seat = p.seat,
            )
        }
        return LeaderboardUi(DoodleStrings.dkTitle, duration, entries, entries.firstOrNull { it.isMe }?.placeLabel.orEmpty())
    }

    fun audience(match: DoodleMatch, voice: DoodleVoiceInputs): List<TableAvatarUi> = match.spectators.map {
        val muted = it.userId in voice.mutedForMe
        TableAvatarUi(it.userId, it.name, AvatarRing.of(it.userId in voice.speaking && !muted, it.micOn, it.userId in voice.mutedTalkers), muted, it.avatarId)
    }

    /** What every table's seat handover needs to know. */
    fun seatTable(match: DoodleMatch) = SeatTable(
        offer = match.seatOffer,
        viewerUserId = match.viewerUserId,
        seated = match.me != null,
        roomMatch = match.roomId != null,
        spectators = match.spectators.map { it.userId to it.name },
        canOfferSeats = match.canOfferSeats,
        botSeats = match.players.filter { !it.removed && (it.isCpu || it.botHeld) }.associate { it.seat to it.name },
    )

    fun endVote(match: DoodleMatch, now: Instant): EndVoteUi? = match.endVote?.let {
        EndVoteUiMapper.of(it, match.me?.seat, match.player(it.proposerSeat)?.name.orEmpty(), DoodleStrings.dkEndRanking, now)
    }

    fun keepPlaying(match: DoodleMatch, now: Instant): KeepPlayingUi? = match.keepPlaying?.takeIf { match.phase == DoodlePhase.LIMIT }?.let {
        KeepPlayingUiMapper.of(it.voters, it.yes, it.no, it.extraRounds, it.extraMinutes, match.me?.seat, TableStrings.seatCountdown(secondsLeft(it.endsAt, now)))
    }

    fun secondsLeft(endsAt: Instant, now: Instant): Long = ((endsAt - now).inWholeMilliseconds + MILLIS_PER_SECOND - 1).coerceAtLeast(0) / MILLIS_PER_SECOND

    /** The match's running time ("12:05"). */
    fun elapsed(time: Duration): String = time.inWholeSeconds.coerceAtLeast(0).let { LudoStrings.ludoClock(it / SECONDS_PER_MINUTE, it % SECONDS_PER_MINUTE) }

    /** "0:45". */
    fun clock(seconds: Long): String = "${seconds / SECONDS_PER_MINUTE}:${(seconds % SECONDS_PER_MINUTE).toString().padStart(2, '0')}"

    private const val MILLIS_PER_SECOND = 1_000L
    private const val SECONDS_PER_MINUTE = 60L
}
