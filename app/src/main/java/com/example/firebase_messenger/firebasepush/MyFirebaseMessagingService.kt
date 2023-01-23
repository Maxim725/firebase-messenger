package com.example.firebase_messenger.firebasepush

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.example.firebase_messenger.MainActivity
import com.example.firebase_messenger.R
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

const val CHANNEL_ID = "channel_id"
const val CHANNEL_NAME = "firebase_messenger"
class MyFirebaseMessagingService: FirebaseMessagingService() {
    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        if(remoteMessage.notification !== null) {
            try {
                generateNotification(
                    remoteMessage.notification!!.title!!,
                    remoteMessage.notification!!.body!!,
                )
            } catch(e: Exception) {
                Log.d("[NOTIFICATION]", "error")
            }
        }
    }

    private fun generateNotification(title: String, message: String) {
        val intent = Intent(this, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)

        val pendingIntent = PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_ONE_SHOT)

        var builder = NotificationCompat.Builder(applicationContext, CHANNEL_ID)
            .setSmallIcon((R.drawable.ic_launcher_foreground))
            .setAutoCancel(true)
            .setVibrate(longArrayOf(1000, 1000, 1000, 1000))
            .setOnlyAlertOnce(true)
            .setContentIntent(pendingIntent)

        builder = builder.setContent(getRemoteView(title, message))

        val notificationManager = getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

        if(Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            val notificationChannel = NotificationChannel(CHANNEL_ID, CHANNEL_NAME, NotificationManager.IMPORTANCE_HIGH)

            notificationManager.createNotificationChannel(notificationChannel);
        }

        notificationManager.notify(0, builder.build())
    }

    private fun getRemoteView(title: String, message: String): RemoteViews? {
        val remoteView = RemoteViews("com.example.firebase_messenger.firebasepush", R.layout.push_notification)

        remoteView.setTextViewText(R.id.tvTitle, title)
        remoteView.setTextViewText(R.id.tvMessage, message)
        remoteView.setImageViewResource(R.id.imLogo, R.drawable.ic_launcher_foreground)

        return remoteView
    }
}