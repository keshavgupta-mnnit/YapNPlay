package com.kglabs28.yapnplay.appengine.core.ads

/**
 * The free-coins offer shows once each time the app opens (like other
 * games), not every time Home comes back. One instance for the app's life.
 */
class LaunchCoinsOffer(private val store: LaunchOfferStore) {
    private var shown = false

    /**
     * True when the greyed-out "no ads available" card may show at launch:
     * at most once on [today] (a device whose ads are blocked would see it every launch).
     */
    fun takeNoAdNoticeIfDue(today: String): Boolean {
        if (store.noAdDay() == today) return false
        store.saveNoAdDay(today)
        return true
    }

    /** True the first time it's asked in this app session, then never again. */
    fun takeIfDue(): Boolean {
        if (shown) return false
        shown = true
        return true
    }
}
