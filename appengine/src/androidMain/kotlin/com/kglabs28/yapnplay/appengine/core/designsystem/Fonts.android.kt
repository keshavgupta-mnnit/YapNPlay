package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.ui.text.font.Font
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.R

/**
 * Downloadable Google Fonts (see androidMain/res/font/). Lazy so plain-JVM
 * unit tests never touch Android resources; first composition loads them.
 */
actual val HeadlineFontFamily: FontFamily by lazy {
    FontFamily(
        Font(R.font.rajdhani, FontWeight.Normal),
        Font(R.font.rajdhani, FontWeight.Medium),
        Font(R.font.rajdhani, FontWeight.SemiBold),
        Font(R.font.rajdhani, FontWeight.Bold),
    )
}

actual val BodyFontFamily: FontFamily by lazy {
    FontFamily(
        Font(R.font.inter, FontWeight.Normal),
        Font(R.font.inter, FontWeight.Medium),
        Font(R.font.inter, FontWeight.SemiBold),
        Font(R.font.inter, FontWeight.Bold),
    )
}
