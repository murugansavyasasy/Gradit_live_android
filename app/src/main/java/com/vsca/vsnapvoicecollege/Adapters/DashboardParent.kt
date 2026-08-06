package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.content.Intent
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.TextView
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Activities.Assignment
import com.vsca.vsnapvoicecollege.Activities.Attendance
import com.vsca.vsnapvoicecollege.Activities.BaseActivity
import com.vsca.vsnapvoicecollege.Activities.CareerTraining
import com.vsca.vsnapvoicecollege.Activities.CategoryCreditWise
import com.vsca.vsnapvoicecollege.Activities.ChatParent
import com.vsca.vsnapvoicecollege.Activities.Circular
import com.vsca.vsnapvoicecollege.Activities.Communication
import com.vsca.vsnapvoicecollege.Activities.CourseDetails
import com.vsca.vsnapvoicecollege.Activities.DashBoardActivityRewamp
import com.vsca.vsnapvoicecollege.Activities.Events
import com.vsca.vsnapvoicecollege.Activities.ExamList
import com.vsca.vsnapvoicecollege.Activities.Faculty
import com.vsca.vsnapvoicecollege.Activities.FeeDetails
import com.vsca.vsnapvoicecollege.Activities.MessageCommunication
import com.vsca.vsnapvoicecollege.Activities.Noticeboard
import com.vsca.vsnapvoicecollege.Activities.PlacementEvent
import com.vsca.vsnapvoicecollege.Activities.ResumeBuilder.ResumeBuilder
import com.vsca.vsnapvoicecollege.Activities.SemesterCreditCategoryWise
import com.vsca.vsnapvoicecollege.Activities.Video
import com.vsca.vsnapvoicecollege.ActivitySender.Hall_Ticket
import com.vsca.vsnapvoicecollege.ActivitySender.PunchStaffAttendanceUsingFinger
import com.vsca.vsnapvoicecollege.ActivitySender.StaffWiseAttendanceReports
import com.vsca.vsnapvoicecollege.Interfaces.HomeMenuClickListener
import com.vsca.vsnapvoicecollege.Model.DashboardOverall
import com.vsca.vsnapvoicecollege.Model.MenuDetailsResponse
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Utils.CommonUtil

