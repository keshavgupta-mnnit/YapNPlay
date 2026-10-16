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
    val GlassSurface = Color(0xD9152235)

    // Base Accents
    val Cyan = Color(0xFF3ADBED)
    val ButtonFace = Color(0xFF09FEFE)
    val Orange = Color(0xFFDF8733)
    val OrangeCore = Color(0xFFC0671F)
    val Pink = Color(0xFFE64DA0)
    val Purple = Color(0xFFC26FE8)
    val Gold = Color(0xFFFFC700)

    // Functional Feature Zone Tokens (Semantic, sampled from the home design)
    val GameZoneAccent = Color(0xFF08F9B5)
    val GameZoneGlow = Color(0x6608F9B5)
    val GameZoneBorder = Color(0x8008F9B5)
    val GameZoneSurface = Color(0xFF191934)
    val GameZoneIconFill = Color(0xFF1C4579)

    val SocialZoneAccent = Color(0xFFB834F7)
    val SocialZoneGlow = Color(0x66B834F7)
    val SocialZoneBorder = Color(0x80B834F7)
    val SocialZoneSurface = Color(0xFF1E1A45)
    val SocialZoneIconFill = Color(0xFF36276C)

    // Home feature pillars
    val FeatureIconPurple = Color(0xFF933CF6)
    val FeatureIconGreen = Color(0xFF22E7B7)

    // Green call-to-action (Quick Play, Create New Room)
    val CtaGreenStart = Color(0xFF03F0C6)
    val CtaGreenMid = Color(0xFF37ECB7)
    val CtaGreenEnd = Color(0xFF1ECD9C)
    val CtaGreenContent = Color(0xFF053B36)

    // Purple call-to-action (Join)
    val CtaPurpleStart = Color(0xFF8B2CC2)
    val CtaPurpleEnd = Color(0xFF7B26B8)
    val CtaPurpleRim = Color(0xFFD198DD)

    // Dark purple button with a light rim (home "Create Room")
    val CreateRoomButtonStart = Color(0xFF49248A)
    val CreateRoomButtonEnd = Color(0xFF3F2470)
    val CreateRoomButtonRim = Color(0xFF754AB4)
    val CreateRoomBannerSurface = Color(0xFF221F46)
    val CreateRoomBannerIcon = Color(0xFF9375CF)

    // Rooms tab (sampled from the rooms design)
    val RoomsHeroTop = Color(0xFF40298C)
    val RoomsHeroBottom = Color(0xFF112245)
    val RoomsHeroRim = Color(0xFF5F96C3)
    val RoomsPanel = Color(0xFF1B2344)
    val RoomsRow = Color(0xFF252F51)
    val RoomsInput = Color(0xFF1E2647)
    val RoomsSmallButton = Color(0xFF282E50)
    val RoomsTicket = Color(0xFF9129D7)
    val RoomsSubtitle = Color(0xFFB3C0EC)
    val RoomsPlaceholderDigits = Color(0xFF626A9C)
    val RoomIconPurple = Color(0xFF8C55EC)
    val RoomIconBlue = Color(0xFF28A5DB)
    val RoomIconSlate = Color(0xFF5A6A8C)
    val RoomIconViolet = Color(0xFF6F4BE0)
    val LiveGreen = Color(0xFF1AEEA9)
    val IconMuted = Color(0xFF9EAEDE)

    // Top Bar (sampled from the top bar design). Surfaces are glass
    // tints: glassPill() applies the transparency and sheen.
    val TopBarBackground = Color(0xFF14122A)
    val TopBarPill = Color(0xFF22213F)
    val TopBarCurrencyPill = Color(0xFF242844)
    val TopBarButton = Color(0xFF2D3151)
    val TopBarTabSelected = Color(0xFF46866F)
    val TopBarTabSelectedBorder = Color(0xFF5BB592)
    val TopBarIcon = Color(0xFF9EAEDE)
    val TopBarText = Color(0xFF96A3C9)

    // Glass highlight (sheen and rim on glass surfaces)
    val GlassShine = Color(0xFFFFFFFF)

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
