package com.kglabs28.yapnplay.appengine.core.auth

import com.google.android.gms.common.api.ApiException
import com.google.android.gms.games.GamesSignInClient
import kotlin.time.Duration.Companion.seconds
import kotlinx.coroutines.withTimeoutOrNull
import com.kglabs28.yapnplay.appengine.core.common.AppLog
import android.app.Activity
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import com.google.android.gms.games.PlayGames
import com.google.android.gms.games.PlayGamesSdk
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import kotlinx.coroutines.tasks.await
import kotlin.system.exitProcess

@Composable
actual fun rememberPlayGamesAuth(): PlayGamesAuth {
    val context = LocalContext.current
    return remember(context) { AndroidPlayGamesAuth(context) }
}

/**
 * Play Games v2: the player is signed in by Play itself at launch (no
 * button, like Clash of Clans); we only ask for a server code so the
 * backend can link or restore the account.
 */
private class AndroidPlayGamesAuth(private val context: Context) : PlayGamesAuth {

    override suspend fun serverAuthCode(): String? {
        AppLog.d(AppConstants.LOG_TAG_AUTH, "play games: checking sign-in")
        val activity = context.findActivity() ?: return null.also { AppLog.d(AppConstants.LOG_TAG_AUTH, "play games: no activity") }
        return try {
            PlayGamesSdk.initialize(context.applicationContext)
            val client = PlayGames.getGamesSignInClient(activity)
            // Play's answers can hang (no Play Games, a stuck sign-in): give up rather than wait forever.
            val silent = withTimeoutOrNull(PLAY_TIMEOUT) { client.isAuthenticated.await().isAuthenticated }
            AppLog.d(AppConstants.LOG_TAG_AUTH, "play games: signed in on this phone = ${silent ?: "no answer (timed out)"}")
            // Not signed in silently (the prompt was dismissed once, or never shown): ask Play to sign in,
            // which shows its own prompt. Its answer says why when it can't (a setup error, cancelled…).
            val signedIn = silent == true || signInWithPrompt(client)
            if (!signedIn) {
                explainRefusal(activity)
                return null
            }
            withTimeoutOrNull(PLAY_TIMEOUT) { client.requestServerSideAccess(AppConstants.PLAY_GAMES_WEB_CLIENT_ID, false).await() }
                .also { AppLog.d(AppConstants.LOG_TAG_AUTH, "play games: server code ${if (it.isNullOrEmpty()) "missing (refused or timed out)" else "received"}") }
        } catch (e: Exception) {
            AppLog.d(AppConstants.LOG_TAG_AUTH, "play games: failed ${e::class.simpleName}: ${e.message}")
            CrashReporter.record(e, "PlayGamesAuth.serverAuthCode")
            null
        }
    }

    /** Play's own sign-in prompt; false when the player cancels or Play refuses (the reason is logged). */
    private suspend fun signInWithPrompt(client: GamesSignInClient): Boolean = try {
        val result = withTimeoutOrNull(PROMPT_TIMEOUT) { client.signIn().await().isAuthenticated }
        AppLog.d(AppConstants.LOG_TAG_AUTH, "play games: sign-in prompt result = ${result ?: "no answer (timed out)"}")
        result == true
    } catch (e: Exception) {
        // An ApiException's status code names the cause: 4 sign-in required, 7 network, 8 internal,
        // 10 developer error (this build's SHA-1 or package not in the Play Games credentials), 13/16 cancelled.
        val code = (e as? ApiException)?.statusCode
        AppLog.d(AppConstants.LOG_TAG_AUTH, "play games: sign-in prompt failed code=$code ${e::class.simpleName}: ${e.message}")
        CrashReporter.record(e, "PlayGamesAuth.signIn")
        false
    }

    /**
     * Play refused without saying why: asking for the current player makes it
     * fail with Play's own status (4 sign-in required, 10 developer error:
     * this build's SHA-1/package/OAuth client, 17 API not connected…), logged
     * for debugging. Nothing else depends on it.
     */
    private suspend fun explainRefusal(activity: Activity) {
        try {
            val player = withTimeoutOrNull(PLAY_TIMEOUT) { PlayGames.getPlayersClient(activity).currentPlayer.await() }
            AppLog.d(AppConstants.LOG_TAG_AUTH, "play games: current player = ${player?.playerId?.take(PLAYER_ID_SHOWN) ?: "none"}")
        } catch (e: Exception) {
            val api = e as? ApiException ?: e.cause as? ApiException
            AppLog.d(AppConstants.LOG_TAG_AUTH, "play games: refused, status=${api?.statusCode} ${api?.status?.statusMessage ?: ""} ${e::class.simpleName}: ${e.message}")
        }
    }

    override fun reloadApp() {
        val launch = context.packageManager.getLaunchIntentForPackage(context.packageName) ?: return
        launch.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK)
        context.startActivity(launch)
        exitProcess(0)
    }

    private fun Context.findActivity(): Activity? {
        var current: Context? = this
        while (current is ContextWrapper) {
            if (current is Activity) return current
            current = current.baseContext
        }
        return null
    }
}

/** How long to wait for each answer from Play Games; the sign-in prompt waits on the player too. */
private val PLAY_TIMEOUT = 15.seconds
private val PROMPT_TIMEOUT = 120.seconds
/** How much of a player id goes in the debug log. */
private const val PLAYER_ID_SHOWN = 6