class DashboardParent constructor(
    private val categoriesModalArrayList: ArrayList<DashboardOverall>,
    private val context: Context
) : RecyclerView.Adapter<DashboardParent.ViewHolder>() {
    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        return ViewHolder(
            LayoutInflater.from(context).inflate(R.layout.dashboard_parent_design, parent, false)
        )
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {

        val modal: DashboardOverall = categoriesModalArrayList.get(position)
        Log.d("menutype", modal.menuHeadings)

        if ((modal.menuHeadings == "Ad")) {
            holder.category.setText(R.string.txt_advertisement)
            holder.lblViewAll.visibility = View.INVISIBLE
        } else if (modal.menuHeadings == "Circular") {
            holder.category.text = context.getString(R.string.txt_img_pdf)
            holder.lblViewAll.visibility = View.VISIBLE
        }
        else if (modal.menuHeadings == "DashBoard_Menu") {
            holder.category.text = "Menu"
            holder.lblViewAll.visibility = View.INVISIBLE
        }
        else {
            holder.category.text = modal.menuHeadings
            holder.lblViewAll.visibility = View.VISIBLE
        }

        if (modal.menuHeadings == "Attendance") {
            holder.lblNoRecords.text = "Today's attendance is not yet available"
        }

        if ((modal.menuHeadings == "Notice Board")) {
            val adapter: DashboardChild =
                DashboardChild(modal.menusubitemlist?:arrayListOf(), context, "Notice Board")
            holder.recyclerDashboardTitle.itemAnimator = DefaultItemAnimator()
            holder.recyclerDashboardTitle.layoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            holder.recyclerDashboardTitle.adapter = adapter
            adapter.notifyDataSetChanged()
            val menuid = BaseActivity.NoticeboardMenuID
            Log.d("MenuID", menuid)
            CommonUtil.MenuIDNoticeboard = menuid

            holder.lblViewAll.setOnClickListener {

                for (i in CommonUtil.MenuListDashboard.indices){
                    if (7 == CommonUtil.MenuListDashboard.get(i).id){
                        CommonUtil.menu_readNoticeBoard = CommonUtil.MenuListDashboard.get(i).is_read_enabled.toString()
                        CommonUtil.menu_writeNoticeBoard = CommonUtil.MenuListDashboard.get(i).is_write_enabled.toString()
                    }
                }

                val i: Intent = Intent(context, Noticeboard::class.java)
                context.startActivity(i)
            }

        }
        else if ((modal.menuHeadings == "Leave Request")) {
            val adapter: DashboardChild =
                DashboardChild(modal.menusubitemlist?:arrayListOf(), context, "Leave Request")
            val linearLayoutManager: LinearLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            holder.recyclerDashboardTitle.layoutManager = linearLayoutManager
            holder.recyclerDashboardTitle.adapter = adapter
            adapter.notifyDataSetChanged()


            holder.lblViewAll.setOnClickListener {
                for (i in CommonUtil.MenuListDashboard.indices){
                    if (4 == CommonUtil.MenuListDashboard.get(i).id){
                        CommonUtil.menu_readAttendance = CommonUtil.MenuListDashboard.get(i).is_read_enabled.toString()
                        CommonUtil.menu_writeAttendance = CommonUtil.MenuListDashboard.get(i).is_write_enabled.toString()
                    }
                }

                val menuid = BaseActivity.AttendanceMeuID
                CommonUtil.MenuIdAttendance = menuid
                val i: Intent = Intent(context, Attendance::class.java)
                context.startActivity(i)
            }

        }
        else if ((modal.menuHeadings == "DashBoard_Menu")) {

//            val adapter: DashboardChild =
//                DashboardChild(modal.menusubitemlist?:arrayListOf(), context, "Leave Request")
//            val linearLayoutManager: LinearLayoutManager =
//                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
//            holder.recyclerDashboardTitle.layoutManager = linearLayoutManager
//            holder.recyclerDashboardTitle.adapter = adapter
//            adapter.notifyDataSetChanged()

            Log.d("DataCOming-----",modal.DashboardMenuData.toString())
            Log.d("DataCOming-----",modal.DashboardMenuData?.size.toString())

            val menuList = (modal.DashboardMenuData ?: emptyList())
                .filter { it.id != 1 }

            var adapter :HomeMenus=
                HomeMenus(context, menuList, object : HomeMenuClickListener {
                    override fun onMenuClick(
                        holder: HomeMenus.MyViewHolder, data: MenuDetailsResponse
                    ) {
                        holder.LayoutHome.setOnClickListener {
                            ParticularMenuClick(
                                data
                            )
                        }
                    }
                })

            val mLayoutManager: RecyclerView.LayoutManager =
                GridLayoutManager(context, 4)
            holder.recyclerDashboardTitle.layoutManager = mLayoutManager
            holder.recyclerDashboardTitle!!.adapter = adapter

            adapter.notifyDataSetChanged()


        }
        else if ((modal.menuHeadings == "Circular")){
            val adapter: DashboardChild = DashboardChild(modal.menusubitemlist?:arrayListOf(), context, "Circular")
            val linearLayoutManager: LinearLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            holder.recyclerDashboardTitle.layoutManager = linearLayoutManager
            holder.recyclerDashboardTitle.adapter = adapter
            adapter.notifyDataSetChanged()

            val menuid = BaseActivity.CircularMenuID
            Log.d("CircularMenuID", menuid)
            CommonUtil.MenuIDCircular = menuid


            holder.lblViewAll.setOnClickListener {

                for (i in CommonUtil.MenuListDashboard.indices){
                    if (6 == CommonUtil.MenuListDashboard.get(i).id){
                        CommonUtil.menu_readCircular = CommonUtil.MenuListDashboard.get(i).is_read_enabled.toString()
                        CommonUtil.menu_writeCircular = CommonUtil.MenuListDashboard.get(i).is_write_enabled.toString()
                    }
                }

                val i: Intent = Intent(context, Circular::class.java)
                context.startActivity(i)
            }
        } else if ((modal.menuHeadings == "Chat")) {

            val adapter: DashboardChild = DashboardChild(modal.menusubitemlist?:arrayListOf(), context, "Chat")
            val linearLayoutManager: LinearLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            holder.recyclerDashboardTitle.layoutManager = linearLayoutManager
            holder.recyclerDashboardTitle.adapter = adapter
            adapter.notifyDataSetChanged()

            val menuid = BaseActivity.ChatMenuID
            Log.d("ChatMenuID", menuid)
            CommonUtil.MenuIDChat = menuid

            holder.lblViewAll.setOnClickListener {

                for (i in CommonUtil.MenuListDashboard.indices){
                    if (11 == CommonUtil.MenuListDashboard.get(i).id){
                        CommonUtil.menu_readChat = CommonUtil.MenuListDashboard.get(i).is_read_enabled.toString()
                        CommonUtil.menu_writeChat = CommonUtil.MenuListDashboard.get(i).is_write_enabled.toString()
                    }
                }

                val i: Intent = Intent(context, ChatParent::class.java)
                context.startActivity(i)
            }

        } else if ((modal.menuHeadings == "Upcoming Events")) {
            val adapter: DashboardChild =
                DashboardChild(modal.menusubitemlist?:arrayListOf(), context, "Upcoming Events")
            val linearLayoutManager: LinearLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            holder.recyclerDashboardTitle.layoutManager = linearLayoutManager
            holder.recyclerDashboardTitle.adapter = adapter
            adapter.notifyDataSetChanged()

            val menuid = BaseActivity.EventsMenuID
            Log.d("EventsMenuID", menuid)
            CommonUtil.MenuIDEvents = menuid


            holder.lblViewAll.setOnClickListener {
                for (i in CommonUtil.MenuListDashboard.indices){
                    if (8 == CommonUtil.MenuListDashboard.get(i).id){
                        CommonUtil.menu_readEvent = CommonUtil.MenuListDashboard.get(i).is_read_enabled.toString()
                        CommonUtil.menu_writeEvent = CommonUtil.MenuListDashboard.get(i).is_write_enabled.toString()
                    }
                }

                val i: Intent = Intent(context, Events::class.java)
                context.startActivity(i)
            }
        } else if ((modal.menuHeadings == "Assignments")) {
            val adapter: DashboardChild =
                DashboardChild(modal.menusubitemlist?:arrayListOf(), context, "Assignments")
            val linearLayoutManager: LinearLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            holder.recyclerDashboardTitle.layoutManager = linearLayoutManager
            holder.recyclerDashboardTitle.adapter = adapter
            adapter.notifyDataSetChanged()

            val menuid = BaseActivity.AssignmentMenuID
            Log.d("AssignmentMenuID", menuid)
            CommonUtil.MenuIDAssignment = menuid

            holder.lblViewAll.setOnClickListener {
                for (i in CommonUtil.MenuListDashboard.indices){
                    if (5 == CommonUtil.MenuListDashboard.get(i).id){
                        CommonUtil.menu_readAssignment = CommonUtil.MenuListDashboard.get(i).is_read_enabled.toString()
                        CommonUtil.menu_writeAssignment = CommonUtil.MenuListDashboard.get(i).is_write_enabled.toString()
                    }
                }

                val i: Intent = Intent(context, Assignment::class.java)
                context.startActivity(i)

            }

        } else if ((modal.menuHeadings == "Emergency Notification")) {
            val adapter: DashboardChild =
                DashboardChild(modal.menusubitemlist?:arrayListOf(), context, "Emergency Notification")
            val linearLayoutManager: LinearLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.HORIZONTAL, false)
            holder.recyclerDashboardTitle.layoutManager = linearLayoutManager
            holder.recyclerDashboardTitle.adapter = adapter
            adapter.notifyDataSetChanged()

            holder.lblViewAll.setOnClickListener {

                for (i in CommonUtil.MenuListDashboard.indices){
                    if (16 == CommonUtil.MenuListDashboard.get(i).id){
                        CommonUtil.menu_readCommunication = CommonUtil.MenuListDashboard.get(i).is_read_enabled.toString()
                        CommonUtil.menu_writeCommunication = CommonUtil.MenuListDashboard.get(i).is_write_enabled.toString()
                    }
                }

                val menuid = BaseActivity.CommunicationMenuID
                CommonUtil.MenuIDCommunication = menuid
                val i: Intent = Intent(context, Communication::class.java)
                context.startActivity(i)
            }

        } else if ((modal.menuHeadings == "Ad")) {
            val adapter: DashboardChild = DashboardChild(modal.menusubitemlist?:arrayListOf(), context, "Ad")
            val linearLayoutManager: LinearLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            holder.recyclerDashboardTitle.layoutManager = linearLayoutManager
            holder.recyclerDashboardTitle.adapter = adapter
            adapter.notifyDataSetChanged()

        } else if ((modal.menuHeadings == "Recent Notifications")) {
            val adapter: DashboardChild =
                DashboardChild(modal.menusubitemlist?:arrayListOf(), context, "Recent Notifications")
            val linearLayoutManager: LinearLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            holder.recyclerDashboardTitle.layoutManager = linearLayoutManager
            holder.recyclerDashboardTitle.adapter = adapter
            adapter.notifyDataSetChanged()

            holder.lblViewAll.setOnClickListener {

                for (i in CommonUtil.MenuListDashboard.indices){
                    if (16 == CommonUtil.MenuListDashboard.get(i).id){
                        CommonUtil.menu_readCommunication = CommonUtil.MenuListDashboard.get(i).is_read_enabled.toString()
                        CommonUtil.menu_writeCommunication = CommonUtil.MenuListDashboard.get(i).is_write_enabled.toString()
                    }
                }

                var menuid = BaseActivity.CommunicationMenuID
                CommonUtil.MenuIDCommunication = menuid
                val i: Intent = Intent(context, Communication::class.java)
                context.startActivity(i)
            }

        } else if ((modal.menuHeadings == "Attendance")) {

            val adapter: DashboardChild =
                DashboardChild(modal.menusubitemlist?:arrayListOf(), context, "Attendance")
            val linearLayoutManager: LinearLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            holder.recyclerDashboardTitle.layoutManager = linearLayoutManager
            holder.recyclerDashboardTitle.adapter = adapter
            adapter.notifyDataSetChanged()

        }
        Log.d("totalmenuHeadings", modal.menuHeadings)
    }

    private fun ParticularMenuClick(data: MenuDetailsResponse) {

        CommonUtil.EventStatus = "Upcoming"
        if (data.id == 1) {
            CommonUtil.menu_readHome = data.is_read_enabled.toString()
            CommonUtil.menu_writeHome = data.is_write_enabled.toString()
            if (CommonUtil.MenuDashboardHome) {
                val i: Intent = Intent(context, DashBoardActivityRewamp::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            }
        }

//        if (data.id == 2) {
//            CommonUtil.MenuIDCommunication = data.id.toString()
//            CommonUtil.menu_readCommunication = data.is_read_enabled.toString()
//            CommonUtil.menu_writeCommunication = data.is_write_enabled.toString()
//            if (CommonUtil.MenuCommunication) {
//                val i: Intent = Intent(context, Communication::class.java)
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                context.startActivity(i)
//            }
//        }

        if (data.id == 3) {
            CommonUtil.menu_readExamination = data.is_read_enabled.toString()
            CommonUtil.menu_writeExamination = data.is_write_enabled.toString()
            CommonUtil.MenuIDExamination = data.id.toString()
            if (CommonUtil.MenuExamination) {
                val i: Intent = Intent(context, ExamList::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            }
        }

        if (data.id == 4) {
            CommonUtil.MenuIdAttendance = data.id.toString()
            CommonUtil.menu_readAttendance = data.is_read_enabled.toString()
            CommonUtil.menu_writeAttendance = data.is_write_enabled.toString()
            if (CommonUtil.MenuAttendance) {
                val i: Intent = Intent(context, Attendance::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            }
        }

        if (data.id == 5) {
            CommonUtil.MenuIDAssignment = data.id.toString()
            CommonUtil.menu_readAssignment = data.is_read_enabled.toString()
            CommonUtil.menu_writeAssignment = data.is_write_enabled.toString()
            if (CommonUtil.MenuAssignment) {
                val i: Intent = Intent(context, Assignment::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            }
        }
        if (data.id == 6) {
            CommonUtil.MenuIDCircular = data.id.toString()
            CommonUtil.menu_readCircular = data.is_read_enabled.toString()
            CommonUtil.menu_writeCircular = data.is_write_enabled.toString()
            if (CommonUtil.MenuCircular) {
                val i: Intent = Intent(context, Circular::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            }
        }

        if (data.id == 7) {
            CommonUtil.MenuIDNoticeboard = data.id.toString()
            CommonUtil.menu_readNoticeBoard = data.is_read_enabled.toString()
            CommonUtil.menu_writeNoticeBoard = data.is_write_enabled.toString()
            if (CommonUtil.MenuNoticeBoard) {
                val i: Intent = Intent(context, Noticeboard::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            }
        }

        if (data.id == 8) {
            CommonUtil.MenuIDEvents = data.id.toString()
            CommonUtil.menu_readEvent = data.is_read_enabled.toString()
            CommonUtil.menu_writeEvent = data.is_write_enabled.toString()
            if (CommonUtil.MenuEvents) {
                val i: Intent = Intent(context, Events::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            }
        }

        if (data.id == 9) {
            CommonUtil.menu_readFaculty = data.is_read_enabled.toString()
            CommonUtil.menu_writeFaculty = data.is_write_enabled.toString()
            if (CommonUtil.MenuFaculty) {
                val i: Intent = Intent(context, Faculty::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            }
        }

        if (data.id == 10) {
            CommonUtil.menu_readVideo = data.is_read_enabled.toString()
            CommonUtil.menu_writeVideo = data.is_write_enabled.toString()
            CommonUtil.MenuIDVideo = data.id.toString()
            if (CommonUtil.MenuVideo) {
                val i: Intent = Intent(context, Video::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            }
        }

        if (data.id == 11) {
            CommonUtil.menu_readChat = data.is_read_enabled.toString()
            CommonUtil.menu_writeChat = data.is_write_enabled.toString()
            CommonUtil.MenuIDChat = data.id.toString()
            if (CommonUtil.MenuChat) {
                val i: Intent = Intent(context, ChatParent::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            }
        }
        if (data.id == 12) {
            CommonUtil.menu_readCourseDetails = data.is_read_enabled.toString()
            CommonUtil.menu_writeCourseDetails = data.is_write_enabled.toString()
            if (CommonUtil.MenuCourseDetails) {
                CommonUtil.parentMenuCourseExam = 0
                val i: Intent = Intent(context, CourseDetails::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            }
        }

        if (data.id == 13) {
            CommonUtil.menu_readCategoryCreditPoints = data.is_read_enabled.toString()
            CommonUtil.menu_writeCategoryCreditPoints = data.is_write_enabled.toString()
            if (CommonUtil.MenuCategoryCredit) {
                val i: Intent = Intent(context, CategoryCreditWise::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            }
        }

        if (data.id == 14) {
            CommonUtil.menu_readSemCreditPoints = data.is_read_enabled.toString()
            CommonUtil.menu_writeSemCreditPoints = data.is_write_enabled.toString()
            if (CommonUtil.MenuSemCredit) {
                val i: Intent = Intent(context, SemesterCreditCategoryWise::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            }
        }
        if (data.id == 15) {
            CommonUtil.menu_readExamApplicationDetails = data.is_read_enabled.toString()
            CommonUtil.menu_writeExamApplicationDetails = data.is_write_enabled.toString()
            if (CommonUtil.MenuExamDetails) {
                CommonUtil.parentMenuCourseExam = 1
                val i: Intent = Intent(context, CourseDetails::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            }
        }

        if (data.id == 19) {
            CommonUtil.menu_readHallTicker = data.is_read_enabled.toString()
            CommonUtil.menu_writeHallTicker = data.is_write_enabled.toString()
            if (CommonUtil.MenuHallTicket) {

                val i: Intent = Intent(context, Hall_Ticket::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            }
        }

        if (data.id == 16) {
            CommonUtil.MenuIDCommunication = data.id.toString()
            CommonUtil.menu_readCommunication = data.is_read_enabled.toString()
            CommonUtil.menu_writeCommunication = data.is_write_enabled.toString()
            if (CommonUtil.MenuCommunication) {
                val i: Intent = Intent(context, Communication::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            }
        }

        if (data.id == 17) {
            CommonUtil.MenuIDCommunicationText = data.id.toString()
            CommonUtil.menu_readCommunicationText = data.is_read_enabled.toString()
            CommonUtil.menu_writeCommunicationText = data.is_write_enabled.toString()
            if (CommonUtil.MenuText) {
                val i: Intent = Intent(context, MessageCommunication::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            }
        }
        if (data.id == 20) {
            if (CommonUtil.MenuFeeDetails) {
                val i: Intent = Intent(context, FeeDetails::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            }
        }

        if (data.id == 21) {
            CommonUtil.menu_writeMarkAttendance = data.is_write_enabled.toString()
            if (CommonUtil.MarkAttendance) {
                val i: Intent =
                    Intent(context, PunchStaffAttendanceUsingFinger::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            }
        }

        if (data.id == 22) {
            if (CommonUtil.AttendanceReport) {
                val i: Intent = Intent(context, StaffWiseAttendanceReports::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            }
        }

        if (data.id == 24) {
            if (CommonUtil.PlacementEvent) {
                val i: Intent = Intent(context, PlacementEvent::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            }
        }

        if (data.id == 25) {
            if (CommonUtil.PlacementCareer) {
                val i: Intent = Intent(context, CareerTraining::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            }
        }

        if (data.id == 23) {
            if (CommonUtil.PlacementTraining) {
                val i: Intent = Intent(context, ResumeBuilder::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                context.startActivity(i)
            }
        }
    }


    override fun getItemCount(): Int {
        return categoriesModalArrayList.size
    }

    inner class ViewHolder constructor(itemView: View) : RecyclerView.ViewHolder(itemView) {
        val recyclerDashboardTitle: RecyclerView
        val category: TextView
        val lblViewAll: TextView
        val lblNoRecords: TextView
        val idCVCategory: CardView

        init {
            category = itemView.findViewById(R.id.idTVCategory)
            lblViewAll = itemView.findViewById(R.id.lblViewAll)
            lblNoRecords = itemView.findViewById(R.id.lblNoRecords)
            idCVCategory = itemView.findViewById(R.id.idCVCategory)
            recyclerDashboardTitle = itemView.findViewById(R.id.recyclerDashboardTitle)
        }
    }
}