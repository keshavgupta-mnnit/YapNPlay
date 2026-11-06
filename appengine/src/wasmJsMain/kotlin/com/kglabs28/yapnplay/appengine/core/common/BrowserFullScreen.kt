package com.kglabs28.yapnplay.appengine.core.common

/**
 * The browser's Fullscreen API, then a landscape lock (allowed only while
 * full screen, e.g. Chrome on Android). iPhone Safari has neither for a
 * page, so there the player is told to add the app to the Home Screen
 * (it then opens full screen and landscape, from the web manifest).
 */
class BrowserFullScreen : FullScreen {
    override fun canEnter(): Boolean = fullScreenAvailable() && !fullScreenNow()
    override fun enter() = requestFullScreenLandscape()
    override fun needsHomeScreen(): Boolean = !fullScreenAvailable() && isIPhone() && !openedFromHomeScreen()
}

@JsFun("() => !!(document.fullscreenEnabled || document.webkitFullscreenEnabled)")
private external fun fullScreenAvailable(): Boolean

@JsFun("() => !!(document.fullscreenElement || document.webkitFullscreenElement)")
private external fun fullScreenNow(): Boolean

@JsFun(
    """() => {
    const el = document.documentElement;
    const request = el.requestFullscreen || el.webkitRequestFullscreen;
    if (!request) return;
    Promise.resolve(request.call(el))
        .then(() => (screen.orientation && screen.orientation.lock) ? screen.orientation.lock('landscape') : null)
        .catch(() => {});
}"""
)
private external fun requestFullScreenLandscape()

@JsFun("() => /iPhone|iPod/.test(navigator.userAgent)")
private external fun isIPhone(): Boolean

@JsFun(
    """() => navigator.standalone === true ||
    window.matchMedia('(display-mode: standalone)').matches ||
    window.matchMedia('(display-mode: fullscreen)').matches"""
)
private external fun openedFromHomeScreen(): Boolean
