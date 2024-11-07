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
import butterknife.BindView
import butterknife.ButterKnife
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Activities.ApplyLeave
import com.vsca.vsnapvoicecollege.Activities.Attendance
import com.vsca.vsnapvoicecollege.Interfaces.LeaveHistoryListener
import com.vsca.vsnapvoicecollege.Model.LeaveApplicationDelete
import com.vsca.vsnapvoicecollege.Model.LeaveHistoryData
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Repository.RestClient
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class LeaveHistoryAdapter(
    var leavelist: List<LeaveHistoryData>,
    private val context: Context?,
    val leaveListener: LeaveHistoryListener
) : RecyclerView.Adapter<LeaveHistoryAdapter.MyViewHolder>() {

    companion object {
        var leavelistenerClick: LeaveHistoryListener? = null
    }

    var mExpandedPosition = -1

    var Position: Int = 0

    inner class MyViewHolder(view: View) : RecyclerView.ViewHolder(view) {
        @JvmField
        @BindView(R.id.lblLeaveCreatedDate)
        var lblLeaveCreatedDate: TextView? = null

        @JvmField
        @BindView(R.id.lblleaveStatus)
        var lblleaveStatus: TextView? = null

        @JvmField
        @BindView(R.id.lblLeaveType)
        var lblLeaveType: TextView? = null

        @JvmField
        @BindView(R.id.lblLeaveNoOfDays)
        var lblLeaveNoOfDays: TextView? = null

        @JvmField
        @BindView(R.id.lblFromDate)
        var lblFromDate: TextView? = null

        @JvmField
        @BindView(R.id.lblToDate)
        var lblToDate: TextView? = null

        @JvmField
        @BindView(R.id.rytLeaveDescription)
        var rytLeaveDescription: RelativeLayout? = null

        @JvmField
        @BindView(R.id.lblLeaveReason)
        var lblLeaveReason: TextView? = null

        @JvmField
        @BindView(R.id.lblEditleave)
        var lblEditleave: TextView? = null

        @JvmField
        @BindView(R.id.lblDelete)
        var lblDelete: TextView? = null


        @JvmField
        @BindView(R.id.lnrNoticeboardd)
        var lnrNoticeboardd: LinearLayout? = null

        init {
            ButterKnife.bind(this, (itemView))
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): MyViewHolder {
        val itemView = LayoutInflater.from(parent.context)
            .inflate(R.layout.leave_history_design, parent, false)
        return MyViewHolder(itemView)
    }

    @RequiresApi(Build.VERSION_CODES.M)
    override fun onBindViewHolder(holder: MyViewHolder, position: Int) {
        val data = leavelist[position]
        leavelistenerClick = leaveListener
        leavelistenerClick?.onLeaveClick(holder, data)
        val isExpanded = position == mExpandedPosition

        try {

            holder.lblLeaveReason!!.text = data.leavereason
            holder.lblLeaveType!!.text = data.leaveapplicationtype
            holder.lblLeaveCreatedDate!!.text = data.createdon
            holder.lblFromDate!!.text = data.leavefromdate
            holder.lblToDate!!.text = data.leavetodate
            holder.lblLeaveNoOfDays!!.text = data.numofdays

            if (data.leavestatus.equals("4")) {
                holder.lblleaveStatus!!.text = data.leavestatus
            } else {
                holder.lblleaveStatus!!.text = data.leavestatus
            }

            if (data.leavestatus.equals("Rejected")) {
                holder.lblleaveStatus!!.setTextColor(
                    context!!.resources.getColor(R.color.clr_txt_red, null)
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

        } catch (e: NullPointerException) {
            e.printStackTrace()
        }

        holder.lnrNoticeboardd!!.setOnClickListener {


            if (isExpanded) {

                mExpandedPosition = if (isExpanded) -1 else position
                holder.rytLeaveDescription!!.visibility = View.GONE
                notifyDataSetChanged()

            } else {

                CommonUtil.LeaveApplicationID = data.applicationid.toString()
                mExpandedPosition = if (isExpanded) -1 else position
                holder.rytLeaveDescription!!.visibility = View.VISIBLE
                if (data.leavestatus.equals("Rejected") || data.leavestatus.equals("Approved")) {

                    holder.lblEditleave!!.visibility = View.GONE
                    holder.lblDelete!!.visibility = View.GONE

                } else {
                    holder.lblEditleave!!.visibility = View.VISIBLE
                    holder.lblDelete!!.visibility = View.VISIBLE

                }
                notifyDataSetChanged()
            }
        }


        holder.lblLeaveReason!!.text = data.leavereason
        holder.lblLeaveType!!.text = data.leaveapplicationtype
        holder.lblLeaveCreatedDate!!.text = data.createdon
        holder.lblFromDate!!.text = data.leavefromdate
        holder.lblToDate!!.text = data.leavetodate
        holder.lblLeaveNoOfDays!!.text = data.numofdays




        holder.lblEditleave!!.setOnClickListener {


            CommonUtil.Leavetype = "Edit"
            val i: Intent = Intent(context, ApplyLeave::class.java)
            CommonUtil.LeavetypeEdit= data.leaveapplicationtype.toString()
            CommonUtil.leavestartdate = data.leavefromdate.toString()
            CommonUtil.leaveenddate = data.leavetodate.toString()
            CommonUtil.numberofday = data.numofdays.toString()
            CommonUtil.LeaveReasion = data.leavereason.toString()
            context!!.startActivity(i)

        }

        holder.lblDelete!!.setOnClickListener {

            val alertDialog: AlertDialog.Builder? = context?.let { it1 -> AlertDialog.Builder(it1) }
            alertDialog!!.setTitle("Delete Leave")
            alertDialog.setMessage("Once done can't be changed")
            alertDialog.setPositiveButton("yes") { _, _ ->

                LeaveapplicationDelete()
            }

            alertDialog.setNegativeButton("No") { _, _ ->


            }
            val alert: AlertDialog = alertDialog.create()
            alert.setCanceledOnTouchOutside(false)
            alert.show()

        }
    }

    override fun getItemCount(): Int {
        return leavelist.size

    }

    private fun LeaveapplicationDelete() {


        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_colgid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_memberid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_applicationid, CommonUtil.LeaveApplicationID)
        jsonObject.addProperty(ApiRequestNames.Req_leavetypeid, "")
        jsonObject.addProperty(ApiRequestNames.Req_leavefromdate, CommonUtil.leavestartdate)
        jsonObject.addProperty(ApiRequestNames.Req_leavetodate, CommonUtil.leaveenddate)
        jsonObject.addProperty(ApiRequestNames.Req_numofdays, CommonUtil.numberofday)
        jsonObject.addProperty(ApiRequestNames.Req_processtype, "delete")
        jsonObject.addProperty(ApiRequestNames.Req_clgsectionid, CommonUtil.SectionId)
        jsonObject.addProperty(ApiRequestNames.Req_leavereason, CommonUtil.LeaveReasion)

        RestClient.apiInterfaces.LeaveApplicatinDelete(jsonObject)
            ?.enqueue(object : Callback<LeaveApplicationDelete?> {
                override fun onResponse(
                    call: Call<LeaveApplicationDelete?>,
                    response: Response<LeaveApplicationDelete?>
                ) {
                    if (response.code() == 200 || response.code() == 201) {
                        if (response.body() != null) {
                            val response = response.body()!!.Message

                            val dlg = context?.let { AlertDialog.Builder(it) }
                            dlg!!.setTitle("Success")
                            dlg.setMessage(response)
                            dlg.setPositiveButton("OK") { dialog, which ->

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

                override fun onFailure(call: Call<LeaveApplicationDelete?>, t: Throwable) {

                }

            })
    }
}