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

    // Lobby START MATCH (sampled from the start button design)
    val StartButtonTop = Color(0xFFF6B755)
    val StartButtonBottom = Color(0xFFB4601B)
    val StartButtonRim = Color(0xFFFFE2A0)
    val StartButtonGlow = Color(0xFFF59E2E)
    val StartButtonSub = Color(0xFFFFE6A6)
    val StartButtonDisabledTop = Color(0xFF6B7080)
    /** Neon green of the Ready pill and ON switches. */
    val ReadyGreen = Color(0xFF5CF08A)
    /** Mic button while the mic is on. */
    val MicOnPink = Color(0xFFB0246E)
    /** Frosted-white member card (design): top / bottom tint and rim. */
    val MemberCardTop = Color(0x38C9D3EE)
    val MemberCardBottom = Color(0x24A3AECB)
    val MemberCardRim = Color(0x66E6ECFF)
    /** Offline member card: darker than the frosted-white one. */
    val MemberCardOffline = Color(0xD9080C18)
    /** Still voice wave of a member who is not talking. */
    val VoiceWaveMuted = Color(0xFF4A5063)
    /** Hologram stage under the chosen game (lobby). */
    val StageLight = Color(0xFF5FE6F5)
    val StageAccent = Color(0xFFD45BF0)
    val StagePlatformTop = Color(0xFF26304A)
    val StagePlatformSideTop = Color(0xFF1C2338)
    val StagePlatformSideBottom = Color(0xFF0E1322)
    val StartButtonDisabledBottom = Color(0xFF3E424E)
    val StartButtonDisabledRim = Color(0xFF9AA0AE)

    // Lobby member card
    val AvatarRingSilver = Color(0xFF8E97B5)
    val VoiceWaveStart = Color(0xFF4FE3F0)
    val VoiceWaveEnd = Color(0xFFC062F0)
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

    // Ludo Player Colors (neon, as in the Royal Ludo design)
    val PlayerRed = Color(0xFFFF3D57)
    val PlayerGreen = Color(0xFF2EE86F)
    val PlayerYellow = Color(0xFFFFD43B)
    val PlayerBlue = Color(0xFF3D9BFF)
    val PlayerPurple = Color(0xFFB45CFF)
    val PlayerOrange = Color(0xFFFF8A2B)
    // City Bazaar players: deeper than the city groups' colours, so the two don't mix up.
    val CbPlayerRed = Color(0xFFB3132B)
    val CbPlayerGreen = Color(0xFF128A3E)
    val CbPlayerYellow = Color(0xFFB88A00)
    val CbPlayerBlue = Color(0xFF1858B8)
    val CbPlayerPurple = Color(0xFF6E28B8)
    val CbPlayerOrange = Color(0xFFB85000)

    // City Bazaar board: matte tiles, colour headers (the original's colours), neon owners
    val CbTile = Color(0xFF2B313C)
    val CbTileEdge = Color(0xFF3B4352)
    val CbTileSide = Color(0xFF12161D)
    val CbField = Color(0xFF1B2029)
    val CbHouse = Color(0xFF3BE07A)
    val CbHotel = Color(0xFFFF4A5E)
    val CbChest = Color(0xFFFF6B6B)
    val CbLuck = Color(0xFFFFC94A)
    val CbCash = Color(0xFFFFD36B)
    val CbGain = Color(0xFF3BE07A)
    val CbLoss = Color(0xFFFF5A6E)

    // Royal Ludo board
    val LudoBoardTop = Color(0xFF353C4A)
    val LudoBoardBottom = Color(0xFF262C37)
    val LudoBoardSide = Color(0xFF0E1117)
    val LudoBoardRim = Color(0xFFC9D4EC)
    val LudoCell = Color(0xFF424A5A)
    val LudoCellSide = Color(0xFF1E232C)
    val LudoCellEdge = Color(0xFF5A6476)
    val LudoBasePad = Color(0xFF2E3542)
    val LudoCentrePlate = Color(0xFF232833)

    // Royal Ludo 3D glass UI (cards, buttons, title)
    val LudoFrostTop = Color(0x5CC9D2E2)
    val LudoFrostMid = Color(0x33909AB0)
    val LudoFrostBottom = Color(0x52262C3A)
    val LudoFrostRim = Color(0xA6FFFFFF)
    val LudoSilverTop = Color(0xFFF4F7FC)
    val LudoSilverMid = Color(0xFFCBD3E0)
    val LudoSilverBottom = Color(0xFF8F9AAE)
    val LudoSilverText = Color(0xFF17202F)
    val LudoSmokeTop = Color(0x995A6273)
    val LudoSmokeBottom = Color(0x99262B36)
    val LudoMetalLight = Color(0xFFF7FAFF)
    val LudoMetalMid = Color(0xFF9FAECA)
    val LudoMetalDark = Color(0xFF5D6B86)
    val LudoMetalOutline = Color(0xFF1B2740)
    val LudoMetalGlow = Color(0xFF4FA3FF)
    val LudoTeal = Color(0xFF3FE3C2)

    // Glass dice (frosted gold, Royal Ludo design)
    val DiceGold = Color(0xFFFFEFCF)
    val DiceIvory = Color(0xFFFFFFFF)
    val DiceAmber = Color(0xFFF1D9A8)
    val DiceDeep = Color(0xFFC4A877)
    val DicePip = Color(0xFF4A3316)

    // Helpers
    val Transparent = Color.Transparent
    val Scrim = Color(0x99050917)
}
