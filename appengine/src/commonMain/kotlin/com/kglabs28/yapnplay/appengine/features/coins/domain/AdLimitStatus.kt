package com.kglabs28.yapnplay.appengine.features.coins.domain

import kotlinx.datetime.Instant

/**
 * Where the user stands with free-coin ads: [adsLeftToday] of [dailyLimit],
 * and [nextAdAt] (this device's clock) when a cooldown is running.
 * [rewards] is what each of the day's ads pays, in order (they rise).
 */
data class AdLimitStatus(
    val coinsPerAd: Int,
    val dailyLimit: Int,
    val adsLeftToday: Int,
    val nextAdAt: Instant?,
    val rewards: List<Int> = emptyList(),
) {
    /** Ads already watched today. */
    val watchedToday: Int get() = (dailyLimit - adsLeftToday).coerceIn(0, dailyLimit)

    fun canWatch(now: Instant): Boolean = adsLeftToday > 0 && (nextAdAt == null || nextAdAt <= now)
}
