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
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
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
import java.text.SimpleDateFormat
import java.util.*

class Attendance : BaseActivity() {

    var attendanceAdapter: AttendanceAdapter? = null
    var leaveHistoryAdapter: LeaveHistoryAdapter? = null
    var leavehistoryptincipleadapter: Leavehistory_principleAdapter? = null
    var Attendance_SenderSide_Adapter: Attendance_SenderSide_Adapter? = null
    var isAttendanceType = "Attendance"

    @JvmField
    @BindView(R.id.recyclerAttendance)
    var recyclerAttendance: RecyclerView? = null

    @JvmField
    @BindView(R.id.recyclerLeaveHistory)
    var recyclerLeaveHistory: RecyclerView? = null

    @JvmField
    @BindView(R.id.imgAdvertisement)
    var imgAdvertisement: ImageView? = null

    @JvmField
    @BindView(R.id.imgthumb)
    var imgthumb: ImageView? = null

    @JvmField
    @BindView(R.id.lbltotalsize)
    var lbltotalsize: TextView? = null

    @JvmField
    @BindView(R.id.lblMenuTitle)
    var lblMenuTitle: TextView? = null

    @JvmField
    @BindView(R.id.lblDepartment)
    var lblDepartment: TextView? = null

    @JvmField
    @BindView(R.id.lblCollege)
    var lblCollege: TextView? = null

    @JvmField
    @BindView(R.id.lblDepartmentSize)
    var lblDepartmentSize: TextView? = null

    @JvmField
    @BindView(R.id.lblCollegeSize)
    var lblCollegeSize: TextView? = null


    @JvmField
    @BindView(R.id.CalendarView)
    var CalendarView: CalendarView? = null

//    @JvmField
//    @BindView(R.id.collapsibleCalendar)
//    var collapsibleCalendar: CollapsibleCalendar? = null


    @JvmField
    @BindView(R.id.lnrCalendar)
    var lnrCalendar: LinearLayout? = null

    @JvmField
    @BindView(R.id.LayoutNoAttendanceData)
    var LayoutNoAttendanceData: ConstraintLayout? = null

    @JvmField
    @BindView(R.id.lblNoDataFound)
    var lblNoDataFound: TextView? = null

    @JvmField
    @BindView(R.id.imgApplyleave)
    var imgApplyleave: ImageView? = null

    @JvmField
    @BindView(R.id.lblAttendanceCount)
    var lblAttendanceCount: TextView? = null

    @JvmField
    @BindView(R.id.lblLeaveHistoryCount)
    var lblLeaveHistoryCount: TextView? = null

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

    override var appViewModel: App? = null
    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        appViewModel = ViewModelProvider(this).get(App::class.java)
        appViewModel!!.init()
        ButterKnife.bind(this)
        ActionBarMethod(this)
        TabDepartmentColor()
        MenuBottomType()

        val selectedDate: Long = CalendarView!!.date
        val simpleDateFormat = SimpleDateFormat("dd/MM/yyyy")
        SelectedDate = simpleDateFormat.format(selectedDate)
        CommonUtil.Selecteddata = SelectedDate.toString()

