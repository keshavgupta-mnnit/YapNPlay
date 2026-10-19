package com.kglabs28.yapnplay.appengine.games.monochaos.data.remote

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.network.SessionProvider
import com.kglabs28.yapnplay.appengine.core.network.MatchChannel
import com.kglabs28.yapnplay.appengine.games.monochaos.data.remote.wire.MonoChaosWireAction
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosCommand
import com.kglabs28.yapnplay.appengine.games.monochaos.domain.MonoChaosUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

/**
 * MONO: Complete Chaos over the MATCH channel: `MatchSnapshot` in, game
 * actions out. Who is viewing comes from the snapshot, so the mapper
 * needs only the session id for the UI's "is this me" checks.
 */
class MonoChaosMatchClientImpl(
    private val match: MatchChannel,
    private val mapper: MonoChaosSnapshotMapper,
    private val session: SessionProvider,
    private val clock: AppClock,
    private val json: Json,
) : MonoChaosMatchClient {

    override fun observeMatch(matchId: String): Flow<MonoChaosUpdate> =
        match.snapshots(matchId).map { mapper.map(it, session.currentUserId().orEmpty(), clock.now()) }

    override suspend fun send(matchId: String, command: MonoChaosCommand) {
        when (command) {
            is MonoChaosCommand.SetAutoplay -> match.setAutoplay(matchId, command.enabled)
            is MonoChaosCommand.SetMic -> match.setMic(matchId, command.on)
            is MonoChaosCommand.SetAway -> match.setAway(matchId, command.away)
            is MonoChaosCommand.Leave -> match.leave(matchId, command.giveToUserId)
            is MonoChaosCommand.StillHere -> match.stillHere(matchId)
            is MonoChaosCommand.AnswerSeatOffer -> match.answerSeatOffer(matchId, command.accept)
            is MonoChaosCommand.ChooseSeatTaker -> match.chooseSeatTaker(matchId, command.giveToUserId)
            is MonoChaosCommand.TakeSeat -> match.takeSeat(matchId, command.seat)
            else -> play(matchId, command.toWire())
        }
    }

    override suspend fun stillHere(matchId: String) = match.stillHere(matchId)

    private suspend fun play(matchId: String, action: MonoChaosWireAction) =
        match.gameAction(matchId, json.encodeToJsonElement(MonoChaosWireAction.serializer(), action).jsonObject)

    private fun MonoChaosCommand.toWire(): MonoChaosWireAction = when (this) {
        is MonoChaosCommand.PlayCard -> MonoChaosWireAction.PlayCard(handIndex)
        MonoChaosCommand.DrawUntilPlayable -> MonoChaosWireAction.DrawUntilPlayable
        is MonoChaosCommand.ChooseColor -> MonoChaosWireAction.ChooseColor(color)
        is MonoChaosCommand.ChooseRouletteColor -> MonoChaosWireAction.ChooseRouletteColor(color)
        MonoChaosCommand.CallMono -> MonoChaosWireAction.CallMono
        is MonoChaosCommand.StackDraw -> MonoChaosWireAction.StackDraw(handIndex)
        MonoChaosCommand.AcceptStack -> MonoChaosWireAction.AcceptStack
        is MonoChaosCommand.SetAutoplay,
        is MonoChaosCommand.SetMic,
        is MonoChaosCommand.SetAway,
        is MonoChaosCommand.Leave,
        MonoChaosCommand.StillHere,
        is MonoChaosCommand.AnswerSeatOffer,
        is MonoChaosCommand.ChooseSeatTaker,
        is MonoChaosCommand.TakeSeat,
        MonoChaosCommand.Roll,
        -> error("not a game move: $this")
    }
}