package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.ui.graphics.Brush

/**
 * Shared backgrounds by role (bg_dialog_generic, bg_dialog_create_group…),
 * built from the role tokens in [YapNPlayColors]. Use these instead of
 * building the same gradient in each component.
 */
object AppBrushes {
    /** Every generic dialog / modal card. */
    val dialogGeneric: Brush get() = Brush.verticalGradient(listOf(YapNPlayColors.BgDialogGenericTop, YapNPlayColors.BgDialogGenericBottom))

    /** Create Group and the other group dialogs. */
    val dialogCreateGroup: Brush get() = Brush.verticalGradient(listOf(YapNPlayColors.BgDialogCreateGroupTop, YapNPlayColors.BgDialogCreateGroupBottom))

    /** The City Bazaar BANK button. */
    val bankButton: Brush get() = Brush.verticalGradient(listOf(YapNPlayColors.BgButtonBankTop, YapNPlayColors.BgButtonBankBottom))

    /** Leaderboard podium boxes. */
    val podiumBase: Brush get() = Brush.verticalGradient(listOf(YapNPlayColors.BgPodiumBaseTop, YapNPlayColors.BgPodiumBaseBottom))

    /** Leaderboard bottom bar. */
    val leaderboardBar: Brush get() = Brush.verticalGradient(listOf(YapNPlayColors.BgLeaderboardBarTop, YapNPlayColors.BgLeaderboardBarBottom))
}
