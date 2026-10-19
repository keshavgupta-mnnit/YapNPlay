package com.kglabs28.yapnplay.appengine.fake

import kotlinx.serialization.KSerializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonObject

/**
 * Encoding for the local host's wire payloads. Uses the same lenient
 * settings as the app's own [Json], so what the host writes is exactly
 * what the app's snapshot mappers read back.
 */
internal object LocalWireJson {
    private val json = Json {
        ignoreUnknownKeys = true
        isLenient = true
        encodeDefaults = true
        classDiscriminator = TYPE_KEY
    }

    /** [value] as JSON, using the serializer its own type declares. */
    fun <T> encode(serializer: KSerializer<T>, value: T): JsonObject =
        json.encodeToJsonElement(serializer, value) as JsonObject

    /** As [encode], but null instead of throwing on an unsupported value. */
    fun <T> encodeOrNull(serializer: KSerializer<T>, value: T): JsonObject? =
        runCatching { encode(serializer, value) }.getOrNull()

    private const val TYPE_KEY = "type"
}