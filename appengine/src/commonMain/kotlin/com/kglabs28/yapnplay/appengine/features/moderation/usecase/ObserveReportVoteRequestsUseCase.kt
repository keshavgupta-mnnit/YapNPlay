package com.kglabs28.yapnplay.appengine.features.moderation.usecase

import com.kglabs28.yapnplay.appengine.features.moderation.domain.ModerationRepository
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ReportVoteRequest
import kotlinx.coroutines.flow.Flow

/** Reports by others that the user is asked to confirm. */
class ObserveReportVoteRequestsUseCase(private val repository: ModerationRepository) {
    operator fun invoke(): Flow<ReportVoteRequest> = repository.voteRequests
}
