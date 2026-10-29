package com.kglabs28.yapnplay.appengine.features.notifications.domain

import com.kglabs28.yapnplay.appengine.protocol.PushKeys
import kotlinx.coroutines.flow.MutableSharedFlow
import kotlinx.coroutines.flow.SharedFlow
import kotlinx.coroutines.flow.asSharedFlow

/**
 * Tells open screens that a push just came in while the app is running,
 * so what it is about shows at once: a friend request or acceptance
 * ([friends]: Profile reloads), a Game Room invite ([invites]: Rooms reloads).
 */
class PushSignals {
    private val friendPushes = MutableSharedFlow<Unit>(extraBufferCapacity = BUFFER)
    private val invitePushes = MutableSharedFlow<Unit>(extraBufferCapacity = BUFFER)
    val friends: SharedFlow<Unit> = friendPushes.asSharedFlow()
    val invites: SharedFlow<Unit> = invitePushes.asSharedFlow()

    fun received(type: String?) {
        when (type) {
            PushKeys.TYPE_FRIEND_REQUEST, PushKeys.TYPE_FRIEND_ACCEPTED -> friendPushes.tryEmit(Unit)
            PushKeys.TYPE_ROOM_INVITE -> invitePushes.tryEmit(Unit)
        }
    }

    private companion object {
        const val BUFFER = 4
    }
}
