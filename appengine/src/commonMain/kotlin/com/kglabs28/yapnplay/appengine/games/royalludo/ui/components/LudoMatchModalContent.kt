package com.kglabs28.yapnplay.appengine.games.royalludo.ui.components

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

/** The table's dialogs: leave confirmation, final ranking, mic permission. */
@Composable
fun LudoMatchModalContent(
    modal: LudoMatchModal,
    onConfirmLeave: () -> Unit,
    onDismiss: () -> Unit,
    onDone: () -> Unit,
    onOpenSettings: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    val ctaHeight = YapNPlayTheme.dimens.ctaHeight

    when (modal) {
        LudoMatchModal.LeaveConfirm -> ModalCard(
            title = Strings.ludoLeaveTitle,
            subtitle = Strings.ludoLeaveBody,
            onClose = onDismiss,
            modifier = modifier,
            actions = {
                GlassPillButton(text = Strings.ludoStay, onClick = onDismiss, leadingIcon = Icons.Rounded.Close, height = ctaHeight)
                PillButton(
                    text = Strings.ludoLeaveConfirm,
                    onClick = onConfirmLeave,
                    gradient = listOf(colors.Error, colors.Error.copy(alpha = LEAVE_GRADIENT_END)),
                    contentColor = colors.TextPrimary,
                    leadingIcon = Icons.AutoMirrored.Rounded.Logout,
                    trailingIcon = null,
                    textStyle = typography.sectionTitle,
                )
            },
        )
        is LudoMatchModal.Result -> ModalCard(
            title = Strings.ludoResultTitle,
            modifier = modifier,
            actions = {
                PillButton(
                    text = Strings.ludoDone,
                    onClick = onDone,
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
                    onClick = onOpenSettings,
                    gradient = listOf(colors.CtaGreenStart, colors.CtaGreenEnd),
                    contentColor = colors.CtaGreenContent,
                    leadingIcon = Icons.Rounded.Settings,
                    trailingIcon = null,
                    textStyle = typography.sectionTitle,
                )
            },
        )
    }
}

private const val LEAVE_GRADIENT_END = 0.8f
