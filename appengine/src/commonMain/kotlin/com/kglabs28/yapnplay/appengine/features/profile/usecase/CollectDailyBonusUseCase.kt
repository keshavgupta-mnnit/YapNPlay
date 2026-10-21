package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository

/** The user tapped COLLECT: the bonus dialog is done (the server already added the coins). */
class CollectDailyBonusUseCase(private val repository: ProfileRepository) {
    operator fun invoke() = repository.collectDailyBonus()
}
