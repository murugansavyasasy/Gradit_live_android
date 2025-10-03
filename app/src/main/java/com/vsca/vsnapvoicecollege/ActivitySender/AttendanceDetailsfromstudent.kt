package com.vsca.vsnapvoicecollege.ActivitySender

import android.os.Bundle
import android.util.Log
import android.widget.ImageView
import android.widget.TextView
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView

import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Activities.ActionBarActivity
import com.vsca.vsnapvoicecollege.Adapters.Attendancedetails
import com.vsca.vsnapvoicecollege.Model.attendance
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityApplyLeaveBinding
import com.vsca.vsnapvoicecollege.databinding.ActivityAttendanceDetailsfromstudentBinding

class AttendanceDetailsfromstudent : ActionBarActivity() {

    var Attendancedetails: Attendancedetails? = null
    var appViewModel: App? = null
    var AttendanceDetails: List<attendance> = java.util.ArrayList()


    private lateinit var binding: ActivityAttendanceDetailsfromstudentBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceDetailsfromstudentBinding.inflate(layoutInflater)
        setContentView(binding.root)
         ActionbarWithoutBottom(this)
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true

        binding.lblattenSunjectname!!.text=CommonUtil.AttendanceSubjectname
        binding.lblattenStaffname!!.text=CommonUtil.AttendanceStaffname


        appViewModel!!.StudentAttendance!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {

                    AttendanceDetails = response.data
                    Attendancedetails = Attendancedetails(AttendanceDetails, this)
                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    binding.rcyAttendanceDetails!!.layoutManager = mLayoutManager
                    binding.rcyAttendanceDetails!!.itemAnimator = DefaultItemAnimator()
                    binding.rcyAttendanceDetails!!.adapter = Attendancedetails
                    binding.rcyAttendanceDetails!!.recycledViewPool.setMaxRecycledViews(0, 80)
                }
            } else {
                CommonUtil.ApiAlert(
                    this, "No records found"
                )
            }
        }
        binding.imgBack!!.setOnClickListener {
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