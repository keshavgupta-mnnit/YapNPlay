package com.kglabs28.yapnplay.appengine.features.matchresult.usecase

import com.kglabs28.yapnplay.appengine.features.matchresult.domain.MatchResultRepository
import com.kglabs28.yapnplay.appengine.features.matchresult.domain.SeatCoins
import kotlinx.coroutines.flow.Flow

/** Each seat's coins (pot winnings + finishing bonus), once the server has paid them. */
class ObserveMatchCoinsUseCase(private val repository: MatchResultRepository) {
    operator fun invoke(): Flow<List<SeatCoins>> = repository.coins()
}
