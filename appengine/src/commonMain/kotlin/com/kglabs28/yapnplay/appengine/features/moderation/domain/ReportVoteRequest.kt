package com.kglabs28.yapnplay.appengine.features.moderation.domain

import kotlinx.datetime.Instant

/** Someone was reported: did they do it? Answer before [expiresAt]. */
data class ReportVoteRequest(val reportId: String, val reporterName: String, val reportedName: String, val expiresAt: Instant)
