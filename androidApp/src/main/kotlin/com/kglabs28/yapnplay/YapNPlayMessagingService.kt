package com.kglabs28.yapnplay

import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.kglabs28.yapnplay.appengine.core.common.CrashReporter
import com.kglabs28.yapnplay.appengine.features.notifications.usecase.RegisterPushTokenUseCase
import com.kglabs28.yapnplay.appengine.features.notifications.usecase.SaveNotificationUseCase
import com.kglabs28.yapnplay.appengine.protocol.PushKeys
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import org.koin.core.context.GlobalContext

/**
 * Receives Firebase pushes. Room-activity data messages become
 * notifications; a new token is sent to the server so pushes keep
 * reaching this device.
 */
class YapNPlayMessagingService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        registerPushToken(token)
    }

    override fun onMessageReceived(message: RemoteMessage) {
        // History first: the bell lists it even when notifications are off.
        try {
            GlobalContext.get().get<SaveNotificationUseCase>()(message.data)
        } catch (e: Exception) {
            CrashReporter.record(e, "YapNPlayMessagingService.saveNotification")
        }
        RoomNotifications.show(this, message.data)
    }

    companion object {
        private val scope = CoroutineScope(SupervisorJob() + Dispatchers.IO)

        /** Sends [token] to the server through the shared use case. */
        fun registerPushToken(token: String) {
            scope.launch {
                try {
                    GlobalContext.get().get<RegisterPushTokenUseCase>()(token, PushKeys.PLATFORM_ANDROID)
                } catch (e: Exception) {
                    CrashReporter.record(e, "YapNPlayMessagingService.registerPushToken")
                }
            }
        }
    }
}
