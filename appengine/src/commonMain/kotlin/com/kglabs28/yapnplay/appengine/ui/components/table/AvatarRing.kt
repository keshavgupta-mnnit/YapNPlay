package com.kglabs28.yapnplay.appengine.ui.components.table

/** Avatar ring in a table's top bar: talking, mic off, or neither. */
enum class AvatarRing {
    TALKING, MUTED, IDLE;

    companion object {
        /** Mic off wins; then talking (heard by the user); else idle. */
        fun of(talking: Boolean, micOn: Boolean): AvatarRing = when {
            !micOn -> MUTED
            talking -> TALKING
            else -> IDLE
        }
    }
}
