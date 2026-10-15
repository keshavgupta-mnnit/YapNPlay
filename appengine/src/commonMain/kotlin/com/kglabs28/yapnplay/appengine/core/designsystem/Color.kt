package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.ui.graphics.Color

/**
 * YapNPlay palette. Values match the approved design list.
 * Never use Color(...) literals outside this file.
 */
object YapNPlayColors {
    // Surfaces
    val Background = Color(0xFF050917)
    val Panel = Color(0xFF152235)
    val Glass = Color(0xB3233246)
    val Raised = Color(0xFF323B4D)

    // Accents
    val Cyan = Color(0xFF3ADBED)
    val ButtonFace = Color(0xFF09FEFE)
    val Orange = Color(0xFFDF8733)
    val OrangeCore = Color(0xFFC0671F)
    val Pink = Color(0xFFE64DA0)
    val Purple = Color(0xFFC26FE8)

    // States
    val Success = Color(0xFF72F597)
    val Warning = Color(0xFFF0A030)
    val Error = Color(0xFFD9344A)

    // Text
    val TextPrimary = Color(0xFFF2F6FF)
    val TextSecondary = Color(0xFF9FB0C8)
    val TextDisabled = Color(0xFF5B6880)

    // Ludo players, each paired with a shape (circle, square, triangle, diamond)
    val PlayerRed = Color(0xFFD9344A)
    val PlayerGreen = Color(0xFF2DD462)
    val PlayerYellow = Color(0xFFEDD942)
    val PlayerBlue = Color(0xFF338ED0)

    // Transparent helpers
    val Transparent = Color.Transparent
    val Scrim = Color(0x99050917)
}
