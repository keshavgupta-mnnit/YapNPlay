package com.kglabs28.yapnplay.appengine.ui.components.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * The slim ad pill (design): an "AD" badge, the advertiser's icon, the
 * headline and its button (Install, Open…), in the arena's glass, 320 wide
 * unless [modifier] sets a width (fillMaxWidth: as wide as its card). It
 * takes an ad loaded ahead of time, so it shows at once. Nothing while ads
 * are off; until the ad arrives, empty room when [reserveSpace] (so a table
 * never shifts when it comes), else no room at all. With [refresh] (a pill
 * that stays up all game) a new ad replaces it every few seconds
 * (ads_native_refresh_seconds) while the screen is in front.
 * Platforms without ads draw nothing.
 */
@Composable
expect fun AdPill(modifier: Modifier = Modifier, reserveSpace: Boolean = false, refresh: Boolean = false)
