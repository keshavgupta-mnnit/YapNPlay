package com.kglabs28.yapnplay.appengine.features.moderation.usecase

import com.kglabs28.yapnplay.appengine.features.moderation.domain.ModerationRepository
import com.kglabs28.yapnplay.appengine.features.moderation.domain.ModerationRules
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError

/**
 * Reports [reportedUserId] for voice chat. [voters] is everyone present
 * except the reporter and the reported person; too few and nobody could
 * confirm, so the report is refused.
 */
class ReportPlayerUseCase(private val repository: ModerationRepository) {
    suspend operator fun invoke(contextId: String, reportedUserId: String, reportedName: String, voters: Int): AppResult<Unit> =
        if (!ModerationRules.canReport(voters)) {
            AppResult.Failure(YapNPlayError.LimitReached(ModerationRules.MIN_VOTERS))
        } else {
            repository.report(contextId, reportedUserId, reportedName, voters)
        }
}
