package com.kglabs28.yapnplay.appengine.features.coins.domain

/** A Quick Play league: its [entryCoins] fee (0 = Starter, free) and whether the user can pay it now. */
data class CoinLeague(
    val entryCoins: Int,
    val name: String,
    val canEnter: Boolean,
)