        if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                "p2"
            ) || CommonUtil.Priority.equals(
                "p3"
            )
        ) {
            lnrCalendar!!.visibility = View.VISIBLE
            attendanceGet()

        } else {
            lnrCalendar!!.visibility = View.GONE
            attendancelistStudent()
        }

        if (CommonUtil.AttendanceStatus.equals(CommonUtil.Reject_or_Approved)) {
            recyclerLeaveHistory!!.visibility = View.VISIBLE

        } else {
            recyclerLeaveHistory!!.visibility = View.GONE

        }
        imgApplyleave!!.visibility = View.GONE

        CommonUtil.OnMenuClicks("Attendance")
        CommonUtil.PresentlistStudent.clear()
        CommonUtil.AbsendlistStudent.clear()

        lblMenuTitle!!.setText(R.string.txt_attendance)
        lblDepartment!!.setText(R.string.txt_attendance)
        lblCollege!!.setText(R.string.txt_leave_history)


        if (CommonUtil.AttendanceStatus.equals(CommonUtil.Reject_or_Approved)) {

            bottomsheetStateCollpased()
            TabCollegeColor()
            lnrCalendar!!.visibility = View.GONE
            LayoutNoAttendanceData!!.visibility = View.GONE
            recyclerAttendance!!.visibility = View.GONE
            recyclerLeaveHistory!!.visibility = View.VISIBLE

            if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                    "p2"
                ) || CommonUtil.Priority.equals(
                    "p3"
                )
            ) {
                imgApplyleave!!.visibility = View.GONE
                imgAddPlus!!.visibility = View.GONE
                if (CommonUtil.menu_readAttendance.equals("1")) {

                    GetLeaveptincipleHistory()
                }
            } else if (CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals("p5") || CommonUtil.Priority.equals(
                    "p6"
                )
            ) {

                if (CommonUtil.menu_writeAttendance.equals("1")) {
                    imgApplyleave!!.visibility = View.VISIBLE
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

            if (lblMenuTitle!!.text.equals("Attendance")) {
                imgApplyleave!!.visibility = View.GONE
            } else {
                if (CommonUtil.menu_writeAttendance.equals("1")) {
                    imgApplyleave!!.visibility = View.VISIBLE
                }
            }

            imgAddPlus!!.visibility = View.GONE
        } else if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                "p2"
            ) || CommonUtil.Priority.equals(
                "p3"
            )
        ) {
            imgApplyleave!!.visibility = View.GONE
            imgAddPlus!!.visibility = View.GONE
            recyclerAttendance!!.visibility = View.VISIBLE

            if (CommonUtil.AttendanceStatus.equals(CommonUtil.Reject_or_Approved)) {
                recyclerLeaveHistory!!.visibility = View.VISIBLE

            } else {
                recyclerLeaveHistory!!.visibility = View.GONE

            }
        }

        imgRefresh!!.setOnClickListener(View.OnClickListener {
            if (CommonUtil.menu_readAttendance.equals("1")) {

                if (AttendanceScreen.equals("Take_Attendance")) {

                    if (CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals("p5") || CommonUtil.Priority.equals(
                            "p6"
                        )
                    ) {

                        if (lblMenuTitle!!.text.equals("Attendance")) {
                            imgApplyleave!!.visibility = View.GONE
                        } else {
                            if (CommonUtil.menu_writeAttendance.equals("1")) {
                                imgApplyleave!!.visibility = View.VISIBLE
                            }
                        }
                        attendancelistStudent()
                        imgAddPlus!!.visibility = View.GONE
                    } else if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                            "p2"
                        ) || CommonUtil.Priority.equals(
                            "p3"
                        )
                    ) {
                        recyclerAttendance!!.visibility = View.VISIBLE
                        if (CommonUtil.AttendanceStatus.equals(CommonUtil.Reject_or_Approved)) {
                            recyclerLeaveHistory!!.visibility = View.VISIBLE

                        } else {
                            recyclerLeaveHistory!!.visibility = View.GONE

                        }
                        lnrCalendar!!.visibility = View.VISIBLE
                        imgApplyleave!!.visibility = View.GONE
                        imgAddPlus!!.visibility = View.GONE
                        attendanceGet()
                    }
                } else {

                    if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                            "p2"
                        ) || CommonUtil.Priority.equals(
                            "p3"
                        )
                    ) {
                        imgApplyleave!!.visibility = View.GONE
                        imgAddPlus!!.visibility = View.GONE

                        GetLeaveptincipleHistory()
                    } else if (CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals("p5")) {
                        if (CommonUtil.menu_writeAttendance.equals("1")) {
                            imgApplyleave!!.visibility = View.VISIBLE
                        }
                        GetLeaveHistory()
                    }

                }
            }
        })

        CalendarView!!.maxDate = Date().time

        CalendarView!!.setOnDateChangeListener(android.widget.CalendarView.OnDateChangeListener { calendarView, year, month, dayOfMonth ->
            SelectedDate = dayOfMonth.toString() + "/" + (month + 1) + "/" + year
            Log.d("SelectedDateClick", SelectedDate!!)
            CommonUtil.Selecteddata = SelectedDate.toString()
            if (CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals("p5") || CommonUtil.Priority.equals(
                    "p6"
                )
            ) {

                if (lblMenuTitle!!.text.equals("Attendance")) {
                    imgApplyleave!!.visibility = View.GONE
                } else {
                    if (CommonUtil.menu_writeAttendance.equals("1")) {
                        imgApplyleave!!.visibility = View.VISIBLE
                    }
                }
                imgAddPlus!!.visibility = View.GONE
            } else if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                    "p2"
                ) || CommonUtil.Priority.equals(
                    "p3"
                )
            ) {
                recyclerAttendance!!.visibility = View.VISIBLE
                if (CommonUtil.AttendanceStatus.equals(CommonUtil.Reject_or_Approved)) {
                    recyclerLeaveHistory!!.visibility = View.VISIBLE

                } else {
                    recyclerLeaveHistory!!.visibility = View.GONE

                }
                lnrCalendar!!.visibility = View.VISIBLE
                imgApplyleave!!.visibility = View.GONE
                imgAddPlus!!.visibility = View.GONE
                if (CommonUtil.menu_readAttendance.equals("1")) {
                    attendanceGet()
                }
            }
        })

