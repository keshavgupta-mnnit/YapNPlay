package com.kglabs28.yapnplay.appengine.ui.screens.feedback

import com.kglabs28.yapnplay.appengine.core.ui.FeedbackStrings
import com.kglabs28.yapnplay.appengine.core.ui.Strings
import com.kglabs28.yapnplay.appengine.features.feedback.domain.FeedbackConversation
import com.kglabs28.yapnplay.appengine.features.feedback.domain.FeedbackKind
import com.kglabs28.yapnplay.appengine.features.feedback.domain.FeedbackTicket
import kotlinx.datetime.Instant
import kotlinx.datetime.TimeZone
import kotlinx.datetime.toLocalDateTime

/** Feedback data as the screen shows it. Pure. */
object FeedbackUiMapper {
    fun kind(kind: FeedbackKind): String = FeedbackStrings.feedbackKinds[kind.ordinal]

    fun row(ticket: FeedbackTicket) = FeedbackRowUi(
        ticketId = ticket.id,
        kind = kind(ticket.kind),
        preview = ticket.preview,
        date = date(ticket.updatedAt),
        answered = ticket.answered,
        status = if (ticket.answered) FeedbackStrings.feedbackAnswered else FeedbackStrings.feedbackWaiting,
    )

    fun conversation(conversation: FeedbackConversation) = FeedbackConversationUi(
        title = kind(conversation.ticket.kind),
        messages = conversation.messages.map {
            FeedbackBubbleUi(it.fromTeam, if (it.fromTeam) FeedbackStrings.feedbackTeam else FeedbackStrings.feedbackYou, it.body, date(it.createdAt))
        },
        waiting = conversation.messages.lastOrNull()?.fromTeam != true,
    )

    private fun date(millis: Long): String {
        val d = Instant.fromEpochMilliseconds(millis).toLocalDateTime(TimeZone.currentSystemDefault()).date
        return Strings.shortDate(d.dayOfMonth, d.monthNumber, d.year)
    }
}
