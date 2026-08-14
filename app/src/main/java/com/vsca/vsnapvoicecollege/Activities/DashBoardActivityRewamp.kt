package com.vsca.vsnapvoicecollege.Activities

import android.Manifest
import android.app.AlertDialog
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.provider.Settings
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.lifecycleScope
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Adapters.DashboardParent
import com.vsca.vsnapvoicecollege.Model.DashboardDetailsDataResponse
import com.vsca.vsnapvoicecollege.Model.DashboardOverall
import com.vsca.vsnapvoicecollege.Model.DashboardSubItems
import com.vsca.vsnapvoicecollege.Model.DashboardTypeResponse
import com.vsca.vsnapvoicecollege.Model.MenuDetailsResponse
import com.vsca.vsnapvoicecollege.Model.Number
import com.vsca.vsnapvoicecollege.Model.StatusMessageResponse
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.DeviceType
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.databinding.BottomMenuSwipeBinding
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.io.ByteArrayOutputStream

class DashBoardActivityRewamp : BaseActivity<BottomMenuSwipeBinding>() {

    var exist_Count = 0
    var Contact_Count = 0
    var contact_alert_title = ""
    var contact_alert_Content: String? = ""
    var contact_display_name = ""
    var contact_numbers: String? = ""
    var contact_button: String? = ""
    var Display_Name = ""
    lateinit var contacts: Array<String>
    var adapter: DashboardParent? = null
    var DashboardData: List<DashboardTypeResponse> = ArrayList()
    var DashboardDetails: List<DashboardDetailsDataResponse> = ArrayList()
    private val dashboardOverallList = ArrayList<DashboardOverall>()
    private val dashboardNoticeboardlist = ArrayList<DashboardSubItems>()
    private val adimageList1 = ArrayList<DashboardSubItems>()
    private val adimageList2 = ArrayList<DashboardSubItems>()
    private val adimageList4 = ArrayList<DashboardSubItems>()
    private var ContectNumber = ArrayList<Number>()
    private val dashboardCircularlist = ArrayList<DashboardSubItems>()
    private var dashboardMenuItems = ArrayList<MenuDetailsResponse>()
    private val dashboardEventlist = ArrayList<DashboardSubItems>()
    private val dashboardChatlist = ArrayList<DashboardSubItems>()
    private val dashboardLeaveRequestlist = ArrayList<DashboardSubItems>()
    private val dashboardAttendancetlist = ArrayList<DashboardSubItems>()

    private val dashboardEmergencyVoicelist = ArrayList<DashboardSubItems>()
    private val dashboardRecentVoicelist = ArrayList<DashboardSubItems>()
    private val dashboardAssignmentList = ArrayList<DashboardSubItems>()
    var dashboardAttendanceList: ArrayList<DashboardSubItems>? = null
    var category: String? = null
    var deviceToken: String? = null
    var order = 0
    var Success: String? = null
    var isPermission = true

    private var isPermissionDialogShowing = false
    private var permissionsJustGranted = false        // <-- ADD THIS

    private var isPermissionRequestInProgress = false

    // In-memory tracking of which permissions have been requested in this activity instance
    private val requestedPermissions = mutableSetOf<String>()

    companion object {
        private const val KEY_REQUESTED_PERMS = "requested_permissions"
        private const val TAG = "DashBoardPerm"
    }

