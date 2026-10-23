package com.kglabs28.yapnplay.appengine.ui.screens.feedback

import com.kglabs28.yapnplay.appengine.core.mvi.UiEffect
import com.kglabs28.yapnplay.appengine.core.mvi.UiIntent
import com.kglabs28.yapnplay.appengine.core.mvi.UiState

/**
 * Feedback & support: the player's messages to the team (newest activity
 * first), the form for a new one ([form]), and one conversation opened
 * read-only ([conversation]).
 */
data class FeedbackState(
    val loaded: Boolean = false,
    val error: String? = null,
    val rows: List<FeedbackRowUi> = emptyList(),
    val form: FeedbackFormUi? = null,
    val conversation: FeedbackConversationUi? = null,
    /** "Thanks! We'll reply here." after sending. */
    val notice: String? = null,
    /** Opened from "Share feedback": just the form (no list); closing it leaves. */
    val formOnly: Boolean = false,
    /** Form only, sent: the thank-you card shows until OK. */
    val sent: Boolean = false,
) : UiState

/** One message in the list: "Report a bug", its first line, the date, and Replied / Sent. */
data class FeedbackRowUi(
    val ticketId: String,
    val kind: String,
    val preview: String,
    val date: String,
    val answered: Boolean,
    val status: String,
)

/** The new-message form: [kindIndex] in the dropdown (null: not picked yet). */
data class FeedbackFormUi(
    val kindIndex: Int? = null,
    val message: String = "",
    val error: String? = null,
    val sending: Boolean = false,
    /** Both filled: "Send this message?" is showing. */
    val confirming: Boolean = false,
)

/** A conversation, oldest first; [waiting] while the team hasn't replied yet. */
data class FeedbackConversationUi(
    val title: String,
    val messages: List<FeedbackBubbleUi>,
    val waiting: Boolean,
)

/** One message: the player's ("You") or the team's reply. */
data class FeedbackBubbleUi(
    val fromTeam: Boolean,
    val who: String,
    val body: String,
    val date: String,
)

sealed interface FeedbackIntent : UiIntent {
    data object Load : FeedbackIntent
    data object Back : FeedbackIntent
    data object NewMessage : FeedbackIntent
    data class PickKind(val index: Int) : FeedbackIntent
    data class MessageChanged(val text: String) : FeedbackIntent
    /** Checks the form, then asks to confirm. */
    data object Send : FeedbackIntent
    data object ConfirmSend : FeedbackIntent
    /** Back from the confirmation to the form. */
    data object EditAgain : FeedbackIntent
    /** The screen opened from "Share feedback": the form shows at once. */
    data object StartWithForm : FeedbackIntent
    data object CloseForm : FeedbackIntent
    data class Open(val ticketId: String) : FeedbackIntent
    data object CloseConversation : FeedbackIntent
}

sealed interface FeedbackEffect : UiEffect {
    data object Close : FeedbackEffect
}
