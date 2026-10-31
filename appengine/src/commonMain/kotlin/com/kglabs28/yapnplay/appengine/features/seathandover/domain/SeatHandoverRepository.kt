package com.kglabs28.yapnplay.appengine.features.seathandover.domain

import com.kglabs28.yapnplay.appengine.core.common.AppResult

/** Sends seat handover requests for any game's match. */
interface SeatHandoverRepository {
    suspend fun send(matchId: String, command: SeatCommand): AppResult<Unit>
}
