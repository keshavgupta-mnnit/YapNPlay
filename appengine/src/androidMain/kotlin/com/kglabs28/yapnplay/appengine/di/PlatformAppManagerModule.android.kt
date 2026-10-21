package com.kglabs28.yapnplay.appengine.di

import org.koin.android.ext.koin.androidApplication
import com.kglabs28.yapnplay.appengine.core.ads.RewardedAds
import com.kglabs28.yapnplay.appengine.core.ads.AdMobRewardedAds
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
    single<CredentialStore> { AndroidCredentialStore(androidContext()) }
    single<GameAudio> { AndroidGameAudio(androidContext()) }
    single<RemoteConfigSource> { FirebaseRemoteConfigSource() }
    single<AppVersionProvider> { AndroidAppVersionProvider(androidContext()) }
    single<MicLevelMonitor> { AndroidMicLevelMonitor(androidContext()) }
    single<VoiceEngine> { LiveKitVoiceEngine(androidContext()) }
    single<RewardedAds> { AdMobRewardedAds(androidApplication(), get()) }
}
