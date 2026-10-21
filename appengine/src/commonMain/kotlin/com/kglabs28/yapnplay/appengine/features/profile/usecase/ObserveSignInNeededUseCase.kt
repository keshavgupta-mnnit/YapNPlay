package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.core.network.BackendSession
import kotlinx.coroutines.flow.Flow

/** Fires when the server refuses the saved account (401), so Play Games sign-in runs again. */
class ObserveSignInNeededUseCase(private val session: BackendSession) {
    operator fun invoke(): Flow<Unit> = session.signInNeeded
}
