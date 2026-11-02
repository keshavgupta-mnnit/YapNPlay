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
 * Loads one native ad for the ad pill from [unitId]: [onLoaded] gets it,
 * [onFailed] says it didn't come. No fill or no network only logs; other
 * failures (setup mistakes) are reported.
 */
class NativeAdLoader(
    private val context: Context,
    private val unitId: String,
    private val onLoaded: (NativeAd) -> Unit,
    private val onFailed: () -> Unit,
) {
    fun load() {
        AppLog.d(TAG, "native: load started (unit=$unitId)")
        val loader = AdLoader.Builder(context, unitId)
            .forNativeAd { ad ->
                AppLog.d(TAG, "native: ad loaded")
                onLoaded(ad)
            }
            .withAdListener(object : AdListener() {
                override fun onAdFailedToLoad(error: LoadAdError) {
                    AppLog.d(TAG, "native: load failed code=${error.code} message=${error.message}")
                    if (error.code != AdRequest.ERROR_CODE_NETWORK_ERROR && error.code != AdRequest.ERROR_CODE_NO_FILL) {
                        CrashReporter.record(IllegalStateException(error.message), "NativeAdLoader.load:" + error.code)
                    }
                    onFailed()
                }
            })
            .withNativeAdOptions(NativeAdOptions.Builder().setAdChoicesPlacement(NativeAdOptions.ADCHOICES_TOP_RIGHT).build())
            .build()
        loader.loadAd(AdRequest.Builder().build())
    }
}

private const val TAG = AppConstants.LOG_TAG_ADS
