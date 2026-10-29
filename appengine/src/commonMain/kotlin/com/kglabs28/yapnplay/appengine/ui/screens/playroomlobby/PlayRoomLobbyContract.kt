package com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby

import com.kglabs28.yapnplay.appengine.ui.components.coins.LeagueUi
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState
import com.kglabs28.yapnplay.appengine.features.gamecatalog.domain.GameCatalogItem
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.MemberMenuOption
import com.kglabs28.yapnplay.appengine.features.profile.domain.Friend
import com.kglabs28.yapnplay.appengine.ui.components.games.GamePickerViewMode

/**
 * Play-room lobby. Two phases: choosing a game ([selectedGame] null:
 * carousel/grid in the middle) and game chosen (game art in the middle,
 * house rules on the right). Top bar, member list and the bottom bar are
 * the same in both. Everything is formatted here, ready to draw.
 */
data class PlayRoomLobbyState(
    val loaded: Boolean = false,
    val roomName: String = "",
    val isLeader: Boolean = false,
    val isAdmin: Boolean = false,
    val members: List<LobbyMemberUi> = emptyList(),
    val membersLabel: String = "",
    val games: List<GameCatalogItem> = emptyList(),
    val focusedIndex: Int = 0,
    val viewMode: GamePickerViewMode = GamePickerViewMode.CAROUSEL,
    val selectedGame: GameCatalogItem? = null,
    val houseRules: List<HouseRuleUi> = emptyList(),
    /** The leagues the room can play in, cheapest first (locked: someone here can't pay), and the room's own. */
    val leagues: List<LeagueUi> = emptyList(),
    val entryCoins: Int = 0,
    val fixedRules: List<String> = emptyList(),
    val primary: LobbyPrimaryButtonUi = LobbyPrimaryButtonUi(),
    val micOn: Boolean = false,
    val speakerOn: Boolean = true,
    /** People asking to join (admin only). */
    val waitingList: List<WaitingRequestUi> = emptyList(),
    /** At most one modal at a time; null when none. */
    val modal: LobbyModal? = null,
    /** Add Friends or Edit Name, from the ⋮ menu; null when neither is open. */
    val sheet: LobbySheet? = null,
) : UiState

/** The ⋮ menu's forms. */
sealed interface LobbySheet {
    /** A player's [code] and/or friends not yet in the group; [selected] get an invite on SEND. */
    data class AddFriends(
        val code: String = "",
        val friends: List<Friend> = emptyList(),
        val selected: Set<String> = emptySet(),
        /** Friends already invited and not answered yet: shown waiting, can't be picked. */
        val pending: Set<String> = emptySet(),
        val loading: Boolean = true,
        val sending: Boolean = false,
        val error: String? = null,
    ) : LobbySheet {
        /** A full code or at least one friend picked. */
        val canSend: Boolean get() = code.length == AppConstants.USER_CODE_LENGTH || selected.isNotEmpty()
    }

    /** The group's new name, being typed. */
    data class EditName(val name: String) : LobbySheet
}

/** One Waiting List entry, ready to draw. */
data class WaitingRequestUi(val userId: String, val name: String)

/** One member card, ready to draw. */
data class LobbyMemberUi(
    val userId: String,
    val name: String,
    val online: Boolean,
    val isMe: Boolean,
    val isLeader: Boolean,
    val isAdmin: Boolean,
    val talking: Boolean,
    /** Small crossed mic on the card when their mic is off. */
    val micOff: Boolean,
    val status: LobbyMemberStatus,
    val avatarId: String = "",
)

enum class LobbyMemberStatus { READY, NOT_READY, WATCHING, OFFLINE, NONE }

data class HouseRuleUi(
    val key: String,
    val title: String,
    val description: String,
    val enabled: Boolean,
)

/** The big bottom-left button: START MATCH for the leader, READY/WATCH for others. */
data class LobbyPrimaryButtonUi(
    val label: String = "",
    val sublabel: String = "",
    val enabled: Boolean = false,
    /** On-state styling (ready / watching / start possible). */
    val active: Boolean = false,
    val isStart: Boolean = false,
)

