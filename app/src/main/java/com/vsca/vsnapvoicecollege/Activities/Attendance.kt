package com.vsca.vsnapvoicecollege.Activities

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.CalendarView
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Adapters.*
import com.vsca.vsnapvoicecollege.Interfaces.*
import com.vsca.vsnapvoicecollege.Model.*
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.databinding.ActivityApplyLeaveBinding
import com.vsca.vsnapvoicecollege.databinding.ActivityAttendanceBinding
import com.vsca.vsnapvoicecollege.databinding.BottomMenuSwipeBinding
import java.text.SimpleDateFormat
import java.util.*

class Attendance : BaseActivity<ActivityAttendanceBinding>() {

    var attendanceAdapter: AttendanceAdapter? = null
    var leaveHistoryAdapter: LeaveHistoryAdapter? = null
    var leavehistoryptincipleadapter: Leavehistory_principleAdapter? = null
    var Attendance_SenderSide_Adapter: Attendance_SenderSide_Adapter? = null
    var isAttendanceType = "Attendance"

    var GetAttendanceData: List<AttendanceData> = ArrayList()
    var LeaveHistoryLiveData: ArrayList<LeaveHistoryData> = ArrayList()
    var StudentAttendance: ArrayList<StudentAttendance> = ArrayList()
    var LeaveHistoryprincipleLiveData: List<DataXXXX> = ArrayList()
    var SelectedDate: String? = null
    var PreviousAddId: Int = 0
    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var AdWebURl: String? = null
    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()
    var attendanceGet: List<Daum> = ArrayList()
    var AttendanceScreen: String? = "Take_Attendance"

    override fun inflateBinding(): ActivityAttendanceBinding {
        return ActivityAttendanceBinding.inflate(layoutInflater)
    }

