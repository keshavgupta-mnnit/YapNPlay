package com.kglabs28.yapnplay.appengine.games.mono.data.remote

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.network.SessionProvider
import com.kglabs28.yapnplay.appengine.core.network.MatchChannel
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceClient
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceTarget
import com.kglabs28.yapnplay.appengine.games.mono.data.remote.wire.MonoWireAction
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoCommand
import com.kglabs28.yapnplay.appengine.games.mono.domain.MonoUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

/**
 * Standard MONO over the MATCH channel: `MatchSnapshot` in, game actions
 * out. Who is viewing comes from the snapshot, so the mapper needs only
 * the session id for the UI's "is this me" checks.
 */
class MonoMatchClientImpl(
    private val match: MatchChannel,
    private val mapper: MonoSnapshotMapper,
    private val session: SessionProvider,
    private val clock: AppClock,
    private val json: Json,
    private val voice: VoiceClient,
) : MonoMatchClient {

    override fun observeMatch(matchId: String): Flow<MonoUpdate> =
        match.snapshots(matchId).map { mapper.map(it, session.currentUserId().orEmpty(), clock.now()) }

    override fun observeSpeaking(matchId: String): Flow<Set<String>> = voice.speakers(VoiceTarget.Match(matchId))

    override suspend fun send(matchId: String, command: MonoCommand) {
        when (command) {
            is MonoCommand.SetAutoplay -> match.setAutoplay(matchId, command.enabled)
            MonoCommand.ProposeEnd -> match.proposeEnd(matchId)
            is MonoCommand.VoteEnd -> match.castEndVote(matchId, command.agree)
            is MonoCommand.SetMic -> match.setMic(matchId, command.on)
            is MonoCommand.SetAway -> match.setAway(matchId, command.away)
            is MonoCommand.Leave -> match.leave(matchId, command.giveToUserId)
            is MonoCommand.StillHere -> match.stillHere(matchId)
            else -> play(matchId, command.toWire())
        }
    }

    override suspend fun stillHere(matchId: String) = match.stillHere(matchId)

    private suspend fun play(matchId: String, action: MonoWireAction) =
        match.gameAction(matchId, json.encodeToJsonElement(MonoWireAction.serializer(), action).jsonObject)

    private fun MonoCommand.toWire(): MonoWireAction = when (this) {
        is MonoCommand.PlayCard -> MonoWireAction.PlayCard(handIndex)
        MonoCommand.DrawCard -> MonoWireAction.DrawCard
        is MonoCommand.ChooseColor -> MonoWireAction.ChooseColor(color)
        MonoCommand.CallMono -> MonoWireAction.CallMono
        MonoCommand.AcceptStack -> MonoWireAction.AcceptStack
        MonoCommand.Pass -> MonoWireAction.Pass
        MonoCommand.Caught -> MonoWireAction.Caught
        is MonoCommand.VoteContinue -> MonoWireAction.VoteContinue(keepPlaying)
        is MonoCommand.ChooseSwapTarget -> MonoWireAction.ChooseSwapTarget(seat)
        is MonoCommand.SetAutoplay,
        MonoCommand.ProposeEnd,
        is MonoCommand.VoteEnd,
        is MonoCommand.SetMic,
        is MonoCommand.SetAway,
        is MonoCommand.Leave,
        MonoCommand.StillHere,
        MonoCommand.Roll,
        -> error("not a game move: $this")
    }
}