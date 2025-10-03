package com.vsca.vsnapvoicecollege.Activities

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.SearchView
import android.widget.Toast
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
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
import com.vsca.vsnapvoicecollege.databinding.ActivityNoticeboardBinding
import java.util.Locale

class Assignment : BaseActivity<ActivityNoticeboardBinding>() {

    var assignmentadapter: AssignmentAdapter? = null
    override var appViewModel: App? = null

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

    override fun inflateBinding(): ActivityNoticeboardBinding {
        return ActivityNoticeboardBinding.inflate(layoutInflater)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        CommonUtil.SetTheme(this)
        super.onCreate(savedInstanceState)
        binding = ActivityNoticeboardBinding.inflate(layoutInflater)
        setContentView(binding.root)

        appViewModel = ViewModelProvider(this)[App::class.java]
        appViewModel!!.init()
        ActionBarMethod(this@Assignment)
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


        CommonUtil.RequestCameraPermission(this)
        MenuBottomType()

        if (CommonUtil.menu_readAssignment.equals("1")) {
            OverAllMenuCountRequestAssignment(this, CommonUtil.MenuIDAssignment!!)
        }
        OnMenuClicks(CommonUtil._OnclickScreen)
        TabDepartmentColor()
        CommonUtil.pastExam = ""
        binding.CommonLayout.lblMenuTitle!!.setText(R.string.txt_assignment)
        binding.CommonLayout.lblDepartment!!.setText(R.string.txt_upcoming)
        binding.CommonLayout.lblCollege!!.setText(R.string.txt_past)

        SearchList!!.visibility = View.VISIBLE
        SearchList!!.setOnClickListener {
            Search!!.visibility = View.VISIBLE
        }
        binding.CommonLayout.LayoutAdvertisement.setOnClickListener { adclick() }


        binding.CommonLayout.LayoutDepartment.setOnClickListener {
            departmentClick()
        }

        binding.CommonLayout.LayoutCollege.setOnClickListener {
            collegeClick()
        }

        if (CommonUtil.menu_readAssignment == "1") {
            AssignmentRequest(AssignmentType)
        }

        binding.CommonLayout.imgAddPlus.setOnClickListener {
            imgaddclick()
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


        appViewModel!!.AdvertisementLiveData?.observe(
            this,
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
                            .diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.CommonLayout.imgAdvertisement!!)
                        Glide.with(this).load(AdSmallImage).diskCacheStrategy(DiskCacheStrategy.ALL)
                            .into(binding.CommonLayout.imgthumb!!)
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
                            binding.CommonLayout.lblNoRecordsFound!!.visibility = View.GONE
                            binding.CommonLayout.recyclerCommon!!.visibility = View.VISIBLE
                            assignmentadapter =
                                AssignmentAdapter(GetAssignmentData, this@Assignment)
                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this@Assignment)
                            binding.CommonLayout.recyclerCommon!!.layoutManager = mLayoutManager
                            binding.CommonLayout.recyclerCommon!!.itemAnimator =
                                DefaultItemAnimator()
                            binding.CommonLayout.recyclerCommon!!.adapter = assignmentadapter
                            binding.CommonLayout.recyclerCommon!!.recycledViewPool.setMaxRecycledViews(
                                0,
                                80
                            )
                            assignmentadapter!!.notifyDataSetChanged()
                        } else {
                            binding.CommonLayout.lblNoRecordsFound!!.visibility = View.VISIBLE
                            binding.CommonLayout.recyclerCommon!!.visibility = View.GONE
                        }
                    } else {
                        GetAssignmentData = response.data!!
                        val size = GetAssignmentData.size
                        if (size > 0) {
                            binding.CommonLayout.lblNoRecordsFound!!.visibility = View.GONE
                            binding.CommonLayout.recyclerCommon!!.visibility = View.VISIBLE
                            assignmentadapter =
                                AssignmentAdapter(GetAssignmentData, this@Assignment)
                            val mLayoutManager: RecyclerView.LayoutManager =
                                LinearLayoutManager(this@Assignment)
                            binding.CommonLayout.recyclerCommon!!.layoutManager = mLayoutManager
                            binding.CommonLayout.recyclerCommon!!.itemAnimator =
                                DefaultItemAnimator()
                            binding.CommonLayout.recyclerCommon!!.adapter = assignmentadapter
                            binding.CommonLayout.recyclerCommon!!.recycledViewPool.setMaxRecycledViews(
                                0,
                                80
                            )
                            assignmentadapter!!.notifyDataSetChanged()
                        } else {
                            binding.CommonLayout.lblNoRecordsFound!!.visibility = View.VISIBLE
                            binding.CommonLayout.recyclerCommon!!.visibility = View.GONE
                        }
                    }
                } else {
                    if (AssignmentType) {
                        binding.CommonLayout.lblNoRecordsFound!!.visibility = View.VISIBLE
                        binding.CommonLayout.recyclerCommon!!.visibility = View.GONE
                    } else {
                        binding.CommonLayout.lblNoRecordsFound!!.visibility = View.VISIBLE
                        binding.CommonLayout.recyclerCommon!!.visibility = View.GONE
                    }
                }
            } else {
                binding.CommonLayout.lblNoRecordsFound!!.visibility = View.VISIBLE
                binding.CommonLayout.recyclerCommon!!.visibility = View.GONE
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
            binding.CommonLayout.lblDepartmentSize!!.visibility = View.VISIBLE
            binding.CommonLayout.lblDepartmentSize!!.text = CountUpcoming
        } else {
            binding.CommonLayout.lblDepartmentSize!!.visibility = View.GONE
            CountUpcoming = "0"
        }
        if (!Countpast.equals("0") && !Countpast.equals("")) {
            binding.CommonLayout.lblCollegeSize!!.visibility = View.VISIBLE
            binding.CommonLayout.lblCollegeSize!!.text = Countpast
        } else {
            binding.CommonLayout.lblCollegeSize!!.visibility = View.GONE
            Countpast = "0"
        }

        var intdepartment = Integer.parseInt(CountUpcoming!!)
        var intCollegecount = Integer.parseInt(Countpast!!)
        var TotalSizeCount = intdepartment + intCollegecount
        if (TotalSizeCount > 0) {
            binding.CommonLayout.lbltotalsize!!.visibility = View.VISIBLE
            binding.CommonLayout.lbltotalsize!!.text = TotalSizeCount.toString()
        } else {
            binding.CommonLayout.lbltotalsize!!.visibility = View.GONE
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

    fun departmentClick() {
        bottomsheetStateCollpased()
        AssignmentType = true
        if (CommonUtil.menu_readAssignment.equals("1")) {
            AssignmentRequest(AssignmentType)
        }
        TabDepartmentColor()
        CommonUtil.pastExam = ""
    }

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