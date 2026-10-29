package com.kglabs28.yapnplay.appengine.core.ads

import android.content.Context

/** SharedPreferences-backed, so "once a day" holds across app restarts. */
class AndroidLaunchOfferStore(context: Context) : LaunchOfferStore {

    private val prefs = context.getSharedPreferences("yapnplay_ads", Context.MODE_PRIVATE)

    override fun noAdDay(): String? = prefs.getString(KEY_NO_AD_DAY, null)

    override fun saveNoAdDay(day: String) {
        prefs.edit().putString(KEY_NO_AD_DAY, day).apply()
    }

    private companion object {
        const val KEY_NO_AD_DAY = "no_ad_day"
    }
}
