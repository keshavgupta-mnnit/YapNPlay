package com.kglabs28.yapnplay.appengine.features.feedback.domain

import com.kglabs28.yapnplay.appengine.core.common.AppResult

/** The player's feedback and support conversations with the team. */
interface FeedbackRepository {
    suspend fun submit(kind: FeedbackKind, message: String): AppResult<FeedbackTicket>

    /** Latest activity first. */
    suspend fun mine(): AppResult<List<FeedbackTicket>>

    suspend fun conversation(ticketId: String): AppResult<FeedbackConversation>
}
