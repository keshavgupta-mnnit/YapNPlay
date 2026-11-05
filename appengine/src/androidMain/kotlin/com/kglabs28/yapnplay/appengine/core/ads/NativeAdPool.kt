package com.kglabs28.yapnplay.appengine.core.ads

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd
import com.kglabs28.yapnplay.appengine.appmanager.helpers.AdsAppManagerHelper
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

/**
 * The app's native ad: ONE ad at a time, shared by every ad pill, instead of
 * a fresh ad per pill. A pill that is up for a second (a dialog) shows the
 * current ad and costs no request; a new ad is requested only when the
 * current one is older than the refresh time AND a refreshing pill (a game
 * table) is on screen. One spare is kept loaded so a swap is instant.
 *
 * Why: requesting a new ad for every pill made thousands of requests that
 * were never shown, and AdMob answers an app that over-requests with a low
 * match rate. An ad may sit in one view at a time, so when two pills are up
 * (a dialog over a table) the newest one shows it ([holder]).
 * Everything runs on the main thread, as AdMob needs.
 */
class NativeAdPool(private val application: Application, private val config: AdsAppManagerHelper) {

    private val _current = MutableStateFlow<NativeAd?>(null)
    /** The ad on screen now (null until the first loads). */
    val current: StateFlow<NativeAd?> = _current.asStateFlow()

    private val _holder = MutableStateFlow<Any?>(null)
    /** The pill showing [current]: the newest one on screen. */
    val holder: StateFlow<Any?> = _holder.asStateFlow()

    private class Viewer(val token: Any, val refreshes: Boolean)

    private val viewers = mutableListOf<Viewer>()
    private var shownSince = 0L
    private var spare: NativeAd? = null
    private var spareSince = 0L
    private var loading = false
    private var failures = 0
    private var started = false
    private val main = Handler(Looper.getMainLooper())
    private val refreshTick = Runnable { refreshIfDue() }

    init {
        main.post {
            // AdMob starts before the first load (loading before it is up can fail).
            MobileAds.initialize(application) {
                started = true
                // One ad ready for the first pill (the connecting card's), not more.
                load()
            }
        }
    }

    /** A pill came on screen ([refreshes]: it stays up all game and keeps the ad fresh). */
    fun show(token: Any, refreshes: Boolean) = main.post {
        viewers.removeAll { it.token === token }
        viewers += Viewer(token, refreshes)
        _holder.value = token
        if (_current.value == null) useSpareOrLoad()
        scheduleRefresh()
    }

    /** A pill left the screen. */
    fun hide(token: Any) = main.post {
        viewers.removeAll { it.token === token }
        _holder.value = viewers.lastOrNull()?.token
        if (viewers.none { it.refreshes }) main.removeCallbacks(refreshTick)
    }

    /** The current ad is old enough and a table pill is up: the spare (or a new one) takes over. */
    private fun refreshIfDue() {
        if (viewers.none { it.refreshes }) return
        if (age() >= config.nativeRefreshMs()) useSpareOrLoad()
        scheduleRefresh()
    }

    private fun scheduleRefresh() {
        main.removeCallbacks(refreshTick)
        if (viewers.none { it.refreshes }) return
        main.postDelayed(refreshTick, (config.nativeRefreshMs() - age()).coerceAtLeast(MIN_TICK_MS))
    }

    private fun useSpareOrLoad() {
        val ready = spare?.takeIf { SystemClock.elapsedRealtime() - spareSince < MAX_AGE_MS }
        spare?.takeIf { it !== ready }?.destroy()
        spare = null
        if (ready != null) swap(ready) else load()
    }

    private fun swap(ad: NativeAd) {
        val old = _current.value
        _current.value = ad
        shownSince = SystemClock.elapsedRealtime()
        // Destroyed a moment later, once the pill has drawn the new one.
        old?.let { main.postDelayed({ it.destroy() }, DESTROY_DELAY_MS) }
        // The next one ready for the next refresh (only tables refresh, at most once a minute).
        if (viewers.any { it.refreshes }) load()
    }

    private fun load() {
        if (!started || loading || !config.isNativeEnabled()) return
        if (_current.value != null && spare != null) return
        loading = true
        NativeAdLoader(
            context = application,
            unitId = config.nativeUnitId(),
            onLoaded = { ad ->
                loading = false
                failures = 0
                if (_current.value == null) {
                    swap(ad)
                } else {
                    spare?.destroy()
                    spare = ad
                    spareSince = SystemClock.elapsedRealtime()
                }
            },
            onFailed = {
                loading = false
                failures++
                // Tried again only while someone is looking, waiting longer each time.
                if (viewers.isNotEmpty()) RETRY_DELAYS_MS.getOrNull(failures - 1)?.let { main.postDelayed({ load() }, it) }
            },
        ).load()
    }

    private fun age(): Long = if (_current.value == null) Long.MAX_VALUE else SystemClock.elapsedRealtime() - shownSince

    private companion object {
        /** A spare is used only well within AdMob's one hour. */
        const val MAX_AGE_MS = 50 * 60_000L
        const val DESTROY_DELAY_MS = 2_000L
        const val MIN_TICK_MS = 1_000L
        val RETRY_DELAYS_MS = longArrayOf(30_000L, 60_000L, 120_000L)
    }
}
