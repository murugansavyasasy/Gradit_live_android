package com.vsca.vsnapvoicecollege.Activities

import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.graphics.Color
import android.graphics.Rect
import android.os.Bundle
import android.text.method.HideReturnsTransformationMethod
import android.text.method.PasswordTransformationMethod
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.webkit.WebChromeClient
import android.webkit.WebView
import android.widget.*
import androidx.annotation.Nullable
import androidx.appcompat.app.ActionBar
import androidx.appcompat.app.AppCompatActivity
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.ItemDecoration
import butterknife.BindView
import butterknife.ButterKnife
import butterknife.OnClick
import butterknife.Optional
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.google.android.material.bottomsheet.BottomSheetBehavior
import com.google.android.material.bottomsheet.BottomSheetBehavior.BottomSheetCallback
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.ActivitySender.Hall_Ticket
import com.vsca.vsnapvoicecollege.ActivitySender.PunchStaffAttendanceUsingFinger
import com.vsca.vsnapvoicecollege.ActivitySender.StaffWiseAttendanceReports
import com.vsca.vsnapvoicecollege.Adapters.HomeMenus
import com.vsca.vsnapvoicecollege.Interfaces.HomeMenuClickListener
import com.vsca.vsnapvoicecollege.Model.GetOverAllCountDetails
import com.vsca.vsnapvoicecollege.Model.MenuDetailsResponse
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.*
import com.vsca.vsnapvoicecollege.ViewModel.App
import com.vsca.vsnapvoicecollege.ViewModel.Dashboards
import java.io.File


abstract class BaseActivity : AppCompatActivity() {

    private lateinit var bottomSheetBehavior: BottomSheetBehavior<LinearLayout>

    @JvmField
    @BindView(R.id.img_swipe)
    var img_swipe: ImageView? = null

    @JvmField
    @BindView(R.id.btnContinue)
    var btnContinue: Button? = null

    @JvmField
    @Nullable
    @BindView(R.id.layoutbottomCurve)
    var layoutbottomCurve: ConstraintLayout? = null

    @JvmField
    @Nullable
    @BindView(R.id.recyclermenusbottom)
    var recyclermenusbottom: RecyclerView? = null

    @JvmField
    @Nullable
    @BindView(R.id.swipeUpMenus)
    var llBottomSheet: LinearLayout? = null

    @JvmField
    @Nullable
    @BindView(R.id.LayoutDepartment)
    var LayoutDepartment: ConstraintLayout? = null

    @JvmField
    @Nullable
    @BindView(R.id.LayoutCollege)
    var LayoutCollege: ConstraintLayout? = null

    @JvmField
    @Nullable
    @BindView(R.id.imgAddPlus)
    var imgAddPlus: ImageView? = null

    open var appViewModel: App? = null
    open var menuadapter: HomeMenus? = null
    var UserMenuData: ArrayList<MenuDetailsResponse> = ArrayList()
    var MenuList: ArrayList<MenuDetailsResponse> = ArrayList()
    var OverAllMenuCountData: List<GetOverAllCountDetails> = ArrayList()


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(layoutResourceId)
        ButterKnife.bind(this)

        recyclermenusbottom!!.visibility = View.VISIBLE
        appviewModelbase = ViewModelProvider(this)[App::class.java]
        appviewModelbase!!.init()

        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()

        layoutbottomCurve!!.visibility = View.INVISIBLE
        dashboardViewModel = ViewModelProvider(this)[Dashboards::class.java]
        dashboardViewModel!!.init()

