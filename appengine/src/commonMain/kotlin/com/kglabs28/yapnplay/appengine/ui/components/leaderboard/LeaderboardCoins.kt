package com.kglabs28.yapnplay.appengine.ui.components.leaderboard

import com.kglabs28.yapnplay.appengine.core.common.TextUtils
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.features.matchresult.domain.SeatCoins

/** Puts the coins the server paid (MatchEnded) on the leaderboard. */
object LeaderboardCoins {
    fun withCoins(board: LeaderboardUi, coins: List<SeatCoins>): LeaderboardUi {
        val bySeat = coins.associateBy { it.seat }
        val entries = board.entries.map { e ->
            val total = bySeat[e.seat]?.total ?: 0
            e.copy(coins = if (total > 0) TableStrings.leaderboardCoins(TextUtils.groupedThousands(total)) else "")
        }
        val mine = board.entries.firstOrNull { it.isMe }?.let { bySeat[it.seat]?.total } ?: 0
        return board.copy(entries = entries, myCoins = if (mine > 0) "+" + TextUtils.groupedThousands(mine) else "")
    }
}
