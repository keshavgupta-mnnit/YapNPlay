package com.kglabs28.yapnplay.appengine.core.auth

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
        val activity = context.findActivity() ?: return null
        return try {
            PlayGamesSdk.initialize(context.applicationContext)
            val client = PlayGames.getGamesSignInClient(activity)
            if (!client.isAuthenticated.await().isAuthenticated) return null
            client.requestServerSideAccess(AppConstants.PLAY_GAMES_WEB_CLIENT_ID, false).await()
        } catch (e: Exception) {
            CrashReporter.record(e, "PlayGamesAuth.serverAuthCode")
            null
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
