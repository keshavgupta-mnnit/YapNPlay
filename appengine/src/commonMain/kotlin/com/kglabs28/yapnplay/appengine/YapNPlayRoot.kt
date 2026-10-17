package com.kglabs28.yapnplay.appengine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.common.DeepLink
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.layoutTierFor
import com.kglabs28.yapnplay.appengine.navigation.YapNPlayNavHost
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlayHost
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
            BlockingOverlayHost {
                YapNPlayNavHost(
                    modifier = Modifier.fillMaxSize(),
                    deepLink = deepLink,
                    onDeepLinkHandled = onDeepLinkHandled,
                )
            }
        }
    }
}
