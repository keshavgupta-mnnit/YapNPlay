package com.kglabs28.yapnplay.appengine.features.coins.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.coins.domain.CoinLeagues
import com.kglabs28.yapnplay.appengine.features.coins.domain.CoinsRepository

/** The Quick Play leagues and which ones the user can afford. */
class GetCoinLeaguesUseCase(private val repository: CoinsRepository) {
    suspend operator fun invoke(): AppResult<CoinLeagues> = repository.leagues()
}
