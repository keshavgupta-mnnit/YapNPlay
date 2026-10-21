package com.kglabs28.yapnplay.appengine.core.ui

/** Coins: Quick Play leagues, rewarded-ad coins, not enough coins. */
object CoinsStrings {
    const val leaguesTitle = "CHOOSE A LEAGUE"
    fun leaguesBalance(coins: String) = "You have $coins coins. Winners share the pot."
    const val leagueFree = "FREE"
    fun leagueFee(coins: String) = coins
    const val leaguesLoading = "Loading leagues…"
    const val notEnoughTitle = "NOT ENOUGH COINS"
    fun notEnoughBody(league: String, fee: String) = "$league costs $fee coins to enter. Watch a short ad for free coins, or play Starter for free."
    const val notEnoughBodyGeneric = "You need more coins for this league. Watch a short ad for free coins, or play Starter for free."
    const val getCoinsTitle = "GET FREE COINS"
    const val getCoinsBody = "Watch a short video and get free coins to play bigger leagues."
    const val watchAd = "WATCH AD"
    const val watchingAd = "LOADING AD…"
    const val playFree = "PLAY STARTER"
    const val close = "CLOSE"
    fun adGranted(coins: String) = "+$coins coins added!"
    /** Refused by the server (the 2-hour gap or the daily limit); the note under it says which and when. */
    const val adLimit = "No free coins right now."
    const val adUnavailable = "No ad right now. Try again in a moment."
    fun adsLeftToday(left: Int, limit: Int) = "$left of $limit free-coin ads left today"
    fun adNextIn(wait: String, left: Int) = "Next free coins in $wait · $left left today"
    fun adDailyDone(limit: Int) = "You've used all $limit free-coin ads today. Come back tomorrow!"
    fun adWait(hours: Long, minutes: Long) = if (hours > 0) "${hours}h ${minutes}m" else "${minutes}m"
}
