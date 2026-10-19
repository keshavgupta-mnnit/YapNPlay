package com.kglabs28.yapnplay.appengine.fake

import com.kglabs28.yapnplay.appengine.core.network.BackendCredentials
import com.kglabs28.yapnplay.appengine.core.network.SessionProvider

/**
 * The guest account local mode uses: fixed, so the profile and every
 * screen keyed off the user id behave the same on each run without
 * asking a server for anything. It replaces the `POST /auth/guest` call
 * [SessionProvider.credentials] would otherwise make, so no HTTP client
 * and nothing written to disk.
 */
class LocalBackendSession : SessionProvider {

    override suspend fun credentials(): BackendCredentials = CREDENTIALS

    override fun currentUserId(): String? = USER_ID

    companion object {
        const val USER_ID: String = "local-player"
        const val SECRET: String = "local-secret"
        const val DISPLAY_NAME: String = "Local Player"
        const val USER_CODE: String = "LOCL01"
        const val AVATAR_ID: String = "a01"
        const val XP: Int = 1_280
        const val LEVEL: Int = 7
        const val GAMES_PLAYED: Int = 24
        const val GAMES_WON: Int = 9

        val CREDENTIALS = BackendCredentials(USER_ID, SECRET)
    }
}