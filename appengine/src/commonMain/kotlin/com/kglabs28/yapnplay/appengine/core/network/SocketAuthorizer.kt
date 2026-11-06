package com.kglabs28.yapnplay.appengine.core.network

import com.kglabs28.yapnplay.appengine.protocol.WsTicket
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.post
import io.ktor.http.HttpHeaders

/** What the WebSocket opens with: headers and/or URL query values. */
class SocketAccess(
    val headers: Map<String, String> = emptyMap(),
    val query: Map<String, String> = emptyMap(),
)

/** How the app proves who it is (and which games it has) when opening the WebSocket. */
fun interface SocketAuthorizer {
    suspend fun access(bearerToken: String, games: String): SocketAccess
}

/** The phone and desktop: the bearer and the game list as headers. */
class HeaderSocketAuthorizer : SocketAuthorizer {
    override suspend fun access(bearerToken: String, games: String) = SocketAccess(
        headers = mapOf(HttpHeaders.Authorization to BEARER + bearerToken, APP_GAMES_HEADER to games),
    )

    private companion object {
        const val BEARER = "Bearer "
        /** The header listing this app's games (the server knows it). */
        const val APP_GAMES_HEADER = "X-YapNPlay-Games"
    }
}

/**
 * The browser, which can't put headers on a WebSocket: a one-time ticket
 * (asked for over HTTP, which carries the bearer) and the game list go in
 * the URL. The account's secret itself never does.
 */
class TicketSocketAuthorizer(private val http: HttpClient) : SocketAuthorizer {
    override suspend fun access(bearerToken: String, games: String): SocketAccess {
        val ticket = http.post(BackendPaths.WS_TICKET).body<WsTicket>()
        return SocketAccess(query = mapOf(TICKET_QUERY to ticket.ticket, GAMES_QUERY to games))
    }

    private companion object {
        const val TICKET_QUERY = "ticket"
        const val GAMES_QUERY = "games"
    }
}
