package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.core.auth.AccountEntry
import com.kglabs28.yapnplay.appengine.core.network.BackendSession
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.flowOf
import kotlinx.coroutines.flow.map

/**
 * True while the login screen must show instead of the app: only on the web
 * ([AccountEntry.GOOGLE]) with no account saved in this browser. The app
 * (guest + Play Games) never shows it.
 */
class ObserveSignInRequiredUseCase(
    private val session: BackendSession,
    private val entry: AccountEntry,
) {
    operator fun invoke(): Flow<Boolean> =
        if (entry == AccountEntry.GOOGLE) session.savedAccount.map { it == null } else flowOf(false)

    /** The answer right now, so the first frame never flashes the wrong screen. */
    fun now(): Boolean = entry == AccountEntry.GOOGLE && session.savedAccount.value == null
}