    private val permissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestMultiplePermissions()) { permissions ->

            isPermissionRequestInProgress = false
            val deniedPermissions = permissions.filter { !it.value }.keys

            Log.d(TAG, "Launcher result. Denied: $deniedPermissions")

            if (deniedPermissions.isEmpty()) {
                permissionsJustGranted = true

                CommonUtil.MenuListDashboard.clear()
//                dashboardOverallList.clear()
                UserMenuRequest(this) {
                    DashBoardRequest()
                }
            } else {
                // Check individually: which denied permissions are permanently denied
                val permanentlyDenied = deniedPermissions.filter { permission ->
                    !ActivityCompat.shouldShowRequestPermissionRationale(this, permission) &&
                            requestedPermissions.contains(permission)
                }

                Log.d(TAG, "Permanently denied: $permanentlyDenied")

                if (permanentlyDenied.isNotEmpty()) {
                    showPermissionSettingsDialog()
                }
            }
        }

    override fun inflateBinding(): BottomMenuSwipeBinding {
        return BottomMenuSwipeBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Restore requested permissions after rotation
        savedInstanceState?.getStringArrayList(KEY_REQUESTED_PERMS)?.let {
            requestedPermissions.addAll(it)
        }

        CommonUtil.SetTheme(this)
        binding = BottomMenuSwipeBinding.inflate(layoutInflater)
        setContentView(binding.root)
        ActionBarMethod(this@DashBoardActivityRewamp, true)

        val insetsController = WindowInsetsControllerCompat(window, window.decorView)
        insetsController.isAppearanceLightStatusBars = true

        CommonUtil.OnMenuClicks("Home")

        FirebaseMessaging.getInstance().isAutoInitEnabled = true
        accessBottomViewIcons(
            binding,
            R.id.LayoutDepartment,
            R.id.LayoutCollege,
            R.id.imgAddPlus
        )

        FirebaseMessaging.getInstance().token.addOnCompleteListener(OnCompleteListener { task ->
            if (!task.isSuccessful) {
                Log.d("Fetching FCM ", task.exception.toString())
                return@OnCompleteListener
            }

            deviceToken = task.result
            SharedPreference.putDeviceToken(this, deviceToken!!)
            Log.d("FCM_DeviceToken", deviceToken.toString())
            DeviceTokenRequest()
        })

        appviewModelbase!!.UpdateDeviceTokenMutableLiveData?.observe(
            this,
            Observer<StatusMessageResponse?> { response ->
                if (response != null) {
                    val status = response.status
                    val message = response.message
                    if (status == 1) {

                    }
                }
            })

        appViewModel!!.ContectNumber!!.observe(this) { response ->

            if (response != null) {
                val status = response.Status
                val message = response.Message
                if (status == "1") {
                    ContectNumber = response.data
                    for (i in ContectNumber.indices) {
                        contact_alert_title = ContectNumber[i].contact_alert_title
                        contact_alert_Content = ContectNumber[i].contact_alert_content
                        contact_display_name = ContectNumber[i].contact_display_name
                        contact_numbers = ContectNumber[i].contact_numbers
                        contact_button = ContectNumber[i].contact_button_content
                    }
                    if (contact_numbers != "") {
                        contacts =
                            contact_numbers!!.split(",".toRegex()).dropLastWhile { it.isEmpty() }
                                .toTypedArray()
                        getContactPermission()
                    }
                } else {
                    CommonUtil.ApiAlert(this, message)
                }
            } else {
                CommonUtil.ApiAlert(this, CommonUtil.Something_went_wrong)
            }
        }

        dashboardViewModel!!.dashboardLivedata?.observe(this) { response ->

            if (response != null) {
                val status = response.status
                val message = response.message
                dashboardOverallList.clear()

                dashboardCircularlist.clear()
                dashboardMenuItems.clear()
                dashboardEventlist.clear()
                dashboardRecentVoicelist.clear()
                dashboardChatlist.clear()
                dashboardNoticeboardlist.clear()
                dashboardAttendancetlist.clear()
                dashboardLeaveRequestlist.clear()
                dashboardAttendanceList!!.clear()
                dashboardAssignmentList.clear()
                dashboardEmergencyVoicelist.clear()
                if (status == 1) {
                    Success = "Success"
                    val dashboardList = response.data?.toMutableList() ?: mutableListOf()

                    val menuSection = DashboardTypeResponse().apply {
                        Log.d("Coming","Coming")
                        type = "DashBoard_Menu"
                        order = 1000
                        DashboardMenuData = CommonUtil.MenuListDashboard
                    }

                    if (dashboardList.size >= 1) {
                        dashboardList.add(1, menuSection)
                    } else {
                        dashboardList.add(menuSection)
                    }

                    DashboardData = dashboardList

                    lifecycleScope.launch(Dispatchers.Default) {
                        processDashboardData()
                        withContext(Dispatchers.Main) {
                            bindAdapter()
                            getContectNumberSave()
                        }
                    }
                } else {
                    CommonUtil.ApiAlert(this@DashBoardActivityRewamp, message)
                }
            }
        }

        dashboardAttendanceList = ArrayList()

        imgRefresh!!.setOnClickListener(View.OnClickListener {

            dashboardCircularlist.clear()
            dashboardMenuItems.clear()
            dashboardEventlist.clear()
            dashboardRecentVoicelist.clear()
            dashboardChatlist.clear()
            dashboardNoticeboardlist.clear()
            dashboardAttendancetlist.clear()
            dashboardLeaveRequestlist.clear()
            dashboardAttendanceList!!.clear()
            dashboardAssignmentList.clear()
            dashboardEmergencyVoicelist.clear()
            if (Success.equals("Success")) {
                CommonUtil.MenuListDashboard.clear()
//                dashboardOverallList.clear()
                UserMenuRequest(this)
                DashBoardRequest()
                Success = ""
            }
        })
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putStringArrayList(KEY_REQUESTED_PERMS, ArrayList(requestedPermissions))
    }

    private fun processDashboardData() {
        dashboardOverallList.clear()

        for (dashboardItem in DashboardData) {
            category = dashboardItem.type
            order = dashboardItem.order
            Log.d("Categories", category!!)

            when (category) {
                "Ad" -> when (order) {
                    1 -> {
                        adimageList1.clear()
                        DashboardDetails = dashboardItem.data!!
                        for (item in DashboardDetails) {
                            val addimage = item.add_image
                            val adBackgroundImage = item.background_image
                            CommonUtil.CommonAdvertisement = item.background_image!!
                            CommonUtil.CommonAdImageSmall = item.add_image.toString()
                            val Adurl = item.add_url
                            val Id = 1
                            adimageList1.add(DashboardSubItems(addimage, adBackgroundImage, Adurl, Id))
                        }
                        dashboardOverallList.add(DashboardOverall(category!!, adimageList1))
                    }
                    2 -> {
                        adimageList2.clear()
                        DashboardDetails = dashboardItem.data!!
                        for (item in DashboardDetails) {
                            val addimage = item.background_image
                            val adBackgroundImage = item.background_image
                            val Id = 1
                            val Adurl = item.add_url
                            adimageList2.add(DashboardSubItems(addimage, adBackgroundImage, Adurl, Id))
                        }
                        dashboardOverallList.add(DashboardOverall(category!!, adimageList2))
                    }
                    4 -> {
                        adimageList4.clear()
                        DashboardDetails = dashboardItem.data!!
                        for (item in DashboardDetails) {
                            val addimage = item.background_image
                            val adBackgroundImage = item.background_image
                            val Adurl = item.add_url
                            val Id = 1
                            adimageList4.add(DashboardSubItems(addimage, adBackgroundImage, Adurl, Id))
                        }
                        dashboardOverallList.add(DashboardOverall(category!!, adimageList4))
                    }
                }
                "DashBoard_Menu" -> {
                    dashboardMenuItems = ArrayList(dashboardItem.DashboardMenuData ?: emptyList())
                    Log.d("DataComing121123113", dashboardMenuItems.toString())
                    Log.d("DataComing121123113", dashboardMenuItems.size.toString())
                    dashboardOverallList.add(
                        DashboardOverall(
                            category!!,
                            DashboardMenuData = dashboardMenuItems,
                            menusubitemlist = null
                        )
                    )
                }
                "Circular" -> {
                    DashboardDetails = dashboardItem.data!!
                    for (item in DashboardDetails) {
                        val description = item.description
                        val title = item.title
                        val createddate = item.createddate
                        val createdtime = item.createdtime
                        val filepaths = item.filepaths
                        dashboardCircularlist.add(
                            DashboardSubItems(
                                title,
                                description,
                                createddate,
                                createdtime,
                                filepaths as ArrayList<String>
                            )
                        )
                    }
                    dashboardOverallList.add(DashboardOverall(category!!, dashboardCircularlist))
                }
                "Upcoming Events" -> {
                    DashboardDetails = dashboardItem.data!!
                    for (item in DashboardDetails) {
                        val EventTitle = item.eventtopic
                        val Eventtime = item.eventtime
                        val Eventdate = item.eventdate
                        val ideventid = item.ideventdetails
                        dashboardEventlist.add(
                            DashboardSubItems(
                                EventTitle, Eventtime, Eventdate, ideventid
                            )
                        )
                    }
                    dashboardOverallList.add(DashboardOverall(category!!, dashboardEventlist))
                }
                "Chat" -> {
                    DashboardDetails = dashboardItem.data!!
                    for (item in DashboardDetails) {
                        val coursename = item.coursename
                        val departmentname = item.departmentname
                        val yearname = item.yearname
                        val sectionname = item.sectionname
                        val studentname = item.studentname
                        val question = item.question
                        val createdonchat = item.createdon
                        val dummy = item.yearname
                        val message = item.message
                        dashboardChatlist.add(
                            DashboardSubItems(
                                coursename,
                                departmentname,
                                yearname,
                                sectionname,
                                studentname,
                                question,
                                createdonchat,
                                dummy,
                                message
                            )
                        )
                    }
                    dashboardOverallList.add(DashboardOverall(category!!, dashboardChatlist))
                }
                "Leave Request" -> {
                    DashboardDetails = dashboardItem.data!!
                    for (item in DashboardDetails) {
                        val leaveapplicationid = item.leaveapplicationid
                        val studentid = item.studentid
                        val membername = item.membername
                        val coursename = item.coursename
                        val departmentname = item.departmentname
                        val yearname = item.yearname
                        val sectionname = item.sectionname
                        val reason = item.reason
                        val fromdate = item.fromdate
                        val todate = item.todate
                        val leavestatus = item.leavestatus
                        val noofdays = item.noofdays
                        val appliedon = item.appliedon
                        val message = item.message
                        dashboardLeaveRequestlist.add(
                            DashboardSubItems(
                                leaveapplicationid,
                                studentid,
                                membername,
                                coursename,
                                departmentname,
                                yearname,
                                sectionname,
                                reason,
                                fromdate,
                                todate,
                                leavestatus,
                                noofdays,
                                appliedon,
                                message
                            )
                        )
                    }
                    dashboardOverallList.add(DashboardOverall(category!!, dashboardLeaveRequestlist))
                }
                "Assignments" -> {
                    DashboardDetails = dashboardItem.data!!
                    for (item in DashboardDetails) {
                        val idassignmentdetails = item.idassignmentdetails
                        val assignmenttopic = item.assignmenttopic
                        val assignmentdescription = item.assignmentdescription
                        val submissiondate = item.submissiondate
                        val filepath = item.submissiondate
                        val filepathassignment = item.filepaths
                        val filetype = item.file_type
                        dashboardAssignmentList.add(
                            DashboardSubItems(
                                idassignmentdetails,
                                assignmenttopic,
                                assignmentdescription,
                                submissiondate,
                                filepath,
                                filetype,
                                filepathassignment as ArrayList<String>
                            )
                        )
                    }
                    dashboardOverallList.add(DashboardOverall(category!!, dashboardAssignmentList))
                }
                "Notice Board" -> {
                    DashboardDetails = dashboardItem.data!!
                    for (item in DashboardDetails) {
                        val description = item.topicbody
                        val title = item.topicheading
                        val createddate = item.createddate
                        val createdtime = item.createdtime
                        dashboardNoticeboardlist.add(
                            DashboardSubItems(
                                title, "", description, createddate, createdtime, "", category
                            )
                        )
                    }
                    dashboardOverallList.add(DashboardOverall(category!!, dashboardNoticeboardlist))
                }
                "Emergency Notification" -> {
                    DashboardDetails = dashboardItem.data!!
                    for (item in DashboardDetails) {
                        val description = item.description
                        val VoiceFilepath = item.voicefilepath
                        val membername = item.membername
                        val duration = item.duration
                        val createdon = item.createdon
                        val detailsid = item.detailsid
                        dashboardEmergencyVoicelist.add(
                            DashboardSubItems(
                                description,
                                VoiceFilepath,
                                membername,
                                duration,
                                createdon,
                                category,
                                detailsid
                            )
                        )
                    }
                    dashboardOverallList.add(DashboardOverall(category!!, dashboardEmergencyVoicelist))
                }
                "Recent Notifications" -> {
                    DashboardDetails = dashboardItem.data!!
                    for (item in DashboardDetails) {
                        val description = item.description
                        val content = item.content
                        val membername = item.sentbyname
                        val duration = item.duration
                        val createdon = item.createdondate
                        val recentType = item.typ
                        val Createdontime = item.createdontime
                        val detailsID = item.id
                        dashboardRecentVoicelist.add(
                            DashboardSubItems(
                                description,
                                membername,
                                recentType,
                                createdon,
                                Createdontime,
                                content,
                                duration,
                                category,
                                detailsID
                            )
                        )
                    }
                    dashboardOverallList.add(DashboardOverall(category!!, dashboardRecentVoicelist))
                }
                "Attendance" -> {
                    DashboardDetails = dashboardItem.data!!
                    for (item in DashboardDetails) {
                        val attendancetype = item.attendancetype
                        val subjectName = item.subjectname
                        val attendancedate = item.attendancedate
                        val message = item.message
                        val Subjectname = item.subjectname
                        dashboardAttendancetlist.add(
                            DashboardSubItems(
                                subjectName, attendancetype, attendancedate, message, Subjectname
                            )
                        )
                    }
                    dashboardOverallList.add(DashboardOverall(category!!, dashboardAttendancetlist))
                }
            }
        }
    }

    private fun bindAdapter() {
        adapter = DashboardParent(dashboardOverallList, this@DashBoardActivityRewamp)
        val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@DashBoardActivityRewamp)
        binding.idRVCategories!!.layoutManager = mLayoutManager
        binding.idRVCategories.apply {
            setHasFixedSize(true)
            itemAnimator = null
        }
        binding.idRVCategories!!.adapter = adapter
        adapter!!.notifyDataSetChanged()
    }

    private fun getContectNumberSave() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.Appid)
        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        Log.d("getContectNumberSave", jsonObject.toString())
        appViewModel!!.NumberDetails(jsonObject, this)
    }

    private fun DeviceTokenRequest() {

        val mobilenumber = SharedPreference.getSH_MobileNumber(this)

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_mobilenoDivise, mobilenumber)
        jsonObject.addProperty(ApiRequestNames.Req_devicetoken, deviceToken)
        jsonObject.addProperty(ApiRequestNames.Req_devicetype, DeviceType)
        Log.d("deviceToken", jsonObject.toString())
        appviewModelbase!!.UpdateDeviceToken(jsonObject, this)
    }

    override val layoutResourceId: Int
        protected get() = R.layout.bottom_menu_swipe

    private fun getContactPermission() {
        if (ContextCompat.checkSelfPermission(
                this,
                Manifest.permission.READ_CONTACTS
            ) == PackageManager.PERMISSION_GRANTED
        ) {
            checkIfContactsExist()
        } else {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                if (shouldShowRequestPermissionRationale(Manifest.permission.READ_CONTACTS)) {

                }
                requestPermissions(
                    arrayOf<String>(Manifest.permission.READ_CONTACTS), 100
                )
            }
        }
    }

    private fun DashBoardRequest() {
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId?.toString() ?: "")
        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId?.toString() ?: "")
        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        dashboardViewModel!!.dashboard(jsonObject, this@DashBoardActivityRewamp)
        Log.d("DahsboardRequest:", jsonObject.toString())
    }

    override fun onRequestPermissionsResult(
        requestCode: Int,
        permissions: Array<out String>,
        grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)

        if (requestCode == 100 || requestCode == 101) {
            val permanentlyDenied = permissions.indices
                .filter { i -> grantResults[i] == PackageManager.PERMISSION_DENIED }
                .map { permissions[it] }
                .filter { permission ->
                    !ActivityCompat.shouldShowRequestPermissionRationale(this, permission)
                }

            if (permanentlyDenied.isNotEmpty()) {
                showPermissionSettingsDialog()
            }
        }
    }

    private fun showPermissionSettingsDialog() {
        if (isPermissionDialogShowing) return
        isPermissionDialogShowing = true

        val dialog = AlertDialog.Builder(this)
            .setTitle("Permission Required")
            .setMessage("Some permissions were permanently denied. Please enable them in app settings.")
            .setCancelable(false)
            .setPositiveButton("Go to Settings") { d, _ ->
                d.dismiss()
                isPermissionDialogShowing = false

                val intent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                intent.data = Uri.parse("package:$packageName")
                startActivity(intent)
            }
            .create()

        dialog.setCanceledOnTouchOutside(false)
        dialog.show()
    }

    private fun hasAllPermissions(): Boolean {
        return getRequiredPermissions().all {
            ContextCompat.checkSelfPermission(this, it) == PackageManager.PERMISSION_GRANTED
        }
    }

    override fun onResume() {
        super.onResume()
        Log.d(TAG, "onResume. Priority=${CommonUtil.Priority} inProgress=$isPermissionRequestInProgress")

        // If permissions were just granted by the launcher, skip the onResume API calls
        // because the launcher already triggered them. This prevents double binding.
        if (permissionsJustGranted) {                       // <-- ADD THIS BLOCK
            Log.d(TAG, "Permissions just granted, skipping onResume API calls.")
            permissionsJustGranted = false
            return
        }

        if (hasAllPermissions()) {
            Log.d(TAG, "All permissions granted.")
            CommonUtil.MenuListDashboard.clear()
            UserMenuRequest(this)
            DashBoardRequest()
            return
        }

        if (isPermissionDialogShowing) {
            Log.d(TAG, "Dialog already showing. Skipping.")
            return
        }

        if (isPermissionRequestInProgress) {
            Log.d(TAG, "Permission request already in progress. Skipping.")
            return
        }

        val requiredPerms = getRequiredPermissions()
        Log.d(TAG, "Required permissions: ${requiredPerms.toList()}")

        val deniedPerms = requiredPerms.filter {
            ContextCompat.checkSelfPermission(this, it) != PackageManager.PERMISSION_GRANTED
        }
        Log.d(TAG, "Denied permissions: $deniedPerms")

        val askablePerms = deniedPerms.filter {
            val showRationale = ActivityCompat.shouldShowRequestPermissionRationale(this, it)
            val neverRequested = !requestedPermissions.contains(it)
            Log.d(TAG, "Permission=$it rationale=$showRationale neverRequested=$neverRequested")
            showRationale || neverRequested
        }

        Log.d(TAG, "Askable permissions: $askablePerms")

        if (askablePerms.isNotEmpty()) {
            requestedPermissions.addAll(askablePerms)
            isPermissionRequestInProgress = true
            permissionLauncher.launch(askablePerms.toTypedArray())
        } else if (deniedPerms.isNotEmpty()) {
            Log.d(TAG, "All denied permissions are permanently denied. Showing settings dialog.")
            showPermissionSettingsDialog()
        }

    }

    private fun getRequiredPermissions(): Array<String> {
        val perms = if (CommonUtil.Priority == "p4" || CommonUtil.Priority == "p5") {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.POST_NOTIFICATIONS,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )
            } else {
                arrayOf(
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.CAMERA,
                    Manifest.permission.RECORD_AUDIO
                )
            }
        } else {
            // P1, P2, P3, P6
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.READ_MEDIA_AUDIO,
                    Manifest.permission.RECORD_AUDIO,
                    Manifest.permission.POST_NOTIFICATIONS
                )
            } else {
                arrayOf(
                    Manifest.permission.ACCESS_FINE_LOCATION,
                    Manifest.permission.ACCESS_COARSE_LOCATION,
                    Manifest.permission.CAMERA,
                    Manifest.permission.READ_CONTACTS,
                    Manifest.permission.WRITE_CONTACTS,
                    Manifest.permission.WRITE_EXTERNAL_STORAGE,
                    Manifest.permission.READ_EXTERNAL_STORAGE,
                    Manifest.permission.RECORD_AUDIO
                )
            }
        }

        Log.d("isPermission+++++", "Priority=${CommonUtil.Priority} SDK=${Build.VERSION.SDK_INT} Perms=${perms.toList()}")
        return perms
    }

    private fun saveContacts() {
        try {
            if (ActivityCompat.checkSelfPermission(
                    this,
                    Manifest.permission.WRITE_CONTACTS
                ) != PackageManager.PERMISSION_GRANTED
            ) {
                ActivityCompat.requestPermissions(
                    this,
                    arrayOf(Manifest.permission.WRITE_CONTACTS),
                    101
                )
                return
            }

            val bitmap = BitmapFactory.decodeResource(resources, R.drawable.gradit_logo)
            val byteArrayOutputStream = ByteArrayOutputStream()
            bitmap.compress(Bitmap.CompressFormat.PNG, 100, byteArrayOutputStream)
            val photoByteArray = byteArrayOutputStream.toByteArray()

            val data = ArrayList<ContentValues>()

            val photoRow = ContentValues().apply {
                put(
                    ContactsContract.Data.MIMETYPE,
                    ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
                )
                put(ContactsContract.CommonDataKinds.Photo.PHOTO, photoByteArray)
            }
            data.add(photoRow)

            for (contact in contacts) {
                val phoneRow = ContentValues().apply {
                    put(
                        ContactsContract.Data.MIMETYPE,
                        ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
                    )
                    put(ContactsContract.CommonDataKinds.Phone.NUMBER, contact)
                    put(
                        ContactsContract.CommonDataKinds.Phone.TYPE,
                        ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
                    )
                }
                data.add(phoneRow)
            }

            val intent = Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI).apply {
                putExtra(ContactsContract.Intents.Insert.NAME, contact_display_name)
                putParcelableArrayListExtra(
                    ContactsContract.Intents.Insert.DATA,
                    data
                )
            }
            startActivity(intent)
        } catch (e: Exception) {
            Log.e("SaveContactsError", "Error saving contacts: ${e.message}")
        }
    }

    private fun contactSaveContent() {
        val inflater = getSystemService(LAYOUT_INFLATER_SERVICE) as LayoutInflater
        val popupView = inflater.inflate(R.layout.save_contact_alert, null)

        val popupWindow = PopupWindow(
            popupView,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            ViewGroup.LayoutParams.WRAP_CONTENT,
            true
        )

        popupWindow.showAtLocation(binding.OverallLayout, Gravity.CENTER, 0, 0)

        val container = popupWindow.contentView.parent as View
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.5f
        wm.updateViewLayout(container, p)

        val btnSaveContact = popupView.findViewById<TextView>(R.id.btnSaveContact)
        val imgClose = popupView.findViewById<ImageView>(R.id.imgClose)
        val lblHeader = popupView.findViewById<TextView>(R.id.lblHeader)
        val lblContent = popupView.findViewById<TextView>(R.id.lblContent)

        lblHeader.text = contact_alert_title
        lblContent.text = contact_alert_Content
        btnSaveContact.text = contact_button

        btnSaveContact.setOnClickListener {
            popupWindow.dismiss()
            saveContacts()
        }

        imgClose.setOnClickListener {
            popupWindow.dismiss()
        }
    }

    private fun checkIfContactsExist() {

        if (contacts.isEmpty()) return

        var shouldShowPopup = false

        for (number in contacts) {
            if (number.isNotBlank() && !isContactNumberExists(number)) {
                shouldShowPopup = true
                break
            }
        }

        if (shouldShowPopup) {
            contactSaveContent()
        }
    }

    private fun isContactNumberExists(phoneNumber: String): Boolean {

        val lookupUri = Uri.withAppendedPath(
            ContactsContract.PhoneLookup.CONTENT_FILTER_URI,
            Uri.encode(phoneNumber)
        )

        val projection = arrayOf(ContactsContract.PhoneLookup._ID)

        contentResolver.query(lookupUri, projection, null, null, null)?.use { cursor ->
            return cursor.moveToFirst()
        }

        return false
    }
}