package com.kglabs28.yapnplay.appengine.features.emojigift.data.remote

import com.kglabs28.yapnplay.appengine.core.network.BackendPaths
import com.kglabs28.yapnplay.appengine.core.network.RealtimeGateway
import com.kglabs28.yapnplay.appengine.protocol.EmojiGift
import com.kglabs28.yapnplay.appengine.protocol.EmojiGiftPrices
import com.kglabs28.yapnplay.appengine.protocol.EmojiGiftResult
import com.kglabs28.yapnplay.appengine.protocol.SendEmojiGift
import com.kglabs28.yapnplay.appengine.protocol.transport.Channel
import com.kglabs28.yapnplay.appengine.protocol.transport.MessageType
import io.ktor.client.HttpClient
import io.ktor.client.call.body
import io.ktor.client.request.get
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.filter
import kotlinx.coroutines.flow.map
import kotlinx.serialization.json.contentOrNull
import kotlinx.serialization.json.jsonPrimitive

class EmojiGiftClientImpl(
    private val http: HttpClient,
    private val realtime: RealtimeGateway,
) : EmojiGiftClient {

    override suspend fun prices(): EmojiGiftPrices = http.get(BackendPaths.EMOJI_GIFTS).body()

    override suspend fun send(matchId: String, emojiId: String, toSeat: Int): EmojiGiftResult {
        val reply = realtime.request(Channel.MATCH, MessageType.SEND_EMOJI_GIFT, SendEmojiGift.serializer(), SendEmojiGift(matchId, emojiId, toSeat))
        return realtime.decode(reply, EmojiGiftResult.serializer())
    }

    override fun gifts(matchId: String): Flow<EmojiGift> =
        realtime.stream(Channel.MATCH)
            .filter { it.type == MessageType.MATCH_RELAY && it.payload[TYPE]?.jsonPrimitive?.contentOrNull == MessageType.EMOJI_GIFT }
            .map { realtime.decode(it, EmojiGift.serializer()) }
            .filter { it.matchId == matchId }

    private companion object {
        const val TYPE = "type"
    }
}
