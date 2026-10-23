package com.kglabs28.yapnplay.appengine.ui.screens.feedback

import com.kglabs28.yapnplay.appengine.ui.components.common.GlassPillButton
import com.kglabs28.yapnplay.appengine.ui.components.common.ModalCard
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.text.BasicText
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Edit
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.kglabs28.yapnplay.appengine.core.designsystem.Spacing
import com.kglabs28.yapnplay.appengine.core.designsystem.YapNPlayTheme
import com.kglabs28.yapnplay.appengine.core.ui.FeedbackStrings
import com.kglabs28.yapnplay.appengine.ui.components.common.BackTitleBar
import com.kglabs28.yapnplay.appengine.ui.components.common.BlockingOverlay
import com.kglabs28.yapnplay.appengine.ui.components.common.GlassIconButton
import com.kglabs28.yapnplay.appengine.ui.components.feedback.FeedbackConversationCard
import com.kglabs28.yapnplay.appengine.ui.components.feedback.FeedbackFormCard
import com.kglabs28.yapnplay.appengine.ui.components.feedback.FeedbackRow
import com.kglabs28.yapnplay.appengine.ui.components.table.arenaGlass
import com.kglabs28.yapnplay.appengine.ui.placeholders.CozyHomeBackground

/**
 * Feedback & support: the player's messages (newest activity first) on a
 * dark frosted panel so every line reads over the background; a tap opens
 * one read-only with the team's replies. The round pencil (bottom right)
 * opens the new-message form. With [startWithForm] ("Share feedback") it
 * is only the form, a thank-you once sent, then back where the player was.
 */
@Composable
fun FeedbackScreen(
    onBack: () -> Unit,
    modifier: Modifier = Modifier,
    startWithForm: Boolean = false,
    viewModel: FeedbackViewModel? = null,
) {
    val state by (viewModel?.state ?: return).collectAsState()
    val send: (FeedbackIntent) -> Unit = { viewModel.onIntent(it) }
    LaunchedEffect(viewModel) {
        if (startWithForm) viewModel.onIntent(FeedbackIntent.StartWithForm)
        viewModel.onIntent(FeedbackIntent.Load)
        viewModel.effects.collect { effect ->
            when (effect) {
                FeedbackEffect.Close -> onBack()
            }
        }
    }
    val colors = YapNPlayTheme.colors
    val dimens = YapNPlayTheme.dimens
    val typography = YapNPlayTheme.typography
    CozyHomeBackground(modifier = modifier, frosted = true) {
        // From "Share feedback": only the form over the background, no list.
        if (state.formOnly) return@CozyHomeBackground
        Box(modifier = Modifier.fillMaxSize().padding(horizontal = Spacing.Md, vertical = Spacing.Xs)) {
            Column(modifier = Modifier.fillMaxSize(), verticalArrangement = Arrangement.spacedBy(Spacing.Sm)) {
                BackTitleBar(title = FeedbackStrings.feedbackTitle, backDescription = FeedbackStrings.feedbackBack, onBack = { send(FeedbackIntent.Back) }, modifier = Modifier.fillMaxWidth())
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .weight(1f)
                        .arenaGlass(YapNPlayTheme.shapes.panel)
                        .padding(Spacing.Md),
                    verticalArrangement = Arrangement.spacedBy(Spacing.Sm),
                ) {
                    BasicText(text = FeedbackStrings.feedbackIntro, style = typography.label.copy(color = colors.TextPrimary))
                    val message = state.notice ?: state.error ?: FeedbackStrings.feedbackEmpty.takeIf { state.loaded && state.rows.isEmpty() }
                    message?.let {
                        BasicText(text = it, style = typography.label.copy(color = if (it == state.error) colors.Error else colors.Gold))
                    }
                    LazyColumn(modifier = Modifier.fillMaxWidth().weight(1f), verticalArrangement = Arrangement.spacedBy(Spacing.Xs)) {
                        items(state.rows, key = { it.ticketId }) { row ->
                            FeedbackRow(row = row, onClick = { send(FeedbackIntent.Open(row.ticketId)) })
                        }
                    }
                }
            }
            // New message: a round pencil over the list's bottom right corner.
            GlassIconButton(
                imageVector = Icons.Rounded.Edit,
                contentDescription = FeedbackStrings.feedbackNew,
                onClick = { send(FeedbackIntent.NewMessage) },
                size = dimens.fabSize,
                surface = colors.CtaGlassGreen,
                tint = colors.TextPrimary,
                glowColor = colors.CtaGlassGreenRim,
                modifier = Modifier.align(Alignment.BottomEnd).padding(Spacing.Md),
            )
        }
    }
    val form = state.form
    BlockingOverlay(visible = form != null, onDismiss = { send(FeedbackIntent.CloseForm) }) {
        if (form != null) {
            FeedbackFormCard(
                form = form,
                onPickKind = { send(FeedbackIntent.PickKind(it)) },
                onMessageChanged = { send(FeedbackIntent.MessageChanged(it)) },
                onSend = { send(FeedbackIntent.Send) },
                onConfirm = { send(FeedbackIntent.ConfirmSend) },
                onEdit = { send(FeedbackIntent.EditAgain) },
                onCancel = { send(FeedbackIntent.CloseForm) },
            )
        }
    }
    BlockingOverlay(visible = state.sent, onDismiss = { send(FeedbackIntent.Back) }) {
        ModalCard(
            title = FeedbackStrings.feedbackSentTitle,
            subtitle = FeedbackStrings.feedbackSentFormOnly,
            onClose = { send(FeedbackIntent.Back) },
            actions = { GlassPillButton(text = FeedbackStrings.feedbackOk, onClick = { send(FeedbackIntent.Back) }) },
        )
    }
    val conversation = state.conversation
    BlockingOverlay(visible = conversation != null, onDismiss = { send(FeedbackIntent.CloseConversation) }) {
        if (conversation != null) FeedbackConversationCard(conversation = conversation, onClose = { send(FeedbackIntent.CloseConversation) })
    }
}
