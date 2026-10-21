package com.kglabs28.yapnplay.appengine.features.matchresult.domain

import kotlinx.coroutines.flow.Flow

/** The coins paid at the end of a match (any game). */
interface MatchResultRepository {
    fun coins(): Flow<List<SeatCoins>>
}
