package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kglabs28.yapnplay.appengine.core.common.AppConstants

/**
 * Layout tiers from window width. S = phones, M = large phones and
 * landscape handsets, L = tablets. Recomputed whenever the window changes
 * (rotation lock does not stop split screen or folding).
 */
enum class LayoutTier { S, M, L }

/** Pure function so tiers are unit-testable without a window. */
fun layoutTierFor(windowWidthDp: Int): LayoutTier = when {
    windowWidthDp >= AppConstants.TIER_L_MIN_WIDTH_DP -> LayoutTier.L
    windowWidthDp >= AppConstants.TIER_M_MIN_WIDTH_DP -> LayoutTier.M
    else -> LayoutTier.S
}

/** Spacing scale. */
object Spacing {
    val Xxs: Dp = 4.dp
    val Xs: Dp = 8.dp
    val Sm: Dp = 12.dp
    val Md: Dp = 16.dp
    val Lg: Dp = 24.dp
    val Xl: Dp = 32.dp
}

/**
 * All sizes any component or screen may use. Nobody writes a raw dp
 * number outside the design-system package — read from here instead.
 */
data class YapNPlayDimens(
    val touchMin: Dp = 48.dp,
    val buttonHeight: Dp = 52.dp,
    val chipHeight: Dp = 32.dp,
    val memberRowHeight: Dp = 64.dp,
    val seatCardSize: Dp = 64.dp,
    val diceSize: Dp = 72.dp,
    val avatarList: Dp = 40.dp,
    val avatarCard: Dp = 56.dp,
    val avatarRing: Dp = 2.dp,
    val panelRadius: Dp = 16.dp,
    val buttonCut: Dp = 12.dp,
    val timerStroke: Dp = 3.dp,
    val borderThin: Dp = 1.5.dp,
    val hairline: Dp = 1.dp,
    val zero: Dp = 0.dp,
    val timerRing: Dp = 48.dp,
    val statusDot: Dp = 12.dp,
    val voiceToggle: Dp = 64.dp,
    val contentMaxWidth: Dp = AppConstants.CONTENT_MAX_WIDTH_DP.dp,
    val glowBlur: Dp = 12.dp,
)

/** One preset per tier; values converge today, structure diverges by tier. */
val DimensS = YapNPlayDimens()
val DimensM = YapNPlayDimens()
val DimensL = YapNPlayDimens()

fun dimensForTier(tier: LayoutTier): YapNPlayDimens = when (tier) {
    LayoutTier.S -> DimensS
    LayoutTier.M -> DimensM
    LayoutTier.L -> DimensL
}

val LocalDimens = compositionLocalOf { DimensM }
val LocalLayoutTier = compositionLocalOf { LayoutTier.M }
