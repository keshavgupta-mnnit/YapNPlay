package com.kglabs28.yapnplay.appengine.core.ads

/** Remembers the day ("2026-10-29") the "no ads available" card last showed at launch. */
interface LaunchOfferStore {
    fun noAdDay(): String?
    fun saveNoAdDay(day: String)
}

/** Desktop / previews: kept only while the process runs. */
class InMemoryLaunchOfferStore : LaunchOfferStore {
    private var day: String? = null
    override fun noAdDay(): String? = day
    override fun saveNoAdDay(day: String) {
        this.day = day
    }
}
