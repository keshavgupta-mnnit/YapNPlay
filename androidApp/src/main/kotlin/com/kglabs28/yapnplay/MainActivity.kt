package com.kglabs28.yapnplay

import android.content.Intent
import android.os.Build
import android.os.Bundle
import android.view.WindowManager
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.runtime.SideEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.platform.LocalConfiguration
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.kglabs28.yapnplay.appengine.YapNPlayRoot
import com.kglabs28.yapnplay.appengine.core.common.DeepLink
import com.kglabs28.yapnplay.appengine.core.common.DeepLinks
import com.kglabs28.yapnplay.appengine.core.designsystem.AdaptiveScale

/**
 * Single activity. Landscape-locked via the manifest, fullscreen like
 * other games (sticky immersive — bars return on swipe, then hide),
 * resizable for tablets and split screen. Content comes from :appengine;
 * the platform reads the window width and the shared theme derives the tier.
 */
class MainActivity : ComponentActivity() {

    /** Invite or notification link waiting to be handled by the app. */
    private var deepLink by mutableStateOf<DeepLink?>(null)

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        deepLink = DeepLinks.parse(intent?.dataString)
        enableEdgeToEdge()
        WindowCompat.setDecorFitsSystemWindows(window, false)
        // Draw under the camera cutout too, so the art fills the screen.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.P) {
            window.attributes.layoutInDisplayCutoutMode =
                WindowManager.LayoutParams.LAYOUT_IN_DISPLAY_CUTOUT_MODE_SHORT_EDGES
        }
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
            YapNPlayRoot(
                windowWidthDp = configuration.screenWidthDp,
                deepLink = deepLink,
                onDeepLinkHandled = { deepLink = null },
            )
        }
    }

    override fun onNewIntent(intent: Intent) {
        super.onNewIntent(intent)
        setIntent(intent)
        DeepLinks.parse(intent.dataString)?.let { deepLink = it }
    }
}
