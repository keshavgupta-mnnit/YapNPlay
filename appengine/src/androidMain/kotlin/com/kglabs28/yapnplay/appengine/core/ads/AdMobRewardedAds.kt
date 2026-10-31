package com.kglabs28.yapnplay.appengine.core.ads

import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppLog
import android.app.Activity
import android.app.Application
import android.os.Bundle
import android.os.Handler
import android.os.Looper
import com.google.android.gms.ads.AdError
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.FullScreenContentCallback
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.MobileAds
import com.google.android.gms.ads.rewarded.RewardedAd
import com.google.android.gms.ads.rewarded.RewardedAdLoadCallback
import com.kglabs28.yapnplay.appengine.appmanager.helpers.AdsAppManagerHelper
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import java.lang.ref.WeakReference
import kotlin.coroutines.resume
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import kotlinx.coroutines.withTimeoutOrNull

/**
 * AdMob rewarded ads. The unit id comes from AppManager (Google's test
 * unit until a real one is set). The ad needs the visible Activity, so
 * the app's resumed activity is tracked here (it is made when the app
 * starts, so it sees the first one). One ad is loaded when the app
 * launches (with the app's context, as the first screen opens) and the
 * next one after each ad plays; a load that fails ("No fill") is not
 * tried again until then. An offer is only shown when an ad can really play.
 */
