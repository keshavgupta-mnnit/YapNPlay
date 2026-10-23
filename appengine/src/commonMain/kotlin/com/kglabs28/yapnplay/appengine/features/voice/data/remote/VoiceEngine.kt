package com.kglabs28.yapnplay.appengine.features.voice.data.remote

import kotlinx.coroutines.flow.StateFlow

/**
 * The voice provider on this device (LiveKit on Android, silent on
 * desktop): one voice room at a time.
 */
interface VoiceEngine {
    /** True while in a voice room. Goes false if the provider drops us for good. */
    val connected: StateFlow<Boolean>

    /** Identities (user ids) of the other people speaking right now. */
    val remoteSpeakers: StateFlow<Set<String>>

    /** True while this device's user is speaking into their published mic. */
    val localSpeaking: StateFlow<Boolean>

    /** Leaves any current voice room and joins the one [token] grants. Throws if it can't. */
    suspend fun connect(url: String, token: String)

    suspend fun disconnect()

    /** Publishes (true) or stops publishing (false) the mic; remembered across rooms. */
    fun setMicEnabled(enabled: Boolean)

    /** People whose audio this device doesn't play; remembered across rooms. */
    fun setMuted(userIds: Set<String>)

    /** Plays everyone's audio (true) or nobody's (false) on this device; remembered across rooms. */
    fun setSpeakerOn(on: Boolean)
}
