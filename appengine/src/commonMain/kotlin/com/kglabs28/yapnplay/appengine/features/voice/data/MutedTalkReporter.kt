package com.kglabs28.yapnplay.appengine.features.voice.data

import com.kglabs28.yapnplay.appengine.core.network.MatchChannel
import com.kglabs28.yapnplay.appengine.core.network.RealtimeGateway
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceClient
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceTarget
import com.kglabs28.yapnplay.appengine.features.voice.domain.MySpeech
import com.kglabs28.yapnplay.appengine.protocol.SetTalkingMuted
import com.kglabs28.yapnplay.appengine.protocol.transport.Channel
import com.kglabs28.yapnplay.appengine.protocol.transport.MessageType
import kotlinx.coroutines.CancellationException
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.flow.transformLatest
import kotlinx.coroutines.launch

/**
 * Tells the lobby or table on screen when the user talks with their mic
 * off, so everyone there sees a grey glow on them. Talking must last a
 * moment before it is sent, and silence a little longer before it stops,
 * so a cough or a pause between words doesn't flicker the glow. Moving to
 * another screen clears it on the one left behind.
 */
class MutedTalkReporter(
    private val voice: VoiceClient,
    private val match: MatchChannel,
    private val realtime: RealtimeGateway,
    scope: CoroutineScope,
) {
    private var shown: VoiceTarget? = null

    init {
        scope.launch { report() }
    }

    @OptIn(ExperimentalCoroutinesApi::class)
    private suspend fun report() {
        combine(voice.activeTarget, voice.mySpeech.map { it == MySpeech.SPEAKING_MUTED }) { target, talking -> target to talking }
            .distinctUntilChanged()
            .transformLatest { (target, talking) ->
                delay(if (talking) START_MS else STOP_MS)
                emit(target.takeIf { talking })
            }
            .distinctUntilChanged()
            .collect { target ->
                shown?.takeIf { it != target }?.let { send(it, talking = false) }
                target?.let { send(it, talking = true) }
                shown = target
            }
    }

    private suspend fun send(target: VoiceTarget, talking: Boolean) {
        try {
            when (target) {
                is VoiceTarget.Match -> match.setTalkingMuted(target.matchId, talking)
                is VoiceTarget.Room -> realtime.request(Channel.ROOM, MessageType.SET_TALKING_MUTED, SetTalkingMuted.serializer(), SetTalkingMuted(talking))
            }
        } catch (e: CancellationException) {
            throw e
        } catch (e: Exception) {
            // A glow that didn't reach the others is not worth an error: the next change tries again.
        }
    }

    private companion object {
        const val START_MS = 700L
        const val STOP_MS = 1_500L
    }
}
