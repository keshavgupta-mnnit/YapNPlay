package com.kglabs28.yapnplay.appengine.features.coins.domain

/** The user's [coins] and the leagues they can pick from. */
data class CoinLeagues(
    val coins: Int,
    val leagues: List<CoinLeague>,
)
