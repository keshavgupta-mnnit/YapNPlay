package com.kglabs28.yapnplay.appengine.ui.components.feedback

import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.Send
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.FeedbackStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.DropdownPicker
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import com.kglabs28.yapnplay.appengine.ui.components.common.PillButton
import com.kglabs28.yapnplay.appengine.ui.components.profile.ProfileTextField
import com.kglabs28.yapnplay.appengine.ui.screens.feedback.FeedbackFormUi

/**
 * "Write to us": what it's about (dropdown), the message and its error;
 * Cancel / Send. Once both are filled, Send asks "Send this message?"
 * with what will go: Edit / Yes, send.
 */
@Composable
fun FeedbackFormCard(
    form: FeedbackFormUi,
    onPickKind: (Int) -> Unit,
    onMessageChanged: (String) -> Unit,
    onSend: () -> Unit,
    onConfirm: () -> Unit,
    onEdit: () -> Unit,
    onCancel: () -> Unit,
    modifier: Modifier = Modifier,
) {
    val colors = YapNPlayTheme.colors
    val typography = YapNPlayTheme.typography
    if (form.confirming) {
        ModalCard(
            title = FeedbackStrings.feedbackConfirmTitle,
            onClose = onEdit,
            modifier = modifier,
        scrollable = true,
            actions = {
                GlassPillButton(text = FeedbackStrings.feedbackConfirmEdit, onClick = onEdit, leadingIcon = Icons.Rounded.Edit)
                SendButton(text = FeedbackStrings.feedbackConfirmSend, enabled = !form.sending, onClick = onConfirm)
            },
        ) {
            form.kindIndex?.let { BasicText(text = FeedbackStrings.feedbackKinds[it], style = typography.sectionTitle.copy(color = colors.Cyan, fontWeight = FontWeight.Bold)) }
            BasicText(text = form.message.trim(), style = typography.label.copy(color = colors.TextPrimary))
            form.error?.let { BasicText(text = it, style = typography.label.copy(color = colors.Error)) }
        }
        return
    }
    ModalCard(
        title = FeedbackStrings.feedbackFormTitle,
        onClose = onCancel,
        modifier = modifier,
        scrollable = true,
        actions = {
            GlassPillButton(text = FeedbackStrings.feedbackCancel, onClick = onCancel)
            SendButton(text = FeedbackStrings.feedbackSend, enabled = true, onClick = onSend)
        },
    ) {
        DropdownPicker(options = FeedbackStrings.feedbackKinds, selectedIndex = form.kindIndex, placeholder = FeedbackStrings.feedbackKindPick, onSelect = onPickKind)
        ProfileTextField(
            value = form.message,
            onValueChange = onMessageChanged,
            hint = FeedbackStrings.feedbackHint,
            maxLength = AppConstants.FEEDBACK_MAX_LENGTH,
            isError = form.error != null,
            onDone = {},
            lines = MESSAGE_LINES,
        )
        form.error?.let { BasicText(text = it, style = typography.label.copy(color = colors.Error)) }
    }
}

@Composable
private fun SendButton(text: String, enabled: Boolean, onClick: () -> Unit) {
    val colors = YapNPlayTheme.colors
    PillButton(
        text = text,
        onClick = onClick,
        enabled = enabled,
        gradient = listOf(colors.CtaGlassGreen),
        contentColor = colors.TextPrimary,
        rimColor = colors.CtaGlassGreenRim,
        glassFill = colors.CtaGlassGreen,
        leadingIcon = Icons.AutoMirrored.Rounded.Send,
        trailingIcon = null,
        height = YapNPlayTheme.dimens.smallButtonHeight,
        textStyle = YapNPlayTheme.typography.sectionTitle,
    )
}

/** How many lines of the message the field shows. */
private const val MESSAGE_LINES = 4
