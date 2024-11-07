package com.vsca.vsnapvoicecollege.Activities

import android.Manifest
import android.annotation.SuppressLint
import android.content.ContentResolver
import android.content.ContentValues
import android.content.Intent
import android.content.pm.PackageManager
import android.database.Cursor
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.ContactsContract
import android.util.Log
import android.view.Gravity
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.WindowManager
import android.widget.ImageView
import android.widget.PopupWindow
import android.widget.TextView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.coordinatorlayout.widget.CoordinatorLayout
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import butterknife.BindView
import butterknife.ButterKnife
import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.gson.JsonObject
import com.vsca.vsnapvoicecollege.Adapters.DashboardParent
import com.vsca.vsnapvoicecollege.Model.DashboardDetailsDataResponse
import com.vsca.vsnapvoicecollege.Model.DashboardOverall
import com.vsca.vsnapvoicecollege.Model.DashboardSubItems
import com.vsca.vsnapvoicecollege.Model.DashboardTypeResponse
import com.vsca.vsnapvoicecollege.Model.Number
import com.vsca.vsnapvoicecollege.Model.StatusMessageResponse
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.DeviceType
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import java.io.ByteArrayOutputStream


class DashBoard : BaseActivity() {

    @JvmField
    @BindView(R.id.lblSwipedown)
    var lblSwipedown: TextView? = null

    @JvmField
    @BindView(R.id.OverallLayout)
    var rytParent: ConstraintLayout? = null

    @JvmField
    @BindView(R.id.idRVCategories)
    var idRVCategories: RecyclerView? = null

    @JvmField
    @BindView(R.id.LayoutBottomMenus)
    var LayoutBottomMenus: CoordinatorLayout? = null

    var exist_Count = 0
    var Contact_Count = 0
    var contact_alert_title = ""
    var contact_alert_Content: kotlin.String? = ""
    var contact_display_name = ""
    var contact_numbers: kotlin.String? = ""
    var contact_button: kotlin.String? = ""
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

    @SuppressLint("SuspiciousIndentation")
    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        ButterKnife.bind(this)
        CommonUtil.RequestPermission(this)
        ActionBarMethod(this@DashBoard)
        CommonUtil.OnMenuClicks("Home")

