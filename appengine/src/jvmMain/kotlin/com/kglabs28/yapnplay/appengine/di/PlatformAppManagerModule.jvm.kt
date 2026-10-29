package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.core.ads.LaunchOfferStore
import com.kglabs28.yapnplay.appengine.core.ads.InMemoryLaunchOfferStore
import com.kglabs28.yapnplay.appengine.core.common.InMemoryKeyboardPreference
import com.kglabs28.yapnplay.appengine.core.common.KeyboardPreference
import com.kglabs28.yapnplay.appengine.core.network.NoDeviceIdentity
import com.kglabs28.yapnplay.appengine.core.network.DeviceIdentity
import com.kglabs28.yapnplay.appengine.core.ads.RewardedAds
import com.kglabs28.yapnplay.appengine.core.ads.NoRewardedAds
import com.kglabs28.yapnplay.appengine.appmanager.NoRemoteConfigSource
import com.kglabs28.yapnplay.appengine.appmanager.RemoteConfigSource
import com.kglabs28.yapnplay.appengine.core.network.CredentialStore
import com.kglabs28.yapnplay.appengine.core.network.InMemoryCredentialStore
import com.kglabs28.yapnplay.appengine.appmanager.AppConfigStore
import com.kglabs28.yapnplay.appengine.appmanager.JvmConfigStore
import com.kglabs28.yapnplay.appengine.features.notifications.data.local.InMemoryNotificationHistoryStore
import com.kglabs28.yapnplay.appengine.features.notifications.data.local.NotificationHistoryStore
import com.kglabs28.yapnplay.appengine.core.audio.GameAudio
import com.kglabs28.yapnplay.appengine.core.common.AppVersionProvider
import com.kglabs28.yapnplay.appengine.core.common.JvmAppVersionProvider
import com.kglabs28.yapnplay.appengine.core.audio.MicLevelMonitor
import com.kglabs28.yapnplay.appengine.core.audio.SilentMicLevelMonitor
import com.kglabs28.yapnplay.appengine.core.audio.SilentGameAudio
import com.kglabs28.yapnplay.appengine.features.voice.SilentVoiceEngine
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceEngine
import org.koin.dsl.module

/** JVM local stores (in memory): appmanager config, notification history. */
val platformAppManagerModule = module {
    single<AppConfigStore> { JvmConfigStore() }
    single<NotificationHistoryStore> { InMemoryNotificationHistoryStore() }
    single<LaunchOfferStore> { InMemoryLaunchOfferStore() }
    single<CredentialStore> { InMemoryCredentialStore() }
    single<DeviceIdentity> { NoDeviceIdentity() }
    single<KeyboardPreference> { InMemoryKeyboardPreference() }
    single<GameAudio> { SilentGameAudio() }
    single<RemoteConfigSource> { NoRemoteConfigSource() }
    single<AppVersionProvider> { JvmAppVersionProvider() }
    single<MicLevelMonitor> { SilentMicLevelMonitor() }
    single<VoiceEngine> { SilentVoiceEngine() }
    single<RewardedAds> { NoRewardedAds() }
}
