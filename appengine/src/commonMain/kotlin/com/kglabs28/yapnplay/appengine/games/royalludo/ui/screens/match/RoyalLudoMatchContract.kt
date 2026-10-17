package com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match

import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.games.royalludo.domain.LudoColour

/**
 * Royal Ludo table. The board is turned so the user's base is
 * bottom-left; each player card sits in its colour's corner. Pieces move
 * one event at a time so hops and captures can animate. Everything is
 * formatted here, ready to draw.
 */
data class RoyalLudoMatchState(
    val loaded: Boolean = false,
    val title: String = "",
    val subtitle: String = "",
    /** Quarter turns that bring the user's base to the bottom-left. */
    val rotation: Int = 0,
    val pieces: List<LudoPieceUi> = emptyList(),
    /** Each player's card in their colour's corner. */
    val cards: Map<BoardCorner, LudoPlayerCardUi> = emptyMap(),
    /** Top bar: the people watching (players are on the cards). */
    val audience: List<LudoAvatarUi> = emptyList(),
    val spectatorCount: String = "",
    val spectatorLabel: String = "",
    val pingLabel: String = "",
    val pingQuality: PingQuality = PingQuality.GOOD,
    val dice: LudoDiceUi = LudoDiceUi(),
    val canRoll: Boolean = false,
    val autoplay: Boolean = false,
    val micOn: Boolean = false,
    val modal: LudoMatchModal? = null,
) : UiState

/** Connection quality behind the ping colour (good, fair, poor). */
enum class PingQuality { GOOD, FAIR, POOR }

/** How a piece reaches its new square: hop per square, or glide (capture). */
enum class PieceMotion { HOP, SLIDE }

/** One piece on the board ([progress] as in the engine). */
data class LudoPieceUi(
    val id: String,
    val seat: Int,
    val piece: Int,
    val colour: LudoColour,
    val progress: Int,
    val motion: PieceMotion,
    /** The user may move this piece now. */
    val selectable: Boolean,
)

enum class BoardCorner { TOP_LEFT, TOP_RIGHT, BOTTOM_RIGHT, BOTTOM_LEFT }

/** A player card in its corner. */
data class LudoPlayerCardUi(
    val seat: Int,
    val name: String,
    /** Level number, shown under a "Level" label. */
    val level: String,
    val colour: LudoColour,
    val corner: BoardCorner,
    val isMe: Boolean,
    val isTurn: Boolean,
    val talking: Boolean,
    /** 1 → 0 while it is this player's turn. */
    val timerFraction: Float,
    val left: Boolean,
)

/** Avatar ring in the top bar: talking, mic off, or neither. */
enum class AvatarRing { TALKING, MUTED, IDLE }

data class LudoAvatarUi(val userId: String, val name: String, val ring: AvatarRing)

/** The die: [rollId] changes on every roll so the screen animates it once. */
data class LudoDiceUi(val value: Int = 1, val rollId: Int = 0)

sealed interface LudoMatchModal {
    data object LeaveConfirm : LudoMatchModal
    data class Result(val rows: List<LudoResultRowUi>) : LudoMatchModal
    /** Mic permission was refused: explain and offer Settings. */
    data object MicPermissionNeeded : LudoMatchModal
}

data class LudoResultRowUi(val place: String, val name: String, val colour: LudoColour)

sealed interface RoyalLudoMatchIntent : UiIntent {
    data class Load(val matchId: String) : RoyalLudoMatchIntent
    data object Roll : RoyalLudoMatchIntent
    data class PieceClicked(val seat: Int, val piece: Int) : RoyalLudoMatchIntent
    data object ToggleAutoplay : RoyalLudoMatchIntent
    /** [permissionGranted]: whether the OS mic permission is granted right now. */
    data class ToggleMic(val permissionGranted: Boolean) : RoyalLudoMatchIntent
    data class MicPermissionResult(val granted: Boolean) : RoyalLudoMatchIntent
    data object OpenAppSettings : RoyalLudoMatchIntent
    data object ExitClicked : RoyalLudoMatchIntent
    data object ConfirmLeave : RoyalLudoMatchIntent
    data object DismissModal : RoyalLudoMatchIntent
    data object OpenSettings : RoyalLudoMatchIntent
    data object Done : RoyalLudoMatchIntent
}

sealed interface RoyalLudoMatchEffect : UiEffect {
    data object Close : RoyalLudoMatchEffect
    data object RequestMicPermission : RoyalLudoMatchEffect
    data object OpenAppSettings : RoyalLudoMatchEffect
}
