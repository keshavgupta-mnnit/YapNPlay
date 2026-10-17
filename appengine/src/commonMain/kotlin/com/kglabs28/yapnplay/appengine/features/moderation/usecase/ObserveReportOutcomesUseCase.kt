package com.kglabs28.yapnplay.appengine.features.moderation.usecase

import com.kglabs28.yapnplay.appengine.features.moderation.domain.ModerationRepository
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ReportOutcome
import kotlinx.coroutines.flow.Flow

/** How the user's own reports ended. */
class ObserveReportOutcomesUseCase(private val repository: ModerationRepository) {
    operator fun invoke(): Flow<ReportOutcome> = repository.outcomes
}
