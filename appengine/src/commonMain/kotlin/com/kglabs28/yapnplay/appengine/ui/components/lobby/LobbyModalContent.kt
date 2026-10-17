package com.kglabs28.yapnplay.appengine.ui.components.lobby

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
import com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby.LobbyModal

/**
 * What the lobby shows inside its blocking overlay: a member options
 * menu (and the confirmation before any member operation), the fixed
 * rule sheet, the leave confirmation, the match
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
                    DarkButton(Strings.lobbyCancel, onDismiss)
                    PillButton(
                        text = Strings.lobbyConfirm,
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
            title = Strings.lobbyRuleSheetTitle,
            subtitle = gameName,
            modifier = modifier,
            onClose = onDismiss,
        ) {
            BasicText(text = Strings.lobbyRuleSheetSubtitle, style = typography.micro.copy(color = colors.TextSecondary))
            fixedRules.forEach { rule ->
                BasicText(text = "•  $rule", style = typography.label.copy(color = colors.TextPrimary))
            }
        }
        LobbyModal.LeaveConfirm -> ModalCard(
            title = Strings.lobbyLeaveTitle,
            subtitle = Strings.lobbyLeaveBody,
            modifier = modifier,
            actions = {
                DarkButton(Strings.lobbyStay, onDismiss)
                PillButton(
                    text = Strings.lobbyLeaveConfirm,
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
                DarkButton(Strings.lobbyClose, onDismiss)
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
            title = Strings.lobbyMatchStarting,
            subtitle = modal.title,
            modifier = modifier,
            actions = {
                if (modal.canCancel) DarkButton(Strings.lobbyCancel, onCancelCountdown)
            },
        )
    }
}

private val MemberMenuOption.destructive: Boolean
    get() = this == MemberMenuOption.REMOVE_FROM_ROOM || this == MemberMenuOption.REMOVE_FRIEND

private fun MemberMenuOption.labelAndIcon(): Pair<String, ImageVector> = when (this) {
    MemberMenuOption.MAKE_LEADER -> Strings.lobbyMakeLeader to Icons.Rounded.Star
    MemberMenuOption.MAKE_ADMIN -> Strings.lobbyMakeAdmin to Icons.Rounded.WorkspacePremium
    MemberMenuOption.ADD_FRIEND -> Strings.lobbyAddFriend to Icons.Rounded.PersonAdd
    MemberMenuOption.REMOVE_FRIEND -> Strings.lobbyRemoveFriend to Icons.Rounded.PersonRemove
    MemberMenuOption.REMOVE_FROM_ROOM -> Strings.lobbyRemoveFromRoom to Icons.Rounded.PersonOff
}

/** Title and explanation for the "are you sure?" step. */
private fun confirmText(option: MemberMenuOption, name: String): Pair<String, String> = when (option) {
    MemberMenuOption.MAKE_LEADER -> Strings.lobbyConfirmMakeLeader(name) to Strings.lobbyConfirmMakeLeaderBody(name)
    MemberMenuOption.MAKE_ADMIN -> Strings.lobbyConfirmMakeAdmin(name) to Strings.lobbyConfirmMakeAdminBody(name)
    MemberMenuOption.ADD_FRIEND -> Strings.lobbyConfirmAddFriend(name) to Strings.lobbyConfirmAddFriendBody
    MemberMenuOption.REMOVE_FRIEND -> Strings.lobbyConfirmRemoveFriend(name) to Strings.lobbyConfirmRemoveFriendBody
    MemberMenuOption.REMOVE_FROM_ROOM -> Strings.lobbyConfirmRemoveFromRoom(name) to Strings.lobbyConfirmRemoveFromRoomBody
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
