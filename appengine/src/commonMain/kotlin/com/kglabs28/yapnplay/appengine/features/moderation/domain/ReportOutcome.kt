package com.kglabs28.yapnplay.appengine.features.moderation.domain

/**
 * How the user's own report ended: [upheld] means enough people agreed and
 * the incident is recorded against [reportedName]; otherwise it is
 * recorded against the user. The counts cover everyone asked.
 */
data class ReportOutcome(
    val upheld: Boolean,
    val reportedName: String,
    val agreed: Int,
    val disagreed: Int,
    val noResponse: Int,
    val required: Int,
)
