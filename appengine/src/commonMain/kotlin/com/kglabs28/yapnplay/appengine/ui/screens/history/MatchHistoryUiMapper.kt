package com.kglabs28.yapnplay.appengine.ui.screens.history

import androidx.compose.ui.graphics.Color
import com.kglabs28.yapnplay.appengine.core.common.TextUtils
import com.kglabs28.yapnplay.appengine.core.ui.CityBazaarStrings
import com.kglabs28.yapnplay.appengine.core.ui.HistoryStrings
import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import com.kglabs28.yapnplay.appengine.core.ui.MonoStrings
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.features.matchhistory.domain.PastMatch
import com.kglabs28.yapnplay.appengine.games.base.identity.GameIds
import com.kglabs28.yapnplay.appengine.games.mono.identity.MonoConstants
import com.kglabs28.yapnplay.appengine.games.monochaos.identity.MonoChaosConstants
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardEntryUi
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardResult
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardUi
import com.kglabs28.yapnplay.appengine.ui.screens.mono.monoElapsed
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/** Past matches as history rows, and one of them as its leaderboard again. */
object MatchHistoryUiMapper {
    fun row(match: PastMatch): MatchHistoryRowUi {
        val mine = match.mine
        val result = mine?.result
        val ended = Instant.fromEpochMilliseconds(match.endedAt).toLocalDateTime(TimeZone.currentSystemDefault())
        val date = ended.date
        // A paid match shows the net (won and bonus, less the entry); a free one what was won.
        val net = result?.let { if (it.stake > 0) it.net else it.coins } ?: 0
        return MatchHistoryRowUi(
            matchId = match.matchId,
            gameId = match.gameId,
            game = gameName(match.gameId),
            // Left before the end: abandoned, not a place.
            place = mine?.let { if (it.left) HistoryStrings.historyAbandoned else TableStrings.placeLabel(it.place) },
            placeNumber = mine?.takeUnless { it.left }?.place,
            line = listOfNotNull(
                if (match.roomMatch) match.roomName.ifBlank { HistoryStrings.historyRoom } else HistoryStrings.historyQuickPlay,
                league(match),
                Strings.shortDateTime(date.dayOfMonth, date.monthNumber, date.year, ended.hour, ended.minute),
            ).joinToString(SEPARATOR),
            // "Net": unlike the leaderboard's "Won" (the prize alone), this is after the entry and with the finishing bonus.
            coins = when {
                net < 0 -> HistoryStrings.historyNetLoss(TextUtils.groupedThousands(-net))
                net > 0 -> HistoryStrings.historyNetGain(TextUtils.groupedThousands(net))
                else -> HistoryStrings.historyNetEven
            },
            lost = net < 0,
            xp = HistoryStrings.historyXp(TextUtils.groupedThousands(result?.xp ?: 0)),
            opensBoard = mine?.left != true,
        )
    }

    /** "500 coins league", or "Free league": everyone who paid paid the same entry. */
    private fun league(match: PastMatch): String =
        match.seats.maxOfOrNull { it.result.stake }?.takeIf { it > 0 }?.let { HistoryStrings.historyLeague(TextUtils.groupedThousands(it)) }
            ?: HistoryStrings.historyFreeLeague

    /** The match's end-of-game leaderboard, as the table showed it. */
    fun board(match: PastMatch): LeaderboardUi {
        val titles = placeTitles(match.gameId)
        val entries = match.seats.map { seat ->
            LeaderboardEntryUi(
                place = seat.place,
                placeLabel = TableStrings.placeLabel(seat.place),
                name = seat.name,
                avatarId = seat.avatarId,
                title = titles[(seat.place - 1).coerceIn(0, titles.lastIndex)],
                score = "",
                exp = "",
                tint = Color.Unspecified,
                isMe = seat.seat == match.mySeat,
                seat = seat.seat,
            )
        }
        val base = LeaderboardUi(
            game = gameName(match.gameId),
            duration = monoElapsed(Instant.fromEpochMilliseconds(match.startedAt), Instant.fromEpochMilliseconds(match.endedAt)),
            entries = entries,
            myPlace = entries.firstOrNull { it.isMe }?.placeLabel.orEmpty(),
        )
        return LeaderboardResult.withResult(base, match.seats.map { it.result })
    }

    private fun gameName(gameId: String): String = when (gameId) {
        GameIds.CITY_BAZAAR -> CityBazaarStrings.cbTitle
        GameIds.ROYAL_LUDO -> LudoStrings.ludoTitle
        MonoConstants.ID -> MonoStrings.monoTitle
        MonoChaosConstants.ID -> MonoStrings.monoChaosTitle
        else -> gameId
    }

    private fun placeTitles(gameId: String): List<String> = when (gameId) {
        GameIds.CITY_BAZAAR -> CityBazaarStrings.cbPlaceTitles
        GameIds.ROYAL_LUDO -> LudoStrings.ludoPlaceTitles
        else -> MonoStrings.monoPlaceTitles
    }

    private const val SEPARATOR = " · "
}
