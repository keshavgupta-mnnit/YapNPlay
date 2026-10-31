package com.kglabs28.yapnplay.appengine.games.monochaos.data.remote

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.network.SessionProvider
import com.kglabs28.yapnplay.appengine.core.network.MatchChannel
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceClient
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceTarget
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
    private val voice: VoiceClient,
) : MonoChaosMatchClient {

    override fun observeMatch(matchId: String): Flow<MonoChaosUpdate> =
        match.snapshots(matchId).map { mapper.map(it, session.currentUserId().orEmpty(), clock.now()) }

    override fun observeSpeaking(matchId: String): Flow<Set<String>> = voice.speakers(VoiceTarget.Match(matchId))

    override suspend fun send(matchId: String, command: MonoChaosCommand) {
        when (command) {
            is MonoChaosCommand.SetAutoplay -> match.setAutoplay(matchId, command.enabled)
            MonoChaosCommand.ProposeEnd -> match.proposeEnd(matchId)
            is MonoChaosCommand.VoteEnd -> match.castEndVote(matchId, command.agree)
            is MonoChaosCommand.SetMic -> match.setMic(matchId, command.on)
            is MonoChaosCommand.SetAway -> match.setAway(matchId, command.away)
            is MonoChaosCommand.Leave -> match.leave(matchId, command.giveToUserId)
            is MonoChaosCommand.StillHere -> match.stillHere(matchId)
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
        MonoChaosCommand.Pass -> MonoChaosWireAction.Pass
        MonoChaosCommand.Caught -> MonoChaosWireAction.Caught
        is MonoChaosCommand.VoteContinue -> MonoChaosWireAction.VoteContinue(keepPlaying)
        is MonoChaosCommand.ChooseSwapTarget -> MonoChaosWireAction.ChooseSwapTarget(seat)
        is MonoChaosCommand.SetAutoplay,
        MonoChaosCommand.ProposeEnd,
        is MonoChaosCommand.VoteEnd,
        is MonoChaosCommand.SetMic,
        is MonoChaosCommand.SetAway,
        is MonoChaosCommand.Leave,
        MonoChaosCommand.StillHere,
        MonoChaosCommand.Roll,
        -> error("not a game move: $this")
    }
}