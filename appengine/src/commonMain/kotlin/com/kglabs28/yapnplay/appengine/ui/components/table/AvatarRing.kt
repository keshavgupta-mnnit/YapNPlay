package com.kglabs28.yapnplay.appengine.ui.components.table

/** Avatar ring in a table's top bar: talking, mic off, or neither. */
enum class AvatarRing {
    TALKING, TALKING_MUTED, MUTED, IDLE;

    companion object {
        /** Talking with the mic off (grey) first, then mic off; then talking (heard by the user); else idle. */
        fun of(talking: Boolean, micOn: Boolean, talkingMuted: Boolean = false): AvatarRing = when {
            !micOn && talkingMuted -> TALKING_MUTED
            !micOn -> MUTED
            talking -> TALKING
            else -> IDLE
        }
    }
}
