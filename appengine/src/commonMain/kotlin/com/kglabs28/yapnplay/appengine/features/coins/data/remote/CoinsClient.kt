package com.kglabs28.yapnplay.appengine.features.coins.data.remote

import com.kglabs28.yapnplay.appengine.protocol.AdRewardStatus
import com.kglabs28.yapnplay.appengine.protocol.CoinsReward
import com.kglabs28.yapnplay.appengine.protocol.Leagues

/** Coins over HTTP. */
interface CoinsClient {
    /** `GET /coins/leagues` */
    suspend fun leagues(): Leagues

    /** `POST /coins/ad-reward` (429 once today's ads are used). */
    suspend fun adReward(adId: String): CoinsReward

    /** `GET /coins/ad-status` */
    suspend fun adStatus(): AdRewardStatus
}
