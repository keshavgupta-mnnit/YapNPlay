package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color

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


    /** Leaderboard podium: the frosted name card. */
    val podiumCard: Brush get() = Brush.verticalGradient(listOf(YapNPlayColors.BgPodiumCardTop, YapNPlayColors.BgPodiumCardBottom))


    /** Leaderboard podium: the frosted platform step behind each box. */
    val podiumStep: Brush get() = Brush.verticalGradient(listOf(YapNPlayColors.BgPodiumStepTop, YapNPlayColors.BgPodiumStepBottom))

    /** Leaderboard bottom bar. */
    val leaderboardBar: Brush get() = Brush.verticalGradient(listOf(YapNPlayColors.BoardGlassTop, YapNPlayColors.BoardGlassBottom))

    /** Leaderboard panel (4th onwards): the same frosted purple glass as the bar. */
    val leaderboardPanel: Brush get() = Brush.verticalGradient(listOf(YapNPlayColors.BoardGlassTop, YapNPlayColors.BoardGlassBottom))

    /** The light across the top half of a glass button. */
    val glassSheen: Brush get() = Brush.verticalGradient(listOf(YapNPlayColors.BoardGlassSheen, Color.Transparent))
}
