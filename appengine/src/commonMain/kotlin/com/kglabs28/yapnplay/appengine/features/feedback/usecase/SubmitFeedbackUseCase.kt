package com.kglabs28.yapnplay.appengine.features.feedback.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.features.feedback.domain.FeedbackKind
import com.kglabs28.yapnplay.appengine.features.feedback.domain.FeedbackRepository
import com.kglabs28.yapnplay.appengine.features.feedback.domain.FeedbackTicket

/** Sends a new message to the team; empty or too long ([AppConstants.FEEDBACK_MAX_LENGTH]) never leaves the phone. */
class SubmitFeedbackUseCase(private val repository: FeedbackRepository) {
    suspend operator fun invoke(kind: FeedbackKind, message: String): AppResult<FeedbackTicket> {
        val text = message.trim()
        if (text.isEmpty() || text.length > AppConstants.FEEDBACK_MAX_LENGTH) return AppResult.Failure(YapNPlayError.Validation(FIELD_MESSAGE))
        return repository.submit(kind, text)
    }

    companion object {
        const val FIELD_MESSAGE = "message"
    }
}
