package com.kglabs28.yapnplay.appengine.ui.components.lobby

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.MeetingRoom
import androidx.compose.material.icons.rounded.PersonAdd
import androidx.compose.material.icons.rounded.PersonRemove
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.material.icons.rounded.WorkspacePremium
import androidx.compose.material.icons.rounded.Star
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.playroomlobby.domain.MemberMenuOption
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton
import com.kglabs28.yapnplay.appengine.ui.screens.playroomlobby.LobbyModal

/**
 * What the lobby shows inside its blocking overlay: a member options
 * menu, the fixed rule sheet, the leave confirmation, the match
 * countdown, or the mic-permission explanation. One place, so the screen just passes the current modal.
 */
@Composable
fun LobbyModalContent(
    modal: LobbyModal,
    gameName: String,
    fixedRules: List<String>,
    onMenuOption: (String, MemberMenuOption) -> Unit,
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
            actions = { DarkButton(Strings.lobbyClose, onDismiss) },
        ) {
            Column(verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
                modal.options.forEach { option ->
                    val (label, icon) = when (option) {
                        MemberMenuOption.MAKE_LEADER -> Strings.lobbyMakeLeader to Icons.Rounded.Star
                        MemberMenuOption.MAKE_ADMIN -> Strings.lobbyMakeAdmin to Icons.Rounded.WorkspacePremium
                        MemberMenuOption.ADD_FRIEND -> Strings.lobbyAddFriend to Icons.Rounded.PersonAdd
                        MemberMenuOption.REMOVE_FRIEND -> Strings.lobbyRemoveFriend to Icons.Rounded.PersonRemove
                    }
                    PillButton(
                        text = label,
                        onClick = { onMenuOption(modal.userId, option) },
                        gradient = listOf(colors.RoomsSmallButton, colors.RoomsPanel),
                        contentColor = colors.TextPrimary,
                        rimColor = colors.SocialZoneAccent,
                        leadingIcon = icon,
                        trailingIcon = null,
                        textStyle = typography.sectionTitle,
                        modifier = Modifier.fillMaxWidth(),
                    )
                }
            }
        }
        LobbyModal.RuleSheet -> ModalCard(
            title = Strings.lobbyRuleSheetTitle,
            subtitle = gameName,
            modifier = modifier,
            actions = { DarkButton(Strings.lobbyClose, onDismiss) },
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

@Composable
private fun DarkButton(
    label: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    PillButton(
        text = label,
        onClick = onClick,
        gradient = listOf(colors.RoomsSmallButton, colors.RoomsPanel),
        contentColor = colors.TextPrimary,
        leadingIcon = Icons.Rounded.Close,
        trailingIcon = null,
        textStyle = YapNPlayTheme.typography.sectionTitle,
        modifier = modifier,
    )
}
