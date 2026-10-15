package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.ui.text.font.FontFamily

/**
 * JVM (unit tests, desktop previews): downloadable fonts are an Android
 * mechanism, so tests and desktop fall back to the platform default.
 */
actual val HeadlineFontFamily: FontFamily = FontFamily.Default
actual val BodyFontFamily: FontFamily = FontFamily.Default
