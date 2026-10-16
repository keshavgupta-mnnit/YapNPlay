package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.ui.graphics.Color

/**
 * YapNPlay palette with functional semantic naming.
 * Values match the approved design tokens.
 * Never use Color(...) literals outside this file.
 */
object YapNPlayColors {
    // Core Surfaces
    val Background = Color(0xFF050917)
    val Panel = Color(0xFF152235)
    val Glass = Color(0xB3233246)
    val Raised = Color(0xFF323B4D)
    val GlassSurface = Color(0xB3152235)

    // Base Accents
    val Cyan = Color(0xFF3ADBED)
    val ButtonFace = Color(0xFF09FEFE)
    val Orange = Color(0xFFDF8733)
    val OrangeCore = Color(0xFFC0671F)
    val Pink = Color(0xFFE64DA0)
    val Purple = Color(0xFFC26FE8)
    val Gold = Color(0xFFFFC700)

    // Functional Feature Zone Tokens (Semantic)
    val GameZoneAccent = Color(0xFF00E5A3)
    val GameZoneGlow = Color(0x6600E5A3)
    val GameZoneBorder = Color(0x8000E5A3)

    val SocialZoneAccent = Color(0xFFA855F7)
    val SocialZoneGlow = Color(0x66A855F7)
    val SocialZoneBorder = Color(0x80A855F7)

    // States & Status
    val Success = Color(0xFF72F597)
    val Warning = Color(0xFFF0A030)
    val Error = Color(0xFFD9344A)

    // Text & Content
    val TextPrimary = Color(0xFFF2F6FF)
    val TextSecondary = Color(0xFF9FB0C8)
    val TextDisabled = Color(0xFF5B6880)

    // Ludo Player Colors
    val PlayerRed = Color(0xFFD9344A)
    val PlayerGreen = Color(0xFF2DD462)
    val PlayerYellow = Color(0xFFEDD942)
    val PlayerBlue = Color(0xFF338ED0)

    // Helpers
    val Transparent = Color.Transparent
    val Scrim = Color(0x99050917)
}
