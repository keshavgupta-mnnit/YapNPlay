package com.kglabs28.yapnplay

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.ui.platform.LocalConfiguration
import com.kglabs28.yapnplay.appengine.YapNPlayRoot

/**
 * Single activity. Landscape-locked via the manifest, edge-to-edge,
 * resizable for tablets and split screen. Content comes from :appengine;
 * the platform reads the window width and the shared theme derives the tier.
 */
class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            YapNPlayRoot(windowWidthDp = LocalConfiguration.current.screenWidthDp)
        }
    }
}
