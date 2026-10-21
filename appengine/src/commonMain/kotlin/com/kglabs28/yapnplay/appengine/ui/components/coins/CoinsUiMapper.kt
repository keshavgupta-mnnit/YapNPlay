package com.kglabs28.yapnplay.appengine.ui.components.coins

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.TextUtils
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.core.ui.CoinsStrings
import com.kglabs28.yapnplay.appengine.features.coins.domain.AdCoinsReward
import com.kglabs28.yapnplay.appengine.features.coins.domain.CoinLeagues

/** Coins domain → what the dialogs show. */
object CoinsUiMapper {

    fun picker(data: CoinLeagues, message: String? = null) = LeaguePickerUi(
        balanceLabel = CoinsStrings.leaguesBalance(TextUtils.groupedThousands(data.coins)),
        leagues = data.leagues.map {
            LeagueUi(
                entryCoins = it.entryCoins,
                name = it.name,
                feeLabel = if (it.entryCoins == 0) CoinsStrings.leagueFree else CoinsStrings.leagueFee(TextUtils.groupedThousands(it.entryCoins)),
                canEnter = it.canEnter,
            )
        },
        message = message,
    )

    /** The line shown after an ad: coins added, today's limit used, or no ad. */
    fun adMessage(result: AppResult<AdCoinsReward>): String = when (result) {
        is AppResult.Success -> CoinsStrings.adGranted(TextUtils.groupedThousands(result.data.granted))
        is AppResult.Failure -> if (result.error is YapNPlayError.LimitReached) CoinsStrings.adLimit else CoinsStrings.adUnavailable
    }
}
