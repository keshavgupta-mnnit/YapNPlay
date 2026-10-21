package com.kglabs28.yapnplay.appengine.ui.components.coins

/** One league row: "Bronze", "100" (or FREE), locked when the user can't pay. */
data class LeagueUi(
    val entryCoins: Int,
    val name: String,
    val feeLabel: String,
    val canEnter: Boolean,
)
