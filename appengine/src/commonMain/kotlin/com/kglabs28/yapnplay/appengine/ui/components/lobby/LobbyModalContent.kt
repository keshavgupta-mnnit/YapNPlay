package com.kglabs28.yapnplay.appengine.ui.components.lobby

import com.kglabs28.yapnplay.appengine.core.ui.RoomStrings
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.MeetingRoom
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.PersonOff
import androidx.compose.material.icons.rounded.PersonRemove
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material.icons.rounded.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.MemberMenuOption
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.EmptyStateText
import com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby.LobbyModal
import com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby.WaitingRequestUi

/**
 * What the lobby shows inside its blocking overlay: a member options
 * menu (and the confirmation before any member operation), the fixed
 * rule sheet, the admin's Waiting List, the leave confirmation, the match
 * countdown, or the mic-permission explanation. One place, so the screen just passes the current modal.
 */
@Composable
fun LobbyModalContent(
    modal: LobbyModal,
    gameName: String,
    fixedRules: List<String>,
    onMenuOption: (String, MemberMenuOption) -> Unit,
    onConfirmMemberAction: () -> Unit,
    onConfirmLeave: () -> Unit,
    onCancelCountdown: () -> Unit,
    onOpenSettings: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier,
    waitingList: List<WaitingRequestUi> = emptyList(),
    onApproveJoin: (String) -> Unit = {},
    onDeclineJoin: (String) -> Unit = {},
) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography

    when (modal) {
        is LobbyModal.MemberMenu -> ModalCard(
            title = modal.name,
            modifier = modifier,
            onClose = onDismiss,
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
                modal.options.forEach { option ->
                    val (label, icon) = option.labelAndIcon()
                    GlassPillButton(
                        text = label,
                        onClick = { onMenuOption(modal.userId, option) },
                        leadingIcon = icon,
                        accent = if (option.destructive) colors.Error else colors.SocialZoneAccent,
                        contentColor = if (option.destructive) colors.Error else colors.TextPrimary,
                        height = YapNPlayTheme.dimens.ctaHeight,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
        is LobbyModal.ConfirmMemberAction -> {
            val (title, body) = confirmText(modal.option, modal.name)
            ModalCard(
                title = title,
                subtitle = body,
                modifier = modifier,
                onClose = onDismiss,
                actions = {
                    DarkButton(RoomStrings.lobbyCancel, onDismiss)
                    PillButton(
                        text = RoomStrings.lobbyConfirm,
                        onClick = onConfirmMemberAction,
                        gradient = if (modal.option.destructive) {
                            listOf(colors.Error, colors.Error.copy(alpha = 0.8f))
                        } else {
                            listOf(colors.CtaGreenStart, colors.CtaGreenEnd)
                        },
                        contentColor = if (modal.option.destructive) colors.TextPrimary else colors.CtaGreenContent,
                        leadingIcon = modal.option.labelAndIcon().second,
                        trailingIcon = null,
                        textStyle = typography.sectionTitle,
                    )
                },
            )
        }
        LobbyModal.RuleSheet -> ModalCard(
            title = RoomStrings.lobbyRuleSheetTitle,
            subtitle = gameName,
            modifier = modifier,
            onClose = onDismiss,
        ) {
            BasicText(text = RoomStrings.lobbyRuleSheetSubtitle, style = typography.micro.copy(color = colors.TextSecondary))
            fixedRules.forEach { rule ->
                BasicText(text = RoomStrings.lobbyRuleBullet(rule), style = typography.label.copy(color = colors.TextPrimary))
            }
        }
        LobbyModal.WaitingList -> ModalCard(
            title = RoomStrings.lobbyWaitingList,
            subtitle = RoomStrings.lobbyWaitingListSubtitle,
            modifier = modifier,
            onClose = onDismiss,
        ) {
            if (waitingList.isEmpty()) {
                EmptyStateText(text = RoomStrings.lobbyWaitingListEmpty, modifier = Modifier.fillMaxWidth())
            } else {
                Column(verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
                    waitingList.forEach { request ->
                        WaitingRequestRow(
                            name = request.name,
                            onApprove = { onApproveJoin(request.userId) },
                            onDecline = { onDeclineJoin(request.userId) },
                            modifier = Modifier.fillMaxWidth(),
                        )
                    }
                }
            }
        }
        LobbyModal.LeaveConfirm -> ModalCard(
            title = RoomStrings.lobbyLeaveTitle,
            subtitle = RoomStrings.lobbyLeaveBody,
            modifier = modifier,
            actions = {
                DarkButton(RoomStrings.lobbyStay, onDismiss)
                PillButton(
                    text = RoomStrings.lobbyLeaveConfirm,
                    onClick = onConfirmLeave,
                    gradient = listOf(colors.Error, colors.Error.copy(alpha = 0.8f)),
                    contentColor = colors.TextPrimary,
                    leadingIcon = Icons.Rounded.MeetingRoom,
                    trailingIcon = null,
                    textStyle = typography.sectionTitle,
                )
            },
        )
        LobbyModal.MicPermissionNeeded -> ModalCard(
            title = Strings.micPermissionTitle,
            subtitle = Strings.micPermissionBody,
            modifier = modifier,
            actions = {
                DarkButton(RoomStrings.lobbyClose, onDismiss)
                PillButton(
                    text = Strings.micPermissionOpenSettings,
                    onClick = onOpenSettings,
                    gradient = listOf(colors.CtaGreenStart, colors.CtaGreenEnd),
                    contentColor = colors.CtaGreenContent,
                    leadingIcon = Icons.Rounded.Settings,
                    trailingIcon = null,
                    textStyle = typography.sectionTitle,
                )
            },
        )
        is LobbyModal.Countdown -> ModalCard(
            title = RoomStrings.lobbyMatchStarting,
            subtitle = modal.title,
            modifier = modifier,
            actions = {
                if (modal.canCancel) DarkButton(RoomStrings.lobbyCancel, onCancelCountdown)
            },
        )
    }
}

private val MemberMenuOption.destructive: Boolean
    get() = this == MemberMenuOption.REMOVE_FROM_ROOM || this == MemberMenuOption.REMOVE_FRIEND

private fun MemberMenuOption.labelAndIcon(): Pair<String, ImageVector> = when (this) {
    MemberMenuOption.MAKE_LEADER -> RoomStrings.lobbyMakeLeader to Icons.Rounded.Star
    MemberMenuOption.MAKE_ADMIN -> RoomStrings.lobbyMakeAdmin to Icons.Rounded.WorkspacePremium
    MemberMenuOption.ADD_FRIEND -> RoomStrings.lobbyAddFriend to Icons.Rounded.PersonAdd
    MemberMenuOption.REMOVE_FRIEND -> RoomStrings.lobbyRemoveFriend to Icons.Rounded.PersonRemove
    MemberMenuOption.REMOVE_FROM_ROOM -> RoomStrings.lobbyRemoveFromRoom to Icons.Rounded.PersonOff
}

/** Title and explanation for the "are you sure?" step. */
private fun confirmText(option: MemberMenuOption, name: String): Pair<String, String> = when (option) {
    MemberMenuOption.MAKE_LEADER -> RoomStrings.lobbyConfirmMakeLeader(name) to RoomStrings.lobbyConfirmMakeLeaderBody(name)
    MemberMenuOption.MAKE_ADMIN -> RoomStrings.lobbyConfirmMakeAdmin(name) to RoomStrings.lobbyConfirmMakeAdminBody(name)
    MemberMenuOption.ADD_FRIEND -> RoomStrings.lobbyConfirmAddFriend(name) to RoomStrings.lobbyConfirmAddFriendBody
    MemberMenuOption.REMOVE_FRIEND -> RoomStrings.lobbyConfirmRemoveFriend(name) to RoomStrings.lobbyConfirmRemoveFriendBody
    MemberMenuOption.REMOVE_FROM_ROOM -> RoomStrings.lobbyConfirmRemoveFromRoom(name) to RoomStrings.lobbyConfirmRemoveFromRoomBody
}

@Composable
private fun DarkButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    GlassPillButton(
        text = label,
        onClick = onClick,
        leadingIcon = Icons.Rounded.Close,
        height = YapNPlayTheme.dimens.ctaHeight,
        modifier = modifier,
    )
}
