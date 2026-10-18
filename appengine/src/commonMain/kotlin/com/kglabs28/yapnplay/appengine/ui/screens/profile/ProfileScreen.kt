package com.kglabs28.yapnplay.appengine.ui.screens.profile

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.revealWhen
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.profile.AddFriendCard
import com.kglabs28.yapnplay.appengine.ui.components.profile.FriendRequestsCard
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileFriendsPanel
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileIdentityCard
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileModalActions
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileModalContent
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileTopBar
import com.kglabs28.yapnplay.appengine.ui.placeholders.CozyHomeBackground

/**
 * The user's profile (opened from the Home avatar). Left: avatar (tap to
 * change), name and @username (tap to edit), their ID (tap to copy), stats.
 * Right: friends in a sliding row (online first; "See all" for a grid; tap
 * one for their card), then "Add a friend" by ID and the friend requests.
 */
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel? = null,
) {
    val state = viewModel?.state?.collectAsState()?.value ?: ProfileState()
    val send: (ProfileIntent) -> Unit = { viewModel?.onIntent(it) }
    val clipboard = LocalClipboardManager.current
    LaunchedEffect(viewModel) {
        viewModel?.onIntent(ProfileIntent.Load)
        viewModel?.effects?.collect { effect ->
            when (effect) {
                ProfileEffect.Close -> onBack()
            }
        }
    }

    CozyHomeBackground(modifier = modifier, frosted = true) {
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(horizontal = Spacing.Md, vertical = Spacing.Xs),
            verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
        ) {
            ProfileTopBar(title = Strings.profileTitle, onBack = { send(ProfileIntent.Back) }, modifier = Modifier.fillMaxWidth())
            Row(
                modifier = Modifier.weight(1f).fillMaxWidth().revealWhen(state.loaded),
                horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
            ) {
                ProfileIdentityCard(
                    name = state.name,
                    username = state.username,
                    userCode = state.userCode,
                    avatarId = state.avatarId,
                    stats = state.stats,
                    codeCopied = state.codeCopied,
                    canLogOut = state.canLogOut,
                    onEditAvatar = { send(ProfileIntent.EditAvatar) },
                    onEditName = { send(ProfileIntent.EditName) },
                    onEditUsername = { send(ProfileIntent.EditUsername) },
                    onCopyCode = {
                        clipboard.setText(AnnotatedString(state.userCode))
                        send(ProfileIntent.CodeCopied)
                    },
                    onLogout = { send(ProfileIntent.Logout) },
                    modifier = Modifier.weight(AppConstants.PROFILE_LEFT_COLUMN_WEIGHT).fillMaxHeight(),
                )
                Column(
                    modifier = Modifier.weight(AppConstants.PROFILE_RIGHT_COLUMN_WEIGHT).fillMaxHeight(),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
                ) {
                    ProfileFriendsPanel(
                        friends = state.friends,
                        subtitle = state.friendsSubtitle,
                        loaded = state.loaded,
                        onFriendClick = { send(ProfileIntent.FriendClicked(it)) },
                        onSeeAll = { send(ProfileIntent.SeeAllFriends) },
                        modifier = Modifier.weight(AppConstants.PROFILE_FRIENDS_PANEL_WEIGHT).fillMaxWidth(),
                    )
                    Row(
                        modifier = Modifier.weight(AppConstants.PROFILE_ACTIONS_ROW_WEIGHT).fillMaxWidth(),
                        horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
                    ) {
                        AddFriendCard(
                            code = state.addCode,
                            message = state.addMessage,
                            isError = state.addError,
                            sending = state.sending,
                            onCodeChange = { send(ProfileIntent.AddCodeChanged(it)) },
                            onSend = { send(ProfileIntent.SendRequest) },
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                        )
                        FriendRequestsCard(
                            count = state.requests.size,
                            label = state.requestsLabel,
                            onView = { send(ProfileIntent.ViewRequests) },
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                        )
                    }
                }
            }
        }
    }

    val modal = state.modal
    BlockingOverlay(
        visible = modal != null,
        onDismiss = { send(ProfileIntent.DismissModal) },
    ) {
        if (modal != null) {
            ProfileModalContent(
                modal = modal,
                friends = state.friends,
                requests = state.requests,
                actions = ProfileModalActions(
                    onDismiss = { send(ProfileIntent.DismissModal) },
                    onPickAvatar = { send(ProfileIntent.PickAvatar(it)) },
                    onSaveAvatar = { send(ProfileIntent.SaveAvatar) },
                    onEditTextChanged = { send(ProfileIntent.EditTextChanged(it)) },
                    onSaveText = { send(ProfileIntent.SaveText) },
                    onFriendClick = { send(ProfileIntent.FriendClicked(it)) },
                    onRemove = { send(ProfileIntent.RemoveFriend(it)) },
                    onBlock = { send(ProfileIntent.BlockUser(it)) },
                    onConfirm = { send(ProfileIntent.ConfirmAction) },
                    onAccept = { send(ProfileIntent.AcceptRequest(it)) },
                    onDecline = { send(ProfileIntent.DeclineRequest(it)) },
                ),
            )
        }
    }
}
