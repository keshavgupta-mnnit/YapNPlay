package com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby

import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.CountdownStopCause
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.CountdownStopInfo

/** Why a start countdown stopped, in words, naming who stopped it ("You" for the user). */
object CountdownStopText {
    fun of(stop: CountdownStopInfo, myUserId: String): String {
        val name = stop.byName?.takeIf { it.isNotBlank() } ?: RoomStrings.lobbySomeone
        return when (stop.cause) {
            CountdownStopCause.CANCELLED -> if (stop.byUserId == myUserId) RoomStrings.lobbyStopCancelledYou else RoomStrings.lobbyStopCancelled(name)
            CountdownStopCause.NOT_READY -> RoomStrings.lobbyStopNotReady(name)
            CountdownStopCause.LEFT -> RoomStrings.lobbyStopLeft(name)
            CountdownStopCause.GAME_CHANGED -> RoomStrings.lobbyStopGameChanged(name)
            CountdownStopCause.SETTINGS_CHANGED -> RoomStrings.lobbyStopSettingsChanged(name)
            CountdownStopCause.START_FAILED -> RoomStrings.lobbyStopFailed
        }
    }
}
