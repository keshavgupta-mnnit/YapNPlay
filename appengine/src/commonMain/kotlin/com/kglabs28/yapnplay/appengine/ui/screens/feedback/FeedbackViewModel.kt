package com.kglabs28.yapnplay.appengine.ui.screens.feedback

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.core.mvi.MviViewModel
import com.kglabs28.yapnplay.appengine.core.ui.FeedbackStrings
import com.kglabs28.yapnplay.appengine.features.feedback.domain.FeedbackKind
import com.kglabs28.yapnplay.appengine.features.feedback.usecase.GetFeedbackConversationUseCase
import com.kglabs28.yapnplay.appengine.features.feedback.usecase.GetMyFeedbackUseCase
import com.kglabs28.yapnplay.appengine.features.feedback.usecase.SubmitFeedbackUseCase

/**
 * Feedback & support: loads the player's messages, sends a new one (kind
 * from the dropdown + text, checked and confirmed first), and opens one
 * read-only with the team's replies.
 */
class FeedbackViewModel(
    private val submit: SubmitFeedbackUseCase,
    private val getMine: GetMyFeedbackUseCase,
    private val getConversation: GetFeedbackConversationUseCase,
    dispatchers: DispatcherProvider,
) : MviViewModel<FeedbackState, FeedbackIntent, FeedbackEffect>(FeedbackState(), dispatchers) {

    override suspend fun handleIntent(intent: FeedbackIntent) {
        when (intent) {
            FeedbackIntent.Load -> if (!state.value.formOnly) load()
            FeedbackIntent.Back -> emitEffect(FeedbackEffect.Close)
            FeedbackIntent.NewMessage -> setState { copy(form = FeedbackFormUi(), notice = null) }
            is FeedbackIntent.PickKind -> setState { copy(form = form?.copy(kindIndex = intent.index, error = null)) }
            is FeedbackIntent.MessageChanged -> setState { copy(form = form?.copy(message = intent.text, error = null)) }
            FeedbackIntent.Send -> check()
            FeedbackIntent.ConfirmSend -> send()
            FeedbackIntent.EditAgain -> setState { copy(form = form?.copy(confirming = false)) }
            FeedbackIntent.StartWithForm -> if (state.value.form == null && !state.value.sent) setState { copy(form = FeedbackFormUi(), formOnly = true) }
            FeedbackIntent.CloseForm -> if (state.value.formOnly) emitEffect(FeedbackEffect.Close) else setState { copy(form = null) }
            is FeedbackIntent.Open -> open(intent.ticketId)
            FeedbackIntent.CloseConversation -> setState { copy(conversation = null) }
        }
    }

    private suspend fun load() {
        when (val result = getMine()) {
            is AppResult.Success -> setState { copy(loaded = true, error = null, rows = result.data.map(FeedbackUiMapper::row)) }
            is AppResult.Failure -> setState { copy(loaded = true, error = FeedbackStrings.feedbackLoadFailed) }
        }
    }

    /** Both the kind and the message must be there (and not too long) before it asks to confirm. */
    private fun check() {
        val form = state.value.form ?: return
        val message = form.message.trim()
        val error = when {
            form.kindIndex == null -> FeedbackStrings.feedbackKindError
            message.isEmpty() -> FeedbackStrings.feedbackEmptyError
            message.length > AppConstants.FEEDBACK_MAX_LENGTH -> FeedbackStrings.feedbackTooLong(AppConstants.FEEDBACK_MAX_LENGTH)
            else -> null
        }
        setState { copy(form = form.copy(error = error, confirming = error == null)) }
    }

    private suspend fun send() {
        val form = state.value.form ?: return
        if (form.sending) return
        val kind = form.kindIndex?.let { FeedbackKind.entries.getOrNull(it) } ?: return
        setState { copy(form = form.copy(sending = true, error = null)) }
        when (val result = submit(kind, form.message)) {
            is AppResult.Success -> {
                setState { copy(form = null, notice = FeedbackStrings.feedbackSent, sent = formOnly) }
                if (!state.value.formOnly) load()
            }
            is AppResult.Failure -> {
                val error = when (result.error) {
                    is YapNPlayError.Validation -> if (form.message.isBlank()) FeedbackStrings.feedbackEmptyError else FeedbackStrings.feedbackTooLong(AppConstants.FEEDBACK_MAX_LENGTH)
                    is YapNPlayError.LimitReached -> FeedbackStrings.feedbackTooMany
                    else -> FeedbackStrings.feedbackSendFailed
                }
                setState { copy(form = form.copy(sending = false, confirming = false, error = error)) }
            }
        }
    }

    private suspend fun open(ticketId: String) {
        when (val result = getConversation(ticketId)) {
            is AppResult.Success -> setState { copy(conversation = FeedbackUiMapper.conversation(result.data)) }
            is AppResult.Failure -> setState { copy(error = FeedbackStrings.feedbackLoadFailed) }
        }
    }
}