        if (CommonUtil.MenuListDashboard.isEmpty()) {

            Log.d("MenuArrayList", "Empty")

            dashboardViewModel!!.userMenuLiveData!!.observe(this) { response ->
                if (response != null) {
                    val status = response.status
                    val message = response.message
                    MenuList.clear()
                    if (status == 1) {
                        UserMenuData = response.data!!

                        // If you want to add the menu for testing use below code. And don't delete it.

//                        if (CommonUtil.Priority.equals("p1") || CommonUtil.Priority.equals("p2")) {
//                            val newElements = listOf(
//                                MenuDetailsResponse(
//                                    21,
//                                    "Mark Your Attendance",
//                                    "mark_your_attendance",
//                                    1,
//                                    1,
//                                    21,
//                                    2
//                                ),
//                                MenuDetailsResponse(
//                                    22,
//                                    "Attendance Report",
//                                    "attendance_report",
//                                    1,
//                                    1,
//                                    22,
//                                    2
//                                )
//                            )
//                            UserMenuData.addAll(newElements)
//                        } else if (CommonUtil.Priority.equals("p3")) {
//                            val newElements = listOf(
//                                MenuDetailsResponse(
//                                    22,
//                                    "Attendance Report",
//                                    "attendance_report",
//                                    0,
//                                    0,
//                                    22,
//                                    2
//                                )
//                            )
//                            UserMenuData.addAll(newElements)
//                        }
                        //

                        for (j in UserMenuData.indices) {

                            val id = UserMenuData[j].id
                            val name = UserMenuData[j].name
                            val menuslug = UserMenuData[j].menu_slug
                            val is_read_enabled = UserMenuData[j].is_read_enabled
                            val is_write_enabled = UserMenuData[j].is_write_enabled
                            val order_id = UserMenuData[j].order_id
                            val parent_id = UserMenuData[j].parent_id

                            MenuList.add(
                                MenuDetailsResponse(
                                    id,
                                    name,
                                    menuslug!!,
                                    is_read_enabled!!,
                                    is_write_enabled!!,
                                    order_id!!,
                                    parent_id!!
                                )
                            )
                            CommonUtil.MenuListDashboard.add(
                                MenuDetailsResponse(
                                    id,
                                    name,
                                    menuslug,
                                    is_read_enabled,
                                    is_write_enabled,
                                    order_id,
                                    parent_id
                                )
                            )
                        }

                        layoutbottomCurve!!.visibility = View.VISIBLE
                        for (k in MenuList.indices) {

                            CommonUtil.UserMenuListId.add(MenuList.get(k).id)

                            if (MenuList[k].menu_slug.equals(CommonUtil.Home)) {
                                DashboardHomeMenuID = MenuList[k].id.toString()
                                Log.d("DashboardHomeMenuIDbase", DashboardHomeMenuID)
                            }

                            if (MenuList[k].menu_slug.equals(CommonUtil.Chat)) {
                                ChatMenuID = MenuList[k].id.toString()
                                Log.d("Chat", ChatMenuID)
                            }

                            if (MenuList[k].menu_slug.equals(CommonUtil.Communication)) {
                                CommunicationMenuID = MenuList[k].id.toString()
                                Log.d("CommunicationMenu", CommunicationMenuID)
                            }

                            if (MenuList[k].menu_slug.equals(CommonUtil.Examination)) {
                                ExamMenuID = MenuList[k].id.toString()
                                Log.d("ExamMenuID", ExamMenuID)
                            }
                            if (MenuList[k].menu_slug.equals(CommonUtil.Attendance)) {
                                AttendanceMeuID = MenuList[k].id.toString()
                                Log.d("AttendanceMeuID", AttendanceMeuID)
                            }
                            if (MenuList[k].menu_slug.equals(CommonUtil.Assignment)) {
                                AssignmentMenuID = MenuList[k].id.toString()
                            }
                            if (MenuList[k].menu_slug.equals(CommonUtil.Circular)) {
                                CircularMenuID = MenuList[k].id.toString()
                            }
                            if (MenuList[k].menu_slug.equals(CommonUtil.NoticeBoard)) {
                                NoticeboardMenuID = MenuList[k].id.toString()
                            }
                            if (MenuList[k].menu_slug.equals(CommonUtil.Events)) {
                                EventsMenuID = MenuList[k].id.toString()
                            }
                            if (MenuList[k].menu_slug.equals(CommonUtil.Faculty)) {
                                FacultyMenuID = MenuList[k].id.toString()
                            }
                            if (MenuList.get(k).menu_slug.equals(CommonUtil.Video)) {
                                VideoMenuID = MenuList[k].id.toString()
                            }
                            if (MenuList.get(k).menu_slug.equals(CommonUtil.Course_Details)) {
                                CourseDetailsMenuID = MenuList[k].id.toString()
                            }
                            if (MenuList.get(k).menu_slug.equals(CommonUtil.Category_Credit_Points)) {
                                CategoryDetailsMenuID = MenuList[k].id.toString()
                            }
                            if (MenuList.get(k).menu_slug.equals(CommonUtil.Sem_Credit_Points)) {
                                SemesterCreditMenuID = MenuList[k].id.toString()
                            }
                            if (MenuList.get(k).menu_slug.equals(CommonUtil.Exam_Application_Details)) {
                                ExamApplicationMenuID = MenuList[k].id.toString()
                            }
                            if (MenuList.get(k).menu_slug.equals(CommonUtil.Hall_Ticket)) {
                                Hall_TicketId = MenuList[k].id.toString()
                            }
                            if (MenuList.get(k).menu_slug.equals(CommonUtil.FeeDetails)) {
                                isFeeDetails = MenuList[k].id.toString()
                            }
                        }
                        menuadapter =
                            HomeMenus(applicationContext, MenuList, object : HomeMenuClickListener {
                                override fun onMenuClick(
                                    holder: HomeMenus.MyViewHolder, data: MenuDetailsResponse
                                ) {
                                    holder.LayoutHome!!.setOnClickListener {
                                        ParticularMenuClick(
                                            data
                                        )
                                    }
                                }
                            })

                        val mLayoutManager: RecyclerView.LayoutManager =
                            GridLayoutManager(applicationContext, 4)
                        recyclermenusbottom!!.layoutManager = mLayoutManager
                        recyclermenusbottom!!.isNestedScrollingEnabled = false
                        recyclermenusbottom!!.addItemDecoration(GridSpacingItemDecoration(4, false))
                        recyclermenusbottom!!.itemAnimator = DefaultItemAnimator()
                        recyclermenusbottom!!.adapter = menuadapter
                    } else {
                        CommonUtil.ApiAlertContext(applicationContext, message)
                    }
                }
            }

        } else {


            Log.d("MenuArrayList", "isnotEmpty")
            layoutbottomCurve!!.visibility = View.VISIBLE

            for (k in CommonUtil.MenuListDashboard.indices) {

                CommonUtil.UserMenuListId.add(CommonUtil.MenuListDashboard.get(k).id)

                if (CommonUtil.MenuListDashboard[k].menu_slug.equals(CommonUtil.Home)) {
                    DashboardHomeMenuID = CommonUtil.MenuListDashboard.get(k).id.toString()
                    Log.d("DashboardHomeMenuIDbase", DashboardHomeMenuID)
                }

                if (CommonUtil.MenuListDashboard[k].menu_slug.equals(CommonUtil.Chat)) {
                    ChatMenuID = CommonUtil.MenuListDashboard.get(k).id.toString()
                    Log.d("Chat", ChatMenuID)
                }
                if (CommonUtil.MenuListDashboard[k].menu_slug.equals(CommonUtil.Communication)) {
                    CommunicationMenuID = CommonUtil.MenuListDashboard.get(k).id.toString()
                    Log.d("communicationmenu", CommunicationMenuID)
                }
                if (CommonUtil.MenuListDashboard[k].menu_slug.equals(CommonUtil.Examination)) {
                    ExamMenuID = CommonUtil.MenuListDashboard.get(k).id.toString()
                    Log.d("ExamMenuID", ExamMenuID)
                }
                if (CommonUtil.MenuListDashboard[k].menu_slug.equals(CommonUtil.Attendance)) {
                    AttendanceMeuID = CommonUtil.MenuListDashboard.get(k).id.toString()
                    Log.d("AttendanceMeuID", AttendanceMeuID)
                }
                if (CommonUtil.MenuListDashboard[k].menu_slug.equals(CommonUtil.Assignment)) {
                    AssignmentMenuID = CommonUtil.MenuListDashboard.get(k).id.toString()
                }
                if (CommonUtil.MenuListDashboard[k].menu_slug.equals(CommonUtil.Circular)) {
                    CircularMenuID = CommonUtil.MenuListDashboard.get(k).id.toString()
                }
                if (CommonUtil.MenuListDashboard[k].menu_slug.equals(CommonUtil.NoticeBoard)) {
                    NoticeboardMenuID = CommonUtil.MenuListDashboard.get(k).id.toString()
                }
                if (CommonUtil.MenuListDashboard.get(k).menu_slug.equals(CommonUtil.Events)) {
                    EventsMenuID = CommonUtil.MenuListDashboard.get(k).id.toString()
                }
                if (CommonUtil.MenuListDashboard[k].menu_slug.equals(CommonUtil.Faculty)) {
                    FacultyMenuID = CommonUtil.MenuListDashboard.get(k).id.toString()
                }
                if (CommonUtil.MenuListDashboard[k].menu_slug.equals(CommonUtil.Video)) {
                    VideoMenuID = CommonUtil.MenuListDashboard.get(k).id.toString()
                }
                if (CommonUtil.MenuListDashboard[k].menu_slug.equals(CommonUtil.Course_Details)) {
                    CourseDetailsMenuID = CommonUtil.MenuListDashboard.get(k).id.toString()
                }
                if (CommonUtil.MenuListDashboard[k].menu_slug.equals(CommonUtil.Category_Credit_Points)) {
                    CategoryDetailsMenuID = CommonUtil.MenuListDashboard.get(k).id.toString()
                }
                if (CommonUtil.MenuListDashboard[k].menu_slug.equals(CommonUtil.Sem_Credit_Points)) {
                    SemesterCreditMenuID = CommonUtil.MenuListDashboard.get(k).id.toString()
                }
                if (CommonUtil.MenuListDashboard[k].menu_slug.equals(CommonUtil.Exam_Application_Details)) {
                    ExamApplicationMenuID = CommonUtil.MenuListDashboard.get(k).id.toString()
                }
                if (CommonUtil.MenuListDashboard[k].menu_slug.equals(CommonUtil.Hall_Ticket)) {
                    Hall_TicketId = CommonUtil.MenuListDashboard.get(k).id.toString()
                }
                if (CommonUtil.MenuListDashboard[k].menu_slug.equals(CommonUtil.FeeDetails)) {
                    isFeeDetails = CommonUtil.MenuListDashboard.get(k).id.toString()
                }
            }

            menuadapter = HomeMenus(
                applicationContext,
                CommonUtil.MenuListDashboard,
                object : HomeMenuClickListener {
                    override fun onMenuClick(
                        holder: HomeMenus.MyViewHolder, data: MenuDetailsResponse
                    ) {
                        holder.LayoutHome!!.setOnClickListener {
                            ParticularMenuClick(data)
                        }
                    }
                })
            val mLayoutManager: RecyclerView.LayoutManager =
                GridLayoutManager(applicationContext, 4)
            recyclermenusbottom!!.layoutManager = mLayoutManager
            recyclermenusbottom!!.isNestedScrollingEnabled = false
            recyclermenusbottom!!.addItemDecoration(GridSpacingItemDecoration(4, false))
            recyclermenusbottom!!.itemAnimator = DefaultItemAnimator()
            recyclermenusbottom!!.adapter = menuadapter

        }

