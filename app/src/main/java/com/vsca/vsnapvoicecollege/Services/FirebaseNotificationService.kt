package com.vsca.vsnapvoicecollege.Services

import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.ContentResolver
import android.content.Intent
import android.graphics.BitmapFactory
import android.graphics.Color
import android.media.AudioAttributes
import android.net.Uri
import android.os.Build
import android.text.format.DateUtils
import android.util.Log
import android.widget.RemoteViews
import androidx.core.app.NotificationCompat
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.vsca.vsnapvoicecollege.Activities.Splash
import com.vsca.vsnapvoicecollege.R
import java.util.*


class FirebaseNotificationService : FirebaseMessagingService() {
    var notification: Notification? = null
    private var notificationManager: NotificationManager? = null
    private val NotificationID = 1005
    var contentViewBig: RemoteViews? = null
    var contentViewSmall: RemoteViews? = null
    var NOTIFICATION_ID = 100
    val NOTIFICATION_ID_BIG_IMAGE = 101
    var mBuilder: NotificationCompat.Builder? = null

    private val TAG = FirebaseNotificationService::class.java.simpleName

    override fun onNewToken(token: String) {
        Log.i("NotificationsService", "Token was updated 🔒")

    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        Log.d("Received", "Receiveed")
        if (remoteMessage.data["body"] != null) {
            Log.d(TAG, "Notificationbody: " + remoteMessage.data["body"])
            createNotification(
                remoteMessage.data["body"]!!,
                remoteMessage.data["title"]!!,
            )
        }

    }

    private fun createNotification(messageBody: String, title: String) {
        val channelId: String
        channelId = "sound_one"

        val mChannel: NotificationChannel
        notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        mBuilder = NotificationCompat.Builder(applicationContext, channelId)
        val intent = Intent(this, Splash::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        val pendingIntent = PendingIntent.getActivity(
            this, 0 /* Request code */, intent,
            PendingIntent.FLAG_ONE_SHOT
        )
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            mChannel = NotificationChannel(
                channelId, "Custom Remainder Notification",
                NotificationManager.IMPORTANCE_HIGH
            )
//            val attributes = AudioAttributes.Builder()
//                .setContentType(AudioAttributes.CONTENT_TYPE_SONIFICATION)
//                .setUsage(AudioAttributes.USAGE_ALARM)
//                .build()
//            mChannel.lightColor = Color.GREEN
//            mChannel.enableLights(true)
//            mChannel.description = "Custom Notification"
//            mChannel.lockscreenVisibility = Notification.VISIBILITY_PRIVATE
//            mChannel.setSound(neworderSound, attributes)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.createNotificationChannel(mChannel)
            contentViewBig = RemoteViews(packageName, R.layout.exapnded_one)
            contentViewBig!!.setTextViewText(
                R.id.timestamp,
                DateUtils.formatDateTime(
                    this,
                    System.currentTimeMillis(),
                    DateUtils.FORMAT_SHOW_TIME
                )
            )
            contentViewBig!!.setTextViewText(R.id.content_title, title)
            contentViewBig!!.setTextViewText(R.id.content_text, messageBody)
            contentViewSmall = RemoteViews(packageName, R.layout.notification_design)
            contentViewSmall!!.setTextViewText(
                R.id.timestamp,
                DateUtils.formatDateTime(
                    this,
                    System.currentTimeMillis(),
                    DateUtils.FORMAT_SHOW_TIME
                )
            )
            mBuilder!!.setSmallIcon(R.drawable.gradit_logo)
            mBuilder!!.setLargeIcon(
                BitmapFactory.decodeResource(
                    resources,
                    R.drawable.gradit_logo
                )
            )
            mBuilder!!.setContentTitle(title)
            mBuilder!!.setContentText(messageBody)
            mBuilder!!.setChannelId(channelId)
            mBuilder!!.setContentIntent(pendingIntent)
            mBuilder!!.setStyle(
                NotificationCompat.BigTextStyle().bigText(title)
            )
            mBuilder!!.setStyle(
                NotificationCompat.BigTextStyle().bigText(messageBody)
            )
            mBuilder!!.setDefaults(Notification.DEFAULT_ALL)
            mBuilder!!.setAutoCancel(true)
            mBuilder!!.setContent(contentViewBig)
            NOTIFICATION_ID = (Date().time / 1000L % Int.MAX_VALUE).toInt()
            Log.d("Config.NOTIFICATION_ID", "ID: " + NOTIFICATION_ID)
            notification = mBuilder!!.build()
            notificationManager.notify(
                NotificationID,
                notification
            )
        } else {
            mBuilder =
                NotificationCompat.Builder(applicationContext, channelId)
            Log.d("below_8", "notification")
            contentViewBig = RemoteViews(packageName, R.layout.exapnded_one)
            contentViewBig!!.setTextViewText(
                R.id.timestamp,
                DateUtils.formatDateTime(
                    this,
                    System.currentTimeMillis(),
                    DateUtils.FORMAT_SHOW_TIME
                )
            )
            contentViewBig!!.setTextViewText(R.id.content_title, title)
            contentViewBig!!.setTextViewText(R.id.content_text, messageBody)
            contentViewSmall = RemoteViews(packageName, R.layout.notification_design)
            contentViewSmall!!.setTextViewText(
                R.id.timestamp,
                DateUtils.formatDateTime(
                    this,
                    System.currentTimeMillis(),
                    DateUtils.FORMAT_SHOW_TIME
                )
            )
            val notificationBuilder: NotificationCompat.Builder =
                NotificationCompat.Builder(this, channelId)
                    .setSmallIcon(R.drawable.gradit_logo)
                    .setLargeIcon(BitmapFactory.decodeResource(resources, R.drawable.gradit_logo))
                    .setContentTitle(title)
                    .setContentText(messageBody)
                    .setAutoCancel(true)
                    .setPriority(Notification.PRIORITY_MAX)
                    .setContentIntent(pendingIntent)
                    .setStyle(NotificationCompat.BigTextStyle().bigText(title))
                    .setStyle(NotificationCompat.BigTextStyle().bigText(messageBody))
                    .setContent(contentViewBig)
                    .setDefaults(Notification.DEFAULT_ALL)
            val notificationManager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
            notificationManager.notify(0 /* ID of notification */, notificationBuilder.build())
        }
    }
}