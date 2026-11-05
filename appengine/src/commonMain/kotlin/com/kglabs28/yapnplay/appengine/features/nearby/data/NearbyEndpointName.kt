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

    fun encode(player: NearbyPlayer): String =
        listOf(player.name.take(MAX_NAME).replace(SEPARATOR, ' '), player.avatarId, player.userId).joinToString(SEPARATOR.toString())

    fun decode(text: String): NearbyPlayer? {
        val parts = text.split(SEPARATOR)
        if (parts.size != PARTS || parts[2].isBlank()) return null
        return NearbyPlayer(userId = parts[2], name = parts[0], avatarId = parts[1])
    }
}
