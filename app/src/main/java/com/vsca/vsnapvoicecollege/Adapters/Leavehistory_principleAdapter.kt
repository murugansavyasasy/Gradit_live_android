package com.vsca.vsnapvoicecollege.Adapters

import android.content.Context
import android.content.Intent
import android.os.Build
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.LinearLayout
import android.widget.RelativeLayout
import android.widget.TextView
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.RecyclerView

import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Activities.Attendance
import com.vsca.vsnapvoicecollege.Interfaces.LeaveHistoryPrincipleListener
import com.vsca.vsnapvoicecollege.Model.DataXXXX
import com.vsca.vsnapvoicecollege.Model.LeaveRequest
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.RestClient
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class Leavehistory_principleAdapter(
    var leavelist: List<DataXXXX>,
    private val context: Context?,
    val leaveListener: LeaveHistoryPrincipleListener
) : RecyclerView.Adapter<Leavehistory_principleAdapter.MyViewHolder>() {

    var type = ""

    companion object {
        var leavelistenerClick: LeaveHistoryPrincipleListener? = null
    }


    var Position: Int = 0
    var mExpandedPosition = -1
    var appViewModel: App? = null


    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {

        val lblLeaveCreatedDate: TextView = itemView!!.findViewById(R.id.lblLeaveCreatedDate)!!
        val lblleaveStatus: TextView = itemView!!.findViewById(R.id.lblleaveStatus)!!
        val lblLeaveType: TextView = itemView!!.findViewById(R.id.lblLeaveType)!!
        val lblLeaveNoOfDays: TextView = itemView!!.findViewById(R.id.lblLeaveNoOfDays)!!
        val lblFromDate: TextView = itemView!!.findViewById(R.id.lblFromDate)!!
        val lblToDate: TextView = itemView!!.findViewById(R.id.lblToDate)!!
        val rytLeaveDescription: RelativeLayout = itemView!!.findViewById(R.id.rytLeaveDescription)!!
        val lblLeaveReason: TextView = itemView!!.findViewById(R.id.lblLeaveReason)!!
//        val lblEditleave: TextView = itemView!!.findViewById(R.id.lblEditleave)!!
//        val lblDelete: TextView = itemView!!.findViewById(R.id.lblDelete)!!
        val department: TextView = itemView!!.findViewById(R.id.department)!!
        val year: TextView = itemView!!.findViewById(R.id.year)!!
        val section: TextView = itemView!!.findViewById(R.id.section)!!
        val lblApproval: TextView = itemView!!.findViewById(R.id.lblApproval)!!
        val lblRejaect: TextView = itemView!!.findViewById(R.id.lblRejaect)!!
        val lblname: TextView = itemView!!.findViewById(R.id.lblname)!!
        val lnrNoticeboardd: LinearLayout = itemView!!.findViewById(R.id.lnrNoticeboardd)!!
        val btn_rejectLiner: LinearLayout = itemView!!.findViewById(R.id.btn_rejectLiner)!!

    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView =
            LayoutInflater.from(parent.context).inflate(R.layout.leave_history, parent, false)
        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = leavelist[position]
        leavelistenerClick = leaveListener
        leavelistenerClick?.onLeaveClick(holder, data)
        val isExpanded = position == mExpandedPosition

        try {
            holder.department!!.text = data.coursename
            holder.year!!.text = data.yearname
            holder.section!!.text = data.sectionname
            holder.lblLeaveReason!!.text = data.leavereason
            holder.lblLeaveType!!.text = data.leaveapplicationtype
            holder.lblLeaveCreatedDate!!.text = data.createdon
            holder.lblFromDate!!.text = data.leavefromdate
            holder.lblToDate!!.text = data.leavetodate
            holder.lblLeaveNoOfDays!!.text = data.numofdays
            holder.lblname!!.text = data.studentname

            if (data.leavestatus.equals("4")) {
                holder.lblleaveStatus!!.text = data.leavestatus
            } else {
                holder.lblleaveStatus!!.text = data.leavestatus
            }

        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

        holder.lnrNoticeboardd!!.setOnClickListener {

            if (data.leavestatus.equals("Approved") || data.leavestatus.equals("Rejected")) {

                if (isExpanded) {
                    mExpandedPosition = if (isExpanded) -1 else position
                    holder.rytLeaveDescription!!.visibility = View.VISIBLE
                    notifyDataSetChanged()

                } else {

                    mExpandedPosition = if (isExpanded) -1 else position
                    holder.rytLeaveDescription!!.visibility = View.GONE
                    holder.btn_rejectLiner!!.visibility = View.GONE
                    CommonUtil.studentid = data.studentid
                    CommonUtil.applicationid = data.applicationid
                    notifyDataSetChanged()
                }
            } else {
                if (isExpanded) {

                    mExpandedPosition = if (isExpanded) -1 else position
                    holder.rytLeaveDescription!!.visibility = View.GONE
                    notifyDataSetChanged()

                } else {

                    mExpandedPosition = if (isExpanded) -1 else position
                    holder.rytLeaveDescription!!.visibility = View.VISIBLE
                    holder.btn_rejectLiner!!.visibility = View.VISIBLE
                    CommonUtil.studentid = data.studentid
                    CommonUtil.applicationid = data.applicationid
                    notifyDataSetChanged()
                }
            }
        }

        if (data.leavestatus.equals("Rejected")) {
            holder.lblleaveStatus!!.setTextColor(
                context!!.resources.getColor(
                    R.color.clr_txt_red,
                    null
                )
            )
        } else if (data.leavestatus.equals("Approved")) {
            holder.lblleaveStatus!!.setTextColor(
                context!!.resources.getColor(
                    R.color.btn_clr_green,
                    null
                )
            )
        } else {
            holder.lblleaveStatus!!.setTextColor(
                context!!.resources.getColor(
                    R.color.btn_clr_blue,
                    null
                )
            )
        }

        holder.lblApproval!!.setOnClickListener {
            type = "1"


            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
            alertDialog.setTitle("Approve Leave")
            alertDialog.setMessage("Once done can't be changed ")
            alertDialog.setPositiveButton(
                CommonUtil.OK
            ) { _, _ ->


                LeaveRequest(type)

            }

            alertDialog.setNegativeButton(
                CommonUtil.CANCEL
            ) { _, _ -> }
            val alert: AlertDialog = alertDialog.create()
            alert.setCanceledOnTouchOutside(false)
            alert.show()
        }

        holder.lblRejaect!!.setOnClickListener {
            type = "2"

            val alertDialog: AlertDialog.Builder = AlertDialog.Builder(context)
            alertDialog.setTitle("Reject Leave")
            alertDialog.setMessage("Once done can't be changed")
            alertDialog.setPositiveButton(
                CommonUtil.OK
            ) { _, _ ->


                LeaveRequest(type)

            }

            alertDialog.setNegativeButton(
                CommonUtil.CANCEL
            ) { _, _ -> }
            val alert: AlertDialog = alertDialog.create()
            alert.setCanceledOnTouchOutside(false)
            alert.show()

        }
    }

    override fun getItemCount(): Int {
        return leavelist.size

    }

    fun LeaveRequest(type: String) {

        val jsonObject = JsonObject()

        jsonObject.addProperty("leaveid", CommonUtil.applicationid)
        jsonObject.addProperty("userid", CommonUtil.MemberId)
        jsonObject.addProperty("processtype", type)
        Log.d("jsonoblect", jsonObject.toString())

        RestClient.apiInterfaces.GetleaveApproidapi(jsonObject)
            ?.enqueue(object : Callback<LeaveRequest?> {
                override fun onResponse(
                    call: Call<LeaveRequest?>,
                    response: Response<LeaveRequest?>
                ) {
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            val response = response.body()!!.Message
                            Log.d("message", response)

                            val dlg = context?.let { AlertDialog.Builder(it) }
                            dlg!!.setTitle(CommonUtil.Info)
                            dlg.setMessage(response)
                            dlg.setPositiveButton(CommonUtil.OK) { dialog, which ->

                                CommonUtil.AttendanceStatus = "Reject or Approved"
                                val i: Intent = Intent(context, Attendance::class.java)
                                i.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP
                                context!!.startActivity(i)

                            }
                            dlg.setCancelable(false)
                            dlg.create()
                            dlg.show()
                        }

                    } else if (response.code() == 400 || response.code() == 404 || response.code() == 500) {
                        Log.d("resonsemessage", response.message().toString())

                    }
                }

                override fun onFailure(call: Call<LeaveRequest?>, t: Throwable) {

                }

            })
    }
}