package com.kglabs28.yapnplay.appengine.features.voice.data.remote

/** Which voice room to talk in: a play room's lobby, or a match table. */
sealed interface VoiceTarget {
    data class Room(val roomId: String) : VoiceTarget
    data class Match(val matchId: String) : VoiceTarget
}
