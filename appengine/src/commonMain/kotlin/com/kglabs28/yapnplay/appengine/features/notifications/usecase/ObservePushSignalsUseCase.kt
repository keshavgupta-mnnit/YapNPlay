package com.kglabs28.yapnplay.appengine.features.notifications.usecase

import com.kglabs28.yapnplay.appengine.features.notifications.domain.PushSignals
import kotlinx.coroutines.flow.Flow

/** A friend push (request or acceptance), or a Game Room invite push, just arrived. */
class ObservePushSignalsUseCase(private val signals: PushSignals) {
    fun friends(): Flow<Unit> = signals.friends
    fun invites(): Flow<Unit> = signals.invites
}
