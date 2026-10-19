package com.kglabs28.yapnplay.appengine.ui.screens.mono

import com.kglabs28.yapnplay.appengine.core.ui.Strings
import kotlinx.datetime.Instant

/**
 * "12:05" of match time for the table's clock chip. Both MONO games
 * show the same clock, so it is formatted once, here.
 */
fun monoElapsed(since: Instant, now: Instant): String {
    val total = (now - since).inWholeSeconds.coerceAtLeast(0)
    return Strings.ludoClock(total / SECONDS_PER_MINUTE, total % SECONDS_PER_MINUTE)
}

private const val SECONDS_PER_MINUTE = 60L