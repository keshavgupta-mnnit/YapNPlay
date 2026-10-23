package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match

import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.LeaveDialogUi
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardUi
import com.kglabs28.yapnplay.appengine.ui.components.table.TableFeedLineUi
import com.kglabs28.yapnplay.appengine.ui.components.table.TablePlayerCardUi
import com.kglabs28.yapnplay.appengine.ui.components.table.PlayerFriendActionUi
import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.ui.components.moderation.ReportPersonUi
import com.kglabs28.yapnplay.appengine.ui.components.table.TableAvatarUi
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarColour
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CardDeck
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.CityGroup
import com.kglabs28.yapnplay.appengine.games.citybazaar.utils.TileKind

/** The City Bazaar table. */
data class CityBazaarMatchState(
    val loaded: Boolean = false,
    val title: String = "",
    val subtitle: String = "",
    /** Top bar: spectators (room matches only). */
    val audience: List<TableAvatarUi> = emptyList(),
    val roomMatch: Boolean = false,
    val spectatorCount: String = "",
    val spectatorLabel: String = "",
    val elapsed: String = "",
    val tiles: List<CbTileUi> = emptyList(),
    val tokens: List<CbTokenUi> = emptyList(),
    /** Tiles glowing on the board (the cards being looked at). */
    val highlight: Set<Int> = emptySet(),
    /** The board as a 3D diamond (true) or flat 2D square (false). */
    val threeD: Boolean = true,
    val dice: CbDiceUi = CbDiceUi(),
    /** The user's pod (with controls), bottom-left; null while watching. */
    val myCard: CbPlayerCardUi? = null,
    val leftCards: List<CbPlayerCardUi> = emptyList(),
    val rightCards: List<CbPlayerCardUi> = emptyList(),
    /** The user's cities by colour, under their pod. */
    val stacks: List<CbStackUi> = emptyList(),
    val spectating: Boolean = false,
    val canRoll: Boolean = false,
    val rollLabel: String = "",
    val autoplay: Boolean = false,
    val micOn: Boolean = false,
    /** The user's speaker: off, they hear nobody (only on their device). */
    val speakerOn: Boolean = true,
    /** A short balance-sheet note at the top (rent, tax, cards); goes by itself. */
    val toast: CbToastUi? = null,
    /** The latest money transfer, as a popup for a few seconds. */
    val transaction: CbTransactionUi? = null,
    /** Money popups off: the amount flying from payer to receiver for a moment. */
    val moneyFlight: CbMoneyFlightUi? = null,
    /** The latest game event at the bottom of the table; goes by itself. */
    val feedSnack: TableFeedLineUi? = null,
    /** Every event so far, newest first (the History button). */
    val history: List<TableFeedLineUi> = emptyList(),
    val historyOpen: Boolean = false,
    /** The match is over: the full-screen leaderboard. */
    val leaderboard: LeaderboardUi? = null,
    val modal: CbModal? = null,
    /** Long-press add / remove friend, while its confirm is open. */
    val friendAction: PlayerFriendActionUi? = null,
    /** Info in the left column (a tapped tile, player, pile or stack); any touch elsewhere closes it. */
    val panel: CbPanel? = null,
) : UiState {
    /**
     * Everyone but the user in turn order, starting after the user (the 3D
     * board seats them clockwise in this order). Watching, the right column
     * holds the first half of the turn order.
     */
    val seatsInTurnOrder: List<CbPlayerCardUi> get() = if (spectating) rightCards + leftCards else leftCards + rightCards
}

/** One tile as drawn: its owner's colour and buildings (0 land … 4 hotel). */
data class CbTileUi(val index: Int, val ownerColour: CityBazaarColour?, val level: Int)

/** A player's token on [tile]. */
data class CbTokenUi(val seat: Int, val colour: CityBazaarColour, val tile: Int)

/** The two dice: [rollId] changes on every roll; tinted with the roller's [colour]. */
data class CbDiceUi(val die1: Int = 1, val die2: Int = 1, val rollId: Int = 0, val colour: CityBazaarColour? = null)