// CollapsibleCalender Code. Don't remove it.

//        val today = Calendar.getInstance()
//
//
//        collapsibleCalendar.setMaxDate(today)
//
//        collapsibleCalendar!!.setCalendarListener(object : CollapsibleCalendar.CalendarListener {
//            override fun onDaySelect() {
//
//
//                val day: Day = collapsibleCalendar!!.selectedDay
//
//                SelectedDate = "ClickedDate" + day.day + "/" + (day.month + 1) + "/" + day.year
//                Log.d("SelectedDateClick", SelectedDate!!)
//
//                val createdDateTime: String = SelectedDate.toString()
//                val firstvalue: Array<String> =
//                    createdDateTime.split("ClickedDate".toRegex()).toTypedArray()
//                val createddate: String = firstvalue.get(1)
//                Log.d("SelectedDateClick", createddate)
//                CommonUtil.Selecteddata = createddate
//
//                if (CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals("p5") || CommonUtil.Priority.equals(
//                        "p6"
//                    )
//                ) {
//
//                    if (lblMenuTitle!!.text.equals("Attendance")) {
//                        imgApplyleave!!.visibility = View.GONE
//                    } else {
//                        imgApplyleave!!.visibility = View.VISIBLE
//                    }
//
//                    AttendanceRequest(SelectedDate!!)
//                    imgAddPlus!!.visibility = View.GONE
//                } else if (CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals("p2") || CommonUtil.Priority.equals(
//                        "p3"
//                    )
//                ) {
//
//                    recyclerAttendance!!.visibility = View.VISIBLE
//                    if (CommonUtil.AttendanceStatus.equals(CommonUtil.Reject_or_Approved)) {
//                        recyclerLeaveHistory!!.visibility = View.VISIBLE
//
//                    } else {
//                        recyclerLeaveHistory!!.visibility = View.GONE
//                    }
//
//                    lnrCalendar!!.visibility = View.VISIBLE
//                    imgApplyleave!!.visibility = View.GONE
//                    imgAddPlus!!.visibility = View.GONE
//                    GetSubject()
//                }
//            }
//
//            override fun onItemClick(view: View) {}
//            override fun onDataUpdate() {}
//            override fun onMonthChange() {}
//            override fun onWeekChange(i: Int) {}
//        })


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
                        .diskCacheStrategy(DiskCacheStrategy.ALL).into(imgAdvertisement!!)
                    Log.d("AdBackgroundImage", AdBackgroundImage!!)

                    Glide.with(this).load(AdSmallImage).diskCacheStrategy(DiskCacheStrategy.ALL)
                        .into(imgthumb!!)
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
                    lblNoDataFound!!.visibility = View.GONE
                    recyclerAttendance!!.visibility = View.VISIBLE
                    recyclerLeaveHistory!!.visibility = View.GONE
                    StudentAttendance = response.data
                    attendanceAdapter = AttendanceAdapter(StudentAttendance, this@Attendance)
                    val mLayoutManager: RecyclerView.LayoutManager =
                        LinearLayoutManager(this@Attendance)
                    recyclerAttendance!!.layoutManager = mLayoutManager
                    recyclerAttendance!!.itemAnimator = DefaultItemAnimator()
                    recyclerAttendance!!.adapter = attendanceAdapter
                    recyclerAttendance!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    attendanceAdapter!!.notifyDataSetChanged()

                } else {
                    recyclerAttendance!!.visibility = View.GONE
                    recyclerLeaveHistory!!.visibility = View.GONE
                    lblNoDataFound!!.visibility = View.VISIBLE
                }
            } else {
                lblNoDataFound!!.visibility = View.VISIBLE
            }
        }

