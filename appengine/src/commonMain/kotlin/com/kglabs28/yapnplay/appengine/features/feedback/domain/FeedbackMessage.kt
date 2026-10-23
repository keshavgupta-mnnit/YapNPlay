package com.kglabs28.yapnplay.appengine.features.feedback.domain

/** One message: the player's, or the team's reply. */
data class FeedbackMessage(
    val fromTeam: Boolean,
    val body: String,
    val createdAt: Long,
)
