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
)
