package com.kglabs28.yapnplay.appengine.features.playroomlobby.domain

import kotlinx.datetime.Instant

/**
 * A start countdown that stopped without a match: why, and who stopped it
 * ([byUserId] / [byName] null when nobody did: the server could not start
 * the match). [at] is already on this device's clock.
 */
data class CountdownStopInfo(
    val cause: CountdownStopCause,
    val byUserId: String?,
    val byName: String?,
    val at: Instant,
)

enum class CountdownStopCause { CANCELLED, NOT_READY, LEFT, GAME_CHANGED, SETTINGS_CHANGED, START_FAILED, UPDATE_NEEDED }
