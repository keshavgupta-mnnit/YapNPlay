package com.kglabs28.yapnplay.appengine.features.nearby.host

import com.kglabs28.yapnplay.appengine.protocol.Ack
import com.kglabs28.yapnplay.appengine.protocol.ErrorCode
import com.kglabs28.yapnplay.appengine.protocol.Rejected
import com.kglabs28.yapnplay.appengine.protocol.transport.Channel
import com.kglabs28.yapnplay.appengine.protocol.transport.Envelope
import com.kglabs28.yapnplay.appengine.protocol.transport.MessageType
import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.jsonObject
import kotlin.random.Random

/**
 * Builds the envelopes the hosting phone sends, exactly as the backend
 * would: replies carry the request's id, pushes a fresh one.
 */
class HostEnvelopes(val json: Json) {

    fun <T> payload(envelope: Envelope, serializer: KSerializer<T>): T =
        json.decodeFromJsonElement(serializer, envelope.payload)

    fun <T> reply(request: Envelope, type: String, serializer: KSerializer<T>, value: T): Envelope =
        build(request.requestId, request.channel, type, serializer, value)

    fun ack(request: Envelope): Envelope = reply(request, MessageType.ACK, Ack.serializer(), Ack(request.requestId))

    fun rejected(request: Envelope, code: ErrorCode, message: String): Envelope =
        build(request.requestId, request.channel, MessageType.REJECTED, Rejected.serializer(), Rejected(request.requestId, code, message))

    fun <T> push(channel: Channel, type: String, serializer: KSerializer<T>, value: T): Envelope =
        build(newId(), channel, type, serializer, value)

    private fun <T> build(requestId: String, channel: Channel, type: String, serializer: KSerializer<T>, value: T) =
        Envelope(requestId, channel, type, json.encodeToJsonElement(serializer, value).jsonObject)

    private fun newId(): String = buildString { repeat(ID_LENGTH) { append(ID_ALPHABET[Random.nextInt(ID_ALPHABET.length)]) } }

    private companion object {
        const val ID_LENGTH = 24
        const val ID_ALPHABET = "abcdefghijklmnopqrstuvwxyz0123456789"
    }
}

/** A refused request: the code and a developer message (the app words each code itself). */
class HostRefusal(val code: ErrorCode, override val message: String) : Exception(message)
