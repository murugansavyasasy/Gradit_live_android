package com.vsca.vsnapvoicecollege.ActivitySender

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Activities.ActionBarActivity
import com.vsca.vsnapvoicecollege.Adapters.Attendancedetails
import com.vsca.vsnapvoicecollege.Model.attendance
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App

class AttendanceDetailsfromstudent : ActionBarActivity() {

    var Attendancedetails: Attendancedetails? = null
    var appViewModel: App? = null
    var AttendanceDetails: List<attendance> = java.util.ArrayList()

    @JvmField
    @BindView(R.id.rcyAttendanceDetails)
    var rcyAttendanceDetails: RecyclerView? = null

    @JvmField
    @BindView(R.id.img_back)
    var img_back: ImageView? = null

    @JvmField
    @BindView(R.id.lblatten_staffname)
    var lblatten_staffname: TextView? = null

    @JvmField
    @BindView(R.id.lblatten_sunjectname)
    var lblatten_sunjectname: TextView? = null

    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)
        ActionbarWithoutBottom(this)
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()

        lblatten_sunjectname!!.text=CommonUtil.AttendanceSubjectname
        lblatten_staffname!!.text=CommonUtil.AttendanceStaffname


        appViewModel!!.StudentAttendance!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {

                    AttendanceDetails = response.data
                    Attendancedetails = Attendancedetails(AttendanceDetails, this)
                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    rcyAttendanceDetails!!.layoutManager = mLayoutManager
                    rcyAttendanceDetails!!.itemAnimator = DefaultItemAnimator()
                    rcyAttendanceDetails!!.adapter = Attendancedetails
                    rcyAttendanceDetails!!.recycledViewPool.setMaxRecycledViews(0, 80)
                }
            } else {
                CommonUtil.ApiAlert(
                    this, "No records found"
                )
            }
        }
        img_back!!.setOnClickListener {
            onBackPressed()
        }

    }

    private fun AttendanceRequest() {

        val jsonObject = JsonObject()
        run {
            jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
            jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
            jsonObject.addProperty(ApiRequestNames.Req_subjectid, CommonUtil.AttendanceSubjectId)
            jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.Appid)
            jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.AttendanceStaffid)
            appViewModel!!.DetailsforspecificstudentAttendance(jsonObject, this)
            Log.d("DetailsforAttendance:", jsonObject.toString())
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_attendance_detailsfromstudent

    override fun onResume() {
        AttendanceRequest()
        super.onResume()
    }
}