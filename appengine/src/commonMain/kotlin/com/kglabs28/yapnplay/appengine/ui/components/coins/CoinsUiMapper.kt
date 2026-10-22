package com.kglabs28.yapnplay.appengine.ui.components.coins

import kotlinx.datetime.Instant
import com.kglabs28.yapnplay.appengine.features.coins.domain.AdLimitStatus
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

    /** [offer] with today's limits on it: how many ads are left, or how long to wait (button off). */
    fun withLimits(offer: CoinsOfferUi, status: AdLimitStatus?, now: Instant): CoinsOfferUi {
        if (status == null) return offer
        val wait = status.nextAdAt?.takeIf { it > now }?.let { it - now }
        val note = when {
            status.adsLeftToday <= 0 -> CoinsStrings.adDailyDone(status.dailyLimit)
            wait != null -> CoinsStrings.adNextIn(CoinsStrings.adWait(wait.inWholeHours, wait.inWholeMinutes % MINUTES_PER_HOUR), status.adsLeftToday)
            else -> CoinsStrings.adsLeftToday(status.adsLeftToday, status.dailyLimit)
        }
        return offer.copy(canWatch = status.canWatch(now), note = note, adsWatched = status.watchedToday, rewards = status.rewards)
    }

    /** The line shown after an ad: coins added, today's limit used, or no ad. */
    fun adMessage(result: AppResult<AdCoinsReward>): String = when (result) {
        is AppResult.Success -> CoinsStrings.adGranted(TextUtils.groupedThousands(result.data.granted))
        is AppResult.Failure -> if (result.error is YapNPlayError.LimitReached) CoinsStrings.adLimit else CoinsStrings.adUnavailable
    }

    private const val MINUTES_PER_HOUR = 60
}