        appviewModelbase!!.appreadstatusresponseLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {

                    Log.d("AppReadMessage", message!!)

                } else {

                }
            }
        }

        appviewModelbase!!.ChangePasswordLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    ApiAlertOk(this, message, true)
                } else {
                    ApiAlertOk(this, message, false)
                }
            } else {
                ApiAlertOk(this, CommonUtil.Something_went_wrong, false)

            }
        }
    }

    fun ApiAlertOk(activity: Activity?, msg: String?, value: Boolean) {
        if (activity != null) {
            val dlg = androidx.appcompat.app.AlertDialog.Builder(activity)
            dlg.setTitle(CommonUtil.Info)
            dlg.setMessage(msg)
            dlg.setPositiveButton(CommonUtil.OK) { dialog, which ->

                if (value) {
                    changePassword!!.dismiss()
                    val i = Intent(activity, Login::class.java)
                    startActivity(i)
                    finishAffinity()
                }
            }
            dlg.setCancelable(false)
            dlg.create()
            dlg.show()
        }
    }

    private fun ParticularMenuClick(data: MenuDetailsResponse) {

        CommonUtil.EventStatus = "Upcoming"
        if (data.id == 1) {
            CommonUtil.menu_readHome = data.is_read_enabled.toString()
            CommonUtil.menu_writeHome = data.is_write_enabled.toString()
            if (CommonUtil.MenuDashboardHome) {
                val i: Intent = Intent(this@BaseActivity, DashBoard::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
        }

//        if (data.id == 2) {
//            CommonUtil.MenuIDCommunication = data.id.toString()
//            CommonUtil.menu_readCommunication = data.is_read_enabled.toString()
//            CommonUtil.menu_writeCommunication = data.is_write_enabled.toString()
//            if (CommonUtil.MenuCommunication) {
//                val i: Intent = Intent(this@BaseActivity, Communication::class.java)
//                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
//                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
//                startActivity(i)
//            }
//        }

        if (data.id == 3) {
            CommonUtil.menu_readExamination = data.is_read_enabled.toString()
            CommonUtil.menu_writeExamination = data.is_write_enabled.toString()
            CommonUtil.MenuIDExamination = data.id.toString()
            if (CommonUtil.MenuExamination) {
                val i: Intent = Intent(this@BaseActivity, ExamList::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
        }

        if (data.id == 4) {
            CommonUtil.MenuIdAttendance = data.id.toString()
            CommonUtil.menu_readAttendance = data.is_read_enabled.toString()
            CommonUtil.menu_writeAttendance = data.is_write_enabled.toString()
            if (CommonUtil.MenuAttendance) {
                val i: Intent = Intent(this@BaseActivity, Attendance::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
        }

        if (data.id == 5) {
            CommonUtil.MenuIDAssignment = data.id.toString()
            CommonUtil.menu_readAssignment = data.is_read_enabled.toString()
            CommonUtil.menu_writeAssignment = data.is_write_enabled.toString()
            if (CommonUtil.MenuAssignment) {
                val i: Intent = Intent(this@BaseActivity, Assignment::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
        }
        if (data.id == 6) {
            CommonUtil.MenuIDCircular = data.id.toString()
            CommonUtil.menu_readCircular = data.is_read_enabled.toString()
            CommonUtil.menu_writeCircular = data.is_write_enabled.toString()
            if (CommonUtil.MenuCircular) {
                val i: Intent = Intent(this@BaseActivity, Circular::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
        }

        if (data.id == 7) {
            CommonUtil.MenuIDNoticeboard = data.id.toString()
            CommonUtil.menu_readNoticeBoard = data.is_read_enabled.toString()
            CommonUtil.menu_writeNoticeBoard = data.is_write_enabled.toString()
            if (CommonUtil.MenuNoticeBoard) {
                val i: Intent = Intent(this@BaseActivity, Noticeboard::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
        }

        if (data.id == 8) {
            CommonUtil.MenuIDEvents = data.id.toString()
            CommonUtil.menu_readEvent = data.is_read_enabled.toString()
            CommonUtil.menu_writeEvent = data.is_write_enabled.toString()
            if (CommonUtil.MenuEvents) {
                val i: Intent = Intent(this@BaseActivity, Events::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
        }

        if (data.id == 9) {
            CommonUtil.menu_readFaculty = data.is_read_enabled.toString()
            CommonUtil.menu_writeFaculty = data.is_write_enabled.toString()
            if (CommonUtil.MenuFaculty) {
                val i: Intent = Intent(this@BaseActivity, Faculty::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
        }

        if (data.id == 10) {
            CommonUtil.menu_readVideo = data.is_read_enabled.toString()
            CommonUtil.menu_writeVideo = data.is_write_enabled.toString()
            CommonUtil.MenuIDVideo = data.id.toString()
            if (CommonUtil.MenuVideo) {
                val i: Intent = Intent(this@BaseActivity, Video::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
        }

        if (data.id == 11) {
            CommonUtil.menu_readChat = data.is_read_enabled.toString()
            CommonUtil.menu_writeChat = data.is_write_enabled.toString()
            CommonUtil.MenuIDChat = data.id.toString()
            if (CommonUtil.MenuChat) {
                val i: Intent = Intent(this@BaseActivity, ChatParent::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
        }
        if (data.id == 12) {
            CommonUtil.menu_readCourseDetails = data.is_read_enabled.toString()
            CommonUtil.menu_writeCourseDetails = data.is_write_enabled.toString()
            if (CommonUtil.MenuCourseDetails) {
                CommonUtil.parentMenuCourseExam = 0
                val i: Intent = Intent(this@BaseActivity, CourseDetails::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
        }

        if (data.id == 13) {
            CommonUtil.menu_readCategoryCreditPoints = data.is_read_enabled.toString()
            CommonUtil.menu_writeCategoryCreditPoints = data.is_write_enabled.toString()
            if (CommonUtil.MenuCategoryCredit) {
                val i: Intent = Intent(this@BaseActivity, CategoryCreditWise::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
        }

        if (data.id == 14) {
            CommonUtil.menu_readSemCreditPoints = data.is_read_enabled.toString()
            CommonUtil.menu_writeSemCreditPoints = data.is_write_enabled.toString()
            if (CommonUtil.MenuSemCredit) {
                val i: Intent = Intent(this@BaseActivity, SemesterCreditCategoryWise::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
        }
        if (data.id == 15) {
            CommonUtil.menu_readExamApplicationDetails = data.is_read_enabled.toString()
            CommonUtil.menu_writeExamApplicationDetails = data.is_write_enabled.toString()
            if (CommonUtil.MenuExamDetails) {
                CommonUtil.parentMenuCourseExam = 1
                val i: Intent = Intent(this@BaseActivity, CourseDetails::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
        }

        if (data.id == 19) {
            CommonUtil.menu_readHallTicker = data.is_read_enabled.toString()
            CommonUtil.menu_writeHallTicker = data.is_write_enabled.toString()
            if (CommonUtil.MenuHallTicket) {

                val i: Intent = Intent(this@BaseActivity, Hall_Ticket::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
        }

        if (data.id == 16) {
            CommonUtil.MenuIDCommunication = data.id.toString()
            CommonUtil.menu_readCommunication = data.is_read_enabled.toString()
            CommonUtil.menu_writeCommunication = data.is_write_enabled.toString()
            if (CommonUtil.MenuCommunication) {
                val i: Intent = Intent(this@BaseActivity, Communication::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
        }

        if (data.id == 17) {
            CommonUtil.MenuIDCommunicationText = data.id.toString()
            CommonUtil.menu_readCommunicationText = data.is_read_enabled.toString()
            CommonUtil.menu_writeCommunicationText = data.is_write_enabled.toString()
            if (CommonUtil.MenuText) {
                val i: Intent = Intent(this@BaseActivity, MessageCommunication::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
        }
        if (data.id == 20) {
            if (CommonUtil.MenuFeeDetails) {
                val i: Intent = Intent(this@BaseActivity, FeeDetails::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
        }

        if (data.id == 21) {
            CommonUtil.menu_writeMarkAttendance = data.is_write_enabled.toString()
            if (CommonUtil.MarkAttendance) {
                val i: Intent =
                    Intent(this@BaseActivity, PunchStaffAttendanceUsingFinger::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
        }

        if (data.id == 22) {
            if (CommonUtil.AttendanceReport) {
                val i: Intent = Intent(this@BaseActivity, StaffWiseAttendanceReports::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                startActivity(i)
            }
        }
    }

    @Optional
    @OnClick(R.id.btnContinue)
    fun check() {
        val intents = Intent(this@BaseActivity, DashBoard::class.java)
        startActivity(intents)
    }

    protected abstract val layoutResourceId: Int

    fun ActionBarMethod(activity: Activity) {
        supportActionBar!!.displayOptions = ActionBar.DISPLAY_SHOW_CUSTOM
        supportActionBar!!.setDisplayShowCustomEnabled(true)
        supportActionBar!!.setCustomView(R.layout.action_bar_layout)
        val view = supportActionBar!!.customView
        val lblMemberName = view.findViewById<View>(R.id.lblMemberName) as TextView
        val layoutUserDetails = view.findViewById<View>(R.id.layoutUserDetails) as ConstraintLayout
        val layoutWithProfiles = view.findViewById<View>(R.id.layoutUserDetails) as ConstraintLayout

        val lblRole = view.findViewById<View>(R.id.lblRole) as TextView
        val imgMan = view.findViewById<View>(R.id.imgProfile) as ImageView
        SearchList = view.findViewById<View>(R.id.imgSearch) as ImageView
        Search = view.findViewById<View>(R.id.Search) as ConstraintLayout
        idSV = view.findViewById<View>(R.id.idSV) as SearchView
        txt_Cancel = view.findViewById<View>(R.id.txt_Cancel) as TextView
        imgNotification = view.findViewById<View>(R.id.imgNotification) as ImageView
        imgRefresh = view.findViewById<View>(R.id.imgRefresh) as ImageView
        val imgCollegeLogo = view.findViewById<View>(R.id.imgCollegeLogo) as ImageView
        val constAction = view.findViewById<View>(R.id.constAction) as ConstraintLayout
        if (CommonUtil.CollegeLogo == null || CommonUtil.CollegeLogo.isEmpty()) {
            Glide.with(activity).load(R.drawable.dummy_college_icon)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(imgCollegeLogo)
        } else {
            Glide.with(activity).load(CommonUtil.CollegeLogo)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(imgCollegeLogo)
        }
        imgNotification!!.setOnClickListener {
            CommonUtil.HeaderMenuNotification = true
            val i = Intent(activity, Notification::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.startActivity(i)
        }

        imgMan.setOnClickListener { ProfilePopUp(activity) }
        imgCollegeLogo.setOnClickListener { IntentToChangeRole(activity) }
        layoutUserDetails.setOnClickListener { IntentToChangeRole(activity) }
        if (CommonUtil.Priority == "p1") {
            constAction.setBackgroundColor(Color.parseColor(getString(R.string.txt_color_white)))
            lblRole.setTextColor(Color.parseColor(getString(R.string.txt_color_prinicpal)))
            lblMemberName.text = CommonUtil.MemberName
            lblRole.text = CommonUtil.MemberType
        }
        if (CommonUtil.Priority.equals("p7")) {
            constAction.setBackgroundColor(Color.parseColor(getString(R.string.txt_color_white)))
            lblRole.setTextColor(Color.parseColor(getString(R.string.txt_color_lightorang)))
            lblMemberName.text = CommonUtil.MemberName
            lblRole.text = CommonUtil.MemberType
        }
        if (CommonUtil.Priority == "p2" || CommonUtil.Priority == "p3") {
            constAction.setBackgroundColor(Color.parseColor(getString(R.string.txt_color_white)))
            lblRole.setTextColor(Color.parseColor(getString(R.string.txt_clr_staff)))
            lblMemberName.text = CommonUtil.MemberName
            lblRole.text = CommonUtil.MemberType
        }
        if (CommonUtil.Priority == "p4") {
            constAction.setBackgroundColor(Color.parseColor(getString(R.string.txt_color_white)))
            lblRole.setTextColor(Color.parseColor(getString(R.string.txt_color_receiver)))
            lblMemberName.text = CommonUtil.MemberName
            lblRole.text = CommonUtil.MemberType
        }
        if (CommonUtil.Priority == "p5") {
            constAction.setBackgroundColor(Color.parseColor(getString(R.string.txt_color_white)))
            lblRole.setTextColor(Color.parseColor(getString(R.string.txt_color_parent)))
            lblMemberName.text = CommonUtil.MemberName
            lblRole.text = CommonUtil.MemberType
        }
        if (CommonUtil.Priority == "p6") {
            constAction.setBackgroundColor(Color.parseColor(getString(R.string.txt_color_white)))
            lblRole.setTextColor(Color.parseColor(getString(R.string.txt_color_parent)))
            lblMemberName.text = CommonUtil.MemberName
            lblRole.text = CommonUtil.MemberType
        }
    }


    private fun ProfilePopUp(activity: Activity) {

        val layoutInflater = activity.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.popup_profile_menus, null)
        val logout = view.findViewById<View>(R.id.layoutLogout) as ConstraintLayout
        val layoutChangeRoles = view.findViewById<View>(R.id.layoutChangeRole) as ConstraintLayout
        val layoutProfile = view.findViewById<View>(R.id.layoutProfile) as ConstraintLayout
        val layoutFaq = view.findViewById<View>(R.id.layoutFAQ) as ConstraintLayout
        val layoutHelp = view.findViewById<View>(R.id.layoutHelp) as ConstraintLayout
        val layoutPrivacypolicy =
            view.findViewById<View>(R.id.layoutPrivacypolicy) as ConstraintLayout
        val layoutTermsAndCondition =
            view.findViewById<View>(R.id.layoutTermsCondition) as ConstraintLayout
        val layoutChangepassword =
            view.findViewById<View>(R.id.layoutChangePassword) as ConstraintLayout
        val layoutClearCache = view.findViewById<View>(R.id.layoutClearCache) as ConstraintLayout

        if (CommonUtil.Priority == "p4" || CommonUtil.Priority == "p5") {
            layoutProfile.visibility = View.VISIBLE
        } else {
            layoutProfile.visibility = View.GONE
        }

        layoutProfile.setOnClickListener {
            profilePopup!!.dismiss()
            CommonUtil.parentMenuCourseExam = 2
            val i = Intent(activity, CourseDetails::class.java)
            i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
            i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
            activity.startActivity(i)
        }

        layoutChangepassword.setOnClickListener {
            ChangepasswordPopup(activity)
        }
        layoutFaq.setOnClickListener {
            val faq = SharedPreference.getSH_Faq(activity)
            LoadWebView(activity, faq, 0)
        }
        layoutHelp.setOnClickListener {
            val help = SharedPreference.getSH_Help(activity)
            LoadWebView(activity, help, 1)
        }
        layoutPrivacypolicy.setOnClickListener {
            val privacypolicy = SharedPreference.getSH_Privacypolicy(activity)
            LoadWebView(activity, privacypolicy, 2)
        }
        layoutTermsAndCondition.setOnClickListener {
            val termsCondition = SharedPreference.getSH_TermsNcondition(activity)
            LoadWebView(activity, termsCondition, 3)
        }

        logout.setOnClickListener {
            LogoutAlert(getString(R.string.txt_alert_logout), 1, activity)
        }
        layoutChangeRoles.setOnClickListener { IntentToChangeRole(activity) }
        layoutClearCache.setOnClickListener { ClearCache(activity) }
        profilePopup =
            PopupWindow(view, ListPopupWindow.WRAP_CONTENT, ListPopupWindow.WRAP_CONTENT, true)
        profilePopup!!.contentView = view
        profilePopup!!.isOutsideTouchable = false
        profilePopup!!.showAtLocation(view, Gravity.RIGHT or Gravity.TOP, 0, 225)
    }

    private fun ChangepasswordPopup(activity: Activity) {
        val layoutInflater = activity.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val view = layoutInflater.inflate(R.layout.change_password_popup, null)
        val txtOldPassword = view.findViewById<View>(R.id.txtOldPassword) as EditText
        val txtNewPassword = view.findViewById<View>(R.id.txtNewPassword) as EditText
        val txtConfirmPassword = view.findViewById<View>(R.id.txtConfirmPassword) as EditText
        val imgOldPassword = view.findViewById<View>(R.id.imgOldPassword) as ImageView
        val imgNewPassword = view.findViewById<View>(R.id.imgNewPassword) as ImageView
        val imgconfirmpassword = view.findViewById<View>(R.id.imgconfirmpassword) as ImageView
        val btnSubmit = view.findViewById<View>(R.id.btnSubmit) as Button
        val imgClose = view.findViewById<View>(R.id.imgClose) as ImageView
        imgClose.setOnClickListener({
            changePassword!!.dismiss()
        })
        imgconfirmpassword.setOnClickListener({
            passwordHideandShow(txtConfirmPassword, imgconfirmpassword)
        })
        imgNewPassword.setOnClickListener({
            passwordHideandShow(txtNewPassword, imgNewPassword)
        })
        imgOldPassword.setOnClickListener({
            passwordHideandShow(txtOldPassword, imgOldPassword)
        })

        btnSubmit.setOnClickListener(View.OnClickListener {
            OldPassword = txtOldPassword.text.toString()
            NewPassword = txtNewPassword.text.toString()
            val confirmpassword = txtConfirmPassword.text.toString()

            if (OldPassword!!.isEmpty()) {
                CommonUtil.ApiAlert(this, getString(R.string.lbl_enter_oldpassword))
            } else if (NewPassword!!.isEmpty()) {
                CommonUtil.ApiAlert(this, getString(R.string.lbl_enter_new_password))
            } else if (confirmpassword.isEmpty()) {
                CommonUtil.ApiAlert(this, getString(R.string.lbl_confim_password))
            } else if (OldPassword == NewPassword) {
                CommonUtil.ApiAlert(this, getString(R.string.lbl_similar_password))
            } else if (NewPassword == confirmpassword) {
                ChangePasswordRequest(activity)
            } else {
                CommonUtil.ApiAlert(this, getString(R.string.lbl_pswrd_not_match))
            }
        })

        changePassword =
            PopupWindow(view, ListPopupWindow.MATCH_PARENT, ListPopupWindow.MATCH_PARENT, true)
        changePassword!!.contentView = view
        changePassword!!.isOutsideTouchable = false
        changePassword!!.showAtLocation(view, Gravity.CENTER or Gravity.TOP, 0, 0)
    }

    private fun passwordHideandShow(txtPassword: EditText?, imgEye: ImageView?) {

        if (passwordvisible == true) {
            txtPassword?.transformationMethod = HideReturnsTransformationMethod.getInstance()
            txtPassword?.setSelection(txtPassword.text!!.length)
            passwordvisible = false
            imgEye?.setImageResource(R.drawable.ic_lock)
        } else {
            txtPassword?.transformationMethod = PasswordTransformationMethod.getInstance()
            txtPassword?.setSelection(txtPassword.text!!.length)
            passwordvisible = true
            imgEye?.setImageResource(R.drawable.ic_lock_open)
        }
    }

    override fun onBackPressed() {
        CommonUtil.OnBackSetBottomMenuClickTrue()
        super.onBackPressed()
    }

    private fun IntentToChangeRole(activity: Activity) {
        val i = Intent(activity, LoginRoles::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        activity.startActivity(i)
    }

    fun MenuBottomType() {

        Log.d("BottomMenu", "BottomMenu")
        when (CommonUtil.Priority) {
            "p1" -> {
                layoutbottomCurve!!.setBackgroundResource(R.drawable.img_prinicipal_bottom_card)
            }

            "p2", "p3" -> {
                layoutbottomCurve!!.setBackgroundResource(R.drawable.img_staff_bottom_card)
            }

            "p4" -> {
                layoutbottomCurve!!.setBackgroundResource(R.drawable.img_student_bottom_card)
            }

            "p5" -> {
                layoutbottomCurve!!.setBackgroundResource(R.drawable.img_parent_bottom_card)
            }

            "p6" -> {
                layoutbottomCurve!!.setBackgroundResource(R.drawable.img_staff_bottom_card)
            }

            "p7" -> {
                layoutbottomCurve!!.setBackgroundResource(R.drawable.img_header_bottom_card)
            }
        }

        bottomSheetBehavior = BottomSheetBehavior.from(llBottomSheet!!)
        bottomSheetBehavior.addBottomSheetCallback(object : BottomSheetCallback() {
            override fun onStateChanged(bottomSheet: View, newState: Int) {

                if (newState == 3) {
                    img_swipe!!.setImageResource(R.drawable.ic_arrowdown_white)

                } else if (newState == 4) {
                    img_swipe!!.setImageResource(R.drawable.ic_arrowup_white)
                }
            }

            override fun onSlide(bottomSheet: View, slideOffset: Float) {}
        })
    }

    fun bottomsheetStateCollpased() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_EXPANDED) {
            Log.d("expanded", "expanded")
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        }
    }

    fun bottomsheetStateHidden() {
        if (bottomSheetBehavior.state == BottomSheetBehavior.STATE_COLLAPSED) {
            Log.d("hidden", "hidden")
            bottomSheetBehavior.state = BottomSheetBehavior.STATE_HIDDEN
        }
    }

    fun ClearCache(activity: Activity) {
        try {
            val dir = activity.cacheDir
            if (deleteDir(dir)) {
                AlertOk(activity, CommonUtil.Cache_cleared, true)
            } else {
                AlertOk(activity, CommonUtil.Cache_not_cleared, true)
            }
        } catch (e: Exception) {
            e.printStackTrace()
        }
    }

    private fun AlertOk(activity: Activity, Msg: String, value: Boolean) {
        val builder = AlertDialog.Builder(activity)
        builder.setTitle("Clear Cache")
        builder.setMessage(Msg)
        builder.setCancelable(false)
        builder.setPositiveButton(CommonUtil.OK) { dialog, which ->
            if (value) {
                profilePopup!!.dismiss()
            }
        }
        builder.create().show()
    }

    fun LoadWebView(activity: Activity?, url: String?, Type: Int) {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val layout = inflater.inflate(R.layout.activity_terms_condition, null)
        popupWebview = PopupWindow(
            layout,
            android.app.ActionBar.LayoutParams.MATCH_PARENT,
            android.app.ActionBar.LayoutParams.MATCH_PARENT,
            true
        )
        popupWebview!!.contentView = layout
        popupWebview!!.showAtLocation(layout, Gravity.CENTER, 0, 0)
        val webview = layout.findViewById<WebView>(R.id.webview)
        val lblMenuHeaderName = layout.findViewById<TextView>(R.id.lblMenuHeaderName)
        val imgBack = layout.findViewById<ImageView>(R.id.imgBack)
        val btnTerms = layout.findViewById<Button>(R.id.btnTermsAndCondition)
        val LayoutHeader = layout.findViewById<ConstraintLayout>(R.id.LayoutHeader)
        val viewLine = layout.findViewById<View>(R.id.viewline)
        btnTerms.visibility = View.GONE
        LayoutHeader.visibility = View.VISIBLE
        viewLine.visibility = View.VISIBLE
        when (Type) {
            0 -> {
                lblMenuHeaderName.setText(R.string.txt_faq)
            }

            1 -> {
                lblMenuHeaderName.setText(R.string.txt_help)
            }

            2 -> {
                lblMenuHeaderName.setText(R.string.txt_privacy_policy)
            }

            3 -> {
                lblMenuHeaderName.setText(R.string.txt_terms_amp_condition)
            }
        }
        imgBack.setOnClickListener { popupWebview!!.dismiss() }
        val progressDialog = CustomLoading.createProgressDialog(activity)
        webview.webChromeClient = object : WebChromeClient() {
            override fun onProgressChanged(view: WebView, progress: Int) {
                progressDialog.show()
                setProgress(progress * 100)
                if (progress == 100) {
                    progressDialog.dismiss()
                }
            }
        }

        webview.webViewClient = MyWebViewClient(activity!!)
        webview.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
        val webSettings = webview.settings
        webSettings.loadsImagesAutomatically = true
        webSettings.builtInZoomControls = true
        webSettings.javaScriptEnabled = true
        webview.loadUrl(url!!)
        progressDialog.dismiss()
    }

    class GridSpacingItemDecoration(private val spanCount: Int, includeEdge: Boolean) :
        ItemDecoration() {
        private var spacing = 4
        private val includeEdge: Boolean
        override fun getItemOffsets(
            outRect: Rect, view: View, parent: RecyclerView, state: RecyclerView.State
        ) {
            val position = parent.getChildAdapterPosition(view) // item position
            val column = position % spanCount
            if (includeEdge) {
                outRect.left = spacing - column * spacing / spanCount
                outRect.right = (column - 1) * spacing / spanCount
                if (position < spanCount) {
                    outRect.top = spacing
                }
                outRect.bottom = spacing
            } else {
                outRect.left = column * spacing / spanCount
                outRect.right = spacing - (column + 1) * spacing / spanCount
                if (position >= spanCount) {
                    outRect.top = spacing
                }
            }
        }

        init {
            this.includeEdge = includeEdge
        }
    }

    override fun onResume() {
        bottomSheetBehavior.state = BottomSheetBehavior.STATE_COLLAPSED
        super.onResume()
    }

    companion object {
        var profilePopup: PopupWindow? = null
        var popupWebview: PopupWindow? = null
        var changePassword: PopupWindow? = null
        var dashboardViewModel: Dashboards? = null
        var appviewModelbase: App? = null
        var imgRefresh: ImageView? = null
        var imgNotification: ImageView? = null
        var SearchList: ImageView? = null
        var txt_Cancel: TextView? = null
        var Search: ConstraintLayout? = null
        var idSV: SearchView? = null
        var OldPassword: String? = null
        var NewPassword: String? = null

        var NoticeboardMenuID = "0"
        var CircularMenuID = "0"
        var CommunicationMenuID = "0"
        var EventsMenuID = "0"
        var AssignmentMenuID = "0"
        var DashboardHomeMenuID = "0"
        var ExamMenuID = "0"
        var AttendanceMeuID = "0"
        var FacultyMenuID = "0"
        var VideoMenuID = "0"
        var ChatMenuID = "0"
        var CourseDetailsMenuID = "0"
        var CategoryDetailsMenuID = "0"
        var SemesterCreditMenuID = "0"
        var ExamApplicationMenuID = "0"
        var Hall_TicketId = "0"
        var isFeeDetails = "0"
        private var passwordvisible = true


        fun LogoutAlert(title: String?, value: Int, activity: Activity) {
            val builder = AlertDialog.Builder(activity)
            builder.setTitle(title)
            builder.setCancelable(false)
            builder.setPositiveButton(CommonUtil.Yes) { dialog, which ->
                if (value == 1) {
                    profilePopup!!.dismiss()
                }
                SharedPreference.clearShLogin(activity)
                CommonUtil.Priority = ""
                CommonUtil.MemberId = 0
                CommonUtil.MemberName = ""
                CommonUtil.MemberType = ""
                CommonUtil.CollegeLogo = ""
                CommonUtil.CollegeId = 0
                CommonUtil.DivisionId = ""
                CommonUtil.Courseid = ""
                CommonUtil.DepartmentId = ""
                CommonUtil.YearId = ""
                CommonUtil.SemesterId = ""
                CommonUtil.SectionId = ""
                CommonUtil.MobileNUmber = ""
                CommonUtil.isParentEnable = ""
                val i = Intent(activity, MobileNumber::class.java)
                i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
                activity.startActivity(i)
                activity.finish()
            }
            builder.setNegativeButton(CommonUtil.No) { dialog, which ->
                builder.setCancelable(false)
            }
            builder.create().show()
        }


        fun LoadWebViewContext(activity: Context?, url: String?) {
            Log.d("addurl", url.toString())
            val inflater = activity!!.getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
            val layout = inflater.inflate(R.layout.activity_terms_condition, null)
            popupWebview = PopupWindow(
                layout,
                android.app.ActionBar.LayoutParams.MATCH_PARENT,
                android.app.ActionBar.LayoutParams.MATCH_PARENT,
                true
            )
            popupWebview!!.contentView = layout
            popupWebview!!.showAtLocation(layout, Gravity.CENTER, 0, 0)
            val webview = layout.findViewById<WebView>(R.id.webview)
            val lblMenuHeaderName = layout.findViewById<TextView>(R.id.lblMenuHeaderName)
            val imgBack = layout.findViewById<ImageView>(R.id.imgBack)
            val btnTerms = layout.findViewById<Button>(R.id.btnTermsAndCondition)
            val LayoutHeader = layout.findViewById<ConstraintLayout>(R.id.LayoutHeader)
            val viewLine = layout.findViewById<View>(R.id.viewline)
            btnTerms.visibility = View.GONE
            LayoutHeader.visibility = View.VISIBLE
            viewLine.visibility = View.VISIBLE
            lblMenuHeaderName.setText(R.string.txt_ad)

            imgBack.setOnClickListener { popupWebview!!.dismiss() }
            val progressDialog = CustomLoading.createProgressDialog(activity)

            webview.webViewClient = MyWebViewClientContext(activity)
            webview.scrollBarStyle = View.SCROLLBARS_INSIDE_OVERLAY
            val webSettings = webview.settings

            webSettings.domStorageEnabled = true
            webSettings.loadsImagesAutomatically = true
            webSettings.builtInZoomControls = true
            webSettings.javaScriptEnabled = true
            webview.loadUrl(url!!)
            progressDialog.dismiss()
        }

        fun deleteDir(dir: File?): Boolean {
            return if (dir != null && dir.isDirectory) {
                val children = dir.list()
                for (i in children.indices) {
                    val success = deleteDir(File(dir, children[i]))
                    if (!success) {
                        return false
                    }
                }
                dir.delete()
            } else if (dir != null && dir.isFile) {
                dir.delete()
            } else {
                false
            }
        }

        fun ChangePasswordRequest(activity: Activity?) {
            val mobilenumber = SharedPreference.getSH_MobileNumber(activity!!)
            val jsonObject = JsonObject()
            jsonObject.addProperty(ApiRequestNames.Req_mobileNumber, mobilenumber)
            jsonObject.addProperty(ApiRequestNames.Req_oldpassword, OldPassword)
            jsonObject.addProperty(ApiRequestNames.Req_newpassword, NewPassword)
            appviewModelbase!!.getChangePassword(jsonObject, activity)
            Log.d("ChangePasswordRequest", jsonObject.toString())
        }

        fun UserMenuRequest(activity: Activity?) {

            if (CommonUtil.MenuListDashboard.isEmpty()) {
                val jsonObject = JsonObject()
                jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)
                jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
                jsonObject.addProperty(ApiRequestNames.Req_user_id, CommonUtil.MemberId)
                dashboardViewModel!!.getUsermenus(jsonObject, activity)
                Log.d("UserMenus_Request", jsonObject.toString())

            }
        }

        fun AppReadStatus(activity: Activity?, msgtype: String, detailsId: String) {
            val jsonObject = JsonObject()
            jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
            jsonObject.addProperty(ApiRequestNames.Req_msgtype, msgtype)
            jsonObject.addProperty(ApiRequestNames.Req_detailsid, detailsId)
            jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
            appviewModelbase!!.getAppreadStatus(jsonObject, activity)
            Log.d("AppReadStatus", jsonObject.toString())
        }

        fun AppReadStatusContext(activity: Context?, msgtype: String, detailsId: String) {
            val jsonObject = JsonObject()
            jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
            jsonObject.addProperty(ApiRequestNames.Req_msgtype, msgtype)
            jsonObject.addProperty(ApiRequestNames.Req_detailsid, detailsId)
            jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
            appviewModelbase!!.getAppreadStatusContext(jsonObject, activity)
            Log.d("AppReadStatuscontext", jsonObject.toString())
        }

        fun OverAllMenuCountRequest(activity: Activity?, menuid: String) {

            val jsonObject = JsonObject()
            jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)

            if (menuid == "8") {
                jsonObject.addProperty(ApiRequestNames.Req_menuid, "9")

            } else {
                jsonObject.addProperty(ApiRequestNames.Req_menuid, menuid)

            }

            jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
            if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority == "p1" || CommonUtil.Priority == "p2" || CommonUtil.Priority == "p3" || CommonUtil.Priority.equals(
                    "p6"
                )
            ) {
                jsonObject.addProperty(ApiRequestNames.Req_departmentid, 0)
                jsonObject.addProperty(ApiRequestNames.Req_sectionid, 0)
            } else {
                jsonObject.addProperty(ApiRequestNames.Req_departmentid, CommonUtil.DepartmentId)
                jsonObject.addProperty(ApiRequestNames.Req_sectionid, CommonUtil.SectionId)
            }
            jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.Appid)
            jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
            appviewModelbase!!.getOverAllMenuCount(jsonObject, activity)
            Log.d("OverAllMenuCount_Req:", jsonObject.toString())
        }
    }

    fun TabCollegeColor() {
        if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority == "p1") {

            LayoutCollege!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.clr_selected)))
            LayoutDepartment!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.clr_unselected)))
            imgAddPlus!!.visibility = View.VISIBLE

        } else if ((CommonUtil.Priority == "p2") || CommonUtil.Priority.equals("p3")) {

            LayoutCollege!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.clr_selected)))
            LayoutDepartment!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.clr_unselected)))
            imgAddPlus!!.visibility = View.VISIBLE

        } else if ((CommonUtil.Priority == "p4")) {

            LayoutCollege!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.clr_selected)))
            LayoutDepartment!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.clr_unselected)))
            imgAddPlus!!.visibility = View.GONE

        } else if ((CommonUtil.Priority == "p5")) {

            LayoutCollege!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.clr_selected)))
            LayoutDepartment!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.clr_unselected)))
            imgAddPlus!!.visibility = View.GONE

        } else if (CommonUtil.Priority == "p6") {

            LayoutCollege!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.clr_selected)))
            LayoutDepartment!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.clr_unselected)))
        }
    }

    fun TabDepartmentColor() {
        if (CommonUtil.Priority.equals("p7") || CommonUtil.Priority == "p1") {

            LayoutDepartment!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.clr_selected)))
            LayoutCollege!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.clr_unselected)))
            if (CommonUtil.menu_writeExamination.equals("1") || CommonUtil.menu_writeAssignment.equals(
                    "1"
                ) || CommonUtil.menu_writeCircular.equals("1") || CommonUtil.menu_writeNoticeBoard.equals(
                    "1"
                ) || CommonUtil.menu_writeEvent.equals("1") || CommonUtil.menu_writeVideo.equals("1")
            ) {
                imgAddPlus!!.visibility = View.VISIBLE
            }

        } else if ((CommonUtil.Priority == "p2") || CommonUtil.Priority.equals("p3")) {

            LayoutDepartment!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.clr_selected)))
            LayoutCollege!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.clr_unselected)))
            if (CommonUtil.menu_writeExamination.equals("1") || CommonUtil.menu_writeAssignment.equals(
                    "1"
                ) || CommonUtil.menu_writeCircular.equals("1") || CommonUtil.menu_writeNoticeBoard.equals(
                    "1"
                ) || CommonUtil.menu_writeEvent.equals("1") || CommonUtil.menu_writeVideo.equals("1")
            ) {
                imgAddPlus!!.visibility = View.VISIBLE
            }

        } else if ((CommonUtil.Priority == "p4")) {

            LayoutDepartment!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.clr_selected)))
            LayoutCollege!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.clr_unselected)))
            imgAddPlus!!.visibility = View.GONE

        } else if ((CommonUtil.Priority.equals("p5"))) {

            LayoutDepartment!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.clr_selected)))
            LayoutCollege!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.clr_unselected)))
            imgAddPlus!!.visibility = View.GONE

        } else if (CommonUtil.Priority == "p6") {

            if (CommonUtil.menu_writeExamination.equals("1")) {
                imgAddPlus!!.visibility = View.VISIBLE
            }
            LayoutDepartment!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.clr_selected)))
            LayoutCollege!!.setBackgroundColor(Color.parseColor(resources.getString(R.string.clr_unselected)))

        }
    }

}
