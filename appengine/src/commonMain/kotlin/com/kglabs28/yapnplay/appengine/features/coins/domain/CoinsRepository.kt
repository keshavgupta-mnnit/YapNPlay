package com.kglabs28.yapnplay.appengine.features.coins.domain

import com.kglabs28.yapnplay.appengine.core.common.AppResult

/**
 * Coins on the server: the Quick Play leagues and the rewarded-ad grant.
 * A used-up daily ad limit comes back as YapNPlayError.LimitReached.
 */
interface CoinsRepository {
    suspend fun leagues(): AppResult<CoinLeagues>
    suspend fun claimAdReward(adId: String): AppResult<AdCoinsReward>
    /** The free-coin ad limits now (daily count and cooldown). */
    suspend fun adStatus(): AppResult<AdLimitStatus>
}
