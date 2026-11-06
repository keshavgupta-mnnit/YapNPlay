package com.kglabs28.yapnplay.appengine.ui.components.ads

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier

/** Desktop and the web have no ads. */
@Composable
actual fun AdPill(modifier: Modifier, reserveSpace: Boolean, refresh: Boolean) = Unit
