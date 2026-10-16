package com.kglabs28.yapnplay.appengine.ui.placeholders

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * Platform image loading. Android reads module drawables (compose
 * resources are not packaged into the APK); JVM reads the shared
 * compose resources. Callers never change when assets do.
 */
@Composable
expect fun HomeLogoArtwork(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
)

@Composable
expect fun GameZoneArtwork(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
)

@Composable
expect fun FriendZoneArtwork(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
)

@Composable
expect fun HomeBackgroundArtwork(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
)
