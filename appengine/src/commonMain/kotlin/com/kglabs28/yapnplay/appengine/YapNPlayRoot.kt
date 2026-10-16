package com.kglabs28.yapnplay.appengine

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.layoutTierFor
import com.kglabs28.yapnplay.appengine.navigation.YapNPlayNavHost

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
) {
    val tier = windowWidthDp?.let(::layoutTierFor)
    YapNPlayTheme(tierOverride = tier) {
        Box(
            modifier = modifier
                .fillMaxSize()
                .background(YapNPlayTheme.colors.Background),
        ) {
            YapNPlayNavHost(
                modifier = Modifier.fillMaxSize(),
            )
        }
    }
}
