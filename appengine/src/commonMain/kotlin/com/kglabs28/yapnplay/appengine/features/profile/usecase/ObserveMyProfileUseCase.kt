package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.features.profile.domain.UserProfile
import com.kglabs28.yapnplay.appengine.features.profile.repository.ProfileRepository
import kotlinx.coroutines.flow.StateFlow

/** The user as last loaded or edited, live (the Home top bar follows profile edits). */
class ObserveMyProfileUseCase(private val repository: ProfileRepository) {
    operator fun invoke(): StateFlow<UserProfile?> = repository.profile
}
