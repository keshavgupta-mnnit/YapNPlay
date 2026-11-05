package com.kglabs28.yapnplay.appengine.features.nearby.repository

import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyHostResult
import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyRadio
import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyState
import kotlinx.coroutines.flow.StateFlow

/**
 * Nearby play: host a Quick Play party on this phone and invite phones
 * around, or be findable so someone around can invite this phone. While
 * in a party every lobby and table talks to the hosting phone instead of
 * the backend, so it works with no internet.
 */
interface NearbyRepository {
    val state: StateFlow<NearbyState>

    /** False on platforms with no Nearby radios. */
    val supported: Boolean

    /** Lets phones around find this one (to invite it); nothing while in a party. */
    suspend fun becomeFindable()

    /** Stops being findable (the app went to the background). */
    fun stopBeingFindable()

    /** Each switch Nearby play needs on this phone, and whether it is on now. */
    fun radios(): Map<NearbyRadio, Boolean>

    fun openRadioSettings(radio: NearbyRadio)

    /**
     * Hosts a party here and starts looking for phones to invite: its room, or
     * why Nearby could not start (nothing is left half open).
     */
    suspend fun host(): NearbyHostResult

    /** Host: invites the phone [endpointId] from the peers list. */
    suspend fun invite(endpointId: String)

    /** Accepts the waiting invite: this phone joins that party. */
    suspend fun acceptInvite()

    /** Turns the waiting invite down. */
    suspend fun declineInvite()

    /** True when [roomId] is the Nearby party this phone is in. */
    fun isNearbyRoom(roomId: String): Boolean

    /** Clears the last problem shown. */
    fun clearProblem()

    /** Leaves the party (a host ends it for everyone) and goes back online. */
    suspend fun leave()

    /** [leave], finished in the background (the screen asking is already gone). */
    fun leaveInBackground()
}
