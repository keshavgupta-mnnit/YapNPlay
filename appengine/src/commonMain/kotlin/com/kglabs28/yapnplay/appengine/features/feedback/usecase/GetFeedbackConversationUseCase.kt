package com.kglabs28.yapnplay.appengine.features.feedback.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.feedback.domain.FeedbackConversation
import com.kglabs28.yapnplay.appengine.features.feedback.domain.FeedbackRepository

/** One conversation with every message (read-only in the app). */
class GetFeedbackConversationUseCase(private val repository: FeedbackRepository) {
    suspend operator fun invoke(ticketId: String): AppResult<FeedbackConversation> = repository.conversation(ticketId)
}
