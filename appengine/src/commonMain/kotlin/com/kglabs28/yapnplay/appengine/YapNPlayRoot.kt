package com.kglabs28.yapnplay.appengine

import com.kglabs28.yapnplay.appengine.ui.components.common.SlidingTextHost
import com.kglabs28.yapnplay.appengine.ui.components.common.UpdateGate
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import org.koin.compose.koinInject
import com.kglabs28.yapnplay.appengine.core.auth.rememberPlayGamesAuth
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.profile.usecase.SignInWithPlayGamesUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.ObserveSignInNeededUseCase
import com.kglabs28.yapnplay.appengine.ui.components.keyboard.AppKeyboardHost
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.common.DeepLink
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.layoutTierFor
import com.kglabs28.yapnplay.appengine.navigation.YapNPlayNavHost
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlayHost
import com.kglabs28.yapnplay.appengine.ui.components.common.ConnectionGate
import com.kglabs28.yapnplay.appengine.ui.components.common.RequestStartupPermissions

/**
 * App entry composable. Theme + navigation, drawn fullscreen: screens
 * pad their own content away from the camera cutout where needed.
 * The host passes the real window width (platform API); the tier is
 * derived here so it recomputes whenever the window changes. Koin is
 * started by the host (Application or test setup).
 */
@Composable
fun YapNPlayRoot(
    modifier: Modifier = Modifier,
    windowWidthDp: Int? = null,
    deepLink: DeepLink? = null,
    onDeepLinkHandled: () -> Unit = {},
) {
    val tier = windowWidthDp?.let(::layoutTierFor)
    YapNPlayTheme(tierOverride = tier) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(YapNPlayTheme.colors.Background),
        ) {
            // One host for blocking modals (e.g. the matching snackbar):
            // it frosts the whole app behind them and swallows input.
            // Mic + notifications, asked once per launch for anything not yet granted.
            RequestStartupPermissions()
            // Play Games links this account (or brings back the one linked
            // before, on a new phone or after a reinstall: then the app reloads).
            val playGames = rememberPlayGamesAuth()
            val signInWithPlayGames: SignInWithPlayGamesUseCase = koinInject()
            val observeSignInNeeded: ObserveSignInNeededUseCase = koinInject()
            LaunchedEffect(playGames) {
                suspend fun signIn() {
                    val code = playGames.serverAuthCode() ?: return
                    val result = signInWithPlayGames(code)
                    if (result is AppResult.Success && result.data) playGames.reloadApp()
                }
                signIn()
                // Refused later (the account was restored on another phone): sign in again, one at a time.
                observeSignInNeeded().collect { signIn() }
            }
            // The app's own keyboard sits over everything, dialogs included.
            SlidingTextHost {
            AppKeyboardHost {
            BlockingOverlayHost {
                // An outdated app is stopped first (works offline too), then
                // screens open only once the backend answers (no empty pages).
                UpdateGate {
                    ConnectionGate {
                        YapNPlayNavHost(
                            modifier = Modifier.fillMaxSize(),
                            deepLink = deepLink,
                            onDeepLinkHandled = onDeepLinkHandled,
                        )
                    }
                }
            }
            }
            }
        }
    }
}
