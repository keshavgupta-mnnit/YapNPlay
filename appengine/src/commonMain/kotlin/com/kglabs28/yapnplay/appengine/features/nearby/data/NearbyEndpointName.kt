package com.kglabs28.yapnplay.appengine.features.nearby.data

import com.kglabs28.yapnplay.appengine.features.nearby.domain.NearbyPlayer

/**
 * The name a findable phone shows to phones around: who it is, packed
 * into the radio's short endpoint name (name, avatar and account id).
 */
object NearbyEndpointName {
    private const val SEPARATOR = '\u001F'
    private const val MAX_NAME = 24
    private const val PARTS = 3
    /** A phone that took a party over also says which party it hosts (a 4th part). */
    private const val HOST_PARTS = 4

    fun encode(player: NearbyPlayer): String =
        listOf(player.name.take(MAX_NAME).replace(SEPARATOR, ' '), player.avatarId, player.userId).joinToString(SEPARATOR.toString())

    fun decode(text: String): NearbyPlayer? {
        val parts = text.split(SEPARATOR)
        if (parts.size !in PARTS..HOST_PARTS || parts[2].isBlank()) return null
        return NearbyPlayer(userId = parts[2], name = parts[0], avatarId = parts[1])
    }

    /** [player] hosting the party [roomId] (after a takeover). */
    fun encodeHost(player: NearbyPlayer, roomId: String): String = encode(player) + SEPARATOR + roomId

    /** The party a phone says it hosts, or null for an ordinary findable phone. */
    fun hostedRoom(text: String): String? = text.split(SEPARATOR).takeIf { it.size == HOST_PARTS }?.get(3)?.takeIf { it.isNotBlank() }
}
