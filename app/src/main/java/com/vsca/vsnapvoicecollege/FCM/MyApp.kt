package com.vsca.vsnapvoicecollege.FCM

import android.app.Application
import android.app.Notification
import android.app.NotificationChannel
import android.app.NotificationManager
import android.os.Build
import android.util.Log
import androidx.lifecycle.LifecycleObserver
import com.google.firebase.FirebaseApp

class MyApp : Application(), LifecycleObserver {

    companion object {
        const val CHANNEL_ID = "notification_collage"
        const val CHANNEL_NAME= "School Notifications"
    }

    override fun onCreate() {
        super.onCreate()

        FirebaseApp.initializeApp(this)


        val defaultHandler = Thread.getDefaultUncaughtExceptionHandler()

        Thread.setDefaultUncaughtExceptionHandler { thread, throwable ->
            Log.e("CRASH", "Uncaught: ${throwable.message}", throwable)
            defaultHandler?.uncaughtException(thread, throwable)
        }
        createCallChannel()

    }

    private fun createCallChannel() {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {


//            val call_notification_sound = Uri.parse(
//                "android.resource://$packageName/${R.raw.call_notification}"
//            )
//            val attributes = AudioAttributes.Builder()
//                .setUsage(AudioAttributes.USAGE_NOTIFICATION_RINGTONE)
//                .build()

            val channel = NotificationChannel(
                CHANNEL_ID,
                "Collage Emergency Alerts",
                NotificationManager.IMPORTANCE_HIGH
            ).apply {
                description = "Collage Emergency Alerts"
                lockscreenVisibility = Notification.VISIBILITY_PUBLIC
                setSound(null, null)
                enableVibration(false)
            }

            val manager =
                getSystemService(NotificationManager::class.java)

            manager.createNotificationChannel(channel)
        }
    }
}