        FirebaseMessaging.getInstance().isAutoInitEnabled = true

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
                    UserMenuRequest(this@DashBoard)

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
                    DashboardData = response.data!!
                    SetDashboardValues()
                    getContectNumberSave()
                } else {
                    CommonUtil.ApiAlert(this@DashBoard, message)
                }
            }
        }

        dashboardAttendanceList = ArrayList()

        imgRefresh!!.setOnClickListener(View.OnClickListener {

            dashboardCircularlist.clear()
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
                DashBoardRequest()
                Success = ""
            }
        })
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

    private fun SetDashboardValues() {
        dashboardOverallList.clear()
        for (i in DashboardData.indices) {
            category = DashboardData[i].type
            order = DashboardData[i].order
            Log.d("Categories", category!!)
            if (category == "Ad" && order == 1) {
                adimageList1.clear()
                DashboardDetails = DashboardData[i].data!!
                for (j in DashboardDetails.indices) {
                    val addimage = DashboardDetails[j].add_image
                    val adBackgroundImage = DashboardDetails[j].background_image
                    CommonUtil.CommonAdvertisement = DashboardDetails[j].background_image!!
                    CommonUtil.CommonAdImageSmall = DashboardDetails[j].add_image.toString()
                    val Adurl = DashboardDetails[j].add_url
                    val Id = 1
                    adimageList1.add(DashboardSubItems(addimage, adBackgroundImage, Adurl, Id))
                }
            }

            if (category == "Ad" && order == 2) {
                adimageList2.clear()
                DashboardDetails = DashboardData[i].data!!
                for (j in DashboardDetails.indices) {
                    val addimage = DashboardDetails[j].background_image
                    val adBackgroundImage = DashboardDetails[j].background_image
                    val Id = 1
                    val Adurl = DashboardDetails[j].add_url

                    adimageList2.add(DashboardSubItems(addimage, adBackgroundImage, Adurl, Id))
                }
            }

            if (category == "Ad" && order == 4) {
                adimageList4.clear()
                DashboardDetails = DashboardData[i].data!!
                for (j in DashboardDetails.indices) {
                    val addimage = DashboardDetails[j].background_image
                    val adBackgroundImage = DashboardDetails[j].background_image
                    val Adurl = DashboardDetails[j].add_url
                    val Id = 1
                    adimageList4.add(DashboardSubItems(addimage, adBackgroundImage, Adurl, Id))
                }
            }

            if (category == "Circular") {
                DashboardDetails = DashboardData[i].data!!
                for (j in DashboardDetails.indices) {
                    val description = DashboardDetails[j].description
                    val title = DashboardDetails[j].title
                    val createddate = DashboardDetails[j].createddate
                    val createdtime = DashboardDetails[j].createdtime
                    val filepaths = DashboardDetails[j].filepaths
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
            }

            if (category == "Upcoming Events") {
                DashboardDetails = DashboardData[i].data!!
                for (j in DashboardDetails.indices) {
                    val EventTitle = DashboardDetails[j].eventtopic
                    val Eventtime = DashboardDetails[j].eventtime
                    val Eventdate = DashboardDetails[j].eventdate
                    val ideventid = DashboardDetails[j].ideventdetails
                    dashboardEventlist.add(
                        DashboardSubItems(
                            EventTitle, Eventtime, Eventdate, ideventid
                        )
                    )
                }
            }

            if (category == "Chat") {
                DashboardDetails = DashboardData[i].data!!
                for (j in DashboardDetails.indices) {
                    val coursename = DashboardDetails[j].coursename
                    val departmentname = DashboardDetails[j].departmentname
                    val yearname = DashboardDetails[j].yearname
                    val sectionname = DashboardDetails[j].sectionname
                    val studentname = DashboardDetails[j].studentname
                    val question = DashboardDetails[j].question
                    val createdonchat = DashboardDetails[j].createdon
                    val dummy = DashboardDetails[j].yearname
                    val message = DashboardDetails[j].message

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
            }

            if (category == "Leave Request") {
                DashboardDetails = DashboardData[i].data!!
                for (j in DashboardDetails.indices) {
                    val leaveapplicationid = DashboardDetails[j].leaveapplicationid
                    val studentid = DashboardDetails[j].studentid
                    val membername = DashboardDetails[j].membername
                    val coursename = DashboardDetails[j].coursename
                    val departmentname = DashboardDetails[j].departmentname
                    val yearname = DashboardDetails[j].yearname
                    val sectionname = DashboardDetails[j].sectionname
                    val reason = DashboardDetails[j].reason
                    val fromdate = DashboardDetails[j].fromdate
                    val todate = DashboardDetails[j].todate
                    val leavestatus = DashboardDetails[j].leavestatus
                    val noofdays = DashboardDetails[j].noofdays
                    val appliedon = DashboardDetails[j].appliedon
                    val message = DashboardDetails[j].message

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
            }

            if (category == "Assignments") {
                DashboardDetails = DashboardData[i].data!!
                for (j in DashboardDetails.indices) {
                    val idassignmentdetails = DashboardDetails[j].idassignmentdetails
                    val assignmenttopic = DashboardDetails[j].assignmenttopic
                    val assignmentdescription = DashboardDetails[j].assignmentdescription
                    val submissiondate = DashboardDetails[j].submissiondate
                    val filepath = DashboardDetails[j].submissiondate
                    val filepathassignment = DashboardDetails[j].filepaths
                    val filetype = DashboardDetails[j].file_type

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
            }

            if (category == "Notice Board") {
                DashboardDetails = DashboardData[i].data!!
                for (j in DashboardDetails.indices) {
                    val description = DashboardDetails[j].topicbody
                    val title = DashboardDetails[j].topicheading
                    val createddate = DashboardDetails[j].createddate
                    val createdtime = DashboardDetails[j].createdtime
                    dashboardNoticeboardlist.add(
                        DashboardSubItems(
                            title, "", description, createddate, createdtime, "", category
                        )
                    )
                }
            }

            if (category == "Emergency Notification") {
                DashboardDetails = DashboardData[i].data!!
                for (j in DashboardDetails.indices) {
                    val description = DashboardDetails[j].description
                    val VoiceFilepath = DashboardDetails[j].voicefilepath
                    val membername = DashboardDetails[j].membername
                    val duration = DashboardDetails[j].duration
                    val createdon = DashboardDetails[j].createdon
                    val detailsid = DashboardDetails[j].detailsid
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
            }

            if (category == "Recent Notifications") {
                DashboardDetails = DashboardData[i].data!!
                for (j in DashboardDetails.indices) {
                    val description = DashboardDetails[j].description
                    val content = DashboardDetails[j].content
                    val membername = DashboardDetails[j].sentbyname
                    val duration = DashboardDetails[j].duration
                    val createdon = DashboardDetails[j].createdondate
                    val recentType = DashboardDetails[j].typ
                    val Createdontime = DashboardDetails[j].createdontime
                    val detailsID = DashboardDetails[j].id

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
            }

            if (category == "Attendance") {

                DashboardDetails = DashboardData[i].data!!

                for (k in DashboardDetails.indices) {

                    val attendancetype = DashboardDetails[k].attendancetype
                    val subjectName = DashboardDetails[k].subjectname
                    val attendancedate = DashboardDetails[k].attendancedate
                    val message = DashboardDetails[k].message
                    val Subjectname = DashboardDetails[k].subjectname
                    dashboardAttendancetlist.add(
                        DashboardSubItems(
                            subjectName, attendancetype, attendancedate, message, Subjectname
                        )
                    )
                }
            }

            //Setting into adapter

            if (category == "Ad" && order == 1) {
                dashboardOverallList.add(DashboardOverall(category!!, adimageList1))
                adapter = DashboardParent(dashboardOverallList, this@DashBoard)
                val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@DashBoard)
                idRVCategories!!.layoutManager = mLayoutManager
                idRVCategories!!.itemAnimator = DefaultItemAnimator()
                idRVCategories!!.adapter = adapter
                adapter!!.notifyDataSetChanged()
            }
            if (category == "Ad" && order == 2) {
                dashboardOverallList.add(DashboardOverall(category!!, adimageList2))
                adapter = DashboardParent(dashboardOverallList, this@DashBoard)
                val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@DashBoard)
                idRVCategories!!.layoutManager = mLayoutManager
                idRVCategories!!.itemAnimator = DefaultItemAnimator()
                idRVCategories!!.adapter = adapter
                adapter!!.notifyDataSetChanged()
            }
            if (category == "Ad" && order == 4) {
                dashboardOverallList.add(DashboardOverall(category!!, adimageList4))
                adapter = DashboardParent(dashboardOverallList, this@DashBoard)
                val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@DashBoard)
                idRVCategories!!.layoutManager = mLayoutManager
                idRVCategories!!.itemAnimator = DefaultItemAnimator()
                idRVCategories!!.adapter = adapter
                adapter!!.notifyDataSetChanged()
            } else if (category == "Notice Board") {
                dashboardOverallList.add(DashboardOverall(category!!, dashboardNoticeboardlist))
                adapter = DashboardParent(dashboardOverallList, this@DashBoard)
                val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@DashBoard)
                idRVCategories!!.layoutManager = mLayoutManager
                idRVCategories!!.itemAnimator = DefaultItemAnimator()
                idRVCategories!!.adapter = adapter
                adapter!!.notifyDataSetChanged()
            } else if (category == "Circular") {
                dashboardOverallList.add(DashboardOverall(category!!, dashboardCircularlist))
                adapter = DashboardParent(dashboardOverallList, this@DashBoard)
                val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@DashBoard)
                idRVCategories!!.layoutManager = mLayoutManager
                idRVCategories!!.itemAnimator = DefaultItemAnimator()
                idRVCategories!!.adapter = adapter
                adapter!!.notifyDataSetChanged()
            } else if (category == "Leave Request") {
                dashboardOverallList.add(DashboardOverall(category!!, dashboardLeaveRequestlist))
                adapter = DashboardParent(dashboardOverallList, this@DashBoard)
                val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@DashBoard)
                idRVCategories!!.layoutManager = mLayoutManager
                idRVCategories!!.itemAnimator = DefaultItemAnimator()
                idRVCategories!!.adapter = adapter
                adapter!!.notifyDataSetChanged()
            } else if (category == "Upcoming Events") {
                dashboardOverallList.add(DashboardOverall(category!!, dashboardEventlist))
                adapter = DashboardParent(dashboardOverallList, this@DashBoard)
                val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@DashBoard)
                idRVCategories!!.layoutManager = mLayoutManager
                idRVCategories!!.itemAnimator = DefaultItemAnimator()
                idRVCategories!!.adapter = adapter
                adapter!!.notifyDataSetChanged()
            } else if (category == "Assignments") {
                dashboardOverallList.add(DashboardOverall(category!!, dashboardAssignmentList))
                adapter = DashboardParent(dashboardOverallList, this@DashBoard)
                val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@DashBoard)
                idRVCategories!!.layoutManager = mLayoutManager
                idRVCategories!!.itemAnimator = DefaultItemAnimator()
                idRVCategories!!.adapter = adapter
                adapter!!.notifyDataSetChanged()
            } else if (category == "Chat") {
                dashboardOverallList.add(DashboardOverall(category!!, dashboardChatlist))
                adapter = DashboardParent(dashboardOverallList, this@DashBoard)
                val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@DashBoard)
                idRVCategories!!.layoutManager = mLayoutManager
                idRVCategories!!.itemAnimator = DefaultItemAnimator()
                idRVCategories!!.adapter = adapter
                adapter!!.notifyDataSetChanged()
            } else if (category == "Emergency Notification") {
                dashboardOverallList.add(DashboardOverall(category!!, dashboardEmergencyVoicelist))
                adapter = DashboardParent(dashboardOverallList, this@DashBoard)
                val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@DashBoard)
                idRVCategories!!.layoutManager = mLayoutManager
                idRVCategories!!.itemAnimator = DefaultItemAnimator()
                idRVCategories!!.adapter = adapter
                adapter!!.notifyDataSetChanged()
            } else if (category == "Recent Notifications") {
                dashboardOverallList.add(DashboardOverall(category!!, dashboardRecentVoicelist))
                adapter = DashboardParent(dashboardOverallList, this@DashBoard)
                val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@DashBoard)
                idRVCategories!!.layoutManager = mLayoutManager
                idRVCategories!!.itemAnimator = DefaultItemAnimator()
                idRVCategories!!.adapter = adapter
                adapter!!.notifyDataSetChanged()
            } else if (category == "Attendance") {
                dashboardOverallList.add(DashboardOverall(category!!, dashboardAttendancetlist))
                adapter = DashboardParent(dashboardOverallList, this@DashBoard)
                val mLayoutManager: RecyclerView.LayoutManager = LinearLayoutManager(this@DashBoard)
                idRVCategories!!.layoutManager = mLayoutManager
                idRVCategories!!.itemAnimator = DefaultItemAnimator()
                idRVCategories!!.adapter = adapter
                adapter!!.notifyDataSetChanged()
            }
        }
    }

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
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        dashboardViewModel!!.dashboard(jsonObject, this@DashBoard)
        Log.d("DahsboardRequest:", jsonObject.toString())
    }

    override fun onResume() {
//        dashboardCircularlist.clear()
//        dashboardEventlist.clear()
//        dashboardRecentVoicelist.clear()
//        dashboardChatlist.clear()
//        dashboardNoticeboardlist.clear()
//        dashboardAttendancetlist.clear()
//        dashboardLeaveRequestlist.clear()
//        dashboardAttendanceList!!.clear()
//        dashboardAssignmentList.clear()
//        dashboardEmergencyVoicelist.clear()

        MenuBottomType()
        DashBoardRequest()
        super.onResume()
    }


    private fun checkIfContactsExist() {

        exist_Count = 0
        val contentResolver: ContentResolver = this@DashBoard.getContentResolver()
        val uri = ContactsContract.CommonDataKinds.Phone.CONTENT_URI
        val projection = arrayOf(ContactsContract.PhoneLookup._ID)
        val selection = ContactsContract.CommonDataKinds.Phone.DISPLAY_NAME + " = ?"
        val selectionArguments = arrayOf<String>(contact_display_name)
        val cursor = contentResolver.query(uri, projection, selection, selectionArguments, null)
        exist_Count = cursor!!.count
        if (cursor != null) {
            while (cursor.moveToNext()) {
            }
        }
        Contact_Count = 0
        for (i in contacts.indices) {
            val number: String = contacts.get(i)
            if (number != null) {
                val lookupUri = Uri.withAppendedPath(
                    ContactsContract.PhoneLookup.CONTENT_FILTER_URI, Uri.encode(number)
                )
                val mPhoneNumberProjection = arrayOf(ContactsContract.PhoneLookup.DISPLAY_NAME)
                val cur: Cursor? = this@DashBoard.getContentResolver()
                    .query(lookupUri, mPhoneNumberProjection, null, null, null)
                try {
                    if (cur!!.moveToFirst()) {
                        Contact_Count = Contact_Count + 1
                        val indexName = cur.getColumnIndex(ContactsContract.Contacts.DISPLAY_NAME)
                        Display_Name = cur.getString(indexName)
                        if (Display_Name != contact_display_name) {
                            Contact_Count = Contact_Count - 1
                        }
                    }
                } finally {
                    if (cur != null) cur.close()
                }
            }
        }
        if (contacts.size != Contact_Count) {
            if (exist_Count == 0 || exist_Count < contacts.size) {
                contactSaveContent()
            }
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

        popupWindow.showAtLocation(rytParent, Gravity.CENTER, 0, 0)
        val container = popupWindow.contentView.parent as View
        val wm = getSystemService(WINDOW_SERVICE) as WindowManager
        val p = container.layoutParams as WindowManager.LayoutParams
        p.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND
        p.dimAmount = 0.5f
        wm.updateViewLayout(container, p)


        val btnSaveContact = popupView.findViewById<View>(R.id.btnSaveContact) as TextView
        val imgClose = popupView.findViewById<View>(R.id.imgClose) as ImageView
        val lblHeader = popupView.findViewById<View>(R.id.lblHeader) as TextView
        val lblContent = popupView.findViewById<View>(R.id.lblContent) as TextView
        lblHeader.text = contact_alert_title
        lblContent.text = contact_alert_Content
        btnSaveContact.text = contact_button
        btnSaveContact.setOnClickListener {
            try {
                popupWindow.dismiss()
                saveContacts()
            } catch (e: Exception) {
                Log.d("failure_popup_error", e.toString())
            }
        }
        imgClose.setOnClickListener { popupWindow.dismiss() }
    }

    private fun saveContacts() {
        val b = BitmapFactory.decodeResource(resources, R.drawable.gradit_logo)
        val stream = ByteArrayOutputStream()
        b.compress(Bitmap.CompressFormat.PNG, 100, stream)
        val byteArray = stream.toByteArray()
        val data = java.util.ArrayList<ContentValues>()
        val intent = Intent(Intent.ACTION_INSERT, ContactsContract.Contacts.CONTENT_URI)
        val row = ContentValues()
        row.put(
            ContactsContract.Contacts.Data.MIMETYPE,
            ContactsContract.CommonDataKinds.Photo.CONTENT_ITEM_TYPE
        )
        row.put(ContactsContract.CommonDataKinds.Photo.PHOTO, byteArray)
        data.add(row)
        for (i in contacts.indices) {
            val row_Number = ContentValues()
            row_Number.put(
                ContactsContract.RawContacts.Data.MIMETYPE,
                ContactsContract.CommonDataKinds.Phone.CONTENT_ITEM_TYPE
            )
            row_Number.put(ContactsContract.CommonDataKinds.Phone.NUMBER, contacts[i])
            row_Number.put(
                ContactsContract.CommonDataKinds.Phone.TYPE,
                ContactsContract.CommonDataKinds.Phone.TYPE_MOBILE
            )
            data.add(row_Number)
        }
        intent.putExtra(ContactsContract.Intents.Insert.NAME, contact_display_name)
        intent.putParcelableArrayListExtra(ContactsContract.Intents.Insert.DATA, data)
        startActivityForResult(intent, 100)
    }
}