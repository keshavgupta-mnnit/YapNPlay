package com.kglabs28.yapnplay.appengine.features.voice.data.remote

import kotlinx.coroutines.flow.StateFlow

/**
 * The voice connection as far as this device is concerned. Muting
 * someone here only stops *this* device playing their audio — they stay
 * audible to everyone else.
 */
interface VoiceClient {
    /** User ids this device has muted for itself. */
    val locallyMuted: StateFlow<Set<String>>

    fun setLocallyMuted(userId: String, muted: Boolean)

    /** The user's mic switch: the same in every room and game until they change it. */
    val micOn: StateFlow<Boolean>

    fun setMicOn(on: Boolean)

    /** True while the user is speaking into their open mic. */
    val mySpeaking: StateFlow<Boolean>

    /** Opens or closes the user's mic (today: just listens to its level). */
    fun setMicOpen(open: Boolean)
}
