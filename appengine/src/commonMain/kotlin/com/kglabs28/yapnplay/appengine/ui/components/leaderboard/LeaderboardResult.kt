package com.kglabs28.yapnplay.appengine.ui.components.leaderboard

import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayColors
import com.kglabs28.yapnplay.appengine.core.common.TextUtils
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.features.matchresult.domain.SeatResult

/**
 * Puts what the server actually paid (MatchEnded) on the leaderboard: each
 * seat's XP as its points (it grows with match length; CPUs get none, so
 * their line is left out) and the coins each place won from the pot
 * (CPUs' shares included: shown, never credited), with the league (the
 * entry everyone paid) above, so a small or empty prize makes sense. The
 * finishing bonus is left out of the coins: only the user's own shows, by their EXP.
 * Anyone whose seat someone else took over comes last, named, marked as left.
 */
object LeaderboardResult {
    fun withResult(board: LeaderboardUi, results: List<SeatResult>): LeaderboardUi {
        if (results.isEmpty()) return board
        val (leavers, seated) = results.partition { it.leaverName != null }
        val bySeat = seated.associateBy { it.seat }
        val entries = board.entries.map { e ->
            val r = bySeat[e.seat] ?: return@map e
            e.copy(
                score = if (r.xp > 0) TableStrings.leaderboardPts(r.xp) else "",
                exp = if (r.xp > 0) TableStrings.leaderboardExp(r.xp) else "",
                // What the place won from the pot (the league above says what it cost to enter).
                coins = if (r.won > 0) TableStrings.leaderboardWon(TextUtils.groupedThousands(r.won)) else TableStrings.leaderboardNoCoins,
            )
        }
        val leaverRows = leavers.sortedBy { it.place }.map { r ->
            LeaderboardEntryUi(
                place = r.place,
                placeLabel = LudoStrings.ludoPlace(r.place),
                name = r.leaverName.orEmpty(),
                avatarId = r.leaverAvatarId,
                title = TableStrings.leaderboardLeft,
                score = if (r.xp > 0) TableStrings.leaderboardPts(r.xp) else "",
                exp = if (r.xp > 0) TableStrings.leaderboardExp(r.xp) else "",
                tint = YapNPlayColors.IconMuted,
                isMe = false,
                seat = r.seat,
                coins = TableStrings.leaderboardNoCoins,
            )
        }
        val mine = board.entries.firstOrNull { it.isMe }?.let { bySeat[it.seat] }
        // Everyone who paid paid the same entry; a player short of coins played free.
        val entry = results.maxOf { it.stake }
        return board.copy(
            entries = entries + leaverRows.filter { row -> board.entries.none { it.name == row.name && it.title == row.title } },
            league = if (entry > 0) TableStrings.leaderboardLeague(TextUtils.groupedThousands(entry)) else TableStrings.leaderboardFreeLeague,
            myExp = mine?.let { if (it.xp > 0) TableStrings.leaderboardGain(TextUtils.groupedThousands(it.xp)) else "" } ?: board.myExp,
            myBonus = mine?.let { if (it.completion > 0) TableStrings.leaderboardCoins(TextUtils.groupedThousands(it.completion)) else "" } ?: board.myBonus,
        )
    }
}
