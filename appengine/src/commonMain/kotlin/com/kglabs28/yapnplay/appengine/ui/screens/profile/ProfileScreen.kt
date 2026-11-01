package com.kglabs28.yapnplay.appengine.ui.screens.profile

import androidx.compose.ui.Alignment
import com.kglabs28.yapnplay.appengine.ui.components.ads.AdPill
import com.kglabs28.yapnplay.appengine.ui.components.common.rememberShareSheet
import com.kglabs28.yapnplay.appengine.ui.components.common.rememberExitApp
import com.kglabs28.yapnplay.appengine.ui.components.common.ExitAppCard
import com.kglabs28.yapnplay.appengine.ui.components.moderation.AppSettingsDialog
import androidx.compose.foundation.gestures.awaitEachGesture
import androidx.compose.foundation.gestures.awaitFirstDown
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.platform.LocalClipboardManager
import androidx.compose.ui.text.AnnotatedString
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.designsystem.glassPanel
import com.kglabs28.yapnplay.appengine.core.designsystem.revealWhen
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockBackPress
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.profile.AddFriendCard
import com.kglabs28.yapnplay.appengine.ui.components.profile.BlockedUsersCard
import com.kglabs28.yapnplay.appengine.ui.components.profile.FriendCodeKeyboard
import com.kglabs28.yapnplay.appengine.ui.components.profile.FriendRequestsCard
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileFriendsSection
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileIdentityCard
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileModalActions
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileModalContent
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileTopBar
import com.kglabs28.yapnplay.appengine.ui.placeholders.CozyHomeBackground

/**
 * The user's profile (from the Home avatar), as in the design: top bar
 * (back, logo, settings, log out); who they are (avatar with Change
 * Avatar and name to edit, YapNPlay ID to copy, Player Stats
 * with their level); My Friends across the full width (Online / Offline,
 * slider, See All); then Add Friend, Friend Requests and Blocked Users.
 * Fits one screen. Typing an ID: the in-app keyboard takes the place of
 * Friend Requests and Blocked Users.
 */
