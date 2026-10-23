package com.kglabs28.yapnplay.appengine.features.feedback.data

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.core.network.BackendPaths
import com.kglabs28.yapnplay.appengine.features.feedback.domain.FeedbackConversation
import com.kglabs28.yapnplay.appengine.features.feedback.domain.FeedbackKind
import com.kglabs28.yapnplay.appengine.features.feedback.domain.FeedbackMessage
import com.kglabs28.yapnplay.appengine.features.feedback.domain.FeedbackRepository
import com.kglabs28.yapnplay.appengine.features.feedback.domain.FeedbackTicket
import com.kglabs28.yapnplay.appengine.protocol.FeedbackThread
import com.kglabs28.yapnplay.appengine.protocol.FeedbackTicketSummary
import com.kglabs28.yapnplay.appengine.protocol.FeedbackTickets
import com.kglabs28.yapnplay.appengine.protocol.SubmitFeedbackBody
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.request.get
import io.ktor.client.request.post
import io.ktor.client.request.setBody
import io.ktor.http.ContentType
import io.ktor.http.HttpStatusCode
import io.ktor.http.contentType
import kotlinx.coroutines.withContext

/** `POST /feedback`, `GET /feedback` and `GET /feedback/{id}` as [FeedbackTicket]s and conversations. */
class FeedbackRepositoryImpl(
    private val client: HttpClient,
    private val dispatchers: DispatcherProvider,
) : FeedbackRepository {

    override suspend fun submit(kind: FeedbackKind, message: String): AppResult<FeedbackTicket> = call("submit") {
        client.post(BackendPaths.FEEDBACK) {
            contentType(ContentType.Application.Json)
            setBody(SubmitFeedbackBody(kind.name, message))
        }.body<FeedbackTicketSummary>().toDomain()
    }

    override suspend fun mine(): AppResult<List<FeedbackTicket>> = call("mine") {
        client.get(BackendPaths.FEEDBACK).body<FeedbackTickets>().tickets.map { it.toDomain() }
    }

    override suspend fun conversation(ticketId: String): AppResult<FeedbackConversation> = call("conversation") {
        val thread = client.get(BackendPaths.feedbackTicket(ticketId)).body<FeedbackThread>()
        FeedbackConversation(thread.ticket.toDomain(), thread.messages.map { FeedbackMessage(it.fromAdmin, it.body, it.createdAt) })
    }

    /** The server's daily limit comes back as its own error, so the form can say so. */
    private suspend fun <T> call(name: String, block: suspend () -> T): AppResult<T> = withContext(dispatchers.io) {
        try {
            AppResult.Success(block())
        } catch (e: ClientRequestException) {
            if (e.response.status == HttpStatusCode.TooManyRequests) AppResult.Failure(YapNPlayError.LimitReached(DAILY_LIMIT))
            else AppResult.Failure(YapNPlayError.Network("feedback $name failed"))
        } catch (e: Exception) {
            CrashReporter.record(e, "FeedbackRepository.$name")
            AppResult.Failure(YapNPlayError.Network("feedback $name failed"))
        }
    }

    private fun FeedbackTicketSummary.toDomain() = FeedbackTicket(
        id = ticketId,
        kind = runCatching { FeedbackKind.valueOf(kind) }.getOrDefault(FeedbackKind.FEEDBACK),
        answered = answered,
        preview = preview,
        createdAt = createdAt,
        updatedAt = updatedAt,
    )

    private companion object {
        /** The server's daily cap on new conversations (shown in the message). */
        const val DAILY_LIMIT = 10
    }
}