/** A player's HUD pod. [status]: "In Jail", "Away", "Bankrupt", "1st"…; [out] dims it. */
data class CbPlayerCardUi(
    override val seat: Int,
    override val name: String,
    override val avatarId: String = "",
    override val level: String,
    val colour: CityBazaarColour,
    /** Balance (₹); the card counts to it when it changes. */
    val cash: Int,
    /** All the rent they've collected so far (₹). */
    val rentCollected: Int = 0,
    override val isMe: Boolean,
    override val isTurn: Boolean,
    override val talking: Boolean,
    override val timerFraction: Float,
    val status: String?,
    override val mutedForMe: Boolean,
    /** False for a CPU: no voice wave and no mute button (it never talks). */
    override val canSpeak: Boolean = true,
    val out: Boolean,
) : TablePlayerCardUi

/** A stack of the user's cities of one colour. */
data class CbStackUi(val group: CityGroup, val count: Int, val label: String)

/** One line of a balance sheet. */
data class CbSheetRowUi(val label: String, val amount: String, val kind: CbRowKind)

enum class CbRowKind { NORMAL, MINUS, PLUS, TOTAL }

/** One line of a rate card; [highlighted] glows (the current tier, or the next). */
data class CbRateRowUi(val label: String, val rent: String, val highlighted: Boolean)

/**
 * A city's card: rent per tier (the current one highlighted), what it earns
 * now (2X with 3 of a colour), what the next sale brings, and its costs.
 */
data class CbPropertyCardUi(
    val tile: Int,
    val name: String,
    val group: CityGroup?,
    val kind: TileKind,
    val rows: List<CbRateRowUi>,
    val income: String?,
    /** "Rent collected: ₹…" so far (buyable tiles). */
    val collected: String? = null,
    val sale: String?,
    val costs: List<CbSheetRowUi>,
    val owner: String,
    /** The owner's colour for the bottom bar; null while unowned. */
    val ownerColour: CityBazaarColour?,
)

/**
 * The info panel in the left column (in place of the other players): a
 * tapped tile's card, a player, a Luck / Shagun Chest table, or the
 * user's cities.
 */
sealed interface CbPanel {
    /** Luck or Shagun Chest: what each dice total (2–12) does. */
    data class Deck(val deck: CardDeck, val title: String, val body: String, val rows: List<CbDeckRowUi>) : CbPanel
    /** The user's properties (a colour stack, or all of them) as compact cards; a tap opens one. */
    data class Cities(val title: String, val cities: List<CbOwnedRowUi>) : CbPanel
    data class Property(val card: CbPropertyCardUi) : CbPanel
    /** A player's balance and worth, and each of their properties with the rent it earns now. */
    data class Player(val name: String, val colour: CityBazaarColour, val sheet: List<CbSheetRowUi>, val cities: List<CbOwnedRowUi>) : CbPanel
    /** The bank: its money, and every property nobody owns (never bought or sold back). */
    data class Bank(val name: String, val sheet: List<CbSheetRowUi>, val cities: List<CbOwnedRowUi>) : CbPanel
}

/** One property in a list (a player's panel, My Properties): its colour, name and the rent it earns now; a tap opens its card. */
data class CbOwnedRowUi(val tile: Int, val name: String, val group: CityGroup?, val kind: TileKind, val rent: String, val worth: String)

/** One Luck / Shagun Chest result: the dice total, what happens, the amount (may be empty) and whether it pays ([gain]) or costs. */
data class CbDeckRowUi(val roll: String, val text: String, val amount: String, val gain: Boolean)

/**
 * Money moving between players or with the bank, shown for a few seconds:
 * what it is, who pays whom (avatar or bank → avatar or bank), and one
 * player's balance before, the change and after (the user's when it's theirs).
 */
/** A coin animation: [amount] flies from [fromSeat] to [toSeat] (null = the bank). */
data class CbMoneyFlightUi(val id: Long, val fromSeat: Int?, val toSeat: Int?, val amount: String)

data class CbTransactionUi(
    val id: Long,
    val title: String,
    val line: String,
    val status: String,
    val fromAvatarId: String?,
    val toAvatarId: String?,
    /** Null for a notice without money (a card, jail, Rest House…): no transfer, bar or balance. */
    val amount: String?,
    val cash: String,
    val changeLabel: String,
    val change: String,
    val remaining: String,
    val gain: Boolean,
    val art: CbNoticeArt = CbNoticeArt.MONEY,
)

/** The picture of a popup without money (and MONEY for transfers). */
enum class CbNoticeArt { MONEY, LUCK, CHEST, JAIL, REST, FREED, BANKRUPT }

