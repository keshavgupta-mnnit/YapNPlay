package com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby

/** How the room's leaderboard is ranked. */
enum class RoomBoardTab { WINS, COINS }

/** The room's leaderboard: ranked by [tab]; [rows] empty while [loading] or on [error]. */
data class RoomLeaderboardUi(
    val tab: RoomBoardTab = RoomBoardTab.WINS,
    val rows: List<RoomBoardRowUi> = emptyList(),
    val loading: Boolean = true,
    val error: String? = null,
)

/** One member: their place (1st–4th get the diamond, gold, silver, bronze look), name, the ranked value and the other numbers. */
data class RoomBoardRowUi(
    val userId: String,
    val place: Int,
    val name: String,
    val avatarId: String,
    val value: String,
    val detail: String,
    val isMe: Boolean,
)
