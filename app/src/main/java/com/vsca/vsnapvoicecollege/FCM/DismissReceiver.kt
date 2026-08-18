package com.vs.schoolmessenger.FCM

import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationManagerCompat
import com.vs.schoolmessenger.FCM.AnnouncementStatusManager.sendStatus

class DismissReceiver : BroadcastReceiver() {

    override fun onReceive(
        context: Context,
        intent: Intent
    ) {
       val voiceUrl = intent.getStringExtra("url")
       val welcomeUrl = intent.getStringExtra("welcome")
       val notificationId = intent.getIntExtra("isNotificationId", -1)

       val welcome_file = intent.getStringExtra("welcome")
        val school_name = intent.getStringExtra("school_name")
        val member_name = intent.getStringExtra("member_name")
        val call_title = intent.getStringExtra("call_title")

        val ei1 = intent.getStringExtra("ei1")
       val ei2 = intent.getStringExtra("ei2")
       val ei3 = intent.getStringExtra("ei3")
       val ei4 = intent.getStringExtra("ei4")
       val ei5 = intent.getStringExtra("ei5")
       val receiver_id = intent.getStringExtra("isReceiverId")
       val retrycount = intent.getStringExtra("retry_count")
       val circular_id = intent.getStringExtra("circularId")

        sendStatus(
            context,
            voiceUrl,
            welcomeUrl,
            notificationId,
            welcome_file,
            school_name,
            member_name,
            call_title,
            ei1,
            ei2,
            ei3,
            ei4,
            ei5,
            circular_id,
            receiver_id,
            retrycount
        )

        RingtonePlayer.stop()
        if (!circular_id.isNullOrEmpty()) {

            NotificationManagerCompat
                .from(context)
                .cancel(circular_id.hashCode())

            val notificationManager =
                context.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager

            notificationManager.cancel(circular_id.hashCode())
        }
    }
}