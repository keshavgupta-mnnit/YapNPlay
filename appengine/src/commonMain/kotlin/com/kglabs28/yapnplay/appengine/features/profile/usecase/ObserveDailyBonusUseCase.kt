package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository
import kotlinx.coroutines.flow.StateFlow

/** Today's daily bonus waiting to be collected (0 when none). */
class ObserveDailyBonusUseCase(private val repository: ProfileRepository) {
    operator fun invoke(): StateFlow<Int> = repository.pendingDailyBonus
}
