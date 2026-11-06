package com.kglabs28.yapnplay.appengine.features.profile.usecase

import com.kglabs28.yapnplay.appengine.core.auth.GoogleSignInLauncher
import com.kglabs28.yapnplay.appengine.core.auth.PlayGamesPlayer
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppLog
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.core.network.BackendSession

/**
 * The web's Google sign-in. [start] leaves for Google's page; when the page
 * loads again with Google's token, [finishReturned] signs in with it (a new
 * account suggests the Google first name on the welcome). Success carries
 * false when no token came back (nothing to do).
 */
class SignInWithGoogleUseCase(
    private val launcher: GoogleSignInLauncher,
    private val session: BackendSession,
    private val player: PlayGamesPlayer,
) {
    fun start() = launcher.start()

    suspend fun finishReturned(): AppResult<Boolean> {
        val idToken = launcher.takeReturnedIdToken() ?: return AppResult.Success(false)
        return try {
            launcher.returnedFirstName()?.let(player::remember)
            session.signInWithGoogle(idToken)
            AppResult.Success(true)
        } catch (e: Exception) {
            AppLog.d(AppConstants.LOG_TAG_AUTH, "server: Google sign-in failed ${e::class.simpleName}: ${e.message}")
            CrashReporter.record(e, "SignInWithGoogleUseCase")
            AppResult.Failure(YapNPlayError.Network(e.message ?: FAILED))
        }
    }

    private companion object {
        const val FAILED = "google"
    }
}
