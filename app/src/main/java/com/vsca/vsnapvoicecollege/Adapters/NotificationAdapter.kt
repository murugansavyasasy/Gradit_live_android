package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.vsca.vsnapvoicecollege.Activities.*
import com.vsca.vsnapvoicecollege.Model.GetNotificationDetails
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import java.time.LocalTime
import java.time.format.DateTimeFormatter

class NotificationAdapter constructor(data: List<GetNotificationDetails>, context: Context) :
    RecyclerView.Adapter<NotificationAdapter.MyViewHolder>() {
    var notificationList: List<GetNotificationDetails> = ArrayList()
    var context: Context
    var Position: Int = 0
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView: View = LayoutInflater.from(parent.context)
            .inflate(R.layout.activity_notification, parent, false)
        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data: GetNotificationDetails = notificationList.get(position)
        Position = holder.absoluteAdapterPosition
        holder.lblMemberName!!.text = data.title
        holder.lblNotificationContent!!.text = data.notification_content
        val date: String = data.sentOn!!
        val splitDate: Array<String> = date.split("\\s+".toRegex()).toTypedArray()
        val Date: String = splitDate.get(0)
        holder.lblDate!!.text = Date
        val time: String = splitDate.get(1)

        val notificationTime: Array<String> = time.split("\\.".toRegex()).toTypedArray()
        val time1: String = notificationTime.get(0)
        val time2: String = notificationTime.get(1)

        val result = LocalTime.parse(time1).format(DateTimeFormatter.ofPattern("h:mm a"))
        holder.lblNoticeficationTime!!.text = result

        holder.rytOverAll!!.setOnClickListener {

            if (data.module_type.equals("Videos")) {
                val menuid = BaseActivity.VideoMenuID
                Log.d("VideoMenuID", menuid)
                CommonUtil.MenuIDVideo = menuid
                val i = Intent(context, Video::class.java)
                context.startActivity(i)

            } else if (data.module_type.equals("Circular")) {
                val menuid = BaseActivity.CircularMenuID
                Log.d("CircularMenuID", menuid)
                CommonUtil.MenuIDCircular = menuid
                val i = Intent(context, Circular::class.java)
                context.startActivity(i)

            } else if (data.module_type.equals("Communication")) {
                val menuid = BaseActivity.CommunicationMenuID
                Log.d("CommunicationMenuID", menuid)
                CommonUtil.MenuIDCommunication = menuid
                val i = Intent(context, Communication::class.java)
                context.startActivity(i)

            } else if (data.module_type.equals("Events")) {
                val menuid = BaseActivity.EventsMenuID
                Log.d("EventsMenuID", menuid)
                CommonUtil.MenuIDEvents = menuid
                val i = Intent(context, Events::class.java)
                context.startActivity(i)

            } else if (data.module_type.equals("Notice board")) {
                val menuid = BaseActivity.NoticeboardMenuID
                Log.d("NoticeboardMenuID", menuid)
                CommonUtil.MenuIDNoticeboard = menuid
                val i = Intent(context, Noticeboard::class.java)
                context.startActivity(i)

            } else if (data.module_type.equals("Assignments")) {
                val menuid = BaseActivity.AssignmentMenuID
                Log.d("AssignmentMenuID", menuid)
                CommonUtil.MenuIDAssignment = menuid
                val i = Intent(context, Assignment::class.java)
                context.startActivity(i)


            } else if (data.module_type.equals("Chat")) {
                val menuid = BaseActivity.ChatMenuID
                Log.d("ChatMenuID", menuid)
                CommonUtil.MenuIDChat = menuid
                val i = Intent(context, ChatParent::class.java)
                context.startActivity(i)

            } else if (data.module_type.equals("Attendance")) {
                val menuid = BaseActivity.AttendanceMeuID
                Log.d("AttendanceMeuID", menuid)
                CommonUtil.MenuIdAttendance = menuid
                val i = Intent(context, Attendance::class.java)
                context.startActivity(i)

            } else if (data.module_type.equals("Examination")) {
                val menuid = BaseActivity.ExamMenuID
                Log.d("ExamMenuID", menuid)
                CommonUtil.MenuIDExamination = menuid
                val i = Intent(context, ExamList::class.java)
                context.startActivity(i)

            }
        }
    }

    override fun getItemCount(): Int {
        return notificationList.size
    }

    inner class MyViewHolder constructor(itemView: View?) : RecyclerView.ViewHolder(
        (itemView)!!
    ) {
        @JvmField
        @BindView(R.id.lblMemberName)
        var lblMemberName: TextView? = null

        @JvmField
        @BindView(R.id.lblNotificationContent)
        var lblNotificationContent: TextView? = null

        @JvmField
        @BindView(R.id.lblNoticeficationTime)
        var lblNoticeficationTime: TextView? = null

        @JvmField
        @BindView(R.id.lblDate)
        var lblDate: TextView? = null

        @JvmField
        @BindView(R.id.rytOverAll)
        var rytOverAll: RelativeLayout? = null

        init {
            ButterKnife.bind(this, (itemView)!!)
        }
    }

    init {
        notificationList = data
        this.context = context
    }
}