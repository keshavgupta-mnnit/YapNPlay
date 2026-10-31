package com.kglabs28.yapnplay.appengine.features.seathandover.usecase

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.features.seathandover.domain.SeatCommand
import com.kglabs28.yapnplay.appengine.features.seathandover.domain.SeatHandoverRepository

/** Takes, answers, hands on or offers a seat in any game's match. */
class SendSeatCommandUseCase(private val repository: SeatHandoverRepository) {
    suspend operator fun invoke(matchId: String, command: SeatCommand): AppResult<Unit> = repository.send(matchId, command)
}
