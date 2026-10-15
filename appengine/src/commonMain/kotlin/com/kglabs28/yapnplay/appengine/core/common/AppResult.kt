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
    data class Unknown(val cause: String) : YapNPlayError
}
