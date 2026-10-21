package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.runtime.remember
import androidx.compose.runtime.Composable
import androidx.compose.runtime.CompositionLocalProvider
import androidx.compose.runtime.ReadOnlyComposable
import androidx.compose.runtime.compositionLocalOf

/**
 * YapNPlay theme: colour scheme, typography, shapes, spacing and motion,
 * plus the current [LayoutTier]. Every screen sits inside this theme;
 * components read tokens from it, never literals. Sizes scale with the
 * window automatically via [AdaptiveScale]; tiers only drive layout
 * structure.
 */
data class YapNPlayThemeData(
    val colors: YapNPlayColors = YapNPlayColors,
    val typography: YapNPlayTypography = YapNPlayTypography(),
    val shapes: YapNPlayShapes = YapNPlayShapes(),
)

private val LocalThemeData = compositionLocalOf { YapNPlayThemeData() }

object YapNPlayTheme {
    val colors: YapNPlayColors
        @Composable @ReadOnlyComposable get() = LocalThemeData.current.colors
    val typography: YapNPlayTypography
        @Composable @ReadOnlyComposable get() = LocalThemeData.current.typography
    val shapes: YapNPlayShapes
        @Composable @ReadOnlyComposable get() = LocalThemeData.current.shapes
    val dimens: YapNPlayDimens
        @Composable @ReadOnlyComposable get() = LocalDimens.current
    val tier: LayoutTier
        @Composable @ReadOnlyComposable get() = LocalLayoutTier.current
}

/**
 * @param tierOverride fixed tier for previews and the gallery; null follows
 * the default (M) until a real window size is wired in.
 */
@Composable
fun YapNPlayTheme(
    tierOverride: LayoutTier? = null,
    content: @Composable () -> Unit,
) {
    val tier = tierOverride ?: LayoutTier.M
    val headline = headlineFontFamily()
    val body = bodyFontFamily()
    val data = remember(headline, body) { YapNPlayThemeData(typography = YapNPlayTypography(headline, body)) }
    CompositionLocalProvider(
        LocalThemeData provides data,
        LocalLayoutTier provides tier,
        content = content,
    )
}
