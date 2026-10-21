package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components

import com.kglabs28.yapnplay.appengine.core.ui.TableStrings
import com.kglabs28.yapnplay.appengine.core.ui.LudoStrings
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Logout
import androidx.compose.material.icons.rounded.Close
import androidx.compose.material.icons.rounded.EmojiEvents
import androidx.compose.material.icons.rounded.EventSeat
import androidx.compose.material.icons.rounded.Settings
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.components.players.tint
import com.kglabs28.yapnplay.appengine.games.royalludo.ui.screens.match.LudoMatchModal
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton
import com.kglabs28.yapnplay.appengine.ui.components.moderation.MatchSettingsCard
import com.kglabs28.yapnplay.appengine.ui.components.moderation.PenaltyCard
import com.kglabs28.yapnplay.appengine.ui.components.moderation.ReportPlayerCard
import com.kglabs28.yapnplay.appengine.ui.components.moderation.ReportVoteCard
import com.kglabs28.yapnplay.appengine.ui.components.seat.SeatPickerDialog
import com.kglabs28.yapnplay.appengine.ui.components.seat.SeatOfferPromptCard

/**
 * The table's dialogs: leave confirmation, final ranking, mic
 * permission, settings, report a player, vote on someone's report, the
 * report's outcome and a penalty.
 */
@Composable
fun LudoMatchModalContent(
    modal: LudoMatchModal,
    actions: LudoMatchModalActions,
    modifier: Modifier = Modifier,
) {
    val onDismiss = actions.onDismiss
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    val ctaHeight = YapNPlayTheme.dimens.ctaHeight

    when (modal) {
        is LudoMatchModal.LeaveConfirm -> SeatPickerDialog(
            title = LudoStrings.ludoLeaveTitle,
            message = modal.body,
            countdown = modal.countdown,
            options = modal.seatOptions,
            selectedIndex = modal.selectedIndex,
            onSelect = actions.onPickSeatTaker,
            secondaryLabel = TableStrings.seatCancel,
            onSecondary = onDismiss,
            primaryLabel = TableStrings.seatExit,
            onPrimary = actions.onConfirmLeave,
            modifier = modifier,
        )
        is LudoMatchModal.Result -> ModalCard(
            title = LudoStrings.ludoResultTitle,
            modifier = modifier,
            actions = {
                PillButton(
                    text = LudoStrings.ludoDone,
                    onClick = actions.onDone,
                    gradient = listOf(colors.CtaGreenStart, colors.CtaGreenMid, colors.CtaGreenEnd),
                    contentColor = colors.CtaGreenContent,
                    leadingIcon = Icons.Rounded.EmojiEvents,
                    trailingIcon = null,
                    textStyle = typography.sectionTitle,
                )
            },
        ) {
            modal.rows.forEach { row ->
                Row(verticalAlignment = Alignment.CenterVertically, horizontalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
                    BasicText(text = row.place, style = typography.title.copy(color = colors.Gold))
                    Box(Modifier.size(YapNPlayTheme.dimens.statusDot).background(row.colour.tint(), CircleShape))
                    BasicText(text = row.name, style = typography.title.copy(color = colors.TextPrimary))
                }
            }
        }
        LudoMatchModal.MicPermissionNeeded -> ModalCard(
            title = Strings.micPermissionTitle,
            subtitle = Strings.micPermissionBody,
            onClose = onDismiss,
            modifier = modifier,
            actions = {
                PillButton(
                    text = Strings.micPermissionOpenSettings,
                    onClick = actions.onOpenAppSettings,
                    gradient = listOf(colors.CtaGreenStart, colors.CtaGreenEnd),
                    contentColor = colors.CtaGreenContent,
                    leadingIcon = Icons.Rounded.Settings,
                    trailingIcon = null,
                    textStyle = typography.sectionTitle,
                )
            },
        )
        is LudoMatchModal.Settings -> MatchSettingsCard(
            effectsOn = modal.effectsOn,
            musicOn = modal.musicOn,
            onToggleEffects = actions.onToggleEffects,
            onToggleMusic = actions.onToggleMusic,
            onReport = actions.onOpenReport,
            onClose = onDismiss,
            modifier = modifier,
        )
        is LudoMatchModal.Report -> ReportPlayerCard(
            options = modal.people.map { LudoStrings.ludoReportOption(it.name, it.role) },
            selectedIndex = modal.people.indexOfFirst { it.userId == modal.selectedUserId }.takeIf { it >= 0 },
            warning = modal.warning,
            error = modal.error,
            onSelect = { index -> actions.onSelectReportPerson(modal.people[index].userId) },
            onSubmit = actions.onSubmitReport,
            onClose = onDismiss,
            modifier = modifier,
        )
        is LudoMatchModal.Vote -> ReportVoteCard(
            title = modal.title,
            body = modal.body,
            secondsLeft = modal.secondsLeft,
            onAgree = { actions.onVote(true) },
            onDisagree = { actions.onVote(false) },
            modifier = modifier,
        )
        is LudoMatchModal.ReportOutcome -> ModalCard(
            title = modal.title,
            subtitle = modal.body,
            onClose = onDismiss,
            modifier = modifier,
        )
        is LudoMatchModal.Penalty -> PenaltyCard(
            title = modal.title,
            body = modal.body,
            watchingAd = modal.watchingAd,
            onWatchAd = actions.onWatchAd,
            onClose = onDismiss,
            modifier = modifier,
        )
        is LudoMatchModal.SeatInvite -> SeatOfferPromptCard(
            title = modal.title,
            body = modal.body,
            acceptLabel = modal.takeLabel,
            onAccept = { actions.onTakeSeat(modal.seat) },
            onDecline = { actions.onDeclineSeat(modal.seat) },
            modifier = modifier,
        )
        is LudoMatchModal.AbsenceCheck -> SeatPickerDialog(
            title = modal.title,
            message = modal.body,
            options = modal.options,
            selectedIndex = modal.selectedIndex,
            onSelect = actions.onPickSeatTaker,
            countdown = modal.countdown,
            secondaryLabel = TableStrings.seatImHere,
            onSecondary = actions.onStillHere,
            primaryLabel = TableStrings.seatSpectate,
            onPrimary = actions.onSpectate,
            modifier = modifier,
        )
        is LudoMatchModal.SeatOfferPrompt -> SeatOfferPromptCard(
            title = modal.title,
            body = modal.body,
            acceptLabel = modal.acceptLabel,
            onAccept = { actions.onAnswerSeatOffer(true) },
            onDecline = { actions.onAnswerSeatOffer(false) },
            modifier = modifier,
        )
        is LudoMatchModal.LeaderSeatChoice -> SeatPickerDialog(
            title = modal.title,
            message = modal.body,
            options = modal.options,
            selectedIndex = modal.selectedIndex,
            onSelect = actions.onPickSeatTaker,
            countdown = modal.countdown,
            secondaryLabel = TableStrings.seatOfferToBot,
            onSecondary = actions.onOfferSeatToBot,
            primaryLabel = TableStrings.seatInviteToSeat,
            onPrimary = actions.onInviteToSeat,
            primaryEnabled = modal.selectedIndex != null,
            modifier = modifier,
        )
    }
}

