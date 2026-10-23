package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.core.network.BackendSession

/** The player continues the phone's account ([continueIt]) or starts a new one. */
class AnswerDeviceAccountOfferUseCase(private val session: BackendSession) {
    operator fun invoke(continueIt: Boolean) = session.answerDeviceAccount(continueIt)
}
