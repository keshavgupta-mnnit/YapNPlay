package com.kglabs28.yapnplay.appengine.core.common

/** Going full screen in a browser (the app itself always is). */
interface FullScreen {
    /** A tap can go full screen and turn the screen sideways (Android browsers; not iPhone Safari). */
    fun canEnter(): Boolean

    /** Full screen, locked to landscape where the browser allows it. Call from a tap. */
    fun enter()

    /** Full screen needs "Add to Home Screen" instead (iPhone Safari, not opened from the Home Screen). */
    fun needsHomeScreen(): Boolean
}

/** The app and desktop: already full screen. */
class NoFullScreen : FullScreen {
    override fun canEnter(): Boolean = false
    override fun enter() = Unit
    override fun needsHomeScreen(): Boolean = false
}
