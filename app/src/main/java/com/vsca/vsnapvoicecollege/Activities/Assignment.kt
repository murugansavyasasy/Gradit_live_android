package com.vsca.vsnapvoicecollege.Activities

import android.app.Activity
import android.app.ProgressDialog
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
import com.vsca.vsnapvoicecollege.Utils.CustomLoading
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

    // Single loader shown until the initial APIs (count + ads + list) all finish; the individual
    // per-call loaders are suppressed for these calls. pendingInitialApiCount tracks how many remain.
    private var isFirstLoad = true
    private var initialLoader: ProgressDialog? = null
    private var pendingInitialApiCount = 0

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
        setupEdgeToEdgeAuto(
            rootView = binding.Main,
            statusBarBgView = binding.statusBarBackground,
            priority = CommonUtil.Priority
        )

        if (supportActionBar != null) {
            ActionBarMethod(this)
            fixActionBarOverlap(binding.LayoutBottomMenus)
        }

        accessBottomViewIcons(
            binding,
            R.id.LayoutDepartment,
            R.id.LayoutCollege,
            R.id.imgAddPlus
        )

        CommonUtil.RequestCameraPermission(this)

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
                markInitialApiDone()
                if (response != null) {
                    val status = response.status
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
            markInitialApiDone()
            if (response != null) {
                val status = response.status
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
            markInitialApiDone()
            if (response != null) {
                val status = response.status
                if (status == 1) {
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
                            binding.CommonLayout.lblNoRecordsFound!!.text=getString(R.string.txt_no_data_found)
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
                            binding.CommonLayout.lblNoRecordsFound!!.text=getString(R.string.txt_no_data_found)
                            binding.CommonLayout.lblNoRecordsFound!!.visibility = View.VISIBLE
                            binding.CommonLayout.recyclerCommon!!.visibility = View.GONE
                        }
                    }
                } else {
                    if (AssignmentType) {
                        binding.CommonLayout.lblNoRecordsFound!!.text=response.message?:getString(R.string.txt_no_data_found)
                        binding.CommonLayout.lblNoRecordsFound!!.visibility = View.VISIBLE
                        binding.CommonLayout.recyclerCommon!!.visibility = View.GONE
                    } else {
                        binding.CommonLayout.lblNoRecordsFound!!.text=response.message?:getString(R.string.txt_no_data_found)
                        binding.CommonLayout.lblNoRecordsFound!!.visibility = View.VISIBLE
                        binding.CommonLayout.recyclerCommon!!.visibility = View.GONE
                    }
                }
            } else {
                binding.CommonLayout.lblNoRecordsFound!!.text=getString(R.string.error_null_cursor)
                binding.CommonLayout.lblNoRecordsFound!!.visibility = View.VISIBLE
                binding.CommonLayout.recyclerCommon!!.visibility = View.GONE
            }
        }

        imgRefresh!!.setOnClickListener {
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
        }
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

    private fun AdForCollegeApi(showLoader: Boolean = true) {
        val mobilenumber = SharedPreference.getSH_MobileNumber(this)
        val devicetoken = SharedPreference.getSH_DeviceToken(this)
        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_ad_device_token, devicetoken)
        jsonObject.addProperty(ApiRequestNames.Req_MemberID, CommonUtil.MemberId)
        jsonObject.addProperty(ApiRequestNames.Req_mobileno, mobilenumber)
        jsonObject.addProperty(ApiRequestNames.Req_college_id, CommonUtil.CollegeId)
        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        jsonObject.addProperty(ApiRequestNames.Req_previous_add_id, PreviousAddId)
        appviewModelbase!!.getAdforCollege(jsonObject, this, showLoader)
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

        val intdepartment = Integer.parseInt(CountUpcoming!!)
        val intCollegecount = Integer.parseInt(Countpast!!)
        val TotalSizeCount = intdepartment + intCollegecount
        if (TotalSizeCount > 0) {
            binding.CommonLayout.lbltotalsize!!.visibility = View.VISIBLE
            binding.CommonLayout.lbltotalsize!!.text = TotalSizeCount.toString()
        } else {
            binding.CommonLayout.lbltotalsize!!.visibility = View.GONE
        }
    }

    override val layoutResourceId: Int
        get() = R.layout.activity_noticeboard

    private fun AssignmentRequest(type: Boolean, showLoader: Boolean = true) {
        val jsonObject = JsonObject()
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
        appViewModel!!.getAssignmentListbyType(jsonObject, this@Assignment, showLoader)
        Log.d("AssignmentRequest:", jsonObject.toString())
    }

    fun departmentClick() {
        AssignmentType = true
        if (CommonUtil.menu_readAssignment.equals("1")) {
            AssignmentRequest(AssignmentType)
        }
        TabDepartmentColor()
        CommonUtil.pastExam = ""
    }

    fun collegeClick() {
        AssignmentType = false
        if (CommonUtil.menu_readAssignment.equals("1")) {
            AssignmentRequest(AssignmentType)
        }
        TabCollegeColor()
        CommonUtil.pastExam = "1"
    }

    fun adclick() {
        LoadWebViewContext(this, AdWebURl)
    }

    override fun onResume() {
        super.onResume()
        if (isFirstLoad) {
            isFirstLoad = false
            loadInitialData()
        }
    }

    /**
     * Fires the initial APIs once, behind a single shared loader instead of one loader per call:
     *   - GetOverallcountByMenuType (count)
     *   - GetAddsForCollege (ads)
     *   - GetAssignmentListbytype (list)
     * The per-call loaders are suppressed (showLoader = false); the shared loader is dismissed
     * by [markInitialApiDone] once every response has come back.
     */
    private fun loadInitialData() {
        if (CommonUtil.menu_readAssignment != "1") return
        pendingInitialApiCount = 3
        initialLoader = CustomLoading.createProgressDialog(this)
        OverAllMenuCountRequestAssignment(this, CommonUtil.MenuIDAssignment!!, false)
        AdForCollegeApi(false)
        AssignmentRequest(AssignmentType, false)
    }

    /** Counts down the pending initial APIs and dismisses the shared loader when all have finished. */
    private fun markInitialApiDone() {
        if (initialLoader == null || pendingInitialApiCount <= 0) return
        pendingInitialApiCount -= 1
        if (pendingInitialApiCount <= 0) {
            initialLoader?.dismiss()
            initialLoader = null
        }
    }

    override fun onDestroy() {
        initialLoader?.dismiss()
        initialLoader = null
        super.onDestroy()
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

    fun OverAllMenuCountRequestAssignment(activity: Activity?, menuid: String, showLoader: Boolean = true) {

        val jsonObject = JsonObject()
        jsonObject.addProperty(ApiRequestNames.Req_userid, CommonUtil.MemberId?.toString()?:"")
        jsonObject.addProperty(ApiRequestNames.Req_menuid, menuid)
        jsonObject.addProperty(ApiRequestNames.Req_collegeid, CommonUtil.CollegeId?.toString()?:"")

        if (CommonUtil.Priority == "p1") {
            jsonObject.addProperty(ApiRequestNames.Req_departmentid, "0")
            jsonObject.addProperty(ApiRequestNames.Req_sectionid, "0")
        } else {
            jsonObject.addProperty(ApiRequestNames.Req_departmentid, CommonUtil.DepartmentId)
            jsonObject.addProperty(ApiRequestNames.Req_sectionid, "0")
        }

        if (CommonUtil.Priority == "p7" || CommonUtil.Priority == "p1" || CommonUtil.Priority == "p2" || CommonUtil.Priority == "p3") {
            jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.SenderAppId?.toString()?:"")
        } else {
            jsonObject.addProperty(ApiRequestNames.Req_appid, CommonUtil.SenderAppId?.toString()?:"")
        }

        jsonObject.addProperty(ApiRequestNames.Req_priority, CommonUtil.Priority)
        appviewModelbase!!.getOverAllMenuCount(jsonObject, activity, showLoader)
        Log.d("OverAllMenuCount_Req:", jsonObject.toString())
    }
}