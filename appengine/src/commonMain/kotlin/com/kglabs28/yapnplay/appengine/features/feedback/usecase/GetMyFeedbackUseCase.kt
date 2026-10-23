package com.kglabs28.yapnplay.appengine.features.feedback.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.feedback.domain.FeedbackRepository
import com.kglabs28.yapnplay.appengine.features.feedback.domain.FeedbackTicket

/** The player's conversations with the team, latest activity first. */
class GetMyFeedbackUseCase(private val repository: FeedbackRepository) {
    suspend operator fun invoke(): AppResult<List<FeedbackTicket>> = repository.mine()
}
