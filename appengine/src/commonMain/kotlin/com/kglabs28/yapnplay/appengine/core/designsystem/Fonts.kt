package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.ui.text.font.FontFamily

/**
 * Font families. Rajdhani semibold for headings and buttons,
 * Inter for body text. Loaded per platform (see androidMain/jvmMain actuals).
 */
expect val HeadlineFontFamily: FontFamily
expect val BodyFontFamily: FontFamily
