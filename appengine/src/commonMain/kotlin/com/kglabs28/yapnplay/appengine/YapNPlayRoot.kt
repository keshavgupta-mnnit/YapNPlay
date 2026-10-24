package com.kglabs28.yapnplay.appengine

import com.kglabs28.yapnplay.appengine.core.auth.PlayGamesPlayer
import com.kglabs28.yapnplay.appengine.core.designsystem.AdaptiveScale
import kotlin.math.roundToInt
import androidx.compose.runtime.SideEffect
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.ui.unit.Density
import androidx.compose.ui.platform.LocalDensity
import com.kglabs28.yapnplay.appengine.ui.components.keyboard.TapOutsideClosesKeyboard
import androidx.compose.foundation.layout.imePadding
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.common.DeviceAccountCard
import com.kglabs28.yapnplay.appengine.features.profile.usecase.AnswerDeviceAccountOfferUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.ObserveDeviceAccountOfferUseCase
import androidx.compose.runtime.getValue
import androidx.compose.runtime.collectAsState
import com.kglabs28.yapnplay.appengine.core.common.StartupGate
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
    // Text follows the screen size only (AdaptiveScale), not the phone's font-size setting,
    // so a large system font never pushes the design off the screen.
    val density = LocalDensity.current
    CompositionLocalProvider(LocalDensity provides Density(density.density, fontScale = 1f)) {
    YapNPlayTheme(tierOverride = tier) {
        // The whole screen, never the room the keyboard leaves: every size scales with it.
        BoxWithConstraints(modifier = modifier.fillMaxSize().background(YapNPlayTheme.colors.Background)) {
        val screenWidthDp = maxWidth.value.roundToInt()
        val screenHeightDp = maxHeight.value.roundToInt()
        SideEffect { AdaptiveScale.updateFor(screenWidthDp, screenHeightDp) }
        Box(
            modifier = Modifier
                .fillMaxSize()
                // The phone's keyboard (when chosen) takes room from the bottom instead of covering the screen.
                .imePadding(),
        ) {
            // One host for blocking modals (e.g. the matching snackbar):
            // it frosts the whole app behind them and swallows input.
            // Play Games signs in first; the rest waits so nothing knocks its dialog away.
            val startupGate: StartupGate = koinInject()
            val startupReady by startupGate.ready.collectAsState()
            // Mic + notifications, asked once per launch for anything not yet granted (after sign-in).
            RequestStartupPermissions(ready = startupReady)
            // Play Games links this account (or brings back the one linked
            // before, on a new phone or after a reinstall: then the app reloads).
            val playGames = rememberPlayGamesAuth()
            val signInWithPlayGames: SignInWithPlayGamesUseCase = koinInject()
            val playGamesPlayer: PlayGamesPlayer = koinInject()
            val observeSignInNeeded: ObserveSignInNeededUseCase = koinInject()
            LaunchedEffect(playGames) {
                suspend fun signIn() {
                    val code = playGames.serverAuthCode() ?: return
                    val result = signInWithPlayGames(code)
                    if (result is AppResult.Success && result.data) playGames.reloadApp()
                }
                signIn()
                // Their Play Games name, to suggest on the first-launch welcome.
                playGamesPlayer.remember(playGames.playerName())
                startupGate.open()
                // Refused later (the account was restored on another phone): sign in again, one at a time.
                observeSignInNeeded().collect { signIn() }
            }
            // The app's own keyboard sits over everything, dialogs included.
            TapOutsideClosesKeyboard {
            SlidingTextHost {
            AppKeyboardHost {
            BlockingOverlayHost {
                // First launch on a phone that already has an account: continue it or start new (sign-up waits).
                val observeDeviceAccount: ObserveDeviceAccountOfferUseCase = koinInject()
                val answerDeviceAccount: AnswerDeviceAccountOfferUseCase = koinInject()
                val deviceAccount by observeDeviceAccount().collectAsState()
                BlockingOverlay(visible = deviceAccount != null) {
                    deviceAccount?.let { offer ->
                        DeviceAccountCard(offer = offer, onContinue = { answerDeviceAccount(true) }, onStartNew = { answerDeviceAccount(false) })
                    }
                }
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
    }
    }
}
