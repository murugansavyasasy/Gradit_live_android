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
                for (i in CommonUtil.MenuListDashboard.indices){
                    if (10 == CommonUtil.MenuListDashboard.get(i).id){
                        CommonUtil.menu_readVideo = CommonUtil.MenuListDashboard.get(i).is_read_enabled.toString()
                        CommonUtil.menu_writeVideo = CommonUtil.MenuListDashboard.get(i).is_write_enabled.toString()
                    }
                }
                val menuid = BaseActivity.VideoMenuID
                Log.d("VideoMenuID", menuid)
                CommonUtil.MenuIDVideo = menuid
                val i = Intent(context, Video::class.java)
                context.startActivity(i)

            } else if (data.module_type.equals("Circular")) {
                for (i in CommonUtil.MenuListDashboard.indices){
                    if (6 == CommonUtil.MenuListDashboard.get(i).id){
                        CommonUtil.menu_readCircular = CommonUtil.MenuListDashboard.get(i).is_read_enabled.toString()
                        CommonUtil.menu_writeCircular = CommonUtil.MenuListDashboard.get(i).is_write_enabled.toString()
                    }
                }

                val menuid = BaseActivity.CircularMenuID
                Log.d("CircularMenuID", menuid)
                CommonUtil.MenuIDCircular = menuid
                val i = Intent(context, Circular::class.java)
                context.startActivity(i)

            } else if (data.module_type.equals("Communication")) {
                for (i in CommonUtil.MenuListDashboard.indices){
                    if (16 == CommonUtil.MenuListDashboard.get(i).id){
                        CommonUtil.menu_readCommunication = CommonUtil.MenuListDashboard.get(i).is_read_enabled.toString()
                        CommonUtil.menu_writeCommunication = CommonUtil.MenuListDashboard.get(i).is_write_enabled.toString()
                    }
                }

                val menuid = BaseActivity.CommunicationMenuID
                Log.d("CommunicationMenuID", menuid)
                CommonUtil.MenuIDCommunication = menuid
                val i = Intent(context, Communication::class.java)
                context.startActivity(i)

            } else if (data.module_type.equals("Events")) {
                for (i in CommonUtil.MenuListDashboard.indices){
                    if (8 == CommonUtil.MenuListDashboard.get(i).id){
                        CommonUtil.menu_readEvent = CommonUtil.MenuListDashboard.get(i).is_read_enabled.toString()
                        CommonUtil.menu_writeEvent = CommonUtil.MenuListDashboard.get(i).is_write_enabled.toString()
                    }
                }

                val menuid = BaseActivity.EventsMenuID
                Log.d("EventsMenuID", menuid)
                CommonUtil.MenuIDEvents = menuid
                val i = Intent(context, Events::class.java)
                context.startActivity(i)

            } else if (data.module_type.equals("Notice board")) {
                for (i in CommonUtil.MenuListDashboard.indices){
                    if (7 == CommonUtil.MenuListDashboard.get(i).id){
                        CommonUtil.menu_readNoticeBoard = CommonUtil.MenuListDashboard.get(i).is_read_enabled.toString()
                        CommonUtil.menu_writeNoticeBoard = CommonUtil.MenuListDashboard.get(i).is_write_enabled.toString()
                    }
                }
                val menuid = BaseActivity.NoticeboardMenuID
                Log.d("NoticeboardMenuID", menuid)
                CommonUtil.MenuIDNoticeboard = menuid
                val i = Intent(context, Noticeboard::class.java)
                context.startActivity(i)

            } else if (data.module_type.equals("Assignments")) {
                for (i in CommonUtil.MenuListDashboard.indices){
                    if (5 == CommonUtil.MenuListDashboard.get(i).id){
                        CommonUtil.menu_readAssignment = CommonUtil.MenuListDashboard.get(i).is_read_enabled.toString()
                        CommonUtil.menu_writeAssignment = CommonUtil.MenuListDashboard.get(i).is_write_enabled.toString()
                    }
                }

                val menuid = BaseActivity.AssignmentMenuID
                Log.d("AssignmentMenuID", menuid)
                CommonUtil.MenuIDAssignment = menuid
                val i = Intent(context, Assignment::class.java)
                context.startActivity(i)


            } else if (data.module_type.equals("Chat")) {
                for (i in CommonUtil.MenuListDashboard.indices){
                    if (11 == CommonUtil.MenuListDashboard.get(i).id){
                        CommonUtil.menu_readChat = CommonUtil.MenuListDashboard.get(i).is_read_enabled.toString()
                        CommonUtil.menu_writeChat = CommonUtil.MenuListDashboard.get(i).is_write_enabled.toString()
                    }
                }

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
                for (i in CommonUtil.MenuListDashboard.indices){
                    if (3 == CommonUtil.MenuListDashboard.get(i).id){
                        CommonUtil.menu_readExamination = CommonUtil.MenuListDashboard.get(i).is_read_enabled.toString()
                        CommonUtil.menu_writeExamination = CommonUtil.MenuListDashboard.get(i).is_write_enabled.toString()
                    }
                }

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

        val lblMemberName: TextView = itemView!!.findViewById(R.id.lblMemberName)!!
        val lblNotificationContent: TextView = itemView!!.findViewById(R.id.lblNotificationContent)!!
        val lblNoticeficationTime: TextView = itemView!!.findViewById(R.id.lblNoticeficationTime)!!
        val lblDate: TextView = itemView!!.findViewById(R.id.lblDate)!!
        val rytOverAll: RelativeLayout = itemView!!.findViewById(R.id.rytOverAll)!!

    }

    init {
        notificationList = data
        this.context = context
    }
}