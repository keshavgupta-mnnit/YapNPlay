package com.kglabs28.yapnplay.appengine.ui.components.coins

/**
 * The "watch an ad for coins" dialog, ready to show: [offerFree] adds
 * PLAY STARTER (Game Zone, short of a league's fee); [message] is the
 * result of the last ad (coins added, limit reached, no ad).
 */
data class CoinsOfferUi(
    val title: String,
    val body: String,
    val watchingAd: Boolean = false,
    val offerFree: Boolean = false,
    val message: String? = null,
    /** False while a cooldown runs or today's ads are used up (the button greys out). */
    val canWatch: Boolean = true,
    /** "4 of 5 free-coin ads left today" / "Next free coins in 1h 23m" / daily limit reached. */
    val note: String? = null,
    /** Ads watched today, out of [rewards].size (the progress bar). */
    val adsWatched: Int = 0,
    /** What each of today's ads pays, in order; empty hides the progress and ladder. */
    val rewards: List<Int> = emptyList(),
    /** An ad can play now; false greys out Watch Ad and the ad progress, with a note on why (DNS or ad blockers). */
    val adReady: Boolean = true,
    /** Today's ad limits have been asked for (in or not): until then a spinner shows, so the card appears whole. */
    val limitsLoaded: Boolean = false,
) {
    /** The coins the next ad pays; null when today's ads are used up. */
    val nextReward: Int? get() = rewards.getOrNull(adsWatched)
}
