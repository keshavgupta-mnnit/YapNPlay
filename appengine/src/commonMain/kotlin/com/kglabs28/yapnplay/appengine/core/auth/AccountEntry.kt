package com.kglabs28.yapnplay.appengine.core.auth

/** How a player gets their account on this platform (from platformAppManagerModule). */
enum class AccountEntry {
    /** The app: a guest made silently on first launch, kept by Play Games. */
    GUEST,

    /** The web: nothing until the player signs in with Google on the login screen. */
    GOOGLE,
}
