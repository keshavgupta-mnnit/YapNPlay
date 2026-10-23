package com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby

import com.kglabs28.yapnplay.appengine.core.common.TextUtils
import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.RoomMemberRecord

/** A room's records as leaderboard rows. Pure. */
object RoomLeaderboardUiMapper {
    /** Ranked by wins (then coins) or coins (then wins); equal records share a place. */
    fun rows(records: List<RoomMemberRecord>, tab: RoomBoardTab, myUserId: String?): List<RoomBoardRowUi> {
        val key: (RoomMemberRecord) -> Pair<Int, Int> = when (tab) {
            RoomBoardTab.WINS -> { r -> r.wins to r.coins }
            RoomBoardTab.COINS -> { r -> r.coins to r.wins }
        }
        val sorted = records.sortedWith(compareByDescending<RoomMemberRecord> { key(it).first }.thenByDescending { key(it).second }.thenBy { it.name.lowercase() })
        var place = 0
        var last: Pair<Int, Int>? = null
        return sorted.mapIndexed { index, r ->
            if (key(r) != last) place = index + 1
            last = key(r)
            RoomBoardRowUi(
                userId = r.userId,
                place = place,
                name = r.name,
                avatarId = r.avatarId,
                value = when (tab) {
                    RoomBoardTab.WINS -> RoomStrings.boardWins(r.wins)
                    RoomBoardTab.COINS -> RoomStrings.boardCoins(TextUtils.groupedThousands(r.coins))
                },
                detail = when (tab) {
                    RoomBoardTab.WINS -> RoomStrings.boardPlayedCoins(r.played, TextUtils.groupedThousands(r.coins))
                    RoomBoardTab.COINS -> RoomStrings.boardPlayedWins(r.played, r.wins)
                },
                isMe = r.userId == myUserId,
            )
        }
    }
}
