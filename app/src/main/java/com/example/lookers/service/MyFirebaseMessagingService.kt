package com.example.lookers.service

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import com.example.lookers.R
import com.example.lookers.view.activity.MainActivity
import com.example.lookers.view.activity.drawer.DrawerUnitActivity
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import dagger.hilt.android.AndroidEntryPoint
import timber.log.Timber

@AndroidEntryPoint
class MyFirebaseMessagingService : FirebaseMessagingService() {
    override fun onNewToken(token: String) {
        super.onNewToken(token)
        Timber.tag(TAG).d("Firebase fcm token...... $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        super.onMessageReceived(remoteMessage)
        Timber.tag(TAG).d("Firebase fcm message...... $remoteMessage")

        processNotification(remoteMessage)
    }

    private fun processNotification(remoteMessage: RemoteMessage) {
        val title =
            if (remoteMessage.notification != null) {
                remoteMessage.notification?.title
            } else {
                remoteMessage.data["title"]
            }

        val body =
            if (remoteMessage.notification != null) {
                remoteMessage.notification?.body
            } else {
                remoteMessage.data["body"]
            }

        val data = remoteMessage.data

        val intent =
            when (data["type"]) {
                "INSERT", "WITHDRAW" ->
                    Intent(this, DrawerUnitActivity::class.java).apply {
                        putExtra("drawerId", data["drawerId"]?.toInt() ?: -1)
                        putExtra("drawerUnitId", data["drawerUnitId"]?.toInt() ?: -1)
                        putExtra("firebaseMessage", true)
                    }

                else -> Intent(this, MainActivity::class.java)
            }.apply {
                addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            }

        val pendingIntent =
            PendingIntent.getActivity(
                this,
                0,
                intent,
                PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE,
            )

        val notificationManager =
            getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        createNotificationChannelIfNeeded(notificationManager)

        val notificationId = System.currentTimeMillis().toInt()
        val notificationBuilder =
            NotificationCompat
                .Builder(this, CHANNEL_ID)
                .setSmallIcon(R.mipmap.ic_launcher_temp)
                .setContentTitle(title)
                .setContentText(body)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setContentIntent(pendingIntent)

        notificationManager.notify(notificationId, notificationBuilder.build())
    }

    private fun createNotificationChannelIfNeeded(notificationManager: NotificationManager) {
        if (notificationManager.getNotificationChannel(CHANNEL_ID) == null) {
            val channel =
                NotificationChannel(
                    CHANNEL_ID,
                    "FCM Channel",
                    NotificationManager.IMPORTANCE_HIGH,
                ).apply {
                    description = "Channel for FCM notifications"
                }
            notificationManager.createNotificationChannel(channel)
        }
    }

    companion object {
        private const val TAG = "MyFirebaseMessagingService"
        private const val CHANNEL_ID = "fcm_channel"
    }
}
