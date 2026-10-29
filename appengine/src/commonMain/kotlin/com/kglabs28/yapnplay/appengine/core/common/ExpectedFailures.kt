package com.kglabs28.yapnplay.appengine.core.common

import com.kglabs28.yapnplay.appengine.core.network.RealtimeRejectedException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.TimeoutCancellationException

/**
 * Failures that are part of normal use, not bugs: the server's own answer
 * ("This match is over"), something already gone (404), a reply that took
 * too long, or no network. Crash reporting skips them so real errors stand out.
 */
object ExpectedFailures {
    fun isExpected(error: Throwable, isNetworkFailure: (Throwable) -> Boolean): Boolean =
        generateSequence(error) { it.cause.takeIf { cause -> cause !== it } }.take(MAX_CAUSES).any { e ->
            e is RealtimeRejectedException ||
                e is TimeoutCancellationException ||
                (e is ClientRequestException && e.response.status == HttpStatusCode.NotFound) ||
                isNetworkFailure(e)
        }

    private const val MAX_CAUSES = 8
}
