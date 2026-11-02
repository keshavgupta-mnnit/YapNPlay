package com.kglabs28.yapnplay.appengine.core.ads

import android.app.Application
import android.os.Handler
import android.os.Looper
import android.os.SystemClock
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.nativead.NativeAd
import com.kglabs28.yapnplay.appengine.appmanager.helpers.AdsAppManagerHelper
import kotlin.coroutines.resume
import kotlinx.coroutines.CancellableContinuation
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

/**
 * Native ads loaded ahead of time, so an ad pill shows one the moment it
 * appears (a dialog that is up for a few seconds still gets its ad). Up to
 * [POOL_SIZE] wait ready from the app's start; each one taken is replaced.
 * An ad kept too long is dropped (AdMob's expire after an hour). A load that
 * fails is tried again a few times, waiting longer each time. Everything
 * runs on the main thread, as AdMob needs.
 */
class NativeAdPool(private val application: Application, private val config: AdsAppManagerHelper) {

    private class Ready(val ad: NativeAd, val loadedAt: Long)

    private val ready = ArrayDeque<Ready>()
    private val waiters = ArrayDeque<CancellableContinuation<NativeAd>>()
    private var loading = 0
    private var failures = 0
    private var started = false
    private val main = Handler(Looper.getMainLooper())

    init {
        main.post {
            // AdMob starts before the first load (loading before it is up can fail).
            MobileAds.initialize(application) {
                started = true
                fill()
            }
        }
    }

    /** The next ad to show: one waiting at once, else the next to load. The caller destroys it when done. */
    suspend fun next(): NativeAd = withContext(Dispatchers.Main) {
        dropStale()
        val waiting = ready.removeFirstOrNull()
        if (waiting != null) {
            fill()
            waiting.ad
        } else {
            suspendCancellableCoroutine { waiter ->
                waiters.add(waiter)
                waiter.invokeOnCancellation { main.post { waiters.remove(waiter) } }
                fill()
            }
        }
    }

    /** Loads until the pool (and everyone waiting) is served. */
    private fun fill() {
        if (!started || !config.isNativeEnabled()) return
        while (ready.size + loading < POOL_SIZE + waiters.size) load()
    }

    private fun load() {
        loading++
        NativeAdLoader(
            context = application,
            unitId = config.nativeUnitId(),
            onLoaded = { ad ->
                loading--
                failures = 0
                deliver(ad)
            },
            onFailed = {
                loading--
                failures++
                // "No fill" often clears up: a few retries, waiting longer each time; then the next demand tries again.
                RETRY_DELAYS_MS.getOrNull(failures - 1)?.let { main.postDelayed({ fill() }, it) }
            },
        ).load()
    }

    /** To the longest waiting pill, else into the pool. */
    private fun deliver(ad: NativeAd) {
        while (waiters.isNotEmpty()) {
            val waiter = waiters.removeFirst()
            if (waiter.isActive) return waiter.resume(ad)
        }
        ready.addLast(Ready(ad, SystemClock.elapsedRealtime()))
    }

    private fun dropStale() {
        val now = SystemClock.elapsedRealtime()
        while (ready.isNotEmpty() && now - ready.first().loadedAt > MAX_AGE_MS) ready.removeFirst().ad.destroy()
    }

    private companion object {
        /** Ads kept ready: one for the screen and one for a dialog that may open over it. */
        const val POOL_SIZE = 2
        /** Shown well within AdMob's one hour. */
        const val MAX_AGE_MS = 50 * 60_000L
        val RETRY_DELAYS_MS = longArrayOf(15_000L, 30_000L, 60_000L)
    }
}
