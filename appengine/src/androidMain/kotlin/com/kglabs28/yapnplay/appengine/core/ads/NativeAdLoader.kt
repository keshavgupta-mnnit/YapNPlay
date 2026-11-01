package com.kglabs28.yapnplay.appengine.core.ads

import android.content.Context
import com.google.android.gms.ads.AdListener
import com.google.android.gms.ads.AdLoader
import com.google.android.gms.ads.AdRequest
import com.google.android.gms.ads.LoadAdError
import com.google.android.gms.ads.nativead.NativeAd
import com.google.android.gms.ads.nativead.NativeAdOptions
import com.kglabs28.yapnplay.appengine.core.common.AppConstants
import com.kglabs28.yapnplay.appengine.core.common.AppLog
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter

/**
 * Loads one native ad for the ad pill from [unitId]; [onLoaded] gets it
 * (the caller destroys it when done). No fill or no network just logs:
 * the pill stays empty. [cancel] drops a load still on its way.
 */
class NativeAdLoader(private val context: Context, private val unitId: String, private val onLoaded: (NativeAd) -> Unit) {

    private var cancelled = false

    fun load() {
        AppLog.d(TAG, "native: load started (unit=$unitId)")
        val loader = AdLoader.Builder(context, unitId)
            .forNativeAd { ad ->
                // Arrived after the pill left the screen: nobody will show it.
                if (cancelled) ad.destroy() else onLoaded(ad).also { AppLog.d(TAG, "native: ad loaded") }
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    AppLog.d(TAG, "native: load failed code=${error.code} message=${error.message}")
                    if (error.code != AdRequest.ERROR_CODE_NETWORK_ERROR && error.code != AdRequest.ERROR_CODE_NO_FILL) {
                        CrashReporter.record(IllegalStateException(error.message), "NativeAdLoader.load:" + error.code)
                    }
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT).build())
            .build()
        loader.loadAd(AdRequest.Builder().build())
    }

    fun cancel() {
        cancelled = true
    }
}

private const val TAG = AppConstants.LOG_TAG_ADS
