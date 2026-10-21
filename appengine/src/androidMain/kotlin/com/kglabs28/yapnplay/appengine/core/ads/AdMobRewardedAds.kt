package com.kglabs28.yapnplay.appengine.core.ads

import android.app.Activity
import android.app.Application
import android.os.Bundle
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
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext

/**
 * AdMob rewarded ads. The unit id comes from AppManager (Google's test
 * unit until a real one is set). The ad needs the visible Activity, so
 * the app's resumed activity is tracked here.
 */
class AdMobRewardedAds(
    private val application: Application,
    private val config: AdsAppManagerHelper,
) : RewardedAds {

    private var resumed = WeakReference<Activity>(null)
    private var initialized = false

    init {
        application.registerActivityLifecycleCallbacks(object : Application.ActivityLifecycleCallbacks {
            override fun onActivityResumed(activity: Activity) { resumed = WeakReference(activity) }
            override fun onActivityPaused(activity: Activity) = Unit
            override fun onActivityCreated(activity: Activity, savedInstanceState: Bundle?) = Unit
            override fun onActivityStarted(activity: Activity) = Unit
            override fun onActivityStopped(activity: Activity) = Unit
            override fun onActivitySaveInstanceState(activity: Activity, outState: Bundle) = Unit
            override fun onActivityDestroyed(activity: Activity) = Unit
        })
    }

    override suspend fun show(): Boolean = withContext(Dispatchers.Main) {
        if (!config.isEnabled()) return@withContext false
        val activity = resumed.get() ?: return@withContext false
        if (!initialized) {
            MobileAds.initialize(application)
            initialized = true
        }
        val ad = load(activity) ?: return@withContext false
        play(activity, ad)
    }

    private suspend fun load(activity: Activity): RewardedAd? = suspendCancellableCoroutine { cont ->
        RewardedAd.load(activity, config.rewardedUnitId(), AdRequest.Builder().build(), object : RewardedAdLoadCallback() {
            override fun onAdLoaded(ad: RewardedAd) { if (cont.isActive) cont.resume(ad) }
            override fun onAdFailedToLoad(error: LoadAdError) {
                CrashReporter.record(IllegalStateException(error.message), "AdMobRewardedAds.load:" + error.code)
                if (cont.isActive) cont.resume(null)
            }
        })
    }

    /** Plays [ad]; resumes when it closes, true if the reward was earned. */
    private suspend fun play(activity: Activity, ad: RewardedAd): Boolean = suspendCancellableCoroutine { cont ->
        var earned = false
        ad.fullScreenContentCallback = object : FullScreenContentCallback() {
            override fun onAdDismissedFullScreenContent() { if (cont.isActive) cont.resume(earned) }
            override fun onAdFailedToShowFullScreenContent(error: AdError) {
                CrashReporter.record(IllegalStateException(error.message), "AdMobRewardedAds.show:" + error.code)
                if (cont.isActive) cont.resume(false)
            }
        }
        ad.show(activity) { earned = true }
    }
}
