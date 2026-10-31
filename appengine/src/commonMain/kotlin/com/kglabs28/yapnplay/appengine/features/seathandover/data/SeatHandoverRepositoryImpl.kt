package com.kglabs28.yapnplay.appengine.features.seathandover.data

import com.kglabs28.yapnplay.appengine.core.common.AppResult
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.core.common.DispatcherProvider
import com.kglabs28.yapnplay.appengine.core.common.YapNPlayError
import com.kglabs28.yapnplay.appengine.core.network.MatchChannel
import com.kglabs28.yapnplay.appengine.features.seathandover.domain.SeatCommand
import com.kglabs28.yapnplay.appengine.features.seathandover.domain.SeatHandoverRepository
import kotlinx.coroutines.withContext

class SeatHandoverRepositoryImpl(
    private val match: MatchChannel,
    private val dispatchers: DispatcherProvider,
) : SeatHandoverRepository {

    override suspend fun send(matchId: String, command: SeatCommand): AppResult<Unit> =
        withContext(dispatchers.io) {
            try {
                when (command) {
                    SeatCommand.StillHere -> match.stillHere(matchId)
                    is SeatCommand.Answer -> match.answerSeatOffer(matchId, command.accept)
                    is SeatCommand.ChooseTaker -> match.chooseSeatTaker(matchId, command.giveToUserId)
                    is SeatCommand.Take -> match.takeSeat(matchId, command.seat)
                    is SeatCommand.Leave -> match.leave(matchId, command.giveToUserId)
                    is SeatCommand.OfferBotSeat -> match.offerBotSeat(matchId, command.seat, command.toUserId)
                }
                AppResult.Success(Unit)
            } catch (e: Exception) {
                CrashReporter.record(e, "SeatHandoverRepository.send:${command::class.simpleName}")
                AppResult.Failure(YapNPlayError.Network("seat request failed"))
            }
        }
}
