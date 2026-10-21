package com.kglabs28.yapnplay.appengine.ui.components.leaderboard

import androidx.compose.ui.graphics.Color

/** One player on the leaderboard: place (1-based), points, a fun title, whether they talked (mic). */
data class LeaderboardEntryUi(
    val place: Int,
    val placeLabel: String,
    val name: String,
    val avatarId: String,
    val title: String,
    /** "+400 PTS". */
    val score: String,
    val exp: String,
    val tint: Color,
    val isMe: Boolean,
    val micOn: Boolean = false,
    val seat: Int = 0,
    /** "+350 coins" once the server paid them; empty before (or for 0). */
    val coins: String = "",
)
