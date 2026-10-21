package com.kglabs28.yapnplay.appengine.features.matchresult.data

import com.kglabs28.yapnplay.appengine.core.network.MatchChannel
import com.kglabs28.yapnplay.appengine.features.matchresult.domain.MatchResultRepository
import com.kglabs28.yapnplay.appengine.features.matchresult.domain.SeatCoins
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MatchResultRepositoryImpl(private val match: MatchChannel) : MatchResultRepository {
    override fun coins(): Flow<List<SeatCoins>> =
        match.endings().map { ended -> ended.ranking.map { SeatCoins(it.seat, it.coinsWon, it.completionCoins) } }
}
