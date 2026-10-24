package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.core.network.BackendSession
import kotlinx.coroutines.flow.StateFlow

/** True while a brand-new account still has the welcome (name and avatar) to see. */
class ObserveWelcomeNeededUseCase(private val session: BackendSession) {
    operator fun invoke(): StateFlow<Boolean> = session.welcomeNeeded
}
