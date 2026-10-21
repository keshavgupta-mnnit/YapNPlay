package com.kglabs28.yapnplay

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Build
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.kglabs28.yapnplay.appengine.core.common.DeepLinks
import com.kglabs28.yapnplay.appengine.protocol.PushKeys

/**
 * Room notifications: activity ("Neha entered College Crew", one per room,
 * replaced as the count changes; a tap opens that room's lobby through
 * yapnplay://room/<roomId>) and group invites ("Riya invited you to
 * Hostel Squad"; a tap opens the app, whose Rooms tab shows the invite).
 */
object RoomNotifications {

    private const val CHANNEL_ID = "room_activity"

    /** Call once at app start (Android 8+ needs the channel first). */
    fun createChannel(context: Context) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.O) return
        val channel = NotificationChannel(
            CHANNEL_ID,
            context.getString(R.string.notification_channel_rooms),
            NotificationManager.IMPORTANCE_DEFAULT,
        ).apply { description = context.getString(R.string.notification_channel_rooms_description) }
        context.getSystemService(NotificationManager::class.java).createNotificationChannel(channel)
    }

    /** Shows a room-activity or group-invite push; ignores anything else or when not allowed. */
    fun show(context: Context, data: Map<String, String>) {
        val type = data[PushKeys.TYPE]
        if (type != PushKeys.TYPE_ROOM_ACTIVITY && type != PushKeys.TYPE_ROOM_INVITE) return
        val roomId = data[PushKeys.ROOM_ID] ?: return
        if (!canNotify(context)) return
        val invite = type == PushKeys.TYPE_ROOM_INVITE
        val key = if (invite) (data[PushKeys.INVITE_ID] ?: roomId).hashCode() else roomId.hashCode()

        val open = if (invite) {
            Intent(context, MainActivity::class.java)
        } else {
            Intent(Intent.ACTION_VIEW, Uri.parse(DeepLinks.roomUri(roomId)), context, MainActivity::class.java)
        }.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_SINGLE_TOP)
        val tap = PendingIntent.getActivity(
            context,
            key,
            open,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT,
        )
        val notification = NotificationCompat.Builder(context, CHANNEL_ID)
            .setSmallIcon(R.mipmap.ic_launcher)
            .setContentTitle(data[PushKeys.TITLE] ?: data[PushKeys.ROOM_NAME])
            .setContentText(data[PushKeys.BODY])
            .setAutoCancel(true)
            .setContentIntent(tap)
            .build()
        if (canNotify(context)) {
            @Suppress("MissingPermission")
            NotificationManagerCompat.from(context).notify(key, notification)
        }
    }

    private fun canNotify(context: Context): Boolean =
        Build.VERSION.SDK_INT < Build.VERSION_CODES.TIRAMISU ||
            ContextCompat.checkSelfPermission(context, Manifest.permission.POST_NOTIFICATIONS) ==
            PackageManager.PERMISSION_GRANTED
}
