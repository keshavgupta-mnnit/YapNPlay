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
    val GlassSurface = Color(0xD9152235)

    // Base Accents
    val Cyan = Color(0xFF3ADBED)
    val Pink = Color(0xFFE64DA0)
    val Purple = Color(0xFFC26FE8)
    val Gold = Color(0xFFFFC700)

    // Functional Feature Zone Tokens (Semantic, sampled from the home design)
    val GameZoneAccent = Color(0xFF20E8A0)
    val GameZoneSurface = Color(0xFF191934)

    val SocialZoneAccent = Color(0xFFB834F7)
    val SocialZoneSurface = Color(0xFF1E1A45)
    /** Home and top bar text that the design shows as soft white (subtitles, tagline, unselected tabs). */
    val HomeTextSoft = Color(0xFFECE0FC)
    /** The home cards' header icon circles: light frosted glass under a white icon (design). */
    val HomeIconFrost = Color(0x40E7E0FF)
    /** Glassy call-to-action buttons (the selected tab's finish, other colours): lighter green, and dark red. */
    val CtaGlassGreen = Color(0xFF34C9A0)
    val CtaGlassGreenRim = Color(0xFFA8F7DC)
    val CtaGlassRed = Color(0xB3701428)
    val CtaGlassRedRim = Color(0xFFFF8FA6)
    /** The Create Room panel's text: dark, on its frosted white glass. */
    val CreateRoomPanelText = Color(0xFF1E1640)
    /** The Create Room panel: frosted white glass (mostly white, a little see-through) with a pale rim. */
    val CreateRoomFrost = Color(0xB3FFFFFF)
    val CreateRoomFrostRim = Color(0x80FFFFFF)
    /** The top bar's unselected tabs, and its bell and settings: lighter frosted glass (design). */
    val TopBarTabFrost = Color(0xFF594D7E)
    val TopBarButtonFrost = Color(0xFF8C799B)
    /** The home cards' frosted glass (design): a see-through violet, lighter at the top. */
    val HomeCardFrostTop = Color(0x8C45367A)
    val HomeCardFrostBottom = Color(0x80302659)
    /** "FRIENDS" in the home card's title, and that card's rim (home design 2026-10). */
    val FriendsAccent = Color(0xFF9878E8)
    /** The XP level shield in the top bar: purple, lighter at the top. */
    val LevelShieldTop = Color(0xFF8848C0)
    val LevelShieldBottom = Color(0xFF5828B0)
    /** The Rooms JOIN button: deep navy with an indigo rim. */
    val JoinButtonTop = Color(0xFF2C2878)
    val JoinButtonBottom = Color(0xFF202058)
    val JoinButtonRim = Color(0xFF5048B0)

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
    val CreateRoomButtonStart = Color(0xFF542880)
    val CreateRoomButtonEnd = Color(0xFF48246C)
    val CreateRoomButtonRim = Color(0xFF5C2C8C)
    val CreateRoomBannerSurface = Color(0xFF282834)
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
    val VoiceWaveStart = Color(0xFF1C8FB0)
    val VoiceWaveEnd = Color(0xFF8336B8)
    val IconMuted = Color(0xFF9EAEDE)

    // Top Bar (sampled from the top bar design). Surfaces are glass
    // tints: glassPill() applies the transparency and sheen.
    val TopBarBackground = Color(0xFF14122A)
    val TopBarPill = Color(0xFF22213F)
    val TopBarCurrencyPill = Color(0xFF242844)
    val TopBarButton = Color(0xFF2D3151)
    val TopBarTabSelected = Color(0xFF206070)
    val TopBarTabSelectedBorder = Color(0xFF80D8E0)
    val TopBarIcon = Color(0xFF9EAEDE)
    val TopBarText = Color(0xFF96A3C9)

    // Glass highlight (sheen and rim on glass surfaces)
    val GlassShine = Color(0xFFFFFFFF)

    // States & Status
    val Success = Color(0xFF72F597)
    val Warning = Color(0xFFF0A030)
    val Error = Color(0xFFD9344A)
    /** The deep end of red (danger) buttons. */
    val ErrorDeep = Color(0xFFA1203A)
    /** Profile: the see-through shell around the user and their friends. */
    val ProfileShell = Color(0xD91B2344)
    /** Profile dialogs: frosted purple glass, lighter at the top. */
    val GlassDialogTop = Color(0xB3402878)
    val GlassDialogBottom = Color(0xB31B2344)
    /** A person's row inside a list dialog. */
    val ListRow = Color(0x99282E50)

    // Text & Content
    val TextPrimary = Color(0xFFF2F6FF)
    val TextSecondary = Color(0xFF9FB0C8)
    val TextDisabled = Color(0xFF5B6880)

    // Ludo Player Colors (neon, as in the Royal Ludo design)
    val PlayerRed = Color(0xFFFF3D57)
    val PlayerGreen = Color(0xFF2EE86F)
    val PlayerYellow = Color(0xFFFFD43B)
    val PlayerBlue = Color(0xFF3D9BFF)
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
    /** A City Bazaar card's bottom bar while nobody owns it. */
    val CbUnownedBar = Color(0xFF2A2F3A)
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

    // Glass dice (frosted gold, Royal Ludo design)
    val DiceGold = Color(0xFFFFEFCF)
    val DiceIvory = Color(0xFFFFFFFF)
    val DiceAmber = Color(0xFFF1D9A8)
    val DiceDeep = Color(0xFFC4A877)
    val DicePip = Color(0xFF4A3316)

    // ── MONO / MONO: Complete Chaos (dark jewel-tone glass card spec) ──
    /** Smoked obsidian glass the card body is cut from (90% opacity). */
    val MonoCardBase = Color(0xE6101218)

    // Deep but saturated jewels, with neon rims: dark cards that still glow.
    val MonoRedFill = Color(0xFF9E0F2E)
    val MonoRedRim = Color(0xFFFF3D63)
    val MonoGreenFill = Color(0xFF0B6E3E)
    val MonoGreenRim = Color(0xFF2DEB8E)
    val MonoBlueFill = Color(0xFF0F48A8)
    val MonoBlueRim = Color(0xFF4A9BFF)
    val MonoGoldFill = Color(0xFF946300)
    val MonoGoldRim = Color(0xFFFFC23D)


    /** Card back: base and the four converging metallic ribbons. */
    val MonoBackBase = Color(0xFF0B0D13)
    val MonoBackRim = Color(0xFF5E6A82)

    /** The 3D silver of the MONO wordmark on the back. */
    val MonoSilverTop = Color(0xFFF7FAFF)
    val MonoSilverMid = Color(0xFFC3CDDE)
    val MonoSilverBottom = Color(0xFF77839B)

    /** The crimson sub-plate that reads COMPLETE CHAOS. */
    val MonoChaosPlate = Color(0xFF5A0E18)
    val MonoChaosPlateLight = Color(0xFF94202F)
    /** Card metal (bevel frames, octagon rings) and the octagon's dark faceted glass. */
    val MonoMetalLight = Color(0xFF8B94A8)
    val MonoMetalDark = Color(0xFF1E2129)
    val MonoOctagonCore = Color(0xFF1C1F27)
    val MonoOctagonEdge = Color(0xFF0D0F14)
    val MonoChaosPlateRim = Color(0xFF9B2233)

    /** An empty pile on the table, and the pile rims. */
    val MonoTableFeltEdge = Color(0xFF0A0C12)
    val MonoTableRim = Color(0xFF2A3244)
    /** The directional light ring around the centre piles. */
    val MonoRingIdle = Color(0xFF2B3346)
    /** The page edges under the draw pile, and the darker ones under the discard pile. */
    val MonoDeckEdge = Color(0xFFE9E6F2)
    val MonoDeckEdgeShade = Color(0xFF9C97AE)
    val MonoDiscardEdge = Color(0xFF2A2536)

    // ── Free coins card: a festive violet-magenta glass, a rainbow progress bar, one colour per ad ──
    val CoinsCardTop = Color(0xF2361A6E)
    val CoinsCardMid = Color(0xF2521C7A)
    val CoinsCardBottom = Color(0xF27A1F5C)
    val CoinsProgressTrack = Color(0x33FFFFFF)
    val CoinsLadder1 = Color(0xFF29C7FF)
    val CoinsLadder2 = Color(0xFF2EE58A)
    val CoinsLadder3 = Color(0xFFFFC23D)
    val CoinsLadder4 = Color(0xFFFF8A2E)
    val CoinsLadder5 = Color(0xFFFF4FD8)
    val CoinsWatchStart = Color(0xFFFFC23D)
    val CoinsWatchEnd = Color(0xFFFF6A3D)
    val CoinsWatchContent = Color(0xFF2A1200)

    // ── Leaderboard: frosted purple glass for the LEADERBOARD panel and the bottom bar ──
    val BoardGlassTop = Color(0xCC3B2668)
    val BoardGlassBottom = Color(0xCC211538)
    val BoardGlassRim = Color(0x669C86E0)
    val BoardGlassSheen = Color(0x24FFFFFF)
    /** SHARE: bluish glass. PLAY AGAIN: dark reddish glass. */
    val BoardShareTop = Color(0xCC2A4FA8)
    val BoardShareBottom = Color(0xCC14275C)
    val BoardShareRim = Color(0xFF6FA8FF)
    val BoardShareText = Color(0xFFD6E6FF)
    val BoardAgainTop = Color(0xCC8A1734)
    val BoardAgainBottom = Color(0xCC420A19)
    val BoardAgainRim = Color(0xFFFF5A7A)

    // ── League rows: cheap leagues plain, dearer ones richer, the top one royal ──
    val LeagueStarterTop = Color(0xFF2A3340)
    val LeagueStarterBottom = Color(0xFF181D25)
    val LeagueStarterRim = Color(0xFF6B7A8F)
    val LeagueBronzeTop = Color(0xFF55331C)
    val LeagueBronzeBottom = Color(0xFF2A190D)
    val LeagueBronzeRim = Color(0xFFCD7F32)
    val LeagueSilverTop = Color(0xFF454D5E)
    val LeagueSilverBottom = Color(0xFF232832)
    val LeagueSilverRim = Color(0xFFD5DCE8)
    val LeagueGoldTop = Color(0xFF6A4E0E)
    val LeagueGoldBottom = Color(0xFF2E2206)
    val LeagueGoldRim = Color(0xFFFFC23D)
    val LeagueRoyalTop = Color(0xFF5A1E86)
    val LeagueRoyalMid = Color(0xFF3A1060)
    val LeagueRoyalBottom = Color(0xFF7A1F5C)
    val LeagueRoyalRim = Color(0xFFFFD86B)

    // ── Card-table arena (MONO first; Ludo and City Royale later) ──
    /** The glass of a seat's name pill, the top bar's pills and square buttons. */
    val ArenaGlassTop = Color(0xCC2A2340)
    val ArenaGlassBottom = Color(0xCC15111F)
    val ArenaGlassRim = Color(0x66A99CD6)
    /** The talking glow behind a seat: blue into pink. */
    val ArenaTalkBlue = Color(0xFF3FA2FF)
    val ArenaTalkPink = Color(0xFFFF4FD8)
    /** The same glow in grey: the user is talking with their mic off (nobody hears them). */
    val ArenaTalkMutedLight = Color(0xFFC9CDD8)
    val ArenaTalkMutedDark = Color(0xFF6E7383)
    /** A seat switch (Mic / Auto) while it's on: the app's dark pink. */
    val ArenaSwitchOn = Color(0xFFA0216A)
    val ArenaSwitchOnRim = Color(0xFFFF4FB8)
    /** The Auto switch while it's on: dark blue. */
    val ArenaAutoOn = Color(0xFF1F3B9E)
    val ArenaAutoOnRim = Color(0xFF6B95FF)
    /** The table's event snackbar: glassy black. */
    val SnackGlassTop = Color(0xE01A1A22)
    val SnackGlassBottom = Color(0xE0050508)
    /** A dimmed card (not the user's turn): a dark veil over it. */
    val MonoCardVeil = Color(0x73000000)
    /** The rim round a card the user can play now. */
    val MonoCardPlayableRim = Color(0xFFFFFFFF)
    /** The red MONO / CAUGHT button and the purple Draw button. */
    val ArenaCallFillTop = Color(0xFF5A0B1E)
    val ArenaCallFillBottom = Color(0xFF2A0610)
    val ArenaCallNeon = Color(0xFFFF2E55)
    val ArenaDrawFillTop = Color(0xFF4B3A9E)
    val ArenaDrawFillBottom = Color(0xFF2B2063)
    val ArenaDrawRim = Color(0xFF9C8BFF)
    /** The title pill's teal glass. */
    val ArenaTitleTop = Color(0xCC14444C)
    val ArenaTitleBottom = Color(0xCC0E2328)
    val ArenaTitleRim = Color(0xFF3FD0C9)
    val ArenaCrown = Color(0xFFFFC23D)

    // Helpers
    val Transparent = Color.Transparent

    // ── Role tokens: named by WHERE they are used. Restyle one place here and
    // every screen using that role follows (e.g. all generic dialogs). ──
    val BgDialogGenericTop = GlassDialogTop
    val BgDialogGenericBottom = GlassDialogBottom
    val BgDialogCreateGroupTop = GlassDialogTop
    val BgDialogCreateGroupBottom = GlassDialogBottom
    val BgButtonBankTop = Color(0xFF1A1A1A)
    val BgButtonBankBottom = Color(0xFF050505)
    val BgPodiumBaseTop = Color(0xFF2B2E38)
    val BgPodiumBaseBottom = Color(0xFF15171D)
    // Leaderboard podium: frosted name cards and the frosted platform steps the boxes stand in.
    val BgPodiumCardTop = Color(0xD94A4F61)
    val BgPodiumCardBottom = Color(0xD92D313E)
    val BgPodiumStepTop = Color(0xB8474C5C)
    val BgPodiumStepBottom = Color(0xB8262833)
    // Same frosted dark as the LEADERBOARD panel, so the art reads through both alike.
    val MedalGold = Color(0xFFFFC83D)
    val MedalSilver = Color(0xFFD9DEE8)
    val MedalBronze = Color(0xFFCD7F32)
    val TextOnPlayAgainButton = Color(0xFF06291E)
    val TextOnCollectButton = Color(0xFF3A2400)
}
