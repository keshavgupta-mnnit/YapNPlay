package com.kglabs28.yapnplay.appengine.features.nearby.domain

/** Hosting a Nearby party: its room, or why the radios wouldn't start (nothing is left open). */
sealed interface NearbyHostResult {
    data class Hosted(val roomId: String) : NearbyHostResult

    /** [reason]: Nearby's own words (e.g. a missing permission), or null when unknown. */
    data class Failed(val reason: String?) : NearbyHostResult
}
