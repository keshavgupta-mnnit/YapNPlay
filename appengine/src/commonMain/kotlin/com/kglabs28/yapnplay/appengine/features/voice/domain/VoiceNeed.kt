package com.kglabs28.yapnplay.appengine.features.voice.domain

/**
 * Whether one lobby or table needs its voice room: only when another
 * person is there (bots don't talk), and either the user's mic is on (to
 * be heard) or their speaker is on while someone else's mic is on (to
 * hear). Alone, speaker off with the mic off, or nobody's mic on: the
 * device stays out of the voice room. Each change goes to [send] once.
 */
class VoiceNeed(private val send: (Boolean) -> Unit) {
    private var wanted = false

    /** [myMicOpen]: the user's mic is open here; [mySpeakerOn]: they hear others; [othersMicOn]: one entry per other person present. */
    fun update(myMicOpen: Boolean, mySpeakerOn: Boolean, othersMicOn: List<Boolean>) {
        val now = othersMicOn.isNotEmpty() && (myMicOpen || (mySpeakerOn && othersMicOn.any { it }))
        if (now == wanted) return
        wanted = now
        send(now)
    }

    /** The screen closed: no voice for it. */
    fun clear() = update(myMicOpen = false, mySpeakerOn = false, othersMicOn = emptyList())
}
