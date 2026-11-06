package com.kglabs28.yapnplay.appengine.di

import com.kglabs28.yapnplay.appengine.appmanager.AppConfigStore
import com.kglabs28.yapnplay.appengine.appmanager.NoRemoteConfigSource
import com.kglabs28.yapnplay.appengine.appmanager.RemoteConfigSource
import com.kglabs28.yapnplay.appengine.appmanager.WebConfigStore
import com.kglabs28.yapnplay.appengine.core.ads.NoRewardedAds
import com.kglabs28.yapnplay.appengine.core.ads.RewardedAds
import com.kglabs28.yapnplay.appengine.core.audio.GameAudio
import com.kglabs28.yapnplay.appengine.core.audio.MicLevelMonitor
import com.kglabs28.yapnplay.appengine.core.audio.WebGameAudio
import com.kglabs28.yapnplay.appengine.core.audio.SilentMicLevelMonitor
import com.kglabs28.yapnplay.appengine.core.auth.AccountEntry
import com.kglabs28.yapnplay.appengine.core.auth.GoogleRedirectSignIn
import com.kglabs28.yapnplay.appengine.core.auth.GoogleSignInLauncher
import com.kglabs28.yapnplay.appengine.core.common.AppVersionProvider
import com.kglabs28.yapnplay.appengine.core.common.InMemoryKeyboardPreference
import com.kglabs28.yapnplay.appengine.core.common.KeyboardPreference
import com.kglabs28.yapnplay.appengine.core.common.WebAppVersionProvider
import com.kglabs28.yapnplay.appengine.core.network.CredentialStore
import com.kglabs28.yapnplay.appengine.core.network.DeviceIdentity
import com.kglabs28.yapnplay.appengine.core.network.NoDeviceIdentity
import com.kglabs28.yapnplay.appengine.core.network.SocketAuthorizer
import com.kglabs28.yapnplay.appengine.core.network.TicketSocketAuthorizer
import com.kglabs28.yapnplay.appengine.core.network.WebCredentialStore
import com.kglabs28.yapnplay.appengine.features.nearby.domain.HostFitness
import com.kglabs28.yapnplay.appengine.features.nearby.transport.NearbyTransport
import com.kglabs28.yapnplay.appengine.features.nearby.transport.NoNearbyTransport
import com.kglabs28.yapnplay.appengine.features.notifications.data.local.InMemoryNotificationHistoryStore
import com.kglabs28.yapnplay.appengine.features.notifications.data.local.NotificationHistoryStore
import com.kglabs28.yapnplay.appengine.features.voice.LiveKitWebVoiceEngine
import com.kglabs28.yapnplay.appengine.core.common.BrowserFullScreen
import com.kglabs28.yapnplay.appengine.core.common.FullScreen
import com.kglabs28.yapnplay.appengine.features.voice.data.remote.VoiceEngine
import org.koin.dsl.module

/**
 * The web app: Google sign-in only (no guest), the account and settings in
 * the browser's storage, a one-time ticket for the WebSocket, voice over
 * LiveKit's browser SDK, game sounds over Web Audio. No ads or Nearby.
 */
val platformAppManagerModule = module {
    single { AccountEntry.GOOGLE }
    single<GoogleSignInLauncher> { GoogleRedirectSignIn() }
    single<SocketAuthorizer> { TicketSocketAuthorizer(get()) }
    single<AppConfigStore> { WebConfigStore() }
    single<CredentialStore> { WebCredentialStore() }
    single<NotificationHistoryStore> { InMemoryNotificationHistoryStore() }
    single<DeviceIdentity> { NoDeviceIdentity() }
    single<KeyboardPreference> { InMemoryKeyboardPreference() }
    single<GameAudio> { WebGameAudio() }
    single<RemoteConfigSource> { NoRemoteConfigSource() }
    single<AppVersionProvider> { WebAppVersionProvider() }
    single<MicLevelMonitor> { SilentMicLevelMonitor() }
    single<VoiceEngine> { LiveKitWebVoiceEngine() }
    single<FullScreen> { BrowserFullScreen() }
    single<RewardedAds> { NoRewardedAds() }
    single<NearbyTransport> { NoNearbyTransport() }
    single<HostFitness> { HostFitness { NO_NEARBY } }
}

/** The web never hosts Nearby play. */
private const val NO_NEARBY = 0
