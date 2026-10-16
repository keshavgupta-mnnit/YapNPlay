package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kglabs28.yapnplay.appengine.core.common.AppConstants

/**
 * Layout tiers from window width. S = phones, M = large phones and
 * landscape handsets, L = tablets. Recomputed whenever the window changes
 * (rotation lock does not stop split screen or folding). Tiers drive
 * layout *structure*; sizes scale continuously via [AdaptiveScale].
 */
enum class LayoutTier { S, M, L }

/** Pure function so tiers are unit-testable without a window. */
fun layoutTierFor(windowWidthDp: Int): LayoutTier = when {
    windowWidthDp >= AppConstants.TIER_L_MIN_WIDTH_DP -> LayoutTier.L
    windowWidthDp >= AppConstants.TIER_M_MIN_WIDTH_DP -> LayoutTier.M
    else -> LayoutTier.S
}

/**
 * Spacing scale. Every gap multiplies with the screen at read time —
 * add a value here and it scales automatically, nothing else to wire.
 */
class YapNPlaySpacing {
    val Xxs: Dp get() = 4.dp.scaled()
    val Xs: Dp get() = 8.dp.scaled()
    val Sm: Dp get() = 12.dp.scaled()
    val Md: Dp get() = 16.dp.scaled()
    val Lg: Dp get() = 24.dp.scaled()
    val Xl: Dp get() = 32.dp.scaled()
}

/**
 * Gap/padding accessor. Same `Spacing.Md` call sites as always —
 * values now follow the screen automatically.
 */
object Spacing {
    private val live = YapNPlaySpacing()
    val Xxs: Dp get() = live.Xxs
    val Xs: Dp get() = live.Xs
    val Sm: Dp get() = live.Sm
    val Md: Dp get() = live.Md
    val Lg: Dp get() = live.Lg
    val Xl: Dp get() = live.Xl
}

/**
 * All sizes any component or screen may use. Nobody writes a raw dp
 * number outside the design-system package — read from here instead.
 * Every value scales with the screen at read time: add a field with
 * `.scaled()` and it follows automatically, nothing else to wire.
 */
class YapNPlayDimens {
    val touchMin: Dp get() = 48.dp.scaled()
    val buttonHeight: Dp get() = 52.dp.scaled()
    val chipHeight: Dp get() = 32.dp.scaled()
    val memberRowHeight: Dp get() = 64.dp.scaled()
    val seatCardSize: Dp get() = 64.dp.scaled()
    val diceSize: Dp get() = 72.dp.scaled()
    val avatarList: Dp get() = 40.dp.scaled()
    val avatarCard: Dp get() = 56.dp.scaled()
    val avatarRing: Dp get() = 2.dp.scaled()
    val panelRadius: Dp get() = 16.dp.scaled()
    val buttonCut: Dp get() = 12.dp.scaled()
    val timerStroke: Dp get() = 3.dp.scaled()
    val borderThin: Dp get() = 1.5.dp.scaled()
    val hairline: Dp get() = 1.dp.scaled()
    val zero: Dp get() = 0.dp.scaled()
    val timerRing: Dp get() = 48.dp.scaled()
    val statusDot: Dp get() = 12.dp.scaled()
    val statusDotSmall: Dp get() = 6.dp.scaled()
    val voiceToggle: Dp get() = 64.dp.scaled()
    val contentMaxWidth: Dp get() = AppConstants.CONTENT_MAX_WIDTH_DP.scaledDp()
    val glowBlur: Dp get() = 12.dp.scaled()
    val cardRadius: Dp get() = 24.dp.scaled()
    val badgeSize: Dp get() = 28.dp.scaled()
    val badgeMedium: Dp get() = 34.dp.scaled()
    val badgeLarge: Dp get() = 44.dp.scaled()
    val badgeXLarge: Dp get() = 52.dp.scaled()
    val graphicSize: Dp get() = 200.dp.scaled()
    val graphicMaxHeight: Dp get() = 220.dp.scaled()
    val cardHeaderIconSize: Dp get() = 38.dp.scaled()
    val compactButtonHeight: Dp get() = 40.dp.scaled()
    val progressWidth: Dp get() = 112.dp.scaled()
    val progressHeight: Dp get() = 4.dp.scaled()
    val logoWidth: Dp get() = 160.dp.scaled()
    val logoHeight: Dp get() = 60.dp.scaled()
}

val LocalDimens = compositionLocalOf { YapNPlayDimens() }
val LocalLayoutTier = compositionLocalOf { LayoutTier.M }
