package com.kglabs28.yapnplay.appengine.features.matchresult.data

import com.kglabs28.yapnplay.appengine.core.network.MatchChannel
import com.kglabs28.yapnplay.appengine.features.matchresult.domain.MatchResultRepository
import com.kglabs28.yapnplay.appengine.features.matchresult.domain.SeatResult
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class MatchResultRepositoryImpl(private val match: MatchChannel) : MatchResultRepository {
    override fun coins(): Flow<List<SeatResult>> =
        match.endings().map { ended -> ended.ranking.map { SeatResult(it.seat, it.coinsWon, it.completionCoins, it.xpGained) } }
}
