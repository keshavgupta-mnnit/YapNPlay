package com.kglabs28.yapnplay.appengine.core.designsystem

import androidx.compose.ui.unit.sp
import androidx.compose.ui.unit.TextUnit
import androidx.compose.runtime.compositionLocalOf
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.kglabs28.yapnplay.appengine.core.common.AppConstants

/*
 * The only file that writes dp or sp. Every size, gap, corner, stroke and
 * font size in the app is a value here (or in Type, through [scaledSp]),
 * and each multiplies with the screen through [AdaptiveScale] at read time.
 */

/** A design size (drawn for the reference screen) at this screen's scale. Dimens only. */
internal fun Dp.scaled(): Dp = this * AdaptiveScale.multiplier

/** A font size (drawn for the reference screen) at this screen's scale. Usage (Type): `16.scaledSp`. */
internal val Int.scaledSp: TextUnit get() = (this * AdaptiveScale.multiplier).sp

/** A font size (drawn for the reference screen) at this screen's scale. */
internal val Float.scaledSp: TextUnit get() = (this * AdaptiveScale.multiplier).sp

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
    val Xxxs: Dp get() = 2.dp.scaled()
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
    val Xxxs: Dp get() = live.Xxxs
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
    val avatarCard: Dp get() = 56.dp.scaled()
    val avatarRing: Dp get() = 2.dp.scaled()
    val panelRadius: Dp get() = 16.dp.scaled()
    val buttonCut: Dp get() = 12.dp.scaled()
    val borderThin: Dp get() = 1.5.dp.scaled()
    val hairline: Dp get() = 1.dp.scaled()
    /** The coins offer's split line. */
    val offerDivider: Dp get() = 2.dp.scaled()
    /** Room between a field and the top of the app's keyboard. */
    val appKeyboardGap: Dp get() = 12.dp.scaled()
    /** A MONO card narrower than this draws its small face. */
    val monoSmallCard: Dp get() = 40.dp.scaled()
    /** The MONO piles never get narrower than this. */
    val monoMinPileWidth: Dp get() = 34.dp.scaled()
    /** One step of the game stage's neon rims (they are drawn in steps of this). */
    val stageStrokeStep: Dp get() = 0.4.dp.scaled()
    val zero: Dp get() = 0.dp.scaled()
    val statusDot: Dp get() = 12.dp.scaled()
    val statusDotSmall: Dp get() = 6.dp.scaled()
    val glowBlur: Dp get() = 12.dp.scaled()
    val cardRadius: Dp get() = 14.dp.scaled()
    val cardHeaderIconSize: Dp get() = 28.dp.scaled()
    // Logo art is square with transparent margins and is cropped to this
    // box; keep width/height <= 1.45 or the top of the art gets cut.
    val logoWidth: Dp get() = 66.dp.scaled()
    val logoHeight: Dp get() = 46.dp.scaled()

    // Top bar: one fixed bar height; every pill and button shares
    // topBarControlHeight so their tops and bottoms line up.
    val topBarHeight: Dp get() = 68.dp.scaled()
    val topBarControlHeight: Dp get() = 38.dp.scaled()
    /** The room leaderboard's place column ("1st"…). */
    val boardPlaceWidth: Dp get() = 36.dp.scaled()
    /** A round floating button (Feedback's new message). */
    val fabSize: Dp get() = 52.dp.scaled()
    val topBarAvatar: Dp get() = 34.dp.scaled()
    val iconSmall: Dp get() = 14.dp.scaled()
    /** Space reserved for a balance before it loads (fits "99,999"). */
    val balanceMinWidth: Dp get() = 36.dp.scaled()
    val iconMedium: Dp get() = 20.dp.scaled()

    // Home + Rooms cards (from the designs, raised to comfortable touch
    // sizes: anything tappable is at least 32dp tall).
    val ctaHeight: Dp get() = 36.dp.scaled()
    /** Main screen call-to-action (Game Zone QUICK PLAY). */
    val ctaLargeHeight: Dp get() = 44.dp.scaled()
    val ctaSmallHeight: Dp get() = 32.dp.scaled()
    val iconTiny: Dp get() = 10.dp.scaled()
    val rowRadius: Dp get() = 8.dp.scaled()
    val roomRowHeight: Dp get() = 42.dp.scaled()
    val roomIcon: Dp get() = 28.dp.scaled()
    val friendAvatar: Dp get() = 36.dp.scaled()
    val smallButtonHeight: Dp get() = 32.dp.scaled()
    val codeFieldHeight: Dp get() = 36.dp.scaled()
    val joinButtonWidth: Dp get() = 88.dp.scaled()

    // Game pickers (Game Zone carousel/grid, room lobby)
    val carouselArrowWidth: Dp get() = 36.dp.scaled()
    val carouselArrowHeight: Dp get() = 72.dp.scaled()
    val carouselDot: Dp get() = 6.dp.scaled()
    val gameArtGlyph: Dp get() = 56.dp.scaled()
    /** Blur on carousel neighbours: visible but out of focus. */
    val carouselSideBlur: Dp get() = 1.5.dp.scaled()
    // Room lobby
    val lobbyBarHeight: Dp get() = 44.dp.scaled()
    /** The lobby's ⋮ menu. */
    val lobbyMenuWidth: Dp get() = 200.dp.scaled()
    /** The sideways friends row in the lobby's Add Friends dialog. */
    val lobbyFriendsRowHeight: Dp get() = 84.dp.scaled()
    val lobbyStartHeight: Dp get() = 52.dp.scaled()
    val lobbyStartCut: Dp get() = 12.dp.scaled()
    /** Gap between the START button and its outer outline. */
    val lobbyStartOutlineGap: Dp get() = 3.dp.scaled()
    val memberVoiceIcon: Dp get() = 16.dp.scaled()
    // Mic / Speaker tiles under the member list
    val voiceToggleWidth: Dp get() = 58.dp.scaled()
    val voiceToggleHeight: Dp get() = 60.dp.scaled()
    val voiceToggleSwitch: Dp get() = 24.dp.scaled()
    val voiceToggleKnob: Dp get() = 18.dp.scaled()
    /** Glow spread of neon pills and switches. */
    val neonGlow: Dp get() = 8.dp.scaled()
    /** How far the game art floats up and down on the stage. */
    val stageBob: Dp get() = 6.dp.scaled()
    /** Coloured outline around the stage's game title. */
    val titleOutline: Dp get() = 4.dp.scaled()

    // Royal Ludo table
    val ludoAvatar: Dp get() = 46.dp.scaled()
    val ludoAvatarRing: Dp get() = 2.5.dp.scaled()
    val ludoAudienceAvatar: Dp get() = 30.dp.scaled()
    val ludoAudienceNameWidth: Dp get() = 52.dp.scaled()
    val ludoAudienceRing: Dp get() = 2.dp.scaled()
    val ludoCardRadius: Dp get() = 18.dp.scaled()
    val ludoCardAvatar: Dp get() = 44.dp.scaled()
    val ludoAvatarRadius: Dp get() = 12.dp.scaled()
    val ludoGlossHeight: Dp get() = 40.dp.scaled()
    val ludoGlossRadius: Dp get() = 11.dp.scaled()
    val ludoTimerStroke: Dp get() = 5.dp.scaled()
    val ludoMuteButton: Dp get() = 26.dp.scaled()
    val ludoMuteIcon: Dp get() = 15.dp.scaled()
    val modalCloseButton: Dp get() = 24.dp.scaled()
    val modalCloseIcon: Dp get() = 11.dp.scaled()
    val reportListMaxHeight: Dp get() = 160.dp.scaled()
    val ludoCardGlow: Dp get() = 18.dp.scaled()
    val lobbyMemberRow: Dp get() = 56.dp.scaled()
    val lobbyAvatar: Dp get() = 38.dp.scaled()
    val crownSize: Dp get() = 18.dp.scaled()
    val leaderBorder: Dp get() = 2.dp.scaled()
    val switchWidth: Dp get() = 44.dp.scaled()
    val switchHeight: Dp get() = 22.dp.scaled()
    val switchKnob: Dp get() = 16.dp.scaled()
    val voiceWaveHeight: Dp get() = 16.dp.scaled()
    val voiceWaveBar: Dp get() = 2.dp.scaled()
    val voiceWaveSpacing: Dp get() = 4.dp.scaled()
    val modalWidth: Dp get() = 320.dp.scaled()
    val seatDialogWidth: Dp get() = 460.dp.scaled()
    val cutButtonHeight: Dp get() = 42.dp.scaled()
    val ludoSpectatorMic: Dp get() = 44.dp.scaled()
    val cutButtonCut: Dp get() = 10.dp.scaled()
    val cutButtonMinWidth: Dp get() = 150.dp.scaled()
    val seatTileWidth: Dp get() = 76.dp.scaled()
    val seatTileAvatar: Dp get() = 54.dp.scaled()
    val seatTileTick: Dp get() = 18.dp.scaled()
    val seatTileSelectedBorder: Dp get() = 2.dp.scaled()

    // Top bar notifications
    val notificationsWidth: Dp get() = 320.dp.scaled()
    val notificationsMaxHeight: Dp get() = 260.dp.scaled()
    val notificationBadge: Dp get() = 16.dp.scaled()

    /** Background blur when a tab asks for a frosted backdrop. */
    val backdropBlur: Dp get() = 14.dp.scaled()

    // City Bazaar
    val cbCardAvatar: Dp get() = 32.dp.scaled()
    val cbDialogWidth: Dp get() = 520.dp.scaled()
    val cbPropertyCardMinWidth: Dp get() = 150.dp.scaled()
    val cbCardAvatarMax: Dp get() = 56.dp.scaled()
    /** A player pod's height beyond its avatar (status pill, padding, level badge). */
    val cbCardChrome: Dp get() = 36.dp.scaled()
    /** The same in the compact pod (many players). */
    val cbCardChromeCompact: Dp get() = 26.dp.scaled()
    val cbCardAvatarMin: Dp get() = 20.dp.scaled()
    /** A compact city card in the My Properties list. */
    val cbCityCardHeight: Dp get() = 56.dp.scaled()
    // MONO (cards are 2:3 portrait everywhere they appear).
    /** A card in the local player's hand. */
    val monoHandCardWidth: Dp get() = 50.dp.scaled()
    /** A hand card once the hand is big (more than [com.kglabs28.yapnplay.appengine.core.common.AppConstants.MONO_BIG_HAND] cards). */
    val monoHandCardWidthSmall: Dp get() = 44.dp.scaled()
    /** A card on the centre pile, or an opponent's face-down card. */
    val monoPileCardWidth: Dp get() = 68.dp.scaled()
    /** The wide frosted pod across the top of the table. */
    val monoPodWidth: Dp get() = 132.dp.scaled()
    val monoPodWidthCompact: Dp get() = 122.dp.scaled()
    /** The local player's own pod at the bottom left. */
    val monoLocalPodWidth: Dp get() = 150.dp.scaled()
    /** One colour choice in the Prism picker. */
    /** A player's avatar in the Rule of 7 swap choice. */
    val monoSwapAvatar: Dp get() = 30.dp.scaled()
    /** How far a selected card rises out of the hand. */
    val monoCardLift: Dp get() = 14.dp.scaled()
    /** A seat: the round avatar, and the name pill beside it (as tall as this share of the avatar). */
    val monoSeatAvatar: Dp get() = 50.dp.scaled()
    val monoSeatAvatarCompact: Dp get() = 44.dp.scaled()
    val monoSeatRing: Dp get() = 3.dp.scaled()
    val monoSeatTimer: Dp get() = 3.dp.scaled()
    /** The MONO table's top band: a seat (its fan sits beside it). */
    val monoTopBandHeight: Dp get() = 60.dp.scaled()
    /** A top-row seat's fan, beside its pill. */
    val monoSideFanWidth: Dp get() = 90.dp.scaled()
    /** The MONO table's bottom-left corner: the user's seat with its switches over the callout. */
    val monoLocalCornerHeight: Dp get() = 170.dp.scaled()
    /** The face-down fan under a seat. */
    val monoSeatFanCard: Dp get() = 24.dp.scaled()
    /** Each card's step of thickness in a pile (a few cards per step). */
    val monoPileDepthStep: Dp get() = 1.dp.scaled()
    /** A card flying between the piles, the hands and the seats. */
    val monoFlightCard: Dp get() = 56.dp.scaled()
    /** The top bar's height, and its square buttons. */
    val arenaBarHeight: Dp get() = 46.dp.scaled()
    /** The CAUGHT and MONO buttons, either side of the hand. */
    val arenaActionHeight: Dp get() = 50.dp.scaled()
    val arenaCallWidth: Dp get() = 86.dp.scaled()
    /** The small Draw / Take / Pass buttons under the deck, on the user's turn. */
    val monoDeckButtonHeight: Dp get() = 26.dp.scaled()
    /** The MONO table's one message line under the piles (a stack, a colour, a penalty): always this tall. */
    val monoTableMessageHeight: Dp get() = 26.dp.scaled()
    /** The gap between the piles and the message under them. */
    val monoPileGap: Dp get() = 12.dp.scaled()
    /** The room kept under every seat for its tag ("2nd", "Away", "ELIMINATED"), there or not. */
    val seatTagSlot: Dp get() = 14.dp.scaled()
    val monoDeckButtonWidth: Dp get() = 72.dp.scaled()
    /** The round mic / autoplay switches under the user's seat. */
    val arenaSwitchSize: Dp get() = 36.dp.scaled()
    /** The most a Mic / Auto button grows to (it fills half the pill's width up to this). */
    val arenaSwitchMax: Dp get() = 39.dp.scaled()
    /** The dice icon on the bottom-right Roll Dice button. */
    val arenaDiceIcon: Dp get() = 44.dp.scaled()
    /** Kept free under the Ludo board, so the user's card and its switches fit by the bottom base. */
    val ludoBoardBottomStrip: Dp get() = 0.dp
    /** The app's logo beside the game's top bar (larger than the bar). */
    val arenaLogoSize: Dp get() = 64.dp.scaled()
    val cbRollBadge: Dp get() = 22.dp.scaled()
    /** Pods get compact (tighter padding, smaller text) when each has less height than this. */
    val cbCardCompactBelow: Dp get() = 80.dp.scaled()
    val cbStackOffset: Dp get() = 3.dp.scaled()
    val cbToastWidth: Dp get() = 300.dp.scaled()
    val cbAssetCardWidth: Dp get() = 108.dp.scaled()
    val cbHeaderChip: Dp get() = 14.dp.scaled()
    val cbControlHeight: Dp get() = 34.dp.scaled()
    /** 3D City Royale: a seat card round the board and its avatar, My Properties in the bottom bar. */
    val cb3dSeatWidth: Dp get() = 150.dp.scaled()
    val cb3dSeatAvatar: Dp get() = 42.dp.scaled()
    /** The Game Rules list's tallest height; longer lists scroll. */
    val rulesListMaxHeight: Dp get() = 220.dp.scaled()
    val cb3dPropertiesWidth: Dp get() = 100.dp.scaled()
    /** 3D City Royale: how far the board sits below the top bar, and the bottom bar's height (seats keep above it). */
    val cb3dBoardDrop: Dp get() = 34.dp.scaled()
    val cb3dBarHeight: Dp get() = 76.dp.scaled()
    /** 3D City Royale: the snackbar's band at the top, which the top seats keep below. */
    val cb3dSnackReserve: Dp get() = 44.dp.scaled()
    /** 3D City Royale: how far the turn dialogs and payment popups sit above centre, clear of the bottom bar. */
    val cb3dDialogLift: Dp get() = 28.dp.scaled()
    /** 3D City Royale: the gap between the info panel and the board's left corner. */
    val cb3dPanelGap: Dp get() = 3.dp.scaled()
    val cbRowRadius: Dp get() = 8.dp.scaled()

    // Profile screen.
    val profileAvatar: Dp get() = 56.dp.scaled()
    val profileEditBadge: Dp get() = 18.dp.scaled()
    val profileFriendAvatar: Dp get() = 42.dp.scaled()
    val profileCardButtonWidth: Dp get() = 200.dp.scaled()
    val friendKeyboardHeight: Dp get() = 176.dp.scaled()
    val scrollbarWidth: Dp get() = 4.dp.scaled()
    val fieldClearButton: Dp get() = 20.dp.scaled()
    val levelBarHeight: Dp get() = 4.dp.scaled()
    /** The top bar's brand logo (beside the name), and the hairline dividers between its groups. */
    val topBarBrandLogo: Dp get() = 58.dp.scaled()
    /** The level's shield in the XP pill. */
    val levelShield: Dp get() = 18.dp.scaled()
    val topBarDividerHeight: Dp get() = 30.dp.scaled()
    /** The top bar's right group (coins, XP, bell, settings): its pills' and buttons' height, and their icons. */
    val homeBarControl: Dp get() = 30.dp.scaled()
    val homeBarIcon: Dp get() = 14.dp.scaled()
    /** Home's cards (design): the header's and the pillars' icon circles, and the buttons. */
    val homeHeaderIcon: Dp get() = 24.dp.scaled()
    val homePillarIcon: Dp get() = 14.dp.scaled()
    val homeCtaHeight: Dp get() = 28.dp.scaled()
    val homeBannerButtonHeight: Dp get() = 30.dp.scaled()
    /** The home cards' corners on their leaning edges (rounder than their outer ones). */
    val homeCardInnerRadius: Dp get() = 30.dp.scaled()
    /** The space the Create Room panel leaves between itself and Game Zone's corner. */
    val homeBannerClearance: Dp get() = 12.dp.scaled()
    /** The XP pill's text keeps at least this width, so big numbers still fit. */
    val xpLabelMinWidth: Dp get() = 76.dp.scaled()
    /** The Create New Room card's neon rim. */
    val neonRim: Dp get() = 2.dp.scaled()
    val dailyBonusWidth: Dp get() = 340.dp.scaled()
    val dailyBonusCoin: Dp get() = 56.dp.scaled()
    val connectionCardWidth: Dp get() = 360.dp.scaled()
    val connectionSpinner: Dp get() = 44.dp.scaled()
    val leagueRowHeight: Dp get() = 48.dp.scaled()
    // Leaderboard.
    val boardSpotWidth: Dp get() = 132.dp.scaled()
    val boardAvatarFirst: Dp get() = 84.dp.scaled()
    val boardAvatarRow: Dp get() = 46.dp.scaled()
    val boardRing: Dp get() = 3.dp.scaled()
    val boardMicBadge: Dp get() = 22.dp.scaled()
    val boardBoxRadius: Dp get() = 6.dp.scaled()
    val boardBoxFrame: Dp get() = 3.dp.scaled()
    val boardStepPad: Dp get() = 6.dp.scaled()
    val boardBoxInset: Dp get() = 5.dp.scaled()
    val boardLogoWidth: Dp get() = 96.dp.scaled()
    val boardLogoLogoHeight: Dp get() = 66.dp.scaled()
    val boardGameLogo: Dp get() = 100.dp.scaled()
    val boardPanelWidth: Dp get() = 240.dp.scaled()

    // ── Leaderboard podium (ui/components/leaderboard/podium) ──
    /** One podium place's width (its glass box); the player's card is a little narrower so neighbours never touch. */
    val podiumWidth: Dp get() = 136.dp.scaled()
    val podiumCardWidth: Dp get() = 122.dp.scaled()
    val podiumCardHeight: Dp get() = 104.dp.scaled()
    /** The box heights: 1st, 2nd, 3rd. */
    val podiumBoxFirst: Dp get() = 100.dp.scaled()
    val podiumBoxSecond: Dp get() = 78.dp.scaled()
    val podiumBoxThird: Dp get() = 62.dp.scaled()
    /** The glass box's top face (the card stands on it), and how far the card comes down over it. */
    val podiumTopFace: Dp get() = 16.dp.scaled()
    val podiumCardOverTop: Dp get() = 12.dp.scaled()
    /** The plate inside the glass box sits this far in; both have this corner rounding. */
    val podiumPlateInset: Dp get() = 8.dp.scaled()
    val podiumCorner: Dp get() = 8.dp.scaled()
    /** The avatar, and how much of it rises above the card. */
    val podiumAvatar: Dp get() = 64.dp.scaled()
    val podiumAvatarAbove: Dp get() = 38.dp.scaled()
    /** The winner's crown, sitting wholly above the avatar. */
    val podiumCrown: Dp get() = 36.dp.scaled()
    /** The voice symbols either side of the name. */
    val podiumSignal: Dp get() = 16.dp.scaled()
    val boardBottomBar: Dp get() = 64.dp.scaled()
    val boardButtonWidth: Dp get() = 200.dp.scaled()
    val bankButtonGlow: Dp get() = 10.dp.scaled()
    // City Bazaar prompts.
    val promptWideWidth: Dp get() = 540.dp.scaled()
    val promptNoticeWidth: Dp get() = 380.dp.scaled()
    val promptGlow: Dp get() = 10.dp.scaled()
    val promptRim: Dp get() = 2.5.dp.scaled()
    val promptCityCardWidth: Dp get() = 190.dp.scaled()
    val promptNameCardWidth: Dp get() = 84.dp.scaled()
    val promptNameCardHeight: Dp get() = 100.dp.scaled()
    val promptArtIcon: Dp get() = 44.dp.scaled()
    val neonButtonHeight: Dp get() = 40.dp.scaled()
    val neonButtonGlow: Dp get() = 14.dp.scaled()
    val moneyFlightArc: Dp get() = 60.dp.scaled()
    val promptAvatar: Dp get() = 40.dp.scaled()
    val promptAvatarSmall: Dp get() = 32.dp.scaled()
    val promptHouseIcon: Dp get() = 36.dp.scaled()
    val promptProgressHeight: Dp get() = 6.dp.scaled()
    // In-app keyboard.
    val appKeyboardHeight: Dp get() = 236.dp.scaled()
    val appKeyboardMaxWidth: Dp get() = 760.dp.scaled()
    val appKeyboardPreviewHeight: Dp get() = 36.dp.scaled()
    // Game feed.
    val feedSnackMaxWidth: Dp get() = 420.dp.scaled()
    val historyListMaxHeight: Dp get() = 220.dp.scaled()
    /** A match history row's XP and coins columns: the same width on every row, whatever the amount. */
    val historyXpWidth: Dp get() = 72.dp.scaled()
    val historyCoinsWidth: Dp get() = 118.dp.scaled()
    // Groups.
    val groupDialogWidth: Dp get() = 640.dp.scaled()
    val groupIconButton: Dp get() = 40.dp.scaled()
    val groupPanelHeight: Dp get() = 150.dp.scaled()
    val memberChipAvatar: Dp get() = 24.dp.scaled()
    val friendPickAvatar: Dp get() = 32.dp.scaled()
    val groupListMaxHeight: Dp get() = 200.dp.scaled()
    val profileTabHeight: Dp get() = 24.dp.scaled()
    /** The profile's back, settings and exit buttons (a fifth smaller than the app's top bar buttons), and their icons. */
    val profileBarButton: Dp get() = 30.dp.scaled()
    val profileBarIcon: Dp get() = 16.dp.scaled()
    val profileBadge: Dp get() = 18.dp.scaled()
    val personAvatar: Dp get() = 72.dp.scaled()
    val personRowAvatar: Dp get() = 44.dp.scaled()
    val avatarPickerPreview: Dp get() = 88.dp.scaled()
    val avatarPickerItem: Dp get() = 56.dp.scaled()
    val avatarPickerGridMaxHeight: Dp get() = 200.dp.scaled()
    val avatarPickerWidth: Dp get() = 560.dp.scaled()
    val profileWideDialogWidth: Dp get() = 420.dp.scaled()
    val friendsGridMaxHeight: Dp get() = 210.dp.scaled()
    val profileListMaxHeight: Dp get() = 190.dp.scaled()
    // Emoji gifts at the table.
    /** One emoji's tile in the picker (art over its price). */
    /** The largest a tile gets; on smaller screens it follows the window's height (EmojiGiftSizing). */
    val emojiGiftTile: Dp get() = 52.dp.scaled()
    /** The gap between the picker and the seat it opened from. */
    val emojiGiftPickerGap: Dp get() = 6.dp.scaled()
    /** An emoji in flight, at the start of its arc (it grows on the way). */
    val emojiGiftFlight: Dp get() = 34.dp.scaled()
    /** The dots of the trail an emoji leaves behind. */
    val emojiGiftTrailDot: Dp get() = 2.5.dp.scaled()
    // Ads.
    /** The slim ad pill (AD, icon, headline, Install): connecting card, Quick Play search, profile, Ludo table. */
    val adPillHeight: Dp get() = 34.dp.scaled()
    val adPillIcon: Dp get() = 26.dp.scaled()
    val adPillWidth: Dp get() = 320.dp.scaled()
    /** A pill narrower than this (a side card) leaves out the advertiser's icon, so the headline stays readable. */
    val adPillCompactBelow: Dp get() = 260.dp.scaled()
    /** The pill's round ends, badge and button corners, and the gaps inside it. */
    val adPillInset: Dp get() = 6.dp.scaled()
}

val LocalDimens = compositionLocalOf { YapNPlayDimens() }
val LocalLayoutTier = compositionLocalOf { LayoutTier.M }
