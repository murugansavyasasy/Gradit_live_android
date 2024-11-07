package com.vsca.vsnapvoicecollege.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.ImageView
import android.widget.SearchView
import android.widget.TextView
import android.widget.Toast
import androidx.lifecycle.Observer
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
import com.vsca.vsnapvoicecollege.ActivitySender.AddAssignment
import com.vsca.vsnapvoicecollege.Adapters.AssignmentAdapter
import com.vsca.vsnapvoicecollege.Model.GetAdvertiseData
import com.vsca.vsnapvoicecollege.Model.GetAdvertisementResponse
import com.vsca.vsnapvoicecollege.Model.GetAssignmentDetails
import com.vsca.vsnapvoicecollege.Model.GetOverAllCountDetails
import com.vsca.vsnapvoicecollege.R
import com.vsca.vsnapvoicecollege.Repository.ApiRequestNames
import com.vsca.vsnapvoicecollege.Utils.CommonUtil
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.OnBackSetBottomMenuClickTrue
import com.vsca.vsnapvoicecollege.Utils.CommonUtil.OnMenuClicks
import com.vsca.vsnapvoicecollege.Utils.SharedPreference
import com.vsca.vsnapvoicecollege.ViewModel.App
import java.util.Locale

class Assignment : BaseActivity() {

    var assignmentadapter: AssignmentAdapter? = null
    override var appViewModel: App? = null

    @JvmField
    @BindView(R.id.recyclerCommon)
    var recyclerNoticeboard: RecyclerView? = null

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
    @BindView(R.id.lblNoRecordsFound)
    var lblNoRecordsFound: TextView? = null
    var AssignmentType = true
    var GetAssignmentData: List<GetAssignmentDetails> = ArrayList()

    var AdBackgroundImage: String? = null
    var AdSmallImage: String? = null
    var AdWebURl: String? = null
    var GetAdForCollegeData: List<GetAdvertiseData> = ArrayList()
    var PreviousAddId: Int = 0

    var CountUpcoming: String? = null
    var Countpast: String? = null
    var OverAllMenuCountData1: List<GetOverAllCountDetails> = ArrayList()
    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()
        ButterKnife.bind(this)
        ActionBarMethod(this@Assignment)

        CommonUtil.RequestCameraPermission(this)
        MenuBottomType()

        if (CommonUtil.menu_readAssignment.equals("1")) {
            OverAllMenuCountRequestAssignment(this, CommonUtil.MenuIDAssignment!!)
        }
        OnMenuClicks(CommonUtil._OnclickScreen)
        TabDepartmentColor()
        CommonUtil.pastExam = ""
        lblMenuTitle!!.setText(R.string.txt_assignment)
        lblDepartment!!.setText(R.string.txt_upcoming)
        lblCollege!!.setText(R.string.txt_past)

        SearchList!!.visibility = View.VISIBLE
        SearchList!!.setOnClickListener {
            Search!!.visibility = View.VISIBLE
        }

        if (CommonUtil.menu_readAssignment == "1") {
            AssignmentRequest(AssignmentType)
        }

