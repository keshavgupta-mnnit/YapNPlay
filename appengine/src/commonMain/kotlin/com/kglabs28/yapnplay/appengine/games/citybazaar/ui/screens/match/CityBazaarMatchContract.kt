package com.kglabs28.yapnplay.appengine.games.citybazaar.ui.screens.match

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
    val threeD: Boolean = false,
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
    /** A short balance-sheet note at the top (rent, tax, cards); goes by itself. */
    val toast: CbToastUi? = null,
    val modal: CbModal? = null,
    /** Info over the left column (a tapped tile or player); any touch elsewhere closes it. */
    val panel: CbPanel? = null,
) : UiState

/** One tile as drawn: its owner's colour and buildings (0 land … 4 hotel). */
data class CbTileUi(val index: Int, val ownerColour: CityBazaarColour?, val level: Int)

/** A player's token on [tile]. */
data class CbTokenUi(val seat: Int, val colour: CityBazaarColour, val tile: Int)

/** The two dice: [rollId] changes on every roll; tinted with the roller's [colour]. */
data class CbDiceUi(val die1: Int = 1, val die2: Int = 1, val rollId: Int = 0, val colour: CityBazaarColour? = null)

/** A player's HUD pod. [status]: "In Jail", "Away", "Bankrupt", "1st"…; [out] dims it. */
data class CbPlayerCardUi(
    val seat: Int,
    val name: String,
    val level: String,
    val colour: CityBazaarColour,
    /** Balance (₹); the card counts to it when it changes. */
    val cash: Int,
    val isMe: Boolean,
    val isTurn: Boolean,
    val talking: Boolean,
    val timerFraction: Float,
    val status: String?,
    val mutedForMe: Boolean,
    val out: Boolean,
)

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
    val sale: String?,
    val costs: List<CbSheetRowUi>,
    val owner: String,
    /** The owner's colour for the bottom bar; null while unowned. */
    val ownerColour: CityBazaarColour?,
)

/** The info panel over the left column: a tapped tile's card, or a tapped player. */
sealed interface CbPanel {
    data class Property(val card: CbPropertyCardUi) : CbPanel
    /** A player's balance and worth, and each of their properties with the rent it earns now. */
    data class Player(val name: String, val colour: CityBazaarColour, val sheet: List<CbSheetRowUi>, val cities: List<CbOwnedRowUi>) : CbPanel
}

/** One of a player's properties in their panel; a tap opens its card. */
data class CbOwnedRowUi(val tile: Int, val name: String, val group: CityGroup?, val kind: TileKind, val rent: String)

/** A balance-sheet note that shows for a moment. */
data class CbToastUi(val id: Long, val title: String, val rows: List<CbSheetRowUi>, val gain: Boolean)

/** Something the user can sell: the top building of a city, or a bare city. */
data class CbAssetUi(val id: String, val tile: Int, val name: String, val group: CityGroup?, val kind: TileKind, val action: String, val value: String)

data class CbResultRowUi(val place: String, val name: String, val colour: CityBazaarColour, val worth: String)

sealed interface CbModal {
    /** Turn decisions, an auction, a vote and the final ranking need an answer: a tap outside doesn't close them. */
    val needsAnswer: Boolean
        get() = when (this) {
            is Buy, is Build, is JailRoll, is JailChoice, is Debt, is Auction, is Vote, is Result -> true
            else -> false
        }

    /** Unowned tile landed on: rate card + balance sheet, No / Yes. */
    data class Buy(
        val tile: Int,
        val title: String,
        val group: CityGroup?,
        val kind: TileKind,
        val rates: List<CbRateRowUi>,
        val sheet: List<CbSheetRowUi>,
        val note: String,
        val canAfford: Boolean,
        val countdown: String,
    ) : CbModal

    /** Landed on your own city: one more house (or the hotel). */
    data class Build(
        val tile: Int,
        val title: String,
        val group: CityGroup?,
        val rates: List<CbRateRowUi>,
        val sheet: List<CbSheetRowUi>,
        val note: String,
        val canAfford: Boolean,
        val countdown: String,
    ) : CbModal

    data class JailRoll(val body: String, val countdown: String) : CbModal
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

    /** Cards of the user's cities (a colour stack, or all of them). */
    data class Properties(val title: String, val cards: List<CbPropertyCardUi>) : CbModal

    /** Luck or Shagun Chest: what each dice total (2–12) does. */
    data class CardTable(val title: String, val group: CityGroup?, val rows: List<CbRateRowUi>) : CbModal

    data class Result(val rows: List<CbResultRowUi>) : CbModal
    data object LeaveConfirm : CbModal
    data object MicPermissionNeeded : CbModal
    data class Settings(val effectsOn: Boolean, val musicOn: Boolean) : CbModal
    data class Report(val people: List<ReportPersonUi>, val selectedUserId: String?, val warning: String, val error: String?) : CbModal
    data class Vote(val reportId: String, val title: String, val body: String, val secondsLeft: String) : CbModal
    data class ReportOutcome(val title: String, val body: String) : CbModal
    data class Penalty(val title: String, val body: String, val watchingAd: Boolean) : CbModal
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
    data class OpenDeck(val deck: CardDeck) : CityBazaarMatchIntent
    data class PlayerClicked(val seat: Int) : CityBazaarMatchIntent
    data class PanelPropertyClicked(val tile: Int) : CityBazaarMatchIntent
    data object DismissPanel : CityBazaarMatchIntent
    data object Trade : CityBazaarMatchIntent
    data object ToggleAutoplay : CityBazaarMatchIntent
    data class ToggleMic(val permissionGranted: Boolean) : CityBazaarMatchIntent
    data class MicPermissionResult(val granted: Boolean) : CityBazaarMatchIntent
    data object OpenAppSettings : CityBazaarMatchIntent
    data class ToggleHear(val seat: Int) : CityBazaarMatchIntent
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
}

sealed interface CityBazaarMatchEffect : UiEffect {
    data object Close : CityBazaarMatchEffect
    data object RequestMicPermission : CityBazaarMatchEffect
    data object OpenAppSettings : CityBazaarMatchEffect
}
