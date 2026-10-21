package com.kglabs28.yapnplay.appengine.features.coins.data.remote

import com.kglabs28.yapnplay.appengine.core.network.BackendPaths
import com.kglabs28.yapnplay.appengine.protocol.AdRewardBody
import com.kglabs28.yapnplay.appengine.protocol.CoinsReward
import com.kglabs28.yapnplay.appengine.protocol.Leagues
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.contentType

class CoinsClientImpl(private val client: HttpClient) : CoinsClient {

    override suspend fun leagues(): Leagues = client.get(BackendPaths.LEAGUES).body()

    override suspend fun adReward(adId: String): CoinsReward = client.post(BackendPaths.AD_REWARD) {
        contentType(ContentType.Application.Json)
        setBody(AdRewardBody(adId))
    }.body()
}
