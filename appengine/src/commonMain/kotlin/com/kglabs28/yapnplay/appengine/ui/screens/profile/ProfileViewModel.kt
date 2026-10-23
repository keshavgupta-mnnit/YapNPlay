package com.kglabs28.yapnplay.appengine.ui.screens.profile

import com.kglabs28.yapnplay.appengine.ui.components.moderation.AppSettingsController
import com.kglabs28.yapnplay.appengine.core.ui.ProfileStrings
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.features.profile.domain.ProfileScreenData
import com.kglabs28.yapnplay.appengine.features.profile.presentation.ProfileUiMapper
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.GetMyGroupsUseCase
import com.kglabs28.yapnplay.appengine.features.playrooms.usecase.InviteFriendsToGroupUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.BlockUserUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.GetProfileScreenDataUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.RemoveFriendUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.RespondToFriendRequestUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.SendFriendRequestUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.UnblockUserUseCase
import com.kglabs28.yapnplay.appengine.features.profile.usecase.UpdateProfileUseCase
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch

/**
 * Profile screen: loads the user, friends, requests and blocks; edits the
 * avatar, name and username; adds friends by ID; accepts, declines,
 * removes, blocks and unblocks. After every friend action it reloads, so
 * the screen always shows what the server has.
 */
class ProfileViewModel(
    private val getData: GetProfileScreenDataUseCase,
    private val updateProfile: UpdateProfileUseCase,
    private val sendFriendRequest: SendFriendRequestUseCase,
    private val respondToRequest: RespondToFriendRequestUseCase,
    private val removeFriend: RemoveFriendUseCase,
    private val blockUser: BlockUserUseCase,
    private val unblockUser: UnblockUserUseCase,
    private val getMyGroups: GetMyGroupsUseCase,
    private val inviteFriendsToGroup: InviteFriendsToGroupUseCase,
    private val settingsController: AppSettingsController,
    dispatchers: DispatcherProvider,
) : MviViewModel<ProfileState, ProfileIntent, ProfileEffect>(ProfileState(), dispatchers) {

    private var data: ProfileScreenData? = null
    private var copiedJob: Job? = null

    override suspend fun handleIntent(intent: ProfileIntent) {
        val modal = state.value.modal
        when (intent) {
            ProfileIntent.Load -> reload()
            ProfileIntent.Back -> emitEffect(ProfileEffect.Close)
            ProfileIntent.OpenSettings -> setState { copy(settings = settingsController.current()) }
            ProfileIntent.CloseSettings -> setState { copy(settings = null) }
            ProfileIntent.ToggleEffects -> setState { copy(settings = settingsController.toggleEffects()) }
            ProfileIntent.ToggleMusic -> setState { copy(settings = settingsController.toggleMusic()) }
            ProfileIntent.OpenHistory -> {
                setState { copy(settings = null) }
                emitEffect(ProfileEffect.OpenHistory)
            }
            ProfileIntent.EditAvatar -> data?.let { showModal(ProfileModal.AvatarPicker(it.profile.avatarId)) }
            is ProfileIntent.PickAvatar -> (modal as? ProfileModal.AvatarPicker)?.let { showModal(it.copy(selected = intent.avatarId)) }
            ProfileIntent.SaveAvatar -> (modal as? ProfileModal.AvatarPicker)?.let { saveAvatar(it) }
            ProfileIntent.EditName -> data?.let {
                showModal(
                    ProfileModal.EditText(
                        ProfileField.NAME, ProfileStrings.profileEditNameTitle, ProfileStrings.profileNameLabel, it.profile.name,
                        ProfileStrings.profileNameHint, AppConstants.MAX_NAME_LENGTH,
                    ),
                )
            }
            ProfileIntent.EditUsername -> data?.let {
                showModal(
                    ProfileModal.EditText(
                        ProfileField.USERNAME, ProfileStrings.profileEditUsernameTitle, ProfileStrings.profileUsernameLabel, it.profile.username,
                        ProfileStrings.profileUsernameHint, AppConstants.MAX_USERNAME_LENGTH,
                    ),
                )
            }
            is ProfileIntent.EditTextChanged -> (modal as? ProfileModal.EditText)?.let { showModal(it.copy(value = intent.text, error = null)) }
            ProfileIntent.SaveText -> (modal as? ProfileModal.EditText)?.let { saveText(it) }
            ProfileIntent.CodeCopied -> showCopied()
            is ProfileIntent.SelectFriendsTab -> setState { copy(friendsTab = intent.tab, tabFriends = ProfileUiMapper.filter(friends, intent.tab)) }
            is ProfileIntent.SelectAllFriendsTab -> setState { copy(allFriendsTab = intent.tab, allTabFriends = ProfileUiMapper.filter(friends, intent.tab)) }
            is ProfileIntent.FriendClicked -> friend(intent.userId)?.let { showModal(ProfileModal.Person(it)) }
            ProfileIntent.SeeAllFriends -> showModal(ProfileModal.AllFriends)
            is ProfileIntent.RemoveFriend -> friend(intent.userId)?.let { showModal(ProfileModal.ConfirmRemove(it)) }
            is ProfileIntent.BlockUser -> friend(intent.userId)?.let { showModal(ProfileModal.ConfirmBlock(it)) }
            is ProfileIntent.InviteToGroup -> friend(intent.userId)?.let { openInvite(it) }
            is ProfileIntent.InviteToRoom -> (modal as? ProfileModal.InviteToGroup)?.let { invite(it, intent.roomId) }
            ProfileIntent.NewGroupWithFriend -> (modal as? ProfileModal.InviteToGroup)?.let {
                showModal(null)
                emitEffect(ProfileEffect.OpenCreateGroup(it.person.userId))
            }
            ProfileIntent.ConfirmAction -> confirm(modal)
            ProfileIntent.OpenKeyboard -> setState { copy(keyboardOpen = true) }
            ProfileIntent.CloseKeyboard -> setState { copy(keyboardOpen = false) }
            is ProfileIntent.KeyTyped -> setState { copy(addCode = ProfileUiMapper.cleanCode(addCode + intent.key), addMessage = null, addError = false) }
            ProfileIntent.KeyBackspace -> setState { copy(addCode = addCode.dropLast(1), addMessage = null, addError = false) }
            ProfileIntent.ClearCode -> setState { copy(addCode = "", addMessage = null, addError = false) }
            ProfileIntent.SendRequest -> send()
            ProfileIntent.ViewRequests -> if (state.value.requests.isNotEmpty()) showModal(ProfileModal.Requests)
            is ProfileIntent.OpenRequest -> state.value.requests.firstOrNull { it.requestId == intent.requestId }?.let { showModal(ProfileModal.RequestProfile(it)) }
            is ProfileIntent.AcceptRequest -> accept(intent.requestId)
            is ProfileIntent.DeclineRequest -> decline(intent.requestId)
            ProfileIntent.ViewBlocked -> if (state.value.blocked.isNotEmpty()) showModal(ProfileModal.BlockList)
            is ProfileIntent.Unblock -> state.value.blocked.firstOrNull { it.userId == intent.userId }?.let { showModal(ProfileModal.ConfirmUnblock(it)) }
            // Cancelling an unblock goes back to the list.
            ProfileIntent.DismissModal -> showModal(
                when (modal) {
                    is ProfileModal.ConfirmUnblock -> ProfileModal.BlockList
                    // Closing Invite to Group goes back to the friend's card.
                    is ProfileModal.InviteToGroup -> ProfileModal.Person(modal.person)
                    else -> null
                },
            )
        }
    }

    private suspend fun reload() {
        val result = getData() as? AppResult.Success ?: return
        data = result.data
        setState { ProfileUiMapper.state(this, result.data) }
    }

    private fun friend(userId: String): ProfilePersonUi? = data?.friends?.firstOrNull { it.userId == userId }?.let { ProfileUiMapper.person(it) }

    private suspend fun saveAvatar(modal: ProfileModal.AvatarPicker) {
        showModal(modal.copy(saving = true))
        if (updateProfile(avatarId = modal.selected) is AppResult.Success) {
            showModal(null)
            reload()
        } else {
            showModal(modal)
        }
    }

    private suspend fun saveText(modal: ProfileModal.EditText) {
        showModal(modal.copy(saving = true))
        val result = when (modal.field) {
            ProfileField.NAME -> updateProfile(name = modal.value)
            ProfileField.USERNAME -> updateProfile(username = modal.value)
        }
        when (result) {
            is AppResult.Success -> {
                showModal(null)
                reload()
            }
            is AppResult.Failure -> {
                val error = when {
                    result.error !is YapNPlayError.Validation -> ProfileStrings.profileNetworkError
                    modal.field == ProfileField.NAME -> ProfileStrings.profileNameError(AppConstants.MAX_NAME_LENGTH)
                    else -> ProfileStrings.profileUsernameError(AppConstants.MIN_USERNAME_LENGTH, AppConstants.MAX_USERNAME_LENGTH)
                }
                showModal(modal.copy(error = error))
            }
        }
    }

    private suspend fun send() {
        val code = state.value.addCode
        if (state.value.sending) return
        setState { copy(sending = true, keyboardOpen = false) }
        when (val result = sendFriendRequest(code)) {
            is AppResult.Success -> {
                val (message, error) = ProfileUiMapper.outcome(result.data, code)
                setState { copy(sending = false, addMessage = message, addError = error, addCode = if (error) addCode else "") }
                reload()
            }
            is AppResult.Failure -> {
                val message = if (result.error is YapNPlayError.Validation) ProfileStrings.profileInvalidCode(AppConstants.USER_CODE_LENGTH) else ProfileStrings.profileNetworkError
                setState { copy(sending = false, addMessage = message, addError = true) }
            }
        }
    }

    /** Accepting shows "Friend Request Accepted"; Great! closes it. */
    private suspend fun accept(requestId: String) {
        val name = state.value.requests.firstOrNull { it.requestId == requestId }?.person?.name ?: return
        if (respondToRequest(requestId, accept = true) is AppResult.Success) showModal(ProfileModal.RequestAccepted(name))
        reload()
    }

    /** Declining keeps the list open while others are waiting. */
    private suspend fun decline(requestId: String) {
        respondToRequest(requestId, accept = false)
        reload()
        showModal(if (state.value.requests.isEmpty()) null else ProfileModal.Requests)
    }

    private suspend fun confirm(modal: ProfileModal?) {
        when (modal) {
            is ProfileModal.ConfirmRemove -> removeFriend(modal.person.userId)
            is ProfileModal.ConfirmBlock -> blockUser(modal.person.userId)
            is ProfileModal.ConfirmUnblock -> unblockUser(modal.user.userId)
            else -> Unit
        }
        showModal(if (modal is ProfileModal.ConfirmUnblock) ProfileModal.BlockList else null)
        reload()
    }

    private fun showCopied() {
        copiedJob?.cancel()
        setState { copy(codeCopied = true) }
        copiedJob = scope.launch {
            delay(AppConstants.PROFILE_COPIED_MS)
            setState { copy(codeCopied = false) }
        }
    }

    private suspend fun openInvite(person: ProfilePersonUi) {
        showModal(ProfileModal.InviteToGroup(person))
        val groups = (getMyGroups() as? AppResult.Success)?.data.orEmpty()
        val current = state.value.modal as? ProfileModal.InviteToGroup ?: return
        showModal(current.copy(loading = false, groups = groups.map { InviteGroupUi(it.id, it.name, it.icon, ProfileStrings.profileGroupMembers(it.memberCount)) }))
    }

    private suspend fun invite(modal: ProfileModal.InviteToGroup, roomId: String) {
        val group = modal.groups.firstOrNull { it.roomId == roomId } ?: return
        if (group.invited || group.sending) return
        fun mark(change: InviteGroupUi.() -> InviteGroupUi, error: String? = null) {
            val now = state.value.modal as? ProfileModal.InviteToGroup ?: return
            showModal(now.copy(error = error, groups = now.groups.map { if (it.roomId == roomId) it.change() else it }))
        }
        mark({ copy(sending = true) })
        when (val result = inviteFriendsToGroup(roomId, listOf(modal.person.userId))) {
            // Skipped (already a member or invited) still reads as Invited.
            is AppResult.Success -> mark({ copy(sending = false, invited = true) })
            is AppResult.Failure -> mark({ copy(sending = false) }, ProfileStrings.profileInviteFailed)
        }
    }

    private fun showModal(modal: ProfileModal?) {
        setState { copy(modal = modal) }
    }
}
