package com.kglabs28.yapnplay.appengine.core.common

import com.kglabs28.yapnplay.appengine.core.network.RealtimeRejectedException
import io.ktor.client.plugins.ClientRequestException
import io.ktor.http.HttpStatusCode
import kotlinx.coroutines.CancellationException

/**
 * Failures that are part of normal use, not bugs: the server's own answer
 * ("This match is over"), something already gone (404) or a cancelled job.
 * Crash reporting skips them so real errors stand out. No network and
 * timeouts are still reported (how often players go offline matters);
 * only ads keep their failures out of crash reports (logged instead).
 */
object ExpectedFailures {
    fun isExpected(error: Throwable): Boolean =
        generateSequence(error) { it.cause.takeIf { cause -> cause !== it } }.take(MAX_CAUSES).any { e ->
            e is RealtimeRejectedException ||
                // A screen closed or a job was replaced: cancelling is how coroutines stop, never a bug.
                e is CancellationException ||
                (e is ClientRequestException && e.response.status == HttpStatusCode.NotFound)
        }

    private const val MAX_CAUSES = 8
}
