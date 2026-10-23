package com.kglabs28.yapnplay.appengine.features.feedback.domain

/** A conversation with its messages, oldest first. */
data class FeedbackConversation(
    val ticket: FeedbackTicket,
    val messages: List<FeedbackMessage>,
)
