package com.kglabs28.yapnplay.appengine.features.voice.data.remote

import com.kglabs28.yapnplay.appengine.core.network.BackendPaths
import com.kglabs28.yapnplay.appengine.protocol.VoiceToken
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post

/**
 * Voice passes from the backend (`POST …/voice-token`). A room's lobby
 * has one voice room and every match its own (players and spectators
 * only). The backend decides which.
 */
class VoiceTokenApi(private val client: HttpClient) {
    suspend fun token(target: VoiceTarget): VoiceToken = when {
        // A Nearby party lives on the host's phone, not the server: its own kind of pass.
        target.id.startsWith(NEARBY_PREFIX) -> client.post(BackendPaths.nearbyVoiceToken(target.id)).body()
        target is VoiceTarget.Room -> client.post(BackendPaths.roomVoiceToken(target.roomId)).body()
        target is VoiceTarget.Match -> client.post(BackendPaths.matchVoiceToken(target.matchId)).body()
        else -> error("Unknown voice target")
    }

    private val VoiceTarget.id: String get() = when (this) {
        is VoiceTarget.Room -> roomId
        is VoiceTarget.Match -> matchId
    }

    private companion object {
        /** Nearby party rooms and tables ("nearby-room-…", "nearby-match-…"). */
        const val NEARBY_PREFIX = "nearby-"
    }
}