//        appViewModel!!.AttendanceDatesLiveData?.observe(this) { response ->
//
//            if (response != null) {
//                UserMenuRequest(this)
//
//                val status = response.status
//                val message = response.message
//
//                if (status == 1) {
//                    lblNoDataFound!!.visibility = View.GONE
//                    LayoutNoAttendanceData!!.visibility = View.GONE
//                    recyclerAttendance!!.visibility = View.VISIBLE
//                    if (CommonUtil.AttendanceStatus.equals(CommonUtil.Reject_or_Approved)) {
//                        recyclerLeaveHistory!!.visibility = View.VISIBLE
//                    } else {
//                        recyclerLeaveHistory!!.visibility = View.GONE
//
//                    }
//                    GetAttendanceData = response.data!!
//                    lblAttendanceCount!!.visibility = View.VISIBLE
//                    lblAttendanceCount!!.text = GetAttendanceData.size.toString()
//
//                    Log.d("AttendanceDataSize", GetAttendanceData.size.toString())
//
//                    attendanceAdapter = AttendanceAdapter(GetAttendanceData, this@Attendance)
//                    val mLayoutManager: RecyclerView.LayoutManager =
//                        LinearLayoutManager(this@Attendance)
//                    recyclerAttendance!!.layoutManager = mLayoutManager
//                    recyclerAttendance!!.itemAnimator = DefaultItemAnimator()
//                    recyclerAttendance!!.adapter = attendanceAdapter
//                    recyclerAttendance!!.recycledViewPool.setMaxRecycledViews(0, 80)
//                    attendanceAdapter!!.notifyDataSetChanged()
//
//                } else {
//
//                    LayoutNoAttendanceData!!.visibility = View.VISIBLE
//                    recyclerAttendance!!.visibility = View.GONE
//                    lblNoDataFound!!.visibility = View.GONE
//
//                }
//            } else {
//                lblNoDataFound!!.visibility = View.GONE
//                LayoutNoAttendanceData!!.visibility = View.VISIBLE
//                recyclerAttendance!!.visibility = View.GONE
//            }
//        }

        appViewModel!!.LeaveHistoryLiveData?.observe(this) { response ->

            if (response != null) {
                val status = response.status
                val message = response.message

                if (status == 1) {
                    lblNoDataFound!!.visibility = View.GONE
                    lblLeaveHistoryCount!!.visibility = View.VISIBLE
                    LeaveHistoryLiveData = response.data!!
                    lblLeaveHistoryCount!!.text = LeaveHistoryLiveData.size.toString()
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
                    recyclerLeaveHistory!!.layoutManager = mLayoutManager
                    recyclerLeaveHistory!!.itemAnimator = DefaultItemAnimator()
                    recyclerLeaveHistory!!.adapter = leaveHistoryAdapter
                    recyclerLeaveHistory!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    leaveHistoryAdapter!!.notifyDataSetChanged()

                } else {
                    if (isAttendanceType.equals("Attendance")) {
                        lblNoDataFound!!.visibility = View.GONE
                    } else {
                        lblNoDataFound!!.visibility = View.VISIBLE
                    }

                }
            } else {
                if (isAttendanceType.equals("Attendance")) {
                    lblNoDataFound!!.visibility = View.GONE
                } else {
                    lblNoDataFound!!.visibility = View.VISIBLE
                }
            }
        }

        appViewModel!!.Leavehistory?.observe(this) { response ->

            if (response != null) {

                val status = response.Status
                val message = response.Message

                if (status == 1) {

                    lblNoDataFound!!.visibility = View.GONE
                    lblLeaveHistoryCount!!.visibility = View.VISIBLE
                    LeaveHistoryprincipleLiveData = response.data
                    lblLeaveHistoryCount!!.text = LeaveHistoryprincipleLiveData.size.toString()

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
                    recyclerLeaveHistory!!.layoutManager = mLayoutManager
                    recyclerLeaveHistory!!.itemAnimator = DefaultItemAnimator()
                    recyclerLeaveHistory!!.adapter = leavehistoryptincipleadapter
                    recyclerLeaveHistory!!.recycledViewPool.setMaxRecycledViews(0, 80)
                    leavehistoryptincipleadapter!!.notifyDataSetChanged()
                } else {
                    if (isAttendanceType.equals("Attendance")) {
                        lblNoDataFound!!.visibility = View.GONE
                    } else {
                        lblNoDataFound!!.visibility = View.VISIBLE
                    }
                }
            } else {
                if (isAttendanceType.equals("Attendance")) {
                    lblNoDataFound!!.visibility = View.GONE
                } else {
                    lblNoDataFound!!.visibility = View.VISIBLE
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
                    lblNoDataFound!!.visibility = View.GONE
                    lblAttendanceCount!!.visibility = View.VISIBLE
                    lblAttendanceCount!!.text = attendanceGet.size.toString()

                    Attendance_SenderSide_Adapter =
                        Attendance_SenderSide_Adapter(attendanceGet, this)
                    val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this)
                    recyclerAttendance!!.layoutManager = mLayoutManager
                    recyclerAttendance!!.itemAnimator = DefaultItemAnimator()
                    recyclerAttendance!!.adapter = Attendance_SenderSide_Adapter
                    recyclerAttendance!!.recycledViewPool.setMaxRecycledViews(0, 80)
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


    @OnClick(R.id.imgApplyleave)
    fun applayleave() {
        val i = Intent(this, ApplyLeave::class.java)
        startActivity(i)
        CommonUtil.Leavetype = ""
    }

    @OnClick(R.id.LayoutDepartment)
    fun departmentClick() {
        isAttendanceType = "Attendance"
        if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                "p2"
            ) || CommonUtil.Priority.equals(
                "p3"
            )
        ) {
            lnrCalendar!!.visibility = View.VISIBLE
        } else {
            lnrCalendar!!.visibility = View.GONE
        }
        AttendanceScreen = "Take_Attendance"
        TabDepartmentColor()
        CommonUtil.AttendanceStatus = ""
        recyclerAttendance!!.visibility = View.VISIBLE
        recyclerLeaveHistory!!.visibility = View.GONE
        bottomsheetStateCollpased()

        if (CommonUtil.menu_readAttendance.equals("1")) {

            if (CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals("p5") || CommonUtil.Priority.equals(
                    "p6"
                )
            ) {
                if (lblMenuTitle!!.text.equals("Attendance")) {
                    imgApplyleave!!.visibility = View.GONE
                } else {
                    if (CommonUtil.menu_writeAttendance.equals("1")) {
                        imgApplyleave!!.visibility = View.VISIBLE
                    }
                }

                attendancelistStudent()
                imgAddPlus!!.visibility = View.GONE
            } else if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                    "p2"
                ) || CommonUtil.Priority.equals(
                    "p3"
                )
            ) {
                if (CommonUtil.menu_writeAttendance.equals("1")) {
                    imgApplyleave!!.visibility = View.GONE
                }
                imgAddPlus!!.visibility = View.GONE
                attendanceGet()
            }
        }
    }

    @OnClick(R.id.LayoutCollege)
    fun collegeClick() {

        isAttendanceType = "LeaveHistory"
        lnrCalendar!!.visibility = View.GONE
        AttendanceScreen = "Leave_History"
        CommonUtil.AttendanceStatus = ""
        bottomsheetStateCollpased()
        TabCollegeColor()
        LayoutNoAttendanceData!!.visibility = View.GONE
        recyclerAttendance!!.visibility = View.GONE
        recyclerLeaveHistory!!.visibility = View.VISIBLE

        if (CommonUtil.menu_readAttendance.equals("1")) {


            if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                    "p2"
                ) || CommonUtil.Priority.equals(
                    "p3"
                )
            ) {
                imgApplyleave!!.visibility = View.GONE
                imgAddPlus!!.visibility = View.GONE

                GetLeaveptincipleHistory()
            } else if (CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals("p5")) {
                if (CommonUtil.menu_writeAttendance.equals("1")) {
                    imgApplyleave!!.visibility = View.VISIBLE
                }
                GetLeaveHistory()
            }
        }

        if (CommonUtil.Priority.equals("p4") || CommonUtil.Priority.equals("p5")) {
            if (CommonUtil.menu_writeAttendance.equals("1")) {
                imgApplyleave!!.visibility = View.VISIBLE
            }
        }
    }

    override fun onBackPressed() {
        CommonUtil.OnBackSetBottomMenuClickTrue()
        super.onBackPressed()
    }

    override fun onResume() {
        lblNoDataFound!!.visibility = View.GONE
        if (CommonUtil.menu_readAttendance.equals("1")) {
            if (CommonUtil.AttendanceStatus.equals(CommonUtil.Reject_or_Approved)) {
                recyclerLeaveHistory!!.visibility = View.VISIBLE
                if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals(
                        "p2"
                    ) || CommonUtil.Priority.equals(
                        "p3"
                    )
                ) {
                    recyclerLeaveHistory!!.visibility = View.VISIBLE
                    GetLeaveptincipleHistory()
                    recyclerAttendance!!.visibility = View.GONE
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
                    recyclerLeaveHistory!!.visibility = View.GONE
                    GetLeaveptincipleHistory()
                    recyclerAttendance!!.visibility = View.VISIBLE
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
        lblNoDataFound!!.visibility = View.GONE
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