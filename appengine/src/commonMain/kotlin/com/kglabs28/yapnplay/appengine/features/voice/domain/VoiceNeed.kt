package com.kglabs28.yapnplay.appengine.features.voice.domain

/**
 * Whether one lobby or table needs its voice room: only when another
 * person is there (bots don't talk), and either the user's mic is on (to
 * be heard) or their speaker is on while someone else's mic is on (to
 * hear). Alone, speaker off with the mic off, or nobody's mic on: the
 * device stays out of the voice room. Also who should be audible (the
 * others with their mic on), so voice can notice someone it isn't
 * receiving. Each change goes to [send] once.
 */
class VoiceNeed(private val send: (wanted: Boolean, talkers: Set<String>) -> Unit) {
    private var wanted = false
    private var talkers: Set<String> = emptySet()

    /** [myMicOpen]: the user's mic is open here; [mySpeakerOn]: they hear others; [others]: each other person present (user id) and whether their mic is on. */
    fun update(myMicOpen: Boolean, mySpeakerOn: Boolean, others: Map<String, Boolean>) {
        val now = others.isNotEmpty() && (myMicOpen || (mySpeakerOn && others.values.any { it }))
        val on = others.filterValues { it }.keys
        if (now == wanted && on == talkers) return
        wanted = now
        talkers = on
        send(now, on)
    }

    /** The screen closed: no voice for it. */
    fun clear() = update(myMicOpen = false, mySpeakerOn = false, others = emptyMap())
}
