package com.kglabs28.yapnplay.appengine.games.citybaazar.ui.screens.match

import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.ui.components.moderation.ReportPersonUi
import com.kglabs28.yapnplay.appengine.ui.components.table.TableAvatarUi
import com.kglabs28.yapnplay.appengine.games.citybaazar.domain.CityBaazarColour
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.CityGroup
import com.kglabs28.yapnplay.appengine.games.citybaazar.utils.TileKind

/** The City Baazar table. */
data class CityBaazarMatchState(
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
    val dice: CbDiceUi = CbDiceUi(),
    /** The user's pod (with controls), bottom-left; null while watching. */
    val myCard: CbPlayerCardUi? = null,
    val leftCards: List<CbPlayerCardUi> = emptyList(),
    val rightCards: List<CbPlayerCardUi> = emptyList(),
    /** The user's cities by colour, bottom centre. */
    val stacks: List<CbStackUi> = emptyList(),
    val spectating: Boolean = false,
    val canRoll: Boolean = false,
    val rollLabel: String = "",
    val autoplay: Boolean = false,
    val micOn: Boolean = false,
    /** A short balance-sheet note at the top (rent, tax, cards); goes by itself. */
    val toast: CbToastUi? = null,
    val modal: CbModal? = null,
) : UiState

/** One tile as drawn: its owner's colour and buildings (0 land … 4 hotel). */
data class CbTileUi(val index: Int, val ownerColour: CityBaazarColour?, val level: Int)

/** A player's token on [tile]. */
data class CbTokenUi(val seat: Int, val colour: CityBaazarColour, val tile: Int)

/** The two dice: [rollId] changes on every roll; tinted with the roller's [colour]. */
data class CbDiceUi(val die1: Int = 1, val die2: Int = 1, val rollId: Int = 0, val colour: CityBaazarColour? = null)

/** A player's HUD pod. [status]: "In Jail", "Away", "Bankrupt", "1st"…; [out] dims it. */
data class CbPlayerCardUi(
    val seat: Int,
    val name: String,
    val level: String,
    val colour: CityBaazarColour,
    val cash: String,
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
)

/** A balance-sheet note that shows for a moment. */
data class CbToastUi(val id: Long, val title: String, val rows: List<CbSheetRowUi>, val gain: Boolean)

/** Something the user can sell: the top building of a city, or a bare city. */
data class CbAssetUi(val id: String, val tile: Int, val name: String, val group: CityGroup?, val kind: TileKind, val action: String, val value: String)

data class CbResultRowUi(val place: String, val name: String, val colour: CityBaazarColour, val worth: String)

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

    /** A tapped tile's card. */
    data class TileInfo(val card: CbPropertyCardUi) : CbModal

    data class Result(val rows: List<CbResultRowUi>) : CbModal
    data object LeaveConfirm : CbModal
    data object MicPermissionNeeded : CbModal
    data class Settings(val effectsOn: Boolean, val musicOn: Boolean) : CbModal
    data class Report(val people: List<ReportPersonUi>, val selectedUserId: String?, val warning: String, val error: String?) : CbModal
    data class Vote(val reportId: String, val title: String, val body: String, val secondsLeft: String) : CbModal
    data class ReportOutcome(val title: String, val body: String) : CbModal
    data class Penalty(val title: String, val body: String, val watchingAd: Boolean) : CbModal
}

sealed interface CityBaazarMatchIntent : UiIntent {
    data class Load(val matchId: String) : CityBaazarMatchIntent
    data object Roll : CityBaazarMatchIntent
    data object Buy : CityBaazarMatchIntent
    data object Decline : CityBaazarMatchIntent
    data object ConfirmBuild : CityBaazarMatchIntent
    data object SkipBuild : CityBaazarMatchIntent
    data object PayJailFine : CityBaazarMatchIntent
    data object StayInJail : CityBaazarMatchIntent
    data class ToggleSale(val assetId: String) : CityBaazarMatchIntent
    data object ConfirmSale : CityBaazarMatchIntent
    data object StartAuction : CityBaazarMatchIntent
    data object Bid : CityBaazarMatchIntent
    data object PassAuction : CityBaazarMatchIntent
    data object DeclareBankruptcy : CityBaazarMatchIntent
    data class TileClicked(val tile: Int) : CityBaazarMatchIntent
    data class OpenStack(val group: CityGroup) : CityBaazarMatchIntent
    data object OpenMyCities : CityBaazarMatchIntent
    data object Trade : CityBaazarMatchIntent
    data object ToggleAutoplay : CityBaazarMatchIntent
    data class ToggleMic(val permissionGranted: Boolean) : CityBaazarMatchIntent
    data class MicPermissionResult(val granted: Boolean) : CityBaazarMatchIntent
    data object OpenAppSettings : CityBaazarMatchIntent
    data class ToggleHear(val seat: Int) : CityBaazarMatchIntent
    data object ExitClicked : CityBaazarMatchIntent
    data object ConfirmLeave : CityBaazarMatchIntent
    data object DismissModal : CityBaazarMatchIntent
    data object OpenSettings : CityBaazarMatchIntent
    data object ToggleEffects : CityBaazarMatchIntent
    data object ToggleMusic : CityBaazarMatchIntent
    data object OpenReport : CityBaazarMatchIntent
    data class SelectReportPerson(val userId: String) : CityBaazarMatchIntent
    data object SubmitReport : CityBaazarMatchIntent
    data class Vote(val agree: Boolean) : CityBaazarMatchIntent
    data object WatchAd : CityBaazarMatchIntent
    /** The table came to the front, or went away (another screen, or the app to the background). */
    data class ScreenVisible(val visible: Boolean) : CityBaazarMatchIntent
    data object Done : CityBaazarMatchIntent
}

sealed interface CityBaazarMatchEffect : UiEffect {
    data object Close : CityBaazarMatchEffect
    data object RequestMicPermission : CityBaazarMatchEffect
    data object OpenAppSettings : CityBaazarMatchEffect
}
