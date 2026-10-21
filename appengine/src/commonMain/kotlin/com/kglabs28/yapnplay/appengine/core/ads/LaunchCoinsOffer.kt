package com.kglabs28.yapnplay.appengine.core.ads

/**
 * The free-coins offer shows once each time the app opens (like other
 * games), not every time Home comes back. One instance for the app's life.
 */
class LaunchCoinsOffer {
    private var shown = false

    /** True the first time it's asked in this app session, then never again. */
    fun takeIfDue(): Boolean {
        if (shown) return false
        shown = true
        return true
    }
}
