package com.kglabs28.yapnplay.appengine.games.citybazaar.data.remote

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.network.SessionProvider
import com.kglabs28.yapnplay.appengine.core.network.MatchChannel
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceClient
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceTarget
import com.kglabs28.yapnplay.appengine.games.citybazaar.data.remote.wire.CityBazaarWireAction
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarCommand
import com.kglabs28.yapnplay.appengine.games.citybazaar.domain.CityBazaarUpdate
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject

/**
 * City Bazaar over the MATCH channel: snapshots in; moves go out as
 * `GameAction` with the game's own action types. Who is talking comes from the
 * table's LiveKit voice room, joined while the match is open.
 */
class CityBazaarMatchClientImpl(
    private val match: MatchChannel,
    private val mapper: CityBazaarSnapshotMapper,
    private val session: SessionProvider,
    private val clock: AppClock,
    private val voice: VoiceClient,
    private val json: Json,
) : CityBazaarMatchClient {

    override fun observeMatch(matchId: String): Flow<CityBazaarUpdate> =
        match.snapshots(matchId).map { mapper.map(it, session.currentUserId().orEmpty(), clock.now()) }

    override fun observeSpeaking(matchId: String): Flow<Set<String>> = voice.speakers(VoiceTarget.Match(matchId))

    override suspend fun send(matchId: String, command: CityBazaarCommand) {
        when (command) {
            is CityBazaarCommand.SetAutoplay -> match.setAutoplay(matchId, command.enabled)
            is CityBazaarCommand.SetMic -> match.setMic(matchId, command.on)
            is CityBazaarCommand.SetAway -> match.setAway(matchId, command.away)
            CityBazaarCommand.Leave -> match.leave(matchId, giveToUserId = null)
            is CityBazaarCommand.VoteExtension -> match.voteExtension(matchId, command.keepPlaying)
            CityBazaarCommand.ProposeEnd -> match.proposeEnd(matchId)
            is CityBazaarCommand.VoteEnd -> match.castEndVote(matchId, command.agree)
            else -> play(matchId, command.toWire())
        }
    }

    private suspend fun play(matchId: String, action: CityBazaarWireAction) =
        match.gameAction(matchId, json.encodeToJsonElement(CityBazaarWireAction.serializer(), action).jsonObject)

    private fun CityBazaarCommand.toWire(): CityBazaarWireAction = when (this) {
        CityBazaarCommand.Roll -> CityBazaarWireAction.Roll
        CityBazaarCommand.Buy -> CityBazaarWireAction.Buy
        CityBazaarCommand.Decline -> CityBazaarWireAction.Decline
        is CityBazaarCommand.Build -> CityBazaarWireAction.Build(tile)
        CityBazaarCommand.SkipBuild -> CityBazaarWireAction.SkipBuild
        is CityBazaarCommand.SellBuilding -> CityBazaarWireAction.SellBuilding(tile)
        is CityBazaarCommand.SellTile -> CityBazaarWireAction.SellTile(tile)
        is CityBazaarCommand.StartAuction -> CityBazaarWireAction.StartAuction(tile)
        CityBazaarCommand.Bid -> CityBazaarWireAction.Bid
        CityBazaarCommand.PayJailFine -> CityBazaarWireAction.PayJailFine
        CityBazaarCommand.StayInJail -> CityBazaarWireAction.StayInJail
        CityBazaarCommand.DeclareBankruptcy -> CityBazaarWireAction.DeclareBankruptcy
        is CityBazaarCommand.SetAutoplay,
        is CityBazaarCommand.SetMic,
        is CityBazaarCommand.SetAway,
        CityBazaarCommand.Leave,
        is CityBazaarCommand.VoteExtension,
        CityBazaarCommand.ProposeEnd,
        is CityBazaarCommand.VoteEnd,
        -> error("not a game move: $this")
    }
}
