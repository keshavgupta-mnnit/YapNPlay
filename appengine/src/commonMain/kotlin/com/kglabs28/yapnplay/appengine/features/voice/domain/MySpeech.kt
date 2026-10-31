package com.kglabs28.yapnplay.appengine.features.voice.domain

/** What the user's own voice is doing right now. */
enum class MySpeech {
    SILENT,

    /** Talking into their open mic: everyone hears them. */
    SPEAKING,

    /** Talking with their mic off: nobody hears them (the grey glow tells them so). */
    SPEAKING_MUTED,
}
