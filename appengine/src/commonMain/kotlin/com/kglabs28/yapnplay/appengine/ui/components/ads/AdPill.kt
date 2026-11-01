package com.kglabs28.yapnplay.appengine.ui.components.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/**
 * The slim ad pill (design): an "AD" badge, the advertiser's icon, the
 * headline and its button (Install, Open…), in the arena's glass. A fresh
 * native ad (AdMob on Android) is loaded each time it is shown. Nothing
 * while ads are off; until the ad arrives, empty room when [reserveSpace]
 * (so a table never shifts when it comes), else no room at all.
 * Platforms without ads draw nothing.
 */
@Composable
expect fun AdPill(modifier: Modifier = Modifier, reserveSpace: Boolean = false)