    override var appViewModel: App? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)

        super.onCreate(savedInstanceState)
        binding = ActivityAttendanceBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ActionBarMethod(this)

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true

        accessBottomViewIcons(
            binding,
            R.id.img_swipe,
            R.id.layoutbottomCurve,
            R.id.recyclermenusbottom,
            R.id.swipeUpMenus,
            R.id.LayoutDepartment,
            R.id.LayoutCollege,
            R.id.imgAddPlus
        )
        UserMenuRequest(this)
        TabDepartmentColor()
        MenuBottomType()

        val selectedDate: Long = binding.CommonLayout.CalendarView!!.date
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        SelectedDate = simpleDateFormat.format(selectedDate)
        CommonUtil.Selecteddata = SelectedDate.toString()

        binding.imgApplyleave.setOnClickListener { applayleave() }
        binding.CommonLayout.LayoutCollege.setOnClickListener { collegeClick() }

        binding.CommonLayout.LayoutDepartment.setOnClickListener { departmentClick() }

        if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                "p2"
            ) || CommonUtil.Priority.equals(
                "p3"
            )
        ) {
            binding.CommonLayout.lnrCalendar!!.visibility = View.VISIBLE
            attendanceGet()

        } else {
            binding.CommonLayout.lnrCalendar!!.visibility = View.GONE
            attendancelistStudent()
        }

        if (CommonUtil.AttendanceStatus.equals(CommonUtil.Reject_or_Approved)) {
            binding.CommonLayout.recyclerLeaveHistory!!.visibility = View.VISIBLE

        } else {
            binding.CommonLayout.recyclerLeaveHistory!!.visibility = View.GONE

        }
        binding.imgApplyleave.visibility = View.GONE

        CommonUtil.OnMenuClicks("Attendance")
        CommonUtil.PresentlistStudent.clear()
        CommonUtil.AbsendlistStudent.clear()

        binding.CommonLayout.lblMenuTitle!!.setText(R.string.txt_attendance)
        binding.CommonLayout.lblDepartment!!.setText(R.string.txt_attendance)
        binding.CommonLayout.lblCollege!!.setText(R.string.txt_leave_history)


        if (CommonUtil.AttendanceStatus.equals(CommonUtil.Reject_or_Approved)) {

            bottomsheetStateCollpased()
            TabCollegeColor()
            binding.CommonLayout.lnrCalendar!!.visibility = View.GONE
            binding.CommonLayout.LayoutNoAttendanceData!!.visibility = View.GONE
            binding.CommonLayout.recyclerAttendance!!.visibility = View.GONE
            binding.CommonLayout.recyclerLeaveHistory!!.visibility = View.VISIBLE

            if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                    "p2"
                ) || CommonUtil.Priority.equals(
                    "p3"
                )
            ) {
                binding.imgApplyleave.visibility = View.GONE
                binding.imgAddPlus.visibility = View.GONE
                if (CommonUtil.menu_readAttendance.equals("1")) {

                    GetLeaveptincipleHistory()
                }
            } else if (CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals("p5") || CommonUtil.Priority.equals(
                    "p6"
                )
            ) {

                if (CommonUtil.menu_writeAttendance.equals("1")) {
                    binding.imgApplyleave.visibility = View.VISIBLE
                }
                if (CommonUtil.menu_readAttendance.equals("1")) {

                    GetLeaveHistory()
                }
            }
        }


        if (CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals("p5") || CommonUtil.Priority.equals(
                "p6"
            )
        ) {

            if (binding.CommonLayout.lblMenuTitle!!.text.equals("Attendance")) {
                binding.imgApplyleave!!.visibility = View.GONE
            } else {
                if (CommonUtil.menu_writeAttendance.equals("1")) {
                    binding.imgApplyleave.visibility = View.VISIBLE
                }
            }

            binding.imgAddPlus.visibility = View.GONE
        } else if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                "p2"
            ) || CommonUtil.Priority.equals(
                "p3"
            )
        ) {
            binding.imgApplyleave.visibility = View.GONE
            binding.imgAddPlus.visibility = View.GONE
            binding.CommonLayout.recyclerAttendance!!.visibility = View.VISIBLE

            if (CommonUtil.AttendanceStatus.equals(CommonUtil.Reject_or_Approved)) {
                binding.CommonLayout.recyclerLeaveHistory!!.visibility = View.VISIBLE

            } else {
                binding.CommonLayout.recyclerLeaveHistory!!.visibility = View.GONE

            }
        }

        imgRefresh!!.setOnClickListener(View.OnClickListener {
            if (CommonUtil.menu_readAttendance.equals("1")) {

                if (AttendanceScreen.equals("Take_Attendance")) {

                    if (CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals("p5") || CommonUtil.Priority.equals(
                            "p6"
                        )
                    ) {

                        if (binding.CommonLayout.lblMenuTitle!!.text.equals("Attendance")) {
                            binding.imgApplyleave.visibility = View.GONE
                        } else {
                            if (CommonUtil.menu_writeAttendance.equals("1")) {
                                binding.imgApplyleave.visibility = View.VISIBLE
                            }
                        }
                        attendancelistStudent()
                        binding.imgAddPlus.visibility = View.GONE
                    } else if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                            "p2"
                        ) || CommonUtil.Priority.equals(
                            "p3"
                        )
                    ) {
                        binding.CommonLayout.recyclerAttendance!!.visibility = View.VISIBLE
                        if (CommonUtil.AttendanceStatus.equals(CommonUtil.Reject_or_Approved)) {
                            binding.CommonLayout.recyclerLeaveHistory!!.visibility = View.VISIBLE

                        } else {
                            binding.CommonLayout.recyclerLeaveHistory!!.visibility = View.GONE

                        }
                        binding.CommonLayout.lnrCalendar!!.visibility = View.VISIBLE
                        binding.imgApplyleave.visibility = View.GONE
                        binding.imgAddPlus.visibility = View.GONE
                        attendanceGet()
                    }
                } else {

                    if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                            "p2"
                        ) || CommonUtil.Priority.equals(
                            "p3"
                        )
                    ) {
                        binding.imgApplyleave.visibility = View.GONE
                        binding.imgAddPlus.visibility = View.GONE

                        GetLeaveptincipleHistory()
                    } else if (CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals("p5")) {
                        if (CommonUtil.menu_writeAttendance.equals("1")) {
                            binding.imgApplyleave.visibility = View.VISIBLE
                        }
                        GetLeaveHistory()
                    }

                }
            }
        })

        binding.CommonLayout.CalendarView!!.maxDate = Date().time

        binding.CommonLayout.CalendarView!!.setOnDateChangeListener(android.widget.CalendarView.OnDateChangeListener { calendarView, year, month, dayOfMonth ->
            SelectedDate = dayOfMonth.toString() + "/" + (month + 1) + "/" + year
            Log.d("SelectedDateClick", SelectedDate!!)
            CommonUtil.Selecteddata = SelectedDate.toString()
            if (CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals("p5") || CommonUtil.Priority.equals(
                    "p6"
                )
            ) {

                if (binding.CommonLayout.lblMenuTitle!!.text.equals("Attendance")) {
                    binding.imgApplyleave.visibility = View.GONE
                } else {
                    if (CommonUtil.menu_writeAttendance.equals("1")) {
                        binding.imgApplyleave.visibility = View.VISIBLE
                    }
                }
                binding.imgAddPlus.visibility = View.GONE
            } else if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                    "p2"
                ) || CommonUtil.Priority.equals(
                    "p3"
                )
            ) {
                binding.CommonLayout.recyclerAttendance!!.visibility = View.VISIBLE
                if (CommonUtil.AttendanceStatus.equals(CommonUtil.Reject_or_Approved)) {
                    binding.CommonLayout.recyclerLeaveHistory!!.visibility = View.VISIBLE

                } else {
                    binding.CommonLayout.recyclerLeaveHistory!!.visibility = View.GONE

                }
                binding.CommonLayout.lnrCalendar!!.visibility = View.VISIBLE
                binding.imgApplyleave.visibility = View.GONE
                binding.imgAddPlus.visibility = View.GONE
                if (CommonUtil.menu_readAttendance.equals("1")) {
                    attendanceGet()
                }
            }
        })


        appViewModel!!.AdvertisementLiveData?.observe(this) { response ->

            if (response != null) {

                val status = response.status
                val message = response.message
                if (status == 1) {
                    GetAdForCollegeData = response.data!!
                    for (j in GetAdForCollegeData.indices) {
                        AdSmallImage = GetAdForCollegeData[j].add_image
                        AdBackgroundImage = GetAdForCollegeData[0].background_image!!
                        AdWebURl = GetAdForCollegeData[0].add_url.toString()
                    }

                    Glide.with(this).load(AdBackgroundImage)
                        .diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.CommonLayout.imgAdvertisement!!)
                    Log.d("AdBackgroundImage", AdBackgroundImage!!)

                    Glide.with(this).load(AdSmallImage).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(binding.CommonLayout.imgthumb!!)
                }
            }
        }

        appViewModel!!.GetstudentlistAttendance?.observe(this) { response ->

            if (response != null) {
                val status = response.Status
                val message = response.Message
                UserMenuRequest(this)
                AdForCollegeApi()
                if (status == 1) {
                    binding.CommonLayout.lblNoDataFound!!.visibility = View.GONE
                    binding.CommonLayout.recyclerAttendance!!.visibility = View.VISIBLE
                    binding.CommonLayout.recyclerLeaveHistory!!.visibility = View.GONE
                    StudentAttendance = response.data
                    attendanceAdapter = AttendanceAdapter(StudentAttendance, this@Attendance)
                    val mLayoutManager: RecyclerView.LayoutManager =
                        LinearLayoutManager(this@Attendance)
                    binding.CommonLayout.recyclerAttendance!!.layoutManager = mLayoutManager
                    binding.CommonLayout.recyclerAttendance!!.itemAnimator = DefaultItemAnimator()
                    binding.CommonLayout.recyclerAttendance!!.adapter = attendanceAdapter
                    binding.CommonLayout.recyclerAttendance!!.recycledViewPool.setMaxRecycledViews(
                        0,
                        80
                    )
                    attendanceAdapter!!.notifyDataSetChanged()

                } else {
                    binding.CommonLayout.recyclerAttendance!!.visibility = View.GONE
                    binding.CommonLayout.recyclerLeaveHistory!!.visibility = View.GONE
                    binding.CommonLayout.lblNoDataFound!!.visibility = View.VISIBLE
                }
            } else {
                binding.CommonLayout.lblNoDataFound!!.visibility = View.VISIBLE
            }
        }

        appViewModel!!.LeaveHistoryLiveData?.observe(this) { response ->

            if (response != null) {
                val status = response.status
                val message = response.message

                if (status == 1) {
                    binding.CommonLayout.lblNoDataFound.visibility = View.GONE
                    binding.CommonLayout.lblLeaveHistoryCount.visibility = View.VISIBLE
                    LeaveHistoryLiveData = response.data!!
                    binding.CommonLayout.lblLeaveHistoryCount!!.text =
                        LeaveHistoryLiveData.size.toString()
                    Log.d("LeaveHistoryDataSize", LeaveHistoryLiveData.size.toString())
                    leaveHistoryAdapter = LeaveHistoryAdapter(
                        LeaveHistoryLiveData,
                        this@Attendance,
                        object : LeaveHistoryListener {
                            override fun onLeaveClick(
                                holder: LeaveHistoryAdapter.MyViewHolder, item: LeaveHistoryData
                            ) {


                            }
                        })

                    val mLayoutManager: RecyclerView.LayoutManager =
                        LinearLayoutManager(this@Attendance)
                    binding.CommonLayout.recyclerLeaveHistory!!.layoutManager = mLayoutManager
                    binding.CommonLayout.recyclerLeaveHistory!!.itemAnimator = DefaultItemAnimator()
                    binding.CommonLayout.recyclerLeaveHistory!!.adapter = leaveHistoryAdapter
                    binding.CommonLayout.recyclerLeaveHistory!!.recycledViewPool.setMaxRecycledViews(
                        0,
                        80
                    )
                    leaveHistoryAdapter!!.notifyDataSetChanged()

                } else {
                    if (isAttendanceType.equals("Attendance")) {
                        binding.CommonLayout.lblNoDataFound!!.visibility = View.GONE
                    } else {
                        binding.CommonLayout.lblNoDataFound!!.visibility = View.VISIBLE
                    }

                }
            } else {
                if (isAttendanceType == "Attendance") {
                    binding.CommonLayout.lblNoDataFound!!.visibility = View.GONE
                } else {
                    binding.CommonLayout.lblNoDataFound!!.visibility = View.VISIBLE
                }
            }
        }

        appViewModel!!.Leavehistory?.observe(this) { response ->

            if (response != null) {

                val status = response.Status
                val message = response.Message

                if (status == 1) {

                    binding.CommonLayout.lblNoDataFound!!.visibility = View.GONE
                    binding.CommonLayout.lblLeaveHistoryCount!!.visibility = View.VISIBLE
                    LeaveHistoryprincipleLiveData = response.data
                    binding.CommonLayout.lblLeaveHistoryCount!!.text =
                        LeaveHistoryprincipleLiveData.size.toString()

                    Log.d("LeaveHistoryDataSize", LeaveHistoryprincipleLiveData.size.toString())
                    leavehistoryptincipleadapter = Leavehistory_principleAdapter(
                        LeaveHistoryprincipleLiveData,
                        this@Attendance,
                        object : LeaveHistoryPrincipleListener {
                            override fun onLeaveClick(
                                holder: Leavehistory_principleAdapter.MyViewHolder, item: DataXXXX
                            ) {

                            }
                        })
                    val mLayoutManager: RecyclerView.LayoutManager =
                        LinearLayoutManager(this@Attendance)
                    binding.CommonLayout.recyclerLeaveHistory.layoutManager = mLayoutManager
                    binding.CommonLayout.recyclerLeaveHistory.itemAnimator = DefaultItemAnimator()
                    binding.CommonLayout.recyclerLeaveHistory.adapter = leavehistoryptincipleadapter
                    binding.CommonLayout.recyclerLeaveHistory.recycledViewPool.setMaxRecycledViews(
                        0,
                        80
                    )
                    leavehistoryptincipleadapter!!.notifyDataSetChanged()
                } else {
                    if (isAttendanceType.equals("Attendance")) {
                        binding.CommonLayout.lblNoDataFound.visibility = View.GONE
                    } else {
                        binding.CommonLayout.lblNoDataFound.visibility = View.VISIBLE
                    }
                }
            } else {
                if (isAttendanceType.equals("Attendance")) {
                    binding.CommonLayout.lblNoDataFound.visibility = View.GONE
                } else {
                    binding.CommonLayout.lblNoDataFound.visibility = View.VISIBLE
                }
            }
        }

        appViewModel!!.Getattendance!!.observe(this) { response ->
            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == 1) {
                    UserMenuRequest(this)
                    AdForCollegeApi()
                    attendanceGet = response.data
                    binding.CommonLayout.lblNoDataFound!!.visibility = View.GONE
                    binding.CommonLayout.lblAttendanceCount!!.visibility = View.VISIBLE
                    binding.CommonLayout.lblAttendanceCount!!.text = attendanceGet.size.toString()

                    Attendance_SenderSide_Adapter =
                        Attendance_SenderSide_Adapter(attendanceGet, this)
                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    binding.CommonLayout.recyclerAttendance!!.layoutManager = mLayoutManager
                    binding.CommonLayout.recyclerAttendance!!.itemAnimator = DefaultItemAnimator()
                    binding.CommonLayout.recyclerAttendance!!.adapter =
                        Attendance_SenderSide_Adapter
                    binding.CommonLayout.recyclerAttendance!!.recycledViewPool.setMaxRecycledViews(
                        0,
                        80
                    )
                }
            } else {
                CommonUtil.ApiAlert(
                    this, "Subject or Section Not allocated / Students not allocated to the section"
                )
            }
        }
    }

    private fun AttendanceRequest(SelectedDate: String) {

        val jsonObject = JsonObject()
        run {
            jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
            jsonObject.addProperty(ApiRequestNames.Req_Attendancedate, SelectedDate)
            jsonObject.addProperty(ApiRequestNames.Req_sectionid, CommonUtil.SectionId)
            appViewModel!!.getAttendanceReceiver(jsonObject, this)
            Log.d("AttendanceList_Request:", jsonObject.toString())
        }
    }

    private fun attendancelistStudent() {

        val jsonObject = JsonObject()
        run {
            jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
            jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
            jsonObject.addProperty(ApiRequestNames.Req_AppId, CommonUtil.Appid)
            appViewModel!!.attendanceListforStudent(jsonObject, this)
            Log.d("attendanceforStudent:", jsonObject.toString())
        }
    }

    private fun GetSubject() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        appViewModel!!.getsubject(jsonObject, this)
        Log.d("GetStaffRequest", jsonObject.toString())
    }

    private fun attendanceGet() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.dateAttendance, SelectedDate)
        appViewModel!!.AttendanceGettingStaff(jsonObject, this)
        Log.d("GetStaffRequest", jsonObject.toString())
    }


    private fun GetLeaveHistory() {
        val jsonObject = JsonObject()
        run {
            jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
            jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
            appViewModel!!.getleaveHistory(jsonObject, this)
            Log.d("LeaveHistoryRequest:", jsonObject.toString())
        }
    }

    private fun GetLeaveptincipleHistory() {
        val jsonObject = JsonObject()
        run {
            jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
            jsonObject.addProperty(ApiRequestNames.Req_staffid, CommonUtil.MemberId)
            appViewModel!!.Leavehistortprinciple(jsonObject, this)
            Log.d("LeaveHistoryRequest:", jsonObject.toString())
        }
    }


    fun applayleave() {
        val i = Intent(this, ApplyLeave::class.java)
        startActivity(i)
        CommonUtil.Leavetype = ""
    }

    fun departmentClick() {
        isAttendanceType = "Attendance"
        if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                "p2"
            ) || CommonUtil.Priority.equals(
                "p3"
            )
        ) {
            binding.CommonLayout.lnrCalendar!!.visibility = View.VISIBLE
        } else {
            binding.CommonLayout.lnrCalendar!!.visibility = View.GONE
        }
        AttendanceScreen = "Take_Attendance"
        TabDepartmentColor()
        CommonUtil.AttendanceStatus = ""
        binding.CommonLayout.recyclerAttendance!!.visibility = View.VISIBLE
        binding.CommonLayout.recyclerLeaveHistory!!.visibility = View.GONE
        bottomsheetStateCollpased()

        if (CommonUtil.menu_readAttendance.equals("1")) {

            if (CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals("p5") || CommonUtil.Priority.equals(
                    "p6"
                )
            ) {
                if (binding.CommonLayout.lblMenuTitle!!.text.equals("Attendance")) {
                    binding.imgApplyleave.visibility = View.GONE
                } else {
                    if (CommonUtil.menu_writeAttendance.equals("1")) {
                        binding.imgApplyleave.visibility = View.VISIBLE
                    }
                }

                attendancelistStudent()
                binding.imgAddPlus.visibility = View.GONE
            } else if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                    "p2"
                ) || CommonUtil.Priority.equals(
                    "p3"
                )
            ) {
                if (CommonUtil.menu_writeAttendance.equals("1")) {
                    binding.imgApplyleave.visibility = View.GONE
                }
                binding.imgAddPlus.visibility = View.GONE
                attendanceGet()
            }
        }
    }

    fun collegeClick() {

        isAttendanceType = "LeaveHistory"
        binding.CommonLayout.lnrCalendar!!.visibility = View.GONE
        AttendanceScreen = "Leave_History"
        CommonUtil.AttendanceStatus = ""
        bottomsheetStateCollpased()
        TabCollegeColor()
        binding.CommonLayout.LayoutNoAttendanceData!!.visibility = View.GONE
        binding.CommonLayout.recyclerAttendance!!.visibility = View.GONE
        binding.CommonLayout.recyclerLeaveHistory!!.visibility = View.VISIBLE

        if (CommonUtil.menu_readAttendance.equals("1")) {


            if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                    "p2"
                ) || CommonUtil.Priority.equals(
                    "p3"
                )
            ) {
                binding.imgApplyleave.visibility = View.GONE
                binding.imgAddPlus.visibility = View.GONE

                GetLeaveptincipleHistory()
            } else if (CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals("p5")) {
                if (CommonUtil.menu_writeAttendance.equals("1")) {
                    binding.imgApplyleave.visibility = View.VISIBLE
                }
                GetLeaveHistory()
            }
        }

        if (CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals("p5")) {
            if (CommonUtil.menu_writeAttendance.equals("1")) {
                binding.imgApplyleave.visibility = View.VISIBLE
            }
        }
    }

    override fun onBackPressed() {
        CommonUtil.OnBackSetBottomMenuClickTrue()
        super.onBackPressed()
    }

    override fun onResume() {
        binding.CommonLayout.lblNoDataFound!!.visibility = View.GONE
        if (CommonUtil.menu_readAttendance.equals("1")) {
            if (CommonUtil.AttendanceStatus.equals(CommonUtil.Reject_or_Approved)) {
                binding.CommonLayout.recyclerLeaveHistory!!.visibility = View.VISIBLE
                if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                        "p2"
                    ) || CommonUtil.Priority.equals(
                        "p3"
                    )
                ) {
                    binding.CommonLayout.recyclerLeaveHistory!!.visibility = View.VISIBLE
                    GetLeaveptincipleHistory()
                    binding.CommonLayout.recyclerAttendance!!.visibility = View.GONE
                } else if (CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals("p5")) {
                    GetLeaveHistory()
                }
            } else {
                if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                        "p2"
                    ) || CommonUtil.Priority.equals(
                        "p3"
                    )
                ) {
                    binding.CommonLayout.recyclerLeaveHistory!!.visibility = View.GONE
                    GetLeaveptincipleHistory()
                    binding.CommonLayout.recyclerAttendance!!.visibility = View.VISIBLE
                } else if (CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals("p5")) {
                    GetLeaveHistory()
                }
            }
        }
        var AddId: Int = 1
        PreviousAddId = PreviousAddId + 1
        super.onResume()
        CommonUtil.AbsendlistStudent.clear()
        CommonUtil.PresentlistStudent.clear()
        binding.CommonLayout.lblNoDataFound!!.visibility = View.GONE
    }

    private fun AdForCollegeApi() {

        val mobilenumber = SharedPreference.getSH_MobileNumber(this)
        val devicetoken = SharedPreference.getSH_DeviceToken(this)
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_ad_device_token, devicetoken)
        jsonObject.addProperty(ApiRequestNames.Req_MemberID, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_mobileno, mobilenumber)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_previous_add_id, PreviousAddId)
        appviewModelbase!!.getAdforCollege(jsonObject, this)
        Log.d("AdForCollege:", jsonObject.toString())

        PreviousAddId = PreviousAddId + 1
        Log.d("PreviousAddId", PreviousAddId.toString())
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_attendance
}