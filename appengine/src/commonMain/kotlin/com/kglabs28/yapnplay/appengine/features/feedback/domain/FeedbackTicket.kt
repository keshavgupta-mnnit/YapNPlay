package com.kglabs28.yapnplay.appengine.features.feedback.domain

/** One conversation with the team: what it's about, its first message, and whether the team has replied. */
data class FeedbackTicket(
    val id: String,
    val kind: FeedbackKind,
    val answered: Boolean,
    val preview: String,
    val createdAt: Long,
    val updatedAt: Long,
)
