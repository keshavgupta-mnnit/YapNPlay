package com.kglabs28.yapnplay.appengine.ui.screens.playrooms

import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.RoomInvites
import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.TextUtils
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.JoinOutcome
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.CreatePlayRoomUseCase
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.GetGroupInvitesUseCase
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.RespondToGroupInviteUseCase
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoomIcon
import com.kglabs28.yapnplay.appengine.features.playrooms.domain.PlayRoomInvite
import com.kglabs28.yapnplay.appengine.features.profile.usecase.GetFriendsUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.ObserveMyProfileUseCase
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.GetPlayRoomsScreenDataUseCase
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.JoinPlayRoomByCodeUseCase
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.PreviewPlayRoomUseCase

/**
 * Rooms tab: loads rooms, online friends and group invites; owns the
 * join-code field, Create New Group and the invites list; turns taps into
 * navigation effects.
 */
class PlayRoomsViewModel(
    private val getPlayRoomsScreenData: GetPlayRoomsScreenDataUseCase,
    private val joinPlayRoomByCode: JoinPlayRoomByCodeUseCase,
    private val previewPlayRoom: PreviewPlayRoomUseCase,
    private val createPlayRoom: CreatePlayRoomUseCase,
    private val getGroupInvites: GetGroupInvitesUseCase,
    private val respondToInvite: RespondToGroupInviteUseCase,
    private val getFriends: GetFriendsUseCase,
    private val observeMyProfile: ObserveMyProfileUseCase,
    private val roomInvites: RoomInvites,
    dispatchers: DispatcherProvider,
) : MviViewModel<PlayRoomsState, PlayRoomsIntent, PlayRoomsEffect>(PlayRoomsState(), dispatchers) {

    override suspend fun handleIntent(intent: PlayRoomsIntent) {
        when (intent) {
            PlayRoomsIntent.Load -> load()
            is PlayRoomsIntent.RoomCodeChanged -> setState {
                copy(
                    roomCode = TextUtils.roomCode(intent.code, AppConstants.ROOM_CODE_LENGTH),
                    joinError = null,
                )
            }
            PlayRoomsIntent.JoinRoom -> {
                setState { copy(keypadOpen = false) }
                preview(state.value.roomCode)
            }
            PlayRoomsIntent.OpenKeypad -> setState { copy(keypadOpen = true) }
            PlayRoomsIntent.CloseKeypad -> setState { copy(keypadOpen = false) }
            PlayRoomsIntent.DismissRequestSent -> setState { copy(requestSent = null) }
            is PlayRoomsIntent.KeypadDigit -> setState {
                copy(roomCode = TextUtils.roomCode(roomCode + intent.digit, AppConstants.ROOM_CODE_LENGTH), joinError = null)
            }
            PlayRoomsIntent.KeypadBackspace -> setState { copy(roomCode = roomCode.dropLast(1), joinError = null) }
            PlayRoomsIntent.ClearCode -> setState { copy(roomCode = "", joinError = null) }
            is PlayRoomsIntent.PreviewInvite -> {
                val code = TextUtils.roomCode(intent.code, AppConstants.ROOM_CODE_LENGTH)
                setState { copy(roomCode = code, joinError = null) }
                preview(code)
            }
            PlayRoomsIntent.CreateRoom -> openCreateGroup(preselect = null)
            is PlayRoomsIntent.CreateGroupWith -> openCreateGroup(preselect = intent.userId)
            is PlayRoomsIntent.GroupNameChanged -> updateGroup { copy(name = intent.name.take(AppConstants.MAX_ROOM_NAME_LENGTH), error = null) }
            PlayRoomsIntent.CycleGroupIcon -> updateGroup { copy(icon = PlayRoomIcon.entries[(icon.ordinal + 1) % PlayRoomIcon.entries.size]) }
            is PlayRoomsIntent.ToggleGroupMember -> updateGroup {
                when {
                    intent.userId in selectedIds -> copy(selectedIds = selectedIds - intent.userId, error = null)
                    selectedIds.size + 1 >= AppConstants.MAX_MEMBERS -> copy(error = RoomStrings.groupFull(AppConstants.MAX_MEMBERS))
                    else -> copy(selectedIds = selectedIds + intent.userId, error = null)
                }
            }
            is PlayRoomsIntent.GroupSearchChanged -> updateGroup { copy(search = intent.text) }
            PlayRoomsIntent.SubmitGroup -> submitGroup()
            PlayRoomsIntent.DismissCreateGroup -> setState { copy(createGroup = null) }
            PlayRoomsIntent.ShareGroup -> state.value.groupCreated?.let { emitEffect(PlayRoomsEffect.ShareText(it.shareText)) }
            PlayRoomsIntent.OpenCreatedGroup -> state.value.groupCreated?.let {
                setState { copy(groupCreated = null) }
                emitEffect(PlayRoomsEffect.OpenRoom(it.roomId))
            }
            PlayRoomsIntent.DismissGroupCreated -> setState { copy(groupCreated = null) }
            PlayRoomsIntent.OpenInvites -> {
                setState { copy(invitesOpen = true, inviteError = null) }
                refreshInvites()
            }
            PlayRoomsIntent.DismissInvites -> setState { copy(invitesOpen = false, inviteError = null) }
            is PlayRoomsIntent.AcceptInvite -> answer(intent.inviteId, accept = true)
            is PlayRoomsIntent.DeclineInvite -> answer(intent.inviteId, accept = false)
            is PlayRoomsIntent.OpenRoom -> emitEffect(PlayRoomsEffect.OpenRoom(intent.roomId))
            PlayRoomsIntent.SeeAllRooms -> emitEffect(PlayRoomsEffect.OpenAllRooms)
        }
    }

    private suspend fun load() {
        val data = getPlayRoomsScreenData()
        setState {
            copy(rooms = data.rooms, onlineFriends = data.onlineFriends, invites = data.invites.map { it.toUi() }, loaded = true)
        }
    }

    private fun PlayRoomInvite.toUi(): GroupInviteUi =
        GroupInviteUi(inviteId, roomName, icon, RoomStrings.groupInvitedBy(invitedBy.name, memberCount), invitedBy.avatarId)

    private fun updateGroup(change: CreateGroupUi.() -> CreateGroupUi) {
        setState { copy(createGroup = createGroup?.change()) }
    }

    /** Opens Create New Group with every friend to pick from (and [preselect] already picked). */
    private suspend fun openCreateGroup(preselect: String?) {
        val me = observeMyProfile().value
        setState { copy(createGroup = CreateGroupUi(hostName = me?.name.orEmpty(), hostAvatarId = me?.avatarId.orEmpty(), selectedIds = listOfNotNull(preselect))) }
        val friends = (getFriends() as? AppResult.Success)?.data.orEmpty()
            .sortedWith(compareBy({ !it.online }, { it.name.lowercase() }))
        updateGroup { copy(friends = friends) }
    }

    private suspend fun submitGroup() {
        val form = state.value.createGroup ?: return
        if (form.creating) return
        updateGroup { copy(creating = true, error = null) }
        when (val result = createPlayRoom(form.name, form.icon, form.selectedIds)) {
            is AppResult.Success -> {
                val room = result.data
                val link = roomInvites.link(room.code)
                setState {
                    copy(
                        createGroup = null,
                        groupCreated = GroupCreatedUi(
                            roomId = room.roomId,
                            name = room.name,
                            code = room.code,
                            link = link,
                            body = RoomStrings.groupCreatedBody(room.name, form.selectedIds.size),
                            shareText = roomInvites.message(room.name, room.code),
                        ),
                    )
                }
                load()
            }
            is AppResult.Failure -> updateGroup {
                copy(
                    creating = false,
                    error = when (val e = result.error) {
                        is YapNPlayError.Validation -> when (e.field) {
                            CreatePlayRoomUseCase.FIELD_NAME_EMPTY -> RoomStrings.groupNameRequired
                            CreatePlayRoomUseCase.FIELD_MEMBERS -> RoomStrings.groupNeedsMember
                            else -> RoomStrings.groupNameTooLong(AppConstants.MAX_ROOM_NAME_LENGTH)
                        }
                        is YapNPlayError.AlreadyExists -> RoomStrings.groupExists(e.name)
                        is YapNPlayError.LimitReached -> if (e.limit == AppConstants.MAX_MEMBERS) RoomStrings.groupFull(e.limit) else errorMessage(e)
                        else -> RoomStrings.roomsCreateFailed
                    },
                )
            }
        }
    }

    private suspend fun refreshInvites() {
        val invites = (getGroupInvites() as? AppResult.Success)?.data.orEmpty()
        setState { copy(invites = invites.map { it.toUi() }) }
    }

    /** Accepting joins the group (it appears in Your Rooms); the list closes once it's empty. */
    private suspend fun answer(inviteId: String, accept: Boolean) {
        if (respondToInvite(inviteId, accept) is AppResult.Failure) {
            setState { copy(inviteError = RoomStrings.groupInviteFailed) }
            return
        }
        load()
        if (state.value.invites.isEmpty()) setState { copy(invitesOpen = false) }
    }

    /**
     * Looks the code up and asks "Join this room?". A room you already
     * belong to just opens.
     */
    private suspend fun preview(code: String) {
        if (state.value.isJoining) return
        state.value.rooms.firstOrNull { it.code == code }?.let {
            emitEffect(PlayRoomsEffect.OpenRoom(it.id))
            return
        }
        setState { copy(isJoining = true, joinError = null) }
        when (val result = previewPlayRoom(code)) {
            is AppResult.Success -> {
                val room = result.data
                // Already a member (rooms may not be loaded yet on a cold start).
                if (room.isMember) {
                    setState { copy(isJoining = false, roomCode = "") }
                    emitEffect(PlayRoomsEffect.OpenRoom(room.roomId))
                    return
                }
                // An invite link asks to join at once (the admin may still have to let them in).
                setState { copy(isJoining = false) }
                join(room.code)
            }
            is AppResult.Failure -> setState { copy(isJoining = false, joinError = errorMessage(result.error)) }
        }
    }

    private suspend fun join(code: String) {
        if (state.value.isJoining) return
        setState { copy(isJoining = true, joinError = null) }
        when (val result = joinPlayRoomByCode(code, state.value.rooms)) {
            is AppResult.Success -> when (val outcome = result.data) {
                is JoinOutcome.Joined -> {
                    val room = outcome.room
                    setState {
                        copy(
                            rooms = listOf(room) + rooms.filterNot { it.id == room.id },
                            roomCode = "",
                            isJoining = false,
                        )
                    }
                    emitEffect(PlayRoomsEffect.OpenRoom(room.id))
                }
                is JoinOutcome.PendingApproval -> setState {
                    copy(
                        roomCode = "",
                        isJoining = false,
                        requestSent = RequestSentUi(
                            title = RoomStrings.roomsRequestSentTitle,
                            body = RoomStrings.roomsRequestSentBody(outcome.roomName),
                        ),
                    )
                }
            }
            is AppResult.Failure -> setState { copy(isJoining = false, joinError = errorMessage(result.error)) }
        }
    }

    private fun errorMessage(error: YapNPlayError): String = when (error) {
        is YapNPlayError.Validation -> RoomStrings.roomsJoinInvalidCode(AppConstants.ROOM_CODE_LENGTH)
        is YapNPlayError.LimitReached -> RoomStrings.roomsJoinLimit(error.limit)
        is YapNPlayError.NotFound -> RoomStrings.roomsJoinNotFound
        else -> RoomStrings.roomsJoinFailed
    }
}
