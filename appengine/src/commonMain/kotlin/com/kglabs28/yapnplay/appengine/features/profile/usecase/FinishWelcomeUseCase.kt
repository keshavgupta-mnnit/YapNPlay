package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.core.network.BackendSession

/** The welcome was answered (saved or skipped): it never shows again for this account. */
class FinishWelcomeUseCase(private val session: BackendSession) {
    operator fun invoke() = session.finishWelcome()
}
