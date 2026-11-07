package com.kglabs28.yapnplay.appengine.games.cyberblast.data.remote

import com.kglabs28.yapnplay.appengine.core.common.AppClock
import com.kglabs28.yapnplay.appengine.core.network.MatchChannel
import com.kglabs28.yapnplay.appengine.core.network.SessionProvider
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceClient
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceTarget
import com.kglabs28.yapnplay.appengine.games.cyberblast.data.remote.wire.CyberInput
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberCommand
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberFrameData
import com.kglabs28.yapnplay.appengine.games.cyberblast.domain.CyberUpdate
import com.kglabs28.yapnplay.appengine.protocol.transport.MessageType
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.mapNotNull

/**
 * Cyber Blast over the MATCH channel: snapshots in (when something big
 * happens), the moving board in as `CyberFrame` relays about 20 times a
 * second, and the user's steering and bombs out as `CyberInput`.
 */
class CyberBlastMatchClient(
    private val match: MatchChannel,
    private val mapper: CyberBlastSnapshotMapper,
    private val session: SessionProvider,
    private val clock: AppClock,
    private val voice: VoiceClient,
) {

    fun observeMatch(matchId: String): Flow<CyberUpdate> =
        match.snapshots(matchId).map { mapper.map(it, session.currentUserId().orEmpty(), clock.now()) }

    fun observeFrames(): Flow<CyberFrameData> = match.relays().mapNotNull(mapper::relayFrame)

    fun observeSpeaking(matchId: String): Flow<Set<String>> = voice.speakers(VoiceTarget.Match(matchId))

    suspend fun send(matchId: String, command: CyberCommand) {
        when (command) {
            is CyberCommand.Input -> match.request(MessageType.CYBER_INPUT, CyberInput.serializer(), CyberInput(matchId, command.dir?.name, command.bomb, command.tile))
            is CyberCommand.SetMic -> match.setMic(matchId, command.on)
            is CyberCommand.Leave -> match.leave(matchId, giveToUserId = command.giveToUserId)
            CyberCommand.ProposeEnd -> match.proposeEnd(matchId)
            is CyberCommand.VoteEnd -> match.castEndVote(matchId, command.agree)
        }
    }
}
