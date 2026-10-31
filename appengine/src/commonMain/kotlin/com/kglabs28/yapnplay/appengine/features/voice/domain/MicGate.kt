package com.kglabs28.yapnplay.appengine.features.voice.domain

/**
 * The user's mic for one lobby or table: open while their mic is on and
 * the app is in front; with it off (app in front), it only listens for
 * talking on mute, so the seat can glow grey. Each change goes to [send] once.
 */
class MicGate(private val send: (open: Boolean, listenMuted: Boolean) -> Unit) {
    var open = false
        private set
    private var listening = false

    /** [micOn]: the user's mic is on here; [inFront]: the app is in the foreground. */
    fun update(micOn: Boolean, inFront: Boolean) {
        val nowOpen = micOn && inFront
        val nowListening = !micOn && inFront
        if (nowOpen == open && nowListening == listening) return
        open = nowOpen
        listening = nowListening
        send(nowOpen, nowListening)
    }

    /** The screen closed: the mic is closed and nothing listens. */
    fun clear() = update(micOn = false, inFront = false)
}
