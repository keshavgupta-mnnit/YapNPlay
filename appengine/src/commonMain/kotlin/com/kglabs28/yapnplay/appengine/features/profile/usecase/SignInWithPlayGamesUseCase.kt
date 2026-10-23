package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppLog
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.core.network.BackendSession

/**
 * Google Play Games sign-in: links the user's account to their Play Games
 * player, or brings back the account linked before (new phone, reinstall).
 * Success carries true when the account changed.
 */
class SignInWithPlayGamesUseCase(private val session: BackendSession) {
    suspend operator fun invoke(serverAuthCode: String): AppResult<Boolean> = try {
        AppResult.Success(session.signInWithPlayGames(serverAuthCode))
    } catch (e: Exception) {
        AppLog.d(AppConstants.LOG_TAG_AUTH, "server: sign-in failed ${e::class.simpleName}: ${e.message}")
        CrashReporter.record(e, "SignInWithPlayGamesUseCase")
        AppResult.Failure(YapNPlayError.Network(e.message ?: FAILED))
    }

    private companion object {
        const val FAILED = "playGames"
    }
}