/** A balance-sheet note that shows for a moment. */
data class CbToastUi(val id: Long, val title: String, val rows: List<CbSheetRowUi>, val gain: Boolean)

/** Something the user can sell: the top building of a city, or a bare city. */
data class CbAssetUi(val id: String, val tile: Int, val name: String, val group: CityGroup?, val kind: TileKind, val action: String, val value: String)

data class CbResultRowUi(val place: String, val name: String, val colour: CityBazaarColour, val worth: String)

sealed interface CbModal {
    /** Turn decisions, an auction, a vote and the final ranking need an answer: a tap outside doesn't close them. */
    val needsAnswer: Boolean
        get() = when (this) {
            is Buy, is Build, is JailRoll, is JailChoice, is Debt, is Auction, is Vote, is Result, is ExtensionVote, is EndVote -> true
            else -> false
        }

    /** Unowned tile landed on: rate card + balance sheet, No / Yes. */
    /** Landed on a city for sale: the rent it earns, the balance before and after, Yes / No. */
    data class Buy(
        val tile: Int,
        val name: String,
        val group: CityGroup?,
        val kind: TileKind,
        val headline: String,
        /** Land rent and rent with 1 house (cities); null for companies (the headline says it). */
        val rentLand: String? = null,
        val rentHouse: String? = null,
        val price: String = "",
        /** Rent per level (land, 1–3 houses, hotel) and the costs, as on the side card. */
        val rates: List<CbRateRowUi>,
        val costs: List<CbSheetRowUi>,
        val cash: String,
        val cost: String,
        val remaining: String,
        val canAfford: Boolean,
        val countdown: String,
    ) : CbModal

    /** Landed on your own city: one more house (or the hotel), its cost and the new rent. */
    data class Build(
        val tile: Int,
        val name: String,
        val group: CityGroup?,
        val price: String,
        /** Rent per level (the next one highlighted) and the costs, as on the side card. */
        val rates: List<CbRateRowUi>,
        val costs: List<CbSheetRowUi>,
        val what: String,
        val cost: String,
        val newRent: String,
        val cash: String,
        val remaining: String,
        val canAfford: Boolean,
        val countdown: String,
    ) : CbModal

    /** Just jailed (or still in): roll for doubles, pay the fine, or stay for half rent. */
    data class JailRoll(val body: String, val canPay: Boolean, val countdown: String) : CbModal
    data class JailChoice(val canPay: Boolean, val countdown: String) : CbModal

    /** Raise money: pick what to sell; a single bare city can go to auction instead. */
    data class Debt(
        val body: String,
        val assets: List<CbAssetUi>,
        val selected: Set<String>,
        val tracker: String,
        val enough: Boolean,
        val canAuction: Boolean,
        val countdown: String,
    ) : CbModal

    /** A city up for auction: its card, who holds its colour, the bid sheet; [canBid] false for the seller. */
    data class Auction(
        val tile: Int,
        val title: String,
        val body: String,
        val card: CbPropertyCardUi,
        val colourLines: List<String>,
        val leader: String,
        val sheet: List<CbSheetRowUi>,
        val bidLabel: String,
        val isSeller: Boolean,
        val canBid: Boolean,
        val countdown: String,
    ) : CbModal

    data class Result(val rows: List<CbResultRowUi>) : CbModal
    /** Rounds are over: continue for more rounds or end. [canVote] only for players still in who haven't voted. */
    data class ExtensionVote(val body: String, val tally: String, val status: String?, val canVote: Boolean, val countdown: String) : CbModal
    /** Someone asked to end the game: end or keep playing (play is paused). */
    data class EndVote(val body: String, val tally: String, val status: String?, val canVote: Boolean, val countdown: String) : CbModal
    /** The shared exit dialog. */
    data class LeaveConfirm(val dialog: LeaveDialogUi) : CbModal
    data object MicPermissionNeeded : CbModal
    data class Settings(val effectsOn: Boolean, val musicOn: Boolean, val canProposeEnd: Boolean = false) : CbModal
    data class Report(val people: List<ReportPersonUi>, val selectedUserId: String?, val warning: String, val error: String?) : CbModal
    data class Vote(val reportId: String, val title: String, val body: String, val secondsLeft: String) : CbModal
    data class ReportOutcome(val title: String, val body: String) : CbModal
    /** [adOffered]: Watch Ad shows (ads on and one ready). */
    data class Penalty(val title: String, val body: String, val watchingAd: Boolean, val adOffered: Boolean) : CbModal
}

