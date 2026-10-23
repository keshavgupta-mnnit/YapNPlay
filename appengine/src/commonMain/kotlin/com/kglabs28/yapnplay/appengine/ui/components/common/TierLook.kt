package com.kglabs28.yapnplay.appengine.ui.components.common

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/** How rich a ranked row looks: its glass ([fill]) and rim; [royal] is the top (diamond) look. */
data class TierLook(val fill: List<Color>, val rim: Color, val royal: Boolean = false)

/** The tier looks, plainest to richest: shared by the league picker and the room leaderboard. */
object TierLooks {
    @Composable
    fun starter(): TierLook = YapNPlayTheme.colors.let { TierLook(listOf(it.LeagueStarterTop, it.LeagueStarterBottom), it.LeagueStarterRim) }

    @Composable
    fun bronze(): TierLook = YapNPlayTheme.colors.let { TierLook(listOf(it.LeagueBronzeTop, it.LeagueBronzeBottom), it.LeagueBronzeRim) }

    @Composable
    fun silver(): TierLook = YapNPlayTheme.colors.let { TierLook(listOf(it.LeagueSilverTop, it.LeagueSilverBottom), it.LeagueSilverRim) }

    @Composable
    fun gold(): TierLook = YapNPlayTheme.colors.let { TierLook(listOf(it.LeagueGoldTop, it.LeagueGoldBottom), it.LeagueGoldRim) }

    /** The diamond look. */
    @Composable
    fun royal(): TierLook = YapNPlayTheme.colors.let { TierLook(listOf(it.LeagueRoyalTop, it.LeagueRoyalMid, it.LeagueRoyalBottom), it.LeagueRoyalRim, royal = true) }

    /** A league at [rank] (0 = cheapest) of [count]: starter, bronze, silver, gold; the dearest is always royal. */
    @Composable
    fun forLeague(rank: Int, count: Int): TierLook {
        if (count > 1 && rank == count - 1) return royal()
        val ladder = listOf(starter(), bronze(), silver(), gold())
        return ladder[rank.coerceIn(0, ladder.lastIndex)]
    }

    /** A ranking's [place] (1-based): 1st diamond, 2nd gold, 3rd silver, 4th bronze; null after (a plain row). */
    @Composable
    fun forPlace(place: Int): TierLook? = when (place) {
        1 -> royal()
        2 -> gold()
        3 -> silver()
        4 -> bronze()
        else -> null
    }
}
