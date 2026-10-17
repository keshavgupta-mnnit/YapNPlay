package com.kglabs28.yapnplay.appengine.features.moderation.usecase

import com.kglabs28.yapnplay.appengine.features.moderation.domain.ModerationRepository
import com.kglabs28.yapnplay.appengine.core.common.AppResult

/** The user's answer to "did they do it?". */
class CastReportVoteUseCase(private val repository: ModerationRepository) {
    suspend operator fun invoke(reportId: String, agree: Boolean): AppResult<Unit> = repository.vote(reportId, agree)
}
