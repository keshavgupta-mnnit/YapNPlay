package com.kglabs28.yapnplay.appengine.ui.components.leaderboard

import com.kglabs28.yapnplay.appengine.core.common.TextUtils
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.features.matchresult.domain.SeatResult

/**
 * Puts what the server actually paid (MatchEnded) on the leaderboard: each
 * seat's XP as its points (it grows with match length; CPUs get none, so
 * their line is left out) and coins, and the user's own in the bottom bar.
 */
object LeaderboardResult {
    fun withResult(board: LeaderboardUi, results: List<SeatResult>): LeaderboardUi {
        if (results.isEmpty()) return board
        val bySeat = results.associateBy { it.seat }
        val entries = board.entries.map { e ->
            val r = bySeat[e.seat] ?: return@map e
            e.copy(
                score = if (r.xp > 0) TableStrings.leaderboardPts(r.xp) else "",
                exp = if (r.xp > 0) TableStrings.leaderboardExp(r.xp) else "",
                coins = if (r.coins > 0) TableStrings.leaderboardCoins(TextUtils.groupedThousands(r.coins)) else TableStrings.leaderboardNoCoins,
            )
        }
        val mine = board.entries.firstOrNull { it.isMe }?.let { bySeat[it.seat] }
        return board.copy(
            entries = entries,
            myExp = mine?.let { if (it.xp > 0) TableStrings.leaderboardGain(TextUtils.groupedThousands(it.xp)) else "" } ?: board.myExp,
            myCoins = mine?.let { if (it.coins > 0) TableStrings.leaderboardGain(TextUtils.groupedThousands(it.coins)) else "" } ?: board.myCoins,
        )
    }
}
