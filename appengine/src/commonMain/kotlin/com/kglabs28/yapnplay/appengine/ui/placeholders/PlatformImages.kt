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

/** The leaderboard's celebration background (boards, confetti, sparks). */
@Composable
expect fun LeaderboardArtwork(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
)

@Composable
expect fun HomeBackgroundArtwork(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
)

/** Royal Ludo's game art (carousel, lobby, every game picker). */
@Composable
expect fun RoyalLudoArtwork(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
)

/** Preset avatar number [index] (0-based, see AvatarPresets), filling [modifier]. */
@Composable
expect fun PresetAvatarArtwork(
    index: Int,
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
)

/** City Bazaar's game art (carousel, lobby, every game picker). */
@Composable
expect fun CityBazaarArtwork(
    modifier: Modifier = Modifier,
    contentDescription: String? = null,
)
