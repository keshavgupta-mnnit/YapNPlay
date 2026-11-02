package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.core.ads.LaunchOfferStore
import com.kglabs28.yapnplay.appengine.core.ads.AndroidLaunchOfferStore
import com.kglabs28.yapnplay.appengine.core.common.AndroidKeyboardPreference
import com.kglabs28.yapnplay.appengine.core.common.KeyboardPreference
import com.kglabs28.yapnplay.appengine.core.network.AndroidDeviceIdentity
import com.kglabs28.yapnplay.appengine.core.network.DeviceIdentity
import org.koin.android.ext.koin.androidApplication
import com.kglabs28.yapnplay.appengine.core.ads.RewardedAds
import com.kglabs28.yapnplay.appengine.core.ads.AdMobRewardedAds
import com.kglabs28.yapnplay.appengine.core.ads.NativeAdPool
import com.kglabs28.yapnplay.appengine.appmanager.FirebaseRemoteConfigSource
import com.kglabs28.yapnplay.appengine.appmanager.RemoteConfigSource
import com.kglabs28.yapnplay.appengine.core.network.CredentialStore
import com.kglabs28.yapnplay.appengine.core.network.AndroidCredentialStore
import com.kglabs28.yapnplay.appengine.appmanager.AndroidConfigStore
import com.kglabs28.yapnplay.appengine.appmanager.AppConfigStore
import com.kglabs28.yapnplay.appengine.features.notifications.data.local.AndroidNotificationHistoryStore
import com.kglabs28.yapnplay.appengine.features.notifications.data.local.NotificationHistoryStore
import com.kglabs28.yapnplay.appengine.core.audio.AndroidGameAudio
import com.kglabs28.yapnplay.appengine.core.common.AndroidAppVersionProvider
import com.kglabs28.yapnplay.appengine.core.common.AppVersionProvider
import com.kglabs28.yapnplay.appengine.core.audio.AndroidMicLevelMonitor
import com.kglabs28.yapnplay.appengine.core.audio.MicLevelMonitor
import com.kglabs28.yapnplay.appengine.core.audio.GameAudio
import com.kglabs28.yapnplay.appengine.features.voice.LiveKitVoiceEngine
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceEngine
import org.koin.android.ext.koin.androidContext
import org.koin.dsl.module

/** Android platform services: local stores (SharedPreferences), game audio, the mic level, live voice and rewarded ads. */
val platformAppManagerModule = module {
    single<AppConfigStore> { AndroidConfigStore(androidContext()) }
    single<NotificationHistoryStore> { AndroidNotificationHistoryStore(androidContext()) }
    single<LaunchOfferStore> { AndroidLaunchOfferStore(androidContext()) }
    single<CredentialStore> { AndroidCredentialStore(androidContext()) }
    single<DeviceIdentity> { AndroidDeviceIdentity(androidContext()) }
    single<KeyboardPreference> { AndroidKeyboardPreference(androidContext()) }
    single<GameAudio> { AndroidGameAudio(androidContext()) }
    single<RemoteConfigSource> { FirebaseRemoteConfigSource() }
    single<AppVersionProvider> { AndroidAppVersionProvider(androidContext()) }
    single<MicLevelMonitor> { AndroidMicLevelMonitor(androidContext()) }
    single<VoiceEngine> { LiveKitVoiceEngine(androidContext()) }
    // Made at start, so it sees the first screen resume (it needs the visible Activity to play).
    single<RewardedAds>(createdAtStart = true) { AdMobRewardedAds(androidApplication(), get()) }
    // Started with the app, so the first ad pill (the connecting card's) has an ad as soon as possible.
    single(createdAtStart = true) { NativeAdPool(androidApplication(), get()) }
}