sealed interface LobbyModal {
    data class MemberMenu(val userId: String, val name: String, val options: List<MemberMenuOption>) : LobbyModal
    /** "Make Neha the leader?" before any member operation is sent. */
    data class ConfirmMemberAction(val userId: String, val name: String, val option: MemberMenuOption) : LobbyModal
    data object RuleSheet : LobbyModal
    /** The admin's Waiting List; its entries come from the state, so it stays live. */
    data object WaitingList : LobbyModal
    data object LeaveConfirm : LobbyModal
    data class Countdown(val title: String, val canCancel: Boolean) : LobbyModal
    /** Mic permission was refused: explain and offer Settings. */
    data object MicPermissionNeeded : LobbyModal
    /** The room's leaderboard (wins or coins). */
    data class Leaderboard(val board: RoomLeaderboardUi) : LobbyModal
}

sealed interface PlayRoomLobbyIntent : UiIntent {
    data class Load(val roomId: String) : PlayRoomLobbyIntent
    data class FocusGame(val index: Int) : PlayRoomLobbyIntent
    data object ToggleViewMode : PlayRoomLobbyIntent
    /** Leader picks the game at [index] (SELECT GAME or a grid tile). */
    data class SelectGame(val index: Int) : PlayRoomLobbyIntent
    data object ChangeGame : PlayRoomLobbyIntent
    data class ToggleHouseRule(val key: String) : PlayRoomLobbyIntent
    /** The leader picks the room's league (each player's entry, coins). */
    data class SelectLeague(val entryCoins: Int) : PlayRoomLobbyIntent
    data object PrimaryAction : PlayRoomLobbyIntent
    /** [permissionGranted]: whether the OS mic permission is granted right now. */
    data class ToggleMic(val permissionGranted: Boolean) : PlayRoomLobbyIntent
    data class MicPermissionResult(val granted: Boolean) : PlayRoomLobbyIntent
    data object OpenAppSettings : PlayRoomLobbyIntent
    /** The room came to the front, or went away (another screen, or the app to the background). */
    data class ScreenVisible(val visible: Boolean) : PlayRoomLobbyIntent
    data object ToggleSpeaker : PlayRoomLobbyIntent
    data class MemberClicked(val userId: String) : PlayRoomLobbyIntent
    /** Opens the confirmation for [option]; nothing is sent yet. */
    data class MenuOptionChosen(val userId: String, val option: MemberMenuOption) : PlayRoomLobbyIntent
    /** Confirmed: sends the pending member operation. */
    data object ConfirmMemberAction : PlayRoomLobbyIntent
    data object ShowRuleSheet : PlayRoomLobbyIntent
    data object LeaveClicked : PlayRoomLobbyIntent
    data object ConfirmLeave : PlayRoomLobbyIntent
    data object CancelCountdown : PlayRoomLobbyIntent
    data object DismissModal : PlayRoomLobbyIntent
    data object Back : PlayRoomLobbyIntent
    data object OpenWaitingList : PlayRoomLobbyIntent
    data class ApproveJoin(val userId: String) : PlayRoomLobbyIntent
    data class DeclineJoin(val userId: String) : PlayRoomLobbyIntent
    data object OpenLeaderboard : PlayRoomLobbyIntent
    data class SelectLeaderboardTab(val tab: RoomBoardTab) : PlayRoomLobbyIntent
    data object ShareLink : PlayRoomLobbyIntent
    data object OpenAddFriends : PlayRoomLobbyIntent
    data class AddFriendCodeChanged(val code: String) : PlayRoomLobbyIntent
    data class ToggleInvite(val userId: String) : PlayRoomLobbyIntent
    data object SendInvites : PlayRoomLobbyIntent
    data object OpenEditName : PlayRoomLobbyIntent
    data class EditNameChanged(val name: String) : PlayRoomLobbyIntent
    data object SaveName : PlayRoomLobbyIntent
    data object DismissSheet : PlayRoomLobbyIntent
}

sealed interface PlayRoomLobbyEffect : UiEffect {
    data object Close : PlayRoomLobbyEffect
    data class OpenMatch(val gameId: String, val matchId: String) : PlayRoomLobbyEffect
    /** Ask the OS for mic permission, then report MicPermissionResult. */
    data object RequestMicPermission : PlayRoomLobbyEffect
    data object OpenAppSettings : PlayRoomLobbyEffect
    /** Open the share sheet (WhatsApp etc.) with this text. */
    data class ShareText(val text: String) : PlayRoomLobbyEffect
}
