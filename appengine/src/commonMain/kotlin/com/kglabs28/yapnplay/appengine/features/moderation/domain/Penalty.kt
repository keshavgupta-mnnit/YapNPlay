package com.kglabs28.yapnplay.appengine.features.moderation.domain

import kotlinx.datetime.Instant

/** An incident against the user: [points] taken away and play blocked until [blockedUntil]. */
data class Penalty(val points: Int, val blockedUntil: Instant)
