package com.kglabs28.yapnplay.appengine.ui.placeholders

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme

/**
 * JVM never renders the app (unit tests only) — plain fallbacks so the
 * target compiles. Real art lives once in androidMain/res.
 */
@Composable
private fun FallbackArtwork(modifier: Modifier) {
    Box(modifier.background(YapNPlayTheme.colors.Panel))
}

@Composable
actual fun HomeLogoArtwork(modifier: Modifier, contentDescription: String?) {
    FallbackArtwork(modifier)
}

@Composable
actual fun GameZoneArtwork(modifier: Modifier, contentDescription: String?) {
    FallbackArtwork(modifier)
}

@Composable
actual fun FriendZoneArtwork(modifier: Modifier, contentDescription: String?) {
    FallbackArtwork(modifier)
}

@Composable
actual fun HomeBackgroundArtwork(modifier: Modifier, contentDescription: String?) {
    FallbackArtwork(modifier)
}

@Composable
actual fun RoyalLudoArtwork(modifier: Modifier, contentDescription: String?) {
    FallbackArtwork(modifier)
}

@Composable
actual fun CityBaazarArtwork(modifier: Modifier, contentDescription: String?) {
    FallbackArtwork(modifier)
}