@Composable
fun ProfileScreen(
    onBack: () -> Unit,
    onOpenHistory: () -> Unit = {},
    onOpenFeedback: (compose: Boolean) -> Unit = {},
    modifier: Modifier = Modifier,
    viewModel: ProfileViewModel? = null,
    onCreateGroupWith: (userId: String) -> Unit = {},
    /** A friend request push was tapped: open the requests. */
    showRequests: Boolean = false,
    onRequestsShown: () -> Unit = {},
) {
    val state = viewModel?.state?.collectAsState()?.value ?: ProfileState()
    val send: (ProfileIntent) -> Unit = { viewModel?.onIntent(it) }
    // The view model reloads first, so a request that just came in is in the list.
    LaunchedEffect(showRequests, viewModel) {
        if (showRequests && viewModel != null) {
            send(ProfileIntent.ShowRequestsFromPush)
            onRequestsShown()
        }
    }
    val clipboard = LocalClipboardManager.current
    val share = rememberShareSheet()
    LaunchedEffect(viewModel) {
        viewModel?.onIntent(ProfileIntent.Load)
        viewModel?.effects?.collect { effect ->
            when (effect) {
                ProfileEffect.Close -> onBack()
                ProfileEffect.OpenHistory -> onOpenHistory()
                is ProfileEffect.ShareText -> share(effect.text)
                is ProfileEffect.OpenFeedback -> onOpenFeedback(effect.compose)
                is ProfileEffect.OpenCreateGroup -> onCreateGroupWith(effect.userId)
            }
        }
    }

    // The ID keyboard: a tap outside it (and the Add Friend card) or Back closes it.
    val keyboardOpen = state.keyboardOpen
    val scroll = rememberScrollState()
    BlockBackPress(enabled = keyboardOpen, onBack = { send(ProfileIntent.CloseKeyboard) })
    // Opening the keyboard scrolls it fully into view.
    LaunchedEffect(keyboardOpen) { if (keyboardOpen) scroll.animateScrollTo(scroll.maxValue) }

    CozyHomeBackground(
        modifier = modifier.pointerInput(keyboardOpen) {
            if (!keyboardOpen) return@pointerInput
            awaitEachGesture {
                val down = awaitFirstDown(requireUnconsumed = false)
                if (!down.isConsumed) send(ProfileIntent.CloseKeyboard)
            }
        },
    ) {
        // The card starts at the top, the same gap from the top as from the sides; the ad at the bottom centre.
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(Spacing.Md),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
        ) {
            Column(
                modifier = Modifier
                    .weight(1f)
                    .fillMaxWidth()
                    // Sized to fit one screen; scrolls only on very short screens.
                    .verticalScroll(scroll)
                    .revealWhen(state.loaded),
                verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
            ) {
                // One shell holds the top bar, who the user is and their friends.
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .glassPanel(glowColor = YapNPlayTheme.colors.SocialZoneAccent, surface = YapNPlayTheme.colors.ProfileShell)
                        .padding(Spacing.Xs),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Xs),
                ) {
                    ProfileTopBar(
                        onBack = { send(ProfileIntent.Back) },
                        onSettings = { send(ProfileIntent.OpenSettings) },
                        onExit = { send(ProfileIntent.AskExit) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    ProfileIdentityCard(
                        name = state.name,
                        userCode = state.userCode,
                        avatarId = state.avatarId,
                        stats = state.stats,
                        codeCopied = state.codeCopied,
                        onChangeAvatar = { send(ProfileIntent.EditAvatar) },
                        onEditName = { send(ProfileIntent.EditName) },
                        onOpenHistory = onOpenHistory,
                        onCopyCode = {
                            clipboard.setText(AnnotatedString(state.userCode))
                            send(ProfileIntent.CodeCopied)
                        },
                        onShareCode = { send(ProfileIntent.ShareCode) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                    // No friends yet: the section is hidden.
                    if (state.friends.isNotEmpty()) ProfileFriendsSection(
                        friends = state.tabFriends,
                        hasFriends = state.friends.isNotEmpty(),
                        tab = state.friendsTab,
                        onlineCount = state.onlineCount,
                        offlineCount = state.offlineCount,
                        loaded = state.loaded,
                        onSelectTab = { send(ProfileIntent.SelectFriendsTab(it)) },
                        onFriendClick = { send(ProfileIntent.FriendClicked(it)) },
                        onSeeAll = { send(ProfileIntent.SeeAllFriends) },
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
                // The three cards share one height (the tallest).
                Row(
                    modifier = Modifier.fillMaxWidth().height(IntrinsicSize.Min),
                    horizontalArrangement = Arrangement.spacedBy(Spacing.Sm),
                ) {
                    AddFriendCard(
                        code = state.addCode,
                        message = state.addMessage,
                        isError = state.addError,
                        sending = state.sending,
                        keyboardOpen = keyboardOpen,
                        onOpenKeyboard = { send(ProfileIntent.OpenKeyboard) },
                        onSend = { send(ProfileIntent.SendRequest) },
                        onClearCode = { send(ProfileIntent.ClearCode) },
                        modifier = Modifier.weight(ADD_CARD_WEIGHT).fillMaxHeight().keepsKeyboardOpen(),
                    )
                    if (keyboardOpen) {
                        // The keyboard takes the place of Friend Requests and Blocked Users.
                        FriendCodeKeyboard(
                            onKey = { send(ProfileIntent.KeyTyped(it)) },
                            onBackspace = { send(ProfileIntent.KeyBackspace) },
                            onAdd = { send(ProfileIntent.SendRequest) },
                            onClose = { send(ProfileIntent.CloseKeyboard) },
                            addEnabled = state.addCode.length == AppConstants.USER_CODE_LENGTH && !state.sending,
                            modifier = Modifier.weight(KEYBOARD_WEIGHT).height(YapNPlayTheme.dimens.friendKeyboardHeight).keepsKeyboardOpen(),
                        )
                    } else {
                        FriendRequestsCard(
                            count = state.requests.size,
                            label = state.requestsLabel,
                            onView = { send(ProfileIntent.ViewRequests) },
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                        )
                        BlockedUsersCard(
                            count = state.blocked.size,
                            label = state.blockedLabel,
                            onSeeList = { send(ProfileIntent.ViewBlocked) },
                            modifier = Modifier.weight(1f).fillMaxHeight(),
                        )
                    }
                }
            }
            AdPill(reserveSpace = true)
        }
    }

    val modal = state.modal
    val exitApp = rememberExitApp()
    BlockingOverlay(visible = state.exitAsked, onDismiss = { send(ProfileIntent.DismissExit) }) {
        ExitAppCard(onExit = exitApp, onStay = { send(ProfileIntent.DismissExit) })
    }
    AppSettingsDialog(
        settings = state.settings,
        onToggleEffects = { send(ProfileIntent.ToggleEffects) },
        onToggleMusic = { send(ProfileIntent.ToggleMusic) },
        onToggleKeyboard = { send(ProfileIntent.ToggleKeyboard) },
        onHistory = { send(ProfileIntent.OpenHistory) },
        onFeedback = { send(ProfileIntent.OpenFeedback(it)) },
        onClose = { send(ProfileIntent.CloseSettings) },
    )
    BlockingOverlay(
        visible = modal != null,
        onDismiss = { send(ProfileIntent.DismissModal) },
    ) {
        if (modal != null) {
            ProfileModalContent(
                modal = modal,
                state = state,
                actions = ProfileModalActions(
                    onDismiss = { send(ProfileIntent.DismissModal) },
                    onPickAvatar = { send(ProfileIntent.PickAvatar(it)) },
                    onSaveAvatar = { send(ProfileIntent.SaveAvatar) },
                    onEditTextChanged = { send(ProfileIntent.EditTextChanged(it)) },
                    onSaveText = { send(ProfileIntent.SaveText) },
                    onSelectAllFriendsTab = { send(ProfileIntent.SelectAllFriendsTab(it)) },
                    onFriendClick = { send(ProfileIntent.FriendClicked(it)) },
                    onRemove = { send(ProfileIntent.RemoveFriend(it)) },
                    onBlock = { send(ProfileIntent.BlockUser(it)) },
                    onConfirm = { send(ProfileIntent.ConfirmAction) },
                    onOpenRequest = { send(ProfileIntent.OpenRequest(it)) },
                    onAccept = { send(ProfileIntent.AcceptRequest(it)) },
                    onDecline = { send(ProfileIntent.DeclineRequest(it)) },
                    onUnblock = { send(ProfileIntent.Unblock(it)) },
                    onCopyCode = { clipboard.setText(AnnotatedString(it)) },
                    onInviteToGroup = { send(ProfileIntent.InviteToGroup(it)) },
                    onInviteToRoom = { send(ProfileIntent.InviteToRoom(it)) },
                    onNewGroup = { send(ProfileIntent.NewGroupWithFriend) },
                ),
            )
        }
    }
}

private const val ADD_CARD_WEIGHT = 1.3f
private const val KEYBOARD_WEIGHT = 2f

/** Touches here don't count as "outside", so the keyboard stays open. */
private fun Modifier.keepsKeyboardOpen(): Modifier = pointerInput(Unit) {
    awaitEachGesture { awaitFirstDown(requireUnconsumed = false).consume() }
}
