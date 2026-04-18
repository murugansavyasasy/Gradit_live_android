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
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.vsca.vsnapvoicecollege.Activities.Assignment
import com.vsca.vsnapvoicecollege.Activities.Attendance
import com.vsca.vsnapvoicecollege.Activities.BaseActivity
import com.vsca.vsnapvoicecollege.Activities.ChatParent
import com.vsca.vsnapvoicecollege.Activities.Circular
import com.vsca.vsnapvoicecollege.Activities.Communication
import com.vsca.vsnapvoicecollege.Activities.Events
import com.vsca.vsnapvoicecollege.Activities.Noticeboard
import com.vsca.vsnapvoicecollege.Model.DashboardOverall
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
        } else {
            holder.category.text = modal.menuHeadings
            holder.lblViewAll.visibility = View.VISIBLE
        }

        if (modal.menuHeadings == "Attendance") {
            holder.lblNoRecords.text = "Today's attendance is not yet available"
        }

        if ((modal.menuHeadings == "Notice Board")) {
            val adapter: DashboardChild =
                DashboardChild(modal.menusubitemlist, context, "Notice Board")
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

        } else if ((modal.menuHeadings == "Leave Request")) {
            val adapter: DashboardChild =
                DashboardChild(modal.menusubitemlist, context, "Leave Request")
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

        } else if ((modal.menuHeadings == "Circular")){
            val adapter: DashboardChild = DashboardChild(modal.menusubitemlist, context, "Circular")
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

            val adapter: DashboardChild = DashboardChild(modal.menusubitemlist, context, "Chat")
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
                DashboardChild(modal.menusubitemlist, context, "Upcoming Events")
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
                DashboardChild(modal.menusubitemlist, context, "Assignments")
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
                DashboardChild(modal.menusubitemlist, context, "Emergency Notification")
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

                val menuid = BaseActivity.CommunicationMenuID
                CommonUtil.MenuIDCommunication = menuid
                val i: Intent = Intent(context, Communication::class.java)
                context.startActivity(i)
            }

        } else if ((modal.menuHeadings == "Ad")) {
            val adapter: DashboardChild = DashboardChild(modal.menusubitemlist, context, "Ad")
            val linearLayoutManager: LinearLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            holder.recyclerDashboardTitle.layoutManager = linearLayoutManager
            holder.recyclerDashboardTitle.adapter = adapter
            adapter.notifyDataSetChanged()

        } else if ((modal.menuHeadings == "Recent Notifications")) {
            val adapter: DashboardChild =
                DashboardChild(modal.menusubitemlist, context, "Recent Notifications")
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
                DashboardChild(modal.menusubitemlist, context, "Attendance")
            val linearLayoutManager: LinearLayoutManager =
                LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
            holder.recyclerDashboardTitle.layoutManager = linearLayoutManager
            holder.recyclerDashboardTitle.adapter = adapter
            adapter.notifyDataSetChanged()

        }
        Log.d("totalmenuHeadings", modal.menuHeadings)
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