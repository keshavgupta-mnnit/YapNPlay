package com.kglabs28.yapnplay.appengine.core.network

/**
 * This device's guest account: the server-made [userId] and the [secret]
 * it handed out once. Sent as `Authorization: Bearer <userId>.<secret>`.
 */
data class BackendCredentials(
    val userId: String,
    val secret: String,
) {
    val bearerToken: String get() = userId + TOKEN_SEPARATOR + secret

    override fun toString(): String = "BackendCredentials(userId=$userId, secret=***)"

    private companion object {
        const val TOKEN_SEPARATOR = "."
    }
}