class AdMobRewardedAds(
    private val application: Application,
    private val config: AdsAppManagerHelper,
) : RewardedAds {

    private var resumed = WeakReference<Activity>(null)
    private var initialized = false
    private val readyFlow = MutableStateFlow(false)
    override val readiness: StateFlow<Boolean> = readyFlow.asStateFlow()
    /** The ad loaded ahead, ready to play. */
    private var ready: RewardedAd? = null
        set(value) {
            field = value
            readyFlow.value = value != null
        }
    private var loading = false
    /** Failed loads in a row; each retry waits longer, and a load that works starts over. */
    private var failures = 0
    /** The launch load has started (screens coming back later don't load again). */
    private var launched = false
    private val main = Handler(Looper.getMainLooper())

    init {
        AppLog.d(TAG, "created (enabled=${config.isEnabled()}, unit=${config.rewardedUnitId()})")
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) {
                AppLog.d(TAG, "screen in front: ${activity::class.simpleName}")
                resumed = WeakReference(activity)
                if (!launched) {
                    launched = true
                    preload()
                }
            }
            override fun onActivityPaused(activity: Activity) = AppLog.d(TAG, "screen paused: ${activity::class.simpleName}")
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
            override fun onActivityStarted(activity: Activity) = Unit
            override fun onActivityStopped(activity: Activity) = Unit
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
            override fun onActivityDestroyed(activity: Activity) = Unit
        })
    }

    override fun isReady(): Boolean = (config.isEnabled() && ready != null).also {
        AppLog.d(TAG, "isReady=$it (enabled=${config.isEnabled()}, loaded=${ready != null}, loading=$loading)")
    }

    /** Starts loading the next ad unless one is ready or on its way (always on the main thread, as AdMob needs). */
    override fun preload() {
        if (Looper.myLooper() != Looper.getMainLooper()) {
            main.post { preload() }
            return
        }
        if (!config.isEnabled() || ready != null || loading) {
            AppLog.d(TAG, "preload skipped (enabled=${config.isEnabled()}, loaded=${ready != null}, loading=$loading)")
            return
        }
        initialize()
        loading = true
        // AdMob starts before its first load: loading before it is up can fail ("Unable to obtain a JavascriptEngine").
        initialize()
        AppLog.d(TAG, "preload started (unit=${config.rewardedUnitId()})")
        RewardedAd.load(application, config.rewardedUnitId(), AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                AppLog.d(TAG, "preload: ad loaded")
                loading = false
                failures = 0
                ready = ad
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                // No network or no ad to show is normal (ad blockers, a dropped connection): only real setup errors are reported.
                if (error.code != AdRequest.ERROR_CODE_NETWORK_ERROR && error.code != AdRequest.ERROR_CODE_NO_FILL) {
                    CrashReporter.record(IllegalStateException(error.message), "AdMobRewardedAds.preload:" + error.code)
                }
                loading = false
                // "No fill" often clears up: try again a few times, waiting longer each time (a coins tap also retries).
                failures++
                val retry = failures <= RETRY_DELAYS_MS.size
                AppLog.d(TAG, "preload failed: code=${error.code} message=${error.message} domain=${error.domain} response=${error.responseInfo}; retry=$retry")
                if (retry) main.postDelayed({ preload() }, RETRY_DELAYS_MS[failures - 1])
            }
        })
    }

    override suspend fun show(): Boolean = withContext(Dispatchers.Main) {
        AppLog.d(TAG, "show requested (enabled=${config.isEnabled()}, loaded=${ready != null})")
        if (!config.isEnabled()) return@withContext false.also { AppLog.d(TAG, "show: ads are switched off") }
        val activity = resumed.get() ?: return@withContext false.also { AppLog.d(TAG, "show: no visible activity") }
        AppLog.d(TAG, "show: on ${activity::class.simpleName} (finishing=${activity.isFinishing})")
        initialize()
        // Only loading has a time limit: an ad that is playing is never cut short (that lost the reward).
        val ad = ready ?: withTimeoutOrNull(LOAD_TIMEOUT_MS) { load(activity) } ?: return@withContext false.also { AppLog.d(TAG, "show: no ad could be loaded") }
        ready = null
        AppLog.d(TAG, "show: playing the ad")
        play(activity, ad).also {
            AppLog.d(TAG, "show: closed, reward earned=$it")
            preload()
        }
    }

    private fun initialize() {
        if (!initialized) {
            AppLog.d(TAG, "AdMob starting")
            MobileAds.initialize(application) { status ->
                status.adapterStatusMap.forEach { (name, adapter) -> AppLog.d(TAG, "AdMob adapter $name: ${adapter.initializationState} ${adapter.description}") }
            }
            initialized = true
        }
    }

    private suspend fun load(activity: Activity): RewardedAd? = suspendCancellableCoroutine { cont ->
        AppLog.d(TAG, "load on demand started")
        RewardedAd.load(activity, config.rewardedUnitId(), AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) {
                AppLog.d(TAG, "load on demand: ad loaded")
                if (cont.isActive) cont.resume(ad)
            }
            override fun onAdFailedToLoad(error: LoadAdError) {
                AppLog.d(TAG, "load on demand failed: code=${error.code} message=${error.message}")
                CrashReporter.record(IllegalStateException(error.message), "AdMobRewardedAds.load:" + error.code)
                if (cont.isActive) cont.resume(null)
            }
        })
    }

    /** Plays [ad]; resumes when it closes, true if the reward was earned. */
    private suspend fun play(activity: Activity, ad: RewardedAd): Boolean = suspendCancellableCoroutine { cont ->
        var earned = false
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdShowedFullScreenContent() = AppLog.d(TAG, "play: ad on screen")
            override fun onAdDismissedFullScreenContent() {
                AppLog.d(TAG, "play: ad closed")
                if (cont.isActive) cont.resume(earned)
            }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                AppLog.d(TAG, "play failed: code=${error.code} message=${error.message}")
                CrashReporter.record(IllegalStateException(error.message), "AdMobRewardedAds.show:" + error.code)
                if (cont.isActive) cont.resume(false)
            }
        }
        ad.show(activity) {
            AppLog.d(TAG, "play: reward earned")
            earned = true
        }
    }
}

private const val TAG = AppConstants.LOG_TAG_ADS

/** Waits before each retry of a failed load. */
private val RETRY_DELAYS_MS = longArrayOf(30_000L, 60_000L, 120_000L)

/** How long an ad that wasn't ready may take to load when Watch Ad is tapped. */
private const val LOAD_TIMEOUT_MS = 30_000L
