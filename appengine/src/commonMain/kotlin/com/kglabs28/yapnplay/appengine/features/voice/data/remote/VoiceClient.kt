package com.kglabs28.yapnplay.appengine.features.voice.data.remote

import com.kglabs28.yapnplay.appengine.features.voice.domain.MySpeech
import kotlinx.coroutines.flow.Flow
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

    /** The user's speaker: off, this device plays nobody (everyone else still hears everyone). */
    val speakerOn: StateFlow<Boolean>

    fun setSpeakerOn(on: Boolean)

    /** The user's mic switch: the same in every room and game until they change it. */
    val micOn: StateFlow<Boolean>

    fun setMicOn(on: Boolean)

    /** The lobby or table on screen now (the newest one talking), or null. */
    val activeTarget: Flow<VoiceTarget?>

    /** Whether the user is speaking, into their open mic or with it off. */
    val mySpeech: StateFlow<MySpeech>

    /**
     * Opens (publishes to the voice room) or closes the user's mic; closed with
     * [listenMuted], only its level is read, to tell them they're talking on mute.
     */
    fun setMicOpen(open: Boolean, listenMuted: Boolean)

    /**
     * Whether [target]'s screen needs its voice room now (someone to talk with, a mic on); the device joins only then.
     * [talkers]: the others there with their mic on; one this device keeps not receiving makes it rejoin.
     */
    fun setVoiceWanted(target: VoiceTarget, wanted: Boolean, talkers: Set<String>)

    /**
     * While collected (and wanted, [setVoiceWanted]), this device is in [target]'s voice room and the
     * flow gives who else is speaking there. When the screen stops
     * collecting, the device leaves (back to the screen underneath, if any).
     */
    fun speakers(target: VoiceTarget): Flow<Set<String>>
}