sealed interface CityBazaarMatchIntent : UiIntent {
    data class Load(val matchId: String) : CityBazaarMatchIntent
    data object Roll : CityBazaarMatchIntent
    data object Buy : CityBazaarMatchIntent
    data object Decline : CityBazaarMatchIntent
    data object ConfirmBuild : CityBazaarMatchIntent
    data object SkipBuild : CityBazaarMatchIntent
    data object PayJailFine : CityBazaarMatchIntent
    data object StayInJail : CityBazaarMatchIntent
    data class ToggleSale(val assetId: String) : CityBazaarMatchIntent
    data object ConfirmSale : CityBazaarMatchIntent
    data object StartAuction : CityBazaarMatchIntent
    data object Bid : CityBazaarMatchIntent
    data object PassAuction : CityBazaarMatchIntent
    data object DeclareBankruptcy : CityBazaarMatchIntent
    data class TileClicked(val tile: Int) : CityBazaarMatchIntent
    data class OpenStack(val group: CityGroup) : CityBazaarMatchIntent
    data object OpenMyCities : CityBazaarMatchIntent
    data object ToggleView : CityBazaarMatchIntent
    data object OpenHistory : CityBazaarMatchIntent
    data object OpenBank : CityBazaarMatchIntent
    data class VoteExtension(val keepPlaying: Boolean) : CityBazaarMatchIntent
    /** The exit dialog's pick of who takes the seat (a CPU first, then each spectator). */
    data class PickSeatTaker(val index: Int) : CityBazaarMatchIntent
    /** Settings: ask everyone to end the game. */
    data object ProposeEnd : CityBazaarMatchIntent
    data class VoteEnd(val agree: Boolean) : CityBazaarMatchIntent
    data object DismissHistory : CityBazaarMatchIntent
    data class OpenDeck(val deck: CardDeck) : CityBazaarMatchIntent
    data class PlayerClicked(val seat: Int) : CityBazaarMatchIntent
    data class PanelPropertyClicked(val tile: Int) : CityBazaarMatchIntent
    data object DismissPanel : CityBazaarMatchIntent
    data object ToggleAutoplay : CityBazaarMatchIntent
    data class ToggleMic(val permissionGranted: Boolean) : CityBazaarMatchIntent
    data class MicPermissionResult(val granted: Boolean) : CityBazaarMatchIntent
    data object OpenAppSettings : CityBazaarMatchIntent
    data class ToggleHear(val seat: Int) : CityBazaarMatchIntent
    /** The user's speaker on or off (for them only). */
    data object ToggleSpeaker : CityBazaarMatchIntent
    /** Mute or unmute a spectator for the user only. */
    data class ToggleHearSpectator(val userId: String) : CityBazaarMatchIntent
    /** Long press on a player card: add or remove them as a friend. */
    data class PlayerLongPressed(val seat: Int) : CityBazaarMatchIntent
    data object ConfirmFriendAction : CityBazaarMatchIntent
    data object DismissFriendAction : CityBazaarMatchIntent
    data object ExitClicked : CityBazaarMatchIntent
    data object ConfirmLeave : CityBazaarMatchIntent
    data object DismissModal : CityBazaarMatchIntent
    data object OpenSettings : CityBazaarMatchIntent
    data object ToggleEffects : CityBazaarMatchIntent
    data object ToggleMusic : CityBazaarMatchIntent
    data object OpenReport : CityBazaarMatchIntent
    data class SelectReportPerson(val userId: String) : CityBazaarMatchIntent
    data object SubmitReport : CityBazaarMatchIntent
    data class Vote(val agree: Boolean) : CityBazaarMatchIntent
    data object WatchAd : CityBazaarMatchIntent
    /** The table came to the front, or went away (another screen, or the app to the background). */
    data class ScreenVisible(val visible: Boolean) : CityBazaarMatchIntent
    data object Done : CityBazaarMatchIntent
    data object ShareResult : CityBazaarMatchIntent
}

sealed interface CityBazaarMatchEffect : UiEffect {
    data object Close : CityBazaarMatchEffect
    data object RequestMicPermission : CityBazaarMatchEffect
    data object OpenAppSettings : CityBazaarMatchEffect
    data class ShareText(val text: String) : CityBazaarMatchEffect
}
