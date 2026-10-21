package com.kglabs28.yapnplay.appengine.features.coins.domain

/** Coins added after a watched ad: [coins] is the new balance, [adsLeftToday] the rewarded ads still allowed today. */
data class AdCoinsReward(
    val coins: Int,
    val granted: Int,
    val adsLeftToday: Int,
)
