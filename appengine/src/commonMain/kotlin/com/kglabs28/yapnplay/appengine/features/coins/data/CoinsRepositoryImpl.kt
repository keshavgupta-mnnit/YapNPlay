package com.kglabs28.yapnplay.appengine.features.coins.data

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.features.coins.data.remote.CoinsClient
import com.kglabs28.yapnplay.appengine.features.coins.domain.AdCoinsReward
import com.kglabs28.yapnplay.appengine.features.coins.domain.CoinLeague
import com.kglabs28.yapnplay.appengine.features.coins.domain.CoinLeagues
import com.kglabs28.yapnplay.appengine.features.coins.domain.CoinsRepository
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.withContext

/** Maps the coins endpoints to domain. Never throws: failures come back typed. */
class CoinsRepositoryImpl(
    private val client: CoinsClient,
    private val dispatchers: DispatcherProvider,
) : CoinsRepository {

    override suspend fun leagues(): AppResult<CoinLeagues> = withContext(dispatchers.io) {
        try {
            val body = client.leagues()
            AppResult.Success(CoinLeagues(body.coins, body.leagues.map { CoinLeague(it.entryCoins, it.name, it.canEnter) }))
        } catch (e: Exception) {
            CrashReporter.record(e, "CoinsRepository.leagues")
            AppResult.Failure(YapNPlayError.Network("leagues failed"))
        }
    }

    override suspend fun claimAdReward(adId: String): AppResult<AdCoinsReward> = withContext(dispatchers.io) {
        try {
            val body = client.adReward(adId)
            AppResult.Success(AdCoinsReward(body.coins, body.granted, body.adsLeftToday))
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.TooManyRequests) {
                AppResult.Failure(YapNPlayError.LimitReached(0))
            } else {
                CrashReporter.record(e, "CoinsRepository.claimAdReward")
                AppResult.Failure(YapNPlayError.Network("ad reward failed"))
            }
        } catch (e: Exception) {
            CrashReporter.record(e, "CoinsRepository.claimAdReward")
            AppResult.Failure(YapNPlayError.Network("ad reward failed"))
        }
    }
}