        idSV!!.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                return false

            }

            override fun onQueryTextChange(msg: String): Boolean {

                filter(msg)
                return false
            }
        })

        txt_Cancel!!.setOnClickListener {

            Search!!.visibility = View.GONE

        }


        appViewModel!!.AdvertisementLiveData?.observe(this,
            Observer<GetAdvertisementResponse?> { response ->
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
                        Glide.with(this).load(AdSmallImage).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(imgthumb!!)
                    }
                }
            })

        appViewModel!!.OverAllMenuResponseLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                if (status == 1) {
                    if (response.data.isNullOrEmpty()) {
                        OverAllMenuCountData1 = emptyList()
                    } else {
                        OverAllMenuCountData1 = response.data!!
                        CountUpcoming = OverAllMenuCountData1[0].upcomingassignment
                        Countpast = OverAllMenuCountData1[0].pastassignment
                        CountValueSet()
                    }
                } else {

                    OverAllMenuCountData1 = emptyList()
                }
            }
        }

        appViewModel!!.assignmentListResponseLiveData!!.observe(this) { response ->
            if (response != null) {
                val status = response.status
                val message = response.message
                UserMenuRequest(this@Assignment)
                if (status == 1) {
                    UserMenuRequest(this)
                    AdForCollegeApi()
                    if (AssignmentType) {
                        GetAssignmentData = response.data!!
                        val size = GetAssignmentData.size
                        if (size > 0) {
                            lblNoRecordsFound!!.visibility = View.GONE
                            recyclerNoticeboard!!.visibility = View.VISIBLE
                            assignmentadapter =
                                AssignmentAdapter(GetAssignmentData, this@Assignment)
                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this@Assignment)
                            recyclerNoticeboard!!.layoutManager = mLayoutManager
                            recyclerNoticeboard!!.itemAnimator = DefaultItemAnimator()
                            recyclerNoticeboard!!.adapter = assignmentadapter
                            recyclerNoticeboard!!.recycledViewPool.setMaxRecycledViews(0, 80)
                            assignmentadapter!!.notifyDataSetChanged()
                        } else {
                            lblNoRecordsFound!!.visibility = View.VISIBLE
                            recyclerNoticeboard!!.visibility = View.GONE
                        }
                    } else {
                        GetAssignmentData = response.data!!
                        val size = GetAssignmentData.size
                        if (size > 0) {
                            lblNoRecordsFound!!.visibility = View.GONE
                            recyclerNoticeboard!!.visibility = View.VISIBLE
                            assignmentadapter =
                                AssignmentAdapter(GetAssignmentData, this@Assignment)
                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this@Assignment)
                            recyclerNoticeboard!!.layoutManager = mLayoutManager
                            recyclerNoticeboard!!.itemAnimator = DefaultItemAnimator()
                            recyclerNoticeboard!!.adapter = assignmentadapter
                            recyclerNoticeboard!!.recycledViewPool.setMaxRecycledViews(0, 80)
                            assignmentadapter!!.notifyDataSetChanged()
                        } else {
                            lblNoRecordsFound!!.visibility = View.VISIBLE
                            recyclerNoticeboard!!.visibility = View.GONE
                        }
                    }
                } else {
                    if (AssignmentType) {
                        lblNoRecordsFound!!.visibility = View.VISIBLE
                        recyclerNoticeboard!!.visibility = View.GONE
                    } else {
                        lblNoRecordsFound!!.visibility = View.VISIBLE
                        recyclerNoticeboard!!.visibility = View.GONE
                    }
                }
            } else {
                lblNoRecordsFound!!.visibility = View.VISIBLE
                recyclerNoticeboard!!.visibility = View.GONE
            }
        }

        imgRefresh!!.setOnClickListener(View.OnClickListener {
            if (AssignmentType) {
                AssignmentType = true
                if (CommonUtil.menu_readAssignment.equals("1")) {
                    AssignmentRequest(AssignmentType)
                }
            } else {
                AssignmentType = false
                if (CommonUtil.menu_readAssignment.equals("1")) {
                    AssignmentRequest(AssignmentType)
                }
            }
        })
    }

    private fun filter(text: String) {


        val filteredlist: java.util.ArrayList<GetAssignmentDetails> = java.util.ArrayList()

        for (item in GetAssignmentData) {
            if (item.topic!!.lowercase(Locale.getDefault())
                    .contains(text.lowercase(Locale.getDefault()))
            ) {

                filteredlist.add(item)

            }
        }
        if (filteredlist.isEmpty()) {

            Toast.makeText(this, CommonUtil.No_Data_Found, Toast.LENGTH_SHORT).show()
        } else {
            assignmentadapter!!.filterList(filteredlist)

        }

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

    private fun CountValueSet() {

        if (!CountUpcoming.equals("0") && !CountUpcoming.equals("")) {
            lblDepartmentSize!!.visibility = View.VISIBLE
            lblDepartmentSize!!.text = CountUpcoming
        } else {
            lblDepartmentSize!!.visibility = View.GONE
            CountUpcoming = "0"
        }
        if (!Countpast.equals("0") && !Countpast.equals("")) {
            lblCollegeSize!!.visibility = View.VISIBLE
            lblCollegeSize!!.text = Countpast
        } else {
            lblCollegeSize!!.visibility = View.GONE
            Countpast = "0"
        }

        var intdepartment = Integer.parseInt(CountUpcoming!!)
        var intCollegecount = Integer.parseInt(Countpast!!)
        var TotalSizeCount = intdepartment + intCollegecount
        if (TotalSizeCount > 0) {
            lbltotalsize!!.visibility = View.VISIBLE
            lbltotalsize!!.text = TotalSizeCount.toString()
        } else {
            lbltotalsize!!.visibility = View.GONE
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_noticeboard

    private fun AssignmentRequest(type: Boolean) {
        val jsonObject = JsonObject()
        run {
            jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
            jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)
            jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)

            if (CommonUtil.Priority == "p7" || CommonUtil.Priority == "p1" || CommonUtil.Priority == "p2" || CommonUtil.Priority == "p3" || CommonUtil.Priority == "p6") {
                jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.SenderAppId)
                jsonObject.addProperty(ApiRequestNames.Req_departmentid, CommonUtil.DepartmentId)
                jsonObject.addProperty(ApiRequestNames.Req_sectionid, "0")

            } else if (CommonUtil.Priority == "p4" || CommonUtil.Priority == "p5") {
                jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.SenderAppId)
                jsonObject.addProperty(ApiRequestNames.Req_sectionid, CommonUtil.SectionId)
                jsonObject.addProperty(ApiRequestNames.Req_departmentid, CommonUtil.DepartmentId)

            }

            if (type) {
                jsonObject.addProperty(ApiRequestNames.Req_type, CommonUtil.UpcomingAssignment)
            } else {
                jsonObject.addProperty(ApiRequestNames.Req_type, CommonUtil.PastAssignment)
            }
            appViewModel!!.getAssignmentListbyType(jsonObject, this@Assignment)
            Log.d("AssignmentRequest:", jsonObject.toString())
        }
    }

    @OnClick(R.id.LayoutDepartment)
    fun departmentClick() {
        bottomsheetStateCollpased()
        AssignmentType = true
        if (CommonUtil.menu_readAssignment.equals("1")) {
            AssignmentRequest(AssignmentType)
        }
        TabDepartmentColor()
        CommonUtil.pastExam = ""
    }

    @OnClick(R.id.LayoutCollege)
    fun collegeClick() {
        bottomsheetStateCollpased()
        AssignmentType = false
        if (CommonUtil.menu_readAssignment.equals("1")) {
            AssignmentRequest(AssignmentType)
        }
        bottomsheetStateCollpased()
        TabCollegeColor()
        CommonUtil.pastExam = "1"

    }

    @OnClick(R.id.LayoutAdvertisement)
    fun adclick() {
        LoadWebViewContext(this, AdWebURl)
    }

    override fun onResume() {
        super.onResume()
//            if (CommonUtil.menu_readAssignment == "1") {
//                AssignmentRequest(AssignmentType)
//            }
        var AddId: Int = 1
        PreviousAddId += 1
    }

    @OnClick(R.id.imgAddPlus)
    fun imgaddclick() {
        val i: Intent = Intent(this, AddAssignment::class.java)
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        i.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        startActivity(i)

    }

    override fun onBackPressed() {
        OnBackSetBottomMenuClickTrue()
        super.onBackPressed()
    }


    fun OverAllMenuCountRequestAssignment(activity: Activity?, menuid: String) {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_menuid, menuid)
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId)

        if (CommonUtil.Priority == "p1") {
            jsonObject.addProperty(ApiRequestNames.Req_departmentid, 0)
            jsonObject.addProperty(ApiRequestNames.Req_sectionid, 0)
        } else {
            jsonObject.addProperty(ApiRequestNames.Req_departmentid, CommonUtil.DepartmentId)
            jsonObject.addProperty(ApiRequestNames.Req_sectionid, "0")
        }

        if (CommonUtil.Priority == "p7" || CommonUtil.Priority == "p1" || CommonUtil.Priority == "p2" || CommonUtil.Priority == "p3") {
            jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.SenderAppId)
        } else {
            jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.SenderAppId)
        }

        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        appviewModelbase!!.getOverAllMenuCount(jsonObject, activity)
        Log.d("OverAllMenuCount_Req:", jsonObject.toString())
    }
}