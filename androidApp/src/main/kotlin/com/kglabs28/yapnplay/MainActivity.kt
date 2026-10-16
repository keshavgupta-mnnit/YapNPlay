package com.kglabs28.yapnplay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.kglabs28.yapnplay.appengine.YapNPlayRoot
import com.kglabs28.yapnplay.appengine.core.designsystem.AdaptiveScale

/**
 * Single activity. Landscape-locked via the manifest, fullscreen like
 * other games (sticky immersive — bars return on swipe, then hide),
 * resizable for tablets and split screen. Content comes from :appengine;
 * the platform reads the window width and the shared theme derives the tier.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        WindowInsetsControllerCompat(window, window.decorView).apply {
            hide(WindowInsetsCompat.Type.systemBars())
            systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
        }
        setContent {
            val configuration = LocalConfiguration.current
            SideEffect {
                AdaptiveScale.updateFor(
                    configuration.screenWidthDp,
                    configuration.screenHeightDp,
                )
            }
            YapNPlayRoot(windowWidthDp = configuration.screenWidthDp)
        }
    }
}
