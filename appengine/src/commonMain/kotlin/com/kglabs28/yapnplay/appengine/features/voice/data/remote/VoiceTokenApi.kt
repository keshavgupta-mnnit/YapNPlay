package com.kglabs28.yapnplay.appengine.features.voice.data.remote

import com.kglabs28.yapnplay.appengine.core.network.BackendPaths
import com.kglabs28.yapnplay.appengine.protocol.VoiceToken
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post

/**
 * Voice passes from the backend (`POST …/voice-token`). A room's lobby
 * and the matches started from it share one voice room; a Quick Play
 * table has its own. The backend decides which.
 */
class VoiceTokenApi(private val client: HttpClient) {
    suspend fun token(target: VoiceTarget): VoiceToken = when (target) {
        is VoiceTarget.Room -> client.post(BackendPaths.roomVoiceToken(target.roomId)).body()
        is VoiceTarget.Match -> client.post(BackendPaths.matchVoiceToken(target.matchId)).body()
    }
}
