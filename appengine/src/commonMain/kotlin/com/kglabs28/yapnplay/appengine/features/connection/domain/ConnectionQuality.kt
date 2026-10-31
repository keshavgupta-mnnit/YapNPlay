package com.kglabs28.yapnplay.appengine.features.connection.domain

/** How well this device reaches the backend, from its last round trip ([pingMs]; null when it couldn't). */
data class ConnectionQuality(val level: Level, val pingMs: Long?) {
    enum class Level { GOOD, FAIR, POOR, OFFLINE }

    companion object {
        /** Under 150 ms plays smoothly; under 400 ms lags a little; slower (or none) is poor. */
        fun of(pingMs: Long?): ConnectionQuality = ConnectionQuality(
            level = when {
                pingMs == null -> Level.OFFLINE
                pingMs < GOOD_MS -> Level.GOOD
                pingMs < FAIR_MS -> Level.FAIR
                else -> Level.POOR
            },
            pingMs = pingMs,
        )

        private const val GOOD_MS = 150L
        private const val FAIR_MS = 400L
    }
}
