package com.kglabs28.yapnplay.appengine.core.common

/**
 * Typed result for repository and use-case calls. Errors are typed,
 * never strings; nothing fails silently.
 */
sealed interface AppResult<out T> {
    data class Success<T>(val data: T) : AppResult<T>
    data class Failure(val error: YapNPlayError) : AppResult<Nothing>
}

/** All error kinds the app can show or log. Logs never contain secrets. */
sealed interface YapNPlayError {
    data class Network(val cause: String) : YapNPlayError
    data class Validation(val field: String) : YapNPlayError
    data class NotFound(val what: String) : YapNPlayError
    data class Unauthorized(val reason: String) : YapNPlayError
    /** A count limit was hit (e.g. max rooms per user); [limit] is the cap. */
    data class LimitReached(val limit: Int) : YapNPlayError
    /** Something like it is already there ([name]: e.g. the existing group's name). */
    data class AlreadyExists(val name: String) : YapNPlayError
    data class Unknown(val cause: String) : YapNPlayError
}
