package com.kglabs28.yapnplay.appengine.games.cyberblast.bot

/** What one bot remembers between looks: when it looks again, when it may bomb again, and the tiles it is walking. */
data class BlastMind(
    val nextLookAt: Long = 0L,
    val nextBombAt: Long = 0L,
    val path: List<Int> = emptyList(),
)
