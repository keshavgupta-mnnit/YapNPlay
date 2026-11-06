package com.kglabs28.yapnplay.appengine.games.cyberblast.ui.screens.match

import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.LeaveDialogUi
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatHandoverAction
import com.kglabs28.yapnplay.appengine.features.seathandover.presentation.SeatHandoverModal
import com.kglabs28.yapnplay.appengine.games.cyberblast.BlastDir
import com.kglabs28.yapnplay.appengine.ui.components.leaderboard.LeaderboardUi
import com.kglabs28.yapnplay.appengine.ui.components.moderation.EndVoteUi
import com.kglabs28.yapnplay.appengine.ui.components.table.TableAvatarUi
import com.kglabs28.yapnplay.appengine.ui.components.table.TablePlayerCardUi

data class CyberBlastMatchState(
    val loaded: Boolean = false,
    val title: String = "",
    val subtitle: String = "",
    val audience: List<TableAvatarUi> = emptyList(),
    val showAudience: Boolean = false,
    val spectatorCount: String = "",
    val elapsed: String = "",
    /** The same object all match long: the board canvas redraws itself from it. */
    val feed: CyberBoardFeed = CyberBoardFeed(),
    /** Left: the user's own card (none while watching); right: everyone else's, in seat order. */
    val leftCards: List<CyberCardUi> = emptyList(),
    val rightCards: List<CyberCardUi> = emptyList(),
    /** Over the board: "3", "2", "1", "GO!" or a warning; empty: nothing. */
    val banner: String = "",
    /** The user's seat (0: watching): their colour on the bomb button. */
    val mySeat: Int = 0,
    /** The room's Mini-map is on: the whole arena small in the board's corner. */
    val miniMap: Boolean = false,
    /** The user may steer and bomb (seated, hearts left, the match on). */
    val canPlay: Boolean = false,
    val micOn: Boolean = false,
    val speakerOn: Boolean = true,
    val spectating: Boolean = false,
    val modal: CyberModal? = null,
    val leaderboard: LeaderboardUi? = null,
) : UiState

/** A player's card: the arena seat with their hearts (in their colour) and points under the name. */
data class CyberCardUi(
    override val seat: Int,
    override val name: String,
    override val avatarId: String,
    override val level: String,
    override val isMe: Boolean,
    override val isTurn: Boolean,
    override val talking: Boolean,
    override val talkingMuted: Boolean,
    override val turnEndsAtMillis: Long?,
    override val turnTotalMs: Long,
    override val mutedForMe: Boolean,
    override val canSpeak: Boolean,
    override val micOn: Boolean,
    val lives: Int,
    val maxLives: Int,
    /** How far their blast reaches now ("3x"), at the pill's end. */
    val reach: String,
    /** Players they have hit ("4"), by their hearts. */
    val kills: String,
    /** "120 pts". */
    val score: String,
    /** Out, Away or Left; null: none. */
    val tag: String?,
    val faded: Boolean,
) : TablePlayerCardUi

sealed interface CyberModal {
    data class Settings(val effectsOn: Boolean, val musicOn: Boolean, val canProposeEnd: Boolean) : CyberModal
    data class Leave(val dialog: LeaveDialogUi) : CyberModal
    data object MicPermissionNeeded : CyberModal
    data object ConfirmEnd : CyberModal
    data class EndVote(val vote: EndVoteUi) : CyberModal
    data class Seat(val modal: SeatHandoverModal) : CyberModal
}

sealed interface CyberBlastMatchIntent : UiIntent {
    data class Load(val matchId: String) : CyberBlastMatchIntent
    data class ScreenVisible(val visible: Boolean) : CyberBlastMatchIntent
    /** The pad: pushing [dir], null: let go. */
    data class Steer(val dir: BlastDir?) : CyberBlastMatchIntent
    data object DropBomb : CyberBlastMatchIntent
    data class ToggleMic(val permissionGranted: Boolean) : CyberBlastMatchIntent
    data class MicPermissionResult(val granted: Boolean) : CyberBlastMatchIntent
    data object OpenAppSettings : CyberBlastMatchIntent
    data object ToggleSpeaker : CyberBlastMatchIntent
    data class ToggleHear(val seat: Int) : CyberBlastMatchIntent
    data class ToggleHearSpectator(val userId: String) : CyberBlastMatchIntent
    data object OpenSettings : CyberBlastMatchIntent
    data object ToggleEffects : CyberBlastMatchIntent
    data object ToggleMusic : CyberBlastMatchIntent
    data object ExitClicked : CyberBlastMatchIntent
    data object ConfirmLeave : CyberBlastMatchIntent
    data class PickSeatTaker(val index: Int) : CyberBlastMatchIntent
    data class Seat(val action: SeatHandoverAction) : CyberBlastMatchIntent
    data class PlayerLongPressed(val seat: Int) : CyberBlastMatchIntent
    data object ProposeEnd : CyberBlastMatchIntent
    data object ConfirmEnd : CyberBlastMatchIntent
    data class VoteEnd(val agree: Boolean) : CyberBlastMatchIntent
    data object DismissModal : CyberBlastMatchIntent
    data object Done : CyberBlastMatchIntent
}

sealed interface CyberBlastMatchEffect : UiEffect {
    data object Close : CyberBlastMatchEffect
    data object RequestMicPermission : CyberBlastMatchEffect
    data object OpenAppSettings : CyberBlastMatchEffect
}
