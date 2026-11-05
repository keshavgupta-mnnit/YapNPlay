package com.kglabs28.yapnplay.appengine.features.nearby.data

import com.kglabs28.yapnplay.appengine.features.nearby.protocol.NearbyFrame
import kotlinx.serialization.json.Json

/** Turns frames into bytes for the radio and back (JSON, UTF-8). A frame that can't be read is dropped. */
class NearbyFrames(private val json: Json) {
    fun encode(frame: NearbyFrame): ByteArray = json.encodeToString(NearbyFrame.serializer(), frame).encodeToByteArray()

    fun decode(bytes: ByteArray): NearbyFrame? =
        runCatching { json.decodeFromString(NearbyFrame.serializer(), bytes.decodeToString()) }.getOrNull()
}